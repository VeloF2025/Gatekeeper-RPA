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

  // Get projectId from query params (Vercel dynamic route)
  const { projectId } = req.query;

  if (!projectId) {
    return res.status(400).json({ 
      success: false, 
      error: 'Project ID is required' 
    });
  }

  try {
    switch (req.method) {
      case 'GET':
        // Check if table exists first
        const tableCheck = await sql`
          SELECT EXISTS (
            SELECT FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = 'sow_import_status'
          )
        `;
        
        if (!tableCheck[0].exists) {
          // Table doesn't exist, return default status
          return res.status(200).json({
            success: true,
            data: []
          });
        }
        
        // Get import status for a project
        const status = await sql`
          SELECT * FROM sow_import_status
          WHERE project_id = ${projectId}::uuid
          ORDER BY started_at DESC
          LIMIT 1
        `;
        
        res.status(200).json({ 
          success: true, 
          data: status
        });
        break;

      case 'POST':
        // Create or update import status
        const { 
          step_type, 
          status: importStatus, 
          records_imported, 
          file_name, 
          error_message,
          metadata 
        } = req.body;
        
        // First check if table exists and create if needed
        await sql`
          CREATE TABLE IF NOT EXISTS sow_import_status (
            id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
            project_id UUID NOT NULL,
            step_type VARCHAR(50),
            status VARCHAR(50),
            records_imported INTEGER DEFAULT 0,
            file_name TEXT,
            error_message TEXT,
            started_at TIMESTAMP DEFAULT NOW(),
            completed_at TIMESTAMP,
            metadata JSONB,
            created_at TIMESTAMP DEFAULT NOW()
          )
        `;
        
        // Insert new status record
        const newStatus = await sql`
          INSERT INTO sow_import_status (
            project_id, 
            step_type,
            status, 
            records_imported,
            file_name,
            error_message,
            started_at,
            completed_at,
            metadata
          )
          VALUES (
            ${projectId}::uuid,
            ${step_type || 'upload'},
            ${importStatus || 'in_progress'},
            ${records_imported || 0},
            ${file_name || ''},
            ${error_message || null},
            NOW(),
            ${importStatus === 'completed' ? sql`NOW()` : null},
            ${metadata || {}}::jsonb
          )
          RETURNING *
        `;
        
        res.status(201).json({ 
          success: true, 
          data: newStatus[0] 
        });
        break;

      default:
        res.status(405).json({ 
          success: false, 
          error: 'Method not allowed' 
        });
    }
  } catch (error) {
    console.error('Import status API error:', error);
    res.status(500).json({ 
      success: false, 
      error: error.message 
    });
  }
}