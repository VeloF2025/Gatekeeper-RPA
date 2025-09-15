import type { NextApiRequest, NextApiResponse } from 'next';
import { getAuth } from '@clerk/nextjs/server';

/**
 * Project API Route
 * GET /api/projects/[id] - Get a single project
 * PUT /api/projects/[id] - Update a project
 * DELETE /api/projects/[id] - Delete a project
 */
export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse
) {
  const { id } = req.query;

  try {
    // Get authentication from Clerk
    const { userId } = getAuth(req);
    
    if (!userId) {
      return res.status(401).json({ error: 'Unauthorized' });
    }

    if (!id || typeof id !== 'string') {
      return res.status(400).json({ error: 'Project ID is required' });
    }

    const backendUrl = process.env.BACKEND_API_URL || 'http://localhost:3001';

    switch (req.method) {
      case 'GET': {
        const response = await fetch(`${backendUrl}/api/projects/${id}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'X-User-Id': userId,
          },
        });

        if (!response.ok) {
          const error = await response.text();
          return res.status(response.status).json({ 
            success: false,
            error: error || `Failed to fetch project: ${response.status}` 
          });
        }

        const result = await response.json();
        return res.status(200).json(result);
      }

      case 'PUT': {
        const response = await fetch(`${backendUrl}/api/projects/${id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'X-User-Id': userId,
          },
          body: JSON.stringify({
            ...req.body,
            updatedBy: userId,
          }),
        });

        if (!response.ok) {
          const error = await response.text();
          return res.status(response.status).json({ 
            success: false,
            error: error || `Failed to update project: ${response.status}` 
          });
        }

        const result = await response.json();
        return res.status(200).json(result);
      }

      case 'DELETE': {
        const response = await fetch(`${backendUrl}/api/projects/${id}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'X-User-Id': userId,
          },
        });

        if (!response.ok) {
          const error = await response.text();
          return res.status(response.status).json({ 
            success: false,
            error: error || `Failed to delete project: ${response.status}` 
          });
        }

        const result = await response.json();
        return res.status(200).json(result);
      }

      default:
        return res.status(405).json({ error: 'Method not allowed' });
    }
  } catch (error) {
    console.error('Project API error:', error);
    return res.status(500).json({ 
      success: false,
      error: error instanceof Error ? error.message : 'Internal server error' 
    });
  }
}