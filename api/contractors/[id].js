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
    return res.status(400).json({ success: false, error: 'Contractor ID required' });
  }

  try {
    switch (req.method) {
      case 'GET':
        return await handleGetContractor(req, res, id);
      case 'PUT':
        return await handleUpdateContractor(req, res, id);
      case 'DELETE':
        return await handleDeleteContractor(req, res, id);
      default:
        res.status(405).json({ success: false, error: 'Method not allowed' });
    }
  } catch (error) {
    console.error('API Error:', error);
    res.status(500).json({ success: false, error: error.message });
  }
}

async function handleGetContractor(req, res, id) {
  try {
    const result = await sql`
      SELECT * FROM contractors 
      WHERE id = ${id}
    `;

    if (result.length === 0) {
      return res.status(404).json({ success: false, error: 'Contractor not found' });
    }

    // Get associated teams
    const teams = await sql`
      SELECT t.* FROM teams t
      INNER JOIN contractor_teams ct ON t.id = ct.team_id
      WHERE ct.contractor_id = ${id}
    `;

    // Get documents count
    const documents = await sql`
      SELECT COUNT(*) as count, document_type
      FROM contractor_documents
      WHERE contractor_id = ${id}
      GROUP BY document_type
    `;

    // Get project statistics
    const projectStats = await sql`
      SELECT 
        COUNT(*) FILTER (WHERE status = 'active') as active_projects,
        COUNT(*) FILTER (WHERE status = 'completed') as completed_projects,
        COUNT(*) as total_projects
      FROM project_assignments
      WHERE contractor_id = ${id}
    `;

    const contractor = {
      ...result[0],
      teams: teams,
      documents: documents,
      projectStats: projectStats[0] || { active_projects: 0, completed_projects: 0, total_projects: 0 }
    };

    return res.status(200).json({ 
      success: true, 
      data: contractor 
    });
  } catch (error) {
    console.error('Error fetching contractor:', error);
    return res.status(500).json({ success: false, error: error.message });
  }
}

async function handleUpdateContractor(req, res, id) {
  try {
    const {
      companyName,
      contactName,
      email,
      phone,
      address,
      city,
      province,
      postalCode,
      country,
      website,
      businessNumber,
      gstNumber,
      pstNumber,
      wcbNumber,
      businessType,
      yearsInBusiness,
      numberOfEmployees,
      serviceAreas,
      capabilities,
      certifications,
      insuranceCoverage,
      bondingCapacity,
      status,
      complianceStatus,
      ragOverall,
      ragFinancial,
      ragCompliance,
      ragPerformance,
      ragSafety,
      notes
    } = req.body;

    // Build dynamic update query
    const updates = [];
    const values = [];
    let valueIndex = 1;

    if (companyName !== undefined) {
      updates.push(`company_name = $${valueIndex}`);
      values.push(companyName);
      valueIndex++;
    }

    if (contactName !== undefined) {
      updates.push(`contact_name = $${valueIndex}`);
      values.push(contactName);
      valueIndex++;
    }

    if (email !== undefined) {
      updates.push(`email = $${valueIndex}`);
      values.push(email);
      valueIndex++;
    }

    if (phone !== undefined) {
      updates.push(`phone = $${valueIndex}`);
      values.push(phone);
      valueIndex++;
    }

    if (address !== undefined) {
      updates.push(`address = $${valueIndex}`);
      values.push(address);
      valueIndex++;
    }

    if (city !== undefined) {
      updates.push(`city = $${valueIndex}`);
      values.push(city);
      valueIndex++;
    }

    if (province !== undefined) {
      updates.push(`province = $${valueIndex}`);
      values.push(province);
      valueIndex++;
    }

    if (postalCode !== undefined) {
      updates.push(`postal_code = $${valueIndex}`);
      values.push(postalCode);
      valueIndex++;
    }

    if (country !== undefined) {
      updates.push(`country = $${valueIndex}`);
      values.push(country);
      valueIndex++;
    }

    if (website !== undefined) {
      updates.push(`website = $${valueIndex}`);
      values.push(website);
      valueIndex++;
    }

    if (businessNumber !== undefined) {
      updates.push(`business_number = $${valueIndex}`);
      values.push(businessNumber);
      valueIndex++;
    }

    if (gstNumber !== undefined) {
      updates.push(`gst_number = $${valueIndex}`);
      values.push(gstNumber);
      valueIndex++;
    }

    if (pstNumber !== undefined) {
      updates.push(`pst_number = $${valueIndex}`);
      values.push(pstNumber);
      valueIndex++;
    }

    if (wcbNumber !== undefined) {
      updates.push(`wcb_number = $${valueIndex}`);
      values.push(wcbNumber);
      valueIndex++;
    }

    if (businessType !== undefined) {
      updates.push(`business_type = $${valueIndex}`);
      values.push(businessType);
      valueIndex++;
    }

    if (yearsInBusiness !== undefined) {
      updates.push(`years_in_business = $${valueIndex}`);
      values.push(yearsInBusiness);
      valueIndex++;
    }

    if (numberOfEmployees !== undefined) {
      updates.push(`number_of_employees = $${valueIndex}`);
      values.push(numberOfEmployees);
      valueIndex++;
    }

    if (serviceAreas !== undefined) {
      updates.push(`service_areas = $${valueIndex}`);
      values.push(serviceAreas);
      valueIndex++;
    }

    if (capabilities !== undefined) {
      updates.push(`capabilities = $${valueIndex}`);
      values.push(capabilities);
      valueIndex++;
    }

    if (certifications !== undefined) {
      updates.push(`certifications = $${valueIndex}`);
      values.push(certifications);
      valueIndex++;
    }

    if (insuranceCoverage !== undefined) {
      updates.push(`insurance_coverage = $${valueIndex}`);
      values.push(insuranceCoverage);
      valueIndex++;
    }

    if (bondingCapacity !== undefined) {
      updates.push(`bonding_capacity = $${valueIndex}`);
      values.push(bondingCapacity);
      valueIndex++;
    }

    if (status !== undefined) {
      updates.push(`status = $${valueIndex}`);
      values.push(status);
      valueIndex++;
    }

    if (complianceStatus !== undefined) {
      updates.push(`compliance_status = $${valueIndex}`);
      values.push(complianceStatus);
      valueIndex++;
    }

    if (ragOverall !== undefined) {
      updates.push(`rag_overall = $${valueIndex}`);
      values.push(ragOverall);
      valueIndex++;
    }

    if (ragFinancial !== undefined) {
      updates.push(`rag_financial = $${valueIndex}`);
      values.push(ragFinancial);
      valueIndex++;
    }

    if (ragCompliance !== undefined) {
      updates.push(`rag_compliance = $${valueIndex}`);
      values.push(ragCompliance);
      valueIndex++;
    }

    if (ragPerformance !== undefined) {
      updates.push(`rag_performance = $${valueIndex}`);
      values.push(ragPerformance);
      valueIndex++;
    }

    if (ragSafety !== undefined) {
      updates.push(`rag_safety = $${valueIndex}`);
      values.push(ragSafety);
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
      UPDATE contractors 
      SET ${updates.join(', ')}
      WHERE id = $${valueIndex}
      RETURNING *
    `;

    const result = await sql(query, values);

    if (result.length === 0) {
      return res.status(404).json({ success: false, error: 'Contractor not found' });
    }

    return res.status(200).json({ 
      success: true, 
      data: result[0] 
    });
  } catch (error) {
    console.error('Error updating contractor:', error);
    
    // Check for unique constraint violation
    if (error.code === '23505') {
      return res.status(409).json({ 
        success: false, 
        error: 'A contractor with this email already exists' 
      });
    }
    
    return res.status(500).json({ success: false, error: error.message });
  }
}

async function handleDeleteContractor(req, res, id) {
  try {
    const { hard = false } = req.query;

    if (hard === 'true') {
      // Hard delete - permanently remove from database
      const result = await sql`
        DELETE FROM contractors 
        WHERE id = ${id}
        RETURNING id
      `;

      if (result.length === 0) {
        return res.status(404).json({ success: false, error: 'Contractor not found' });
      }

      return res.status(200).json({ 
        success: true, 
        message: 'Contractor permanently deleted' 
      });
    } else {
      // Soft delete - mark as inactive
      const result = await sql`
        UPDATE contractors 
        SET is_active = false, updated_at = NOW()
        WHERE id = ${id}
        RETURNING id
      `;

      if (result.length === 0) {
        return res.status(404).json({ success: false, error: 'Contractor not found' });
      }

      return res.status(200).json({ 
        success: true, 
        message: 'Contractor deactivated' 
      });
    }
  } catch (error) {
    console.error('Error deleting contractor:', error);
    return res.status(500).json({ success: false, error: error.message });
  }
}