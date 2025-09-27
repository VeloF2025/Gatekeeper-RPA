/**
 * Tickets Page - Gatekeeper RPA System
 *
 * Ticket management interface showing all active and processed tickets.
 * Provides filtering, sorting, and detailed ticket management capabilities.
 *
 * Created with comprehensive ticket management and user experience features.
 */

import { Suspense } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';
import Link from 'next/link';

// Loading component
function TicketsLoading() {
  return (
    <div className="space-y-6">
      <div className="grid gap-4">
        {[...Array(5)].map((_, i) => (
          <Card key={i}>
            <CardHeader>
              <Skeleton className="h-4 w-1/2" />
              <Skeleton className="h-3 w-1/3" />
            </CardHeader>
            <CardContent>
              <Skeleton className="h-16" />
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}

// Mock ticket data
const mockTickets = [
  {
    id: 'TKT-001',
    title: 'Fiber Installation - 123 Main St',
    status: 'pending',
    priority: 'high',
    created: '2024-01-15T10:30:00Z',
    assignedTo: 'Tech Team A',
    description: 'Complete fiber installation with photo verification'
  },
  {
    id: 'TKT-002',
    title: 'Network Configuration - 456 Oak Ave',
    status: 'in_progress',
    priority: 'medium',
    created: '2024-01-15T09:15:00Z',
    assignedTo: 'Tech Team B',
    description: 'Configure network settings and test connectivity'
  },
  {
    id: 'TKT-003',
    title: 'Quality Audit - 789 Pine St',
    status: 'completed',
    priority: 'low',
    created: '2024-01-14T14:20:00Z',
    assignedTo: 'Tech Team C',
    description: 'Final quality inspection and documentation'
  }
];

// Status badge component
function StatusBadge({ status }: { status: string }) {
  const variants = {
    pending: 'secondary',
    in_progress: 'default',
    completed: 'outline',
    failed: 'destructive'
  } as const;

  return (
    <Badge variant={variants[status as keyof typeof variants] || 'secondary'}>
      {status.replace('_', ' ').toUpperCase()}
    </Badge>
  );
}

// Priority badge component
function PriorityBadge({ priority }: { priority: string }) {
  const variants = {
    low: 'secondary',
    medium: 'default',
    high: 'destructive'
  } as const;

  return (
    <Badge variant={variants[priority as keyof typeof variants] || 'secondary'}>
      {priority.toUpperCase()}
    </Badge>
  );
}

export default function TicketsPage() {
  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">Tickets</h1>
            <p className="text-muted-foreground">
              Manage and monitor all active tickets
            </p>
          </div>
          <div className="flex gap-2">
            <Button variant="outline" asChild>
              <Link href="/dashboard">Dashboard</Link>
            </Button>
            <Button>Create New Ticket</Button>
          </div>
        </div>
      </div>

      {/* Stats overview */}
      <div className="grid gap-4 md:grid-cols-4 mb-8">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Total Tickets</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{mockTickets.length}</div>
            <p className="text-xs text-muted-foreground">All time</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Pending</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {mockTickets.filter(t => t.status === 'pending').length}
            </div>
            <p className="text-xs text-muted-foreground">Awaiting action</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">In Progress</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {mockTickets.filter(t => t.status === 'in_progress').length}
            </div>
            <p className="text-xs text-muted-foreground">Active work</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Completed</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {mockTickets.filter(t => t.status === 'completed').length}
            </div>
            <p className="text-xs text-muted-foreground">Finished</p>
          </CardContent>
        </Card>
      </div>

      {/* Filter controls */}
      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Filter & Search</CardTitle>
          <CardDescription>
            Filter tickets by status, priority, or search for specific tickets
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex gap-4">
            <div className="flex-1">
              <input
                type="text"
                placeholder="Search tickets..."
                className="w-full px-3 py-2 border border-input rounded-md"
              />
            </div>
            <select className="px-3 py-2 border border-input rounded-md">
              <option value="">All Status</option>
              <option value="pending">Pending</option>
              <option value="in_progress">In Progress</option>
              <option value="completed">Completed</option>
            </select>
            <select className="px-3 py-2 border border-input rounded-md">
              <option value="">All Priority</option>
              <option value="low">Low</option>
              <option value="medium">Medium</option>
              <option value="high">High</option>
            </select>
          </div>
        </CardContent>
      </Card>

      {/* Tickets list */}
      <Suspense fallback={<TicketsLoading />}>
        <div className="space-y-4">
          {mockTickets.map((ticket) => (
            <Card key={ticket.id} className="cursor-pointer hover:shadow-md transition-shadow">
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4">
                    <CardTitle className="text-lg">{ticket.title}</CardTitle>
                    <StatusBadge status={ticket.status} />
                    <PriorityBadge priority={ticket.priority} />
                  </div>
                  <div className="text-sm text-muted-foreground">
                    {ticket.id}
                  </div>
                </div>
                <CardDescription>
                  {ticket.description}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between">
                  <div className="text-sm text-muted-foreground">
                    <div>Assigned to: {ticket.assignedTo}</div>
                    <div>Created: {new Date(ticket.created).toLocaleDateString()}</div>
                  </div>
                  <Button variant="outline" size="sm">
                    View Details
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </Suspense>

      {/* Empty state */}
      {mockTickets.length === 0 && (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <div className="text-6xl mb-4">📋</div>
            <h3 className="text-lg font-semibold mb-2">No tickets found</h3>
            <p className="text-muted-foreground mb-4">
              Create your first ticket to get started
            </p>
            <Button>Create Ticket</Button>
          </CardContent>
        </Card>
      )}
    </div>
  );
}