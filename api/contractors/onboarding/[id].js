import { neon } from '@neondatabase/serverless';

const sql = neon(process.env.DATABASE_URL);

export default async function handler(req, res) {
  // Enable CORS
  res.setHeader('Access-Control-Allow-Credentials', true);
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET,POST,PUT,DELETE,OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version');
  
  if (req.method === 'OPTIONS') {
    res.status(200).end();
    return;
  }

  const { id } = req.query;

  if (!id) {
    return res.status(400).json({ success: false, error: 'Onboarding ID required' });
  }

  try {
    switch (req.method) {
      case 'GET':
        return await handleGetOnboarding(req, res, id);
      case 'PUT':
        return await handleUpdateOnboarding(req, res, id);
      default:
        res.status(405).json({ success: false, error: 'Method not allowed' });
    }
  } catch (error) {
    console.error('API Error:', error);
    res.status(500).json({ success: false, error: error.message });
  }
}

async function handleGetOnboarding(req, res, id) {
  try {
    const result = await sql`
      SELECT 
        co.*,
        c.company_name,
        c.contact_name,
        c.email,
        c.phone,
        c.status as contractor_status
      FROM contractor_onboarding co
      INNER JOIN contractors c ON co.contractor_id = c.id
      WHERE co.id = ${id}
    `;

    if (result.length === 0) {
      return res.status(404).json({ success: false, error: 'Onboarding not found' });
    }

    const onboarding = result[0];

    // Get checklist items
    const checklist = await sql`
      SELECT * FROM contractor_onboarding_checklist
      WHERE onboarding_id = ${id}
      ORDER BY step_order
    `;

    // Get documents
    const documents = await sql`
      SELECT * FROM contractor_documents
      WHERE contractor_id = ${onboarding.contractor_id}
      ORDER BY created_at DESC
    `;

    // Get activity log
    const activityLog = await sql`
      SELECT * FROM contractor_onboarding_activity
      WHERE onboarding_id = ${id}
      ORDER BY created_at DESC
      LIMIT 50
    `;

    return res.status(200).json({ 
      success: true, 
      data: {
        ...onboarding,
        checklist,
        documents,
        activityLog,
        progress: calculateProgress(checklist)
      }
    });
  } catch (error) {
    console.error('Error fetching onboarding:', error);
    return res.status(500).json({ success: false, error: error.message });
  }
}

async function handleUpdateOnboarding(req, res, id) {
  try {
    const {
      status,
      currentStage,
      completedAt,
      completedBy,
      rejectedAt,
      rejectedBy,
      rejectionReason,
      metadata,
      notes
    } = req.body;

    // Build dynamic update query
    const updates = [];
    const values = [];
    let valueIndex = 1;

    if (status !== undefined) {
      updates.push(`status = $${valueIndex}`);
      values.push(status);
      valueIndex++;
    }

    if (currentStage !== undefined) {
      updates.push(`current_stage = $${valueIndex}`);
      values.push(currentStage);
      valueIndex++;
    }

    if (completedAt !== undefined) {
      updates.push(`completed_at = $${valueIndex}`);
      values.push(completedAt);
      valueIndex++;
    }

    if (completedBy !== undefined) {
      updates.push(`completed_by = $${valueIndex}`);
      values.push(completedBy);
      valueIndex++;
    }

    if (rejectedAt !== undefined) {
      updates.push(`rejected_at = $${valueIndex}`);
      values.push(rejectedAt);
      valueIndex++;
    }

    if (rejectedBy !== undefined) {
      updates.push(`rejected_by = $${valueIndex}`);
      values.push(rejectedBy);
      valueIndex++;
    }

    if (rejectionReason !== undefined) {
      updates.push(`rejection_reason = $${valueIndex}`);
      values.push(rejectionReason);
      valueIndex++;
    }

    if (metadata !== undefined) {
      updates.push(`metadata = $${valueIndex}`);
      values.push(JSON.stringify(metadata));
      valueIndex++;
    }

    if (notes !== undefined) {
      updates.push(`notes = $${valueIndex}`);
      values.push(notes);
      valueIndex++;
    }

    // Always update the updated_at timestamp
    updates.push(`updated_at = NOW()`);

    if (updates.length === 1) { // Only updated_at
      return res.status(400).json({ success: false, error: 'No fields to update' });
    }

    // Add the ID as the last parameter
    values.push(id);

    const query = `
      UPDATE contractor_onboarding 
      SET ${updates.join(', ')}
      WHERE id = $${valueIndex}
      RETURNING *
    `;

    const result = await sql(query, values);

    if (result.length === 0) {
      return res.status(404).json({ success: false, error: 'Onboarding not found' });
    }

    // Log activity
    await sql`
      INSERT INTO contractor_onboarding_activity (
        onboarding_id,
        action,
        details,
        performed_by
      )
      VALUES (
        ${id},
        ${status ? `Status changed to ${status}` : 'Onboarding updated'},
        ${JSON.stringify({ updates: req.body })},
        ${req.body.updatedBy || 'system'}
      )
    `;

    // Update contractor status if onboarding is completed or rejected
    if (status === 'completed' || status === 'rejected') {
      const contractorStatus = status === 'completed' ? 'active' : 'inactive';
      await sql`
        UPDATE contractors 
        SET status = ${contractorStatus}, updated_at = NOW()
        WHERE id = ${result[0].contractor_id}
      `;
    }

    return res.status(200).json({ 
      success: true, 
      data: result[0] 
    });
  } catch (error) {
    console.error('Error updating onboarding:', error);
    return res.status(500).json({ success: false, error: error.message });
  }
}

function calculateProgress(checklist) {
  if (!checklist || checklist.length === 0) return 0;
  
  const completed = checklist.filter(item => item.is_completed).length;
  const required = checklist.filter(item => item.is_required).length;
  
  return Math.round((completed / required) * 100);
}