/**
 * Data Visualization Charts Component for Dashboard
 *
 * Modern enterprise-grade charts using Recharts for:
 * - Ticket trends over time
 * - Processing rates and performance metrics
 * - System health monitoring
 * - Real-time data updates with animations
 *
 * Created with comprehensive accessibility and responsive design.
 */

import * as React from 'react';
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { Badge } from '@/components/ui/badge';

// Data types
interface ChartDataPoint {
  name: string;
  timestamp?: string;
  [key: string]: any;
}

interface TicketTrendData extends ChartDataPoint {
  tickets: number;
  processed: number;
  failed: number;
}

interface ProcessingData extends ChartDataPoint {
  rate: number;
  efficiency: number;
}

interface StatusData extends ChartDataPoint {
  name: string;
  value: number;
  color: string;
}

// Chart props interfaces
interface TicketTrendChartProps {
  data: TicketTrendData[];
  loading?: boolean;
  height?: number;
}

interface ProcessingRateChartProps {
  data: ProcessingData[];
  loading?: boolean;
  height?: number;
}

interface StatusDistributionChartProps {
  data: StatusData[];
  loading?: boolean;
  height?: number;
}

// Custom tooltip component
const CustomTooltip: React.FC<any> = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-background border border-border rounded-lg p-3 shadow-lg">
        <p className="font-semibold text-sm">{label}</p>
        {payload.map((entry: any, index: number) => (
          <p key={index} className="text-xs" style={{ color: entry.color }}>
            {entry.name}: {entry.value}
          </p>
        ))}
      </div>
    );
  }
  return null;
};

// Loading skeleton for charts
function ChartSkeleton({ height = 300 }: { height?: number }) {
  return (
    <div className="w-full" style={{ height }}>
      <Skeleton className="w-full h-full" />
    </div>
  );
}

/**
 * Ticket Trend Chart Component
 *
 * Shows ticket processing trends over time with multiple metrics
 */
export const TicketTrendChart: React.FC<TicketTrendChartProps> = ({
  data,
  loading = false,
  height = 300
}) => {
  if (loading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <span>Ticket Trends</span>
            <Badge variant="outline">Last 7 days</Badge>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <ChartSkeleton height={height} />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <span>Ticket Processing Trends</span>
          <Badge variant="outline">Last 7 days</Badge>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={height}>
          <AreaChart data={data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
            <XAxis
              dataKey="name"
              className="text-xs"
              tick={{ fontSize: 12 }}
            />
            <YAxis className="text-xs" tick={{ fontSize: 12 }} />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            <Area
              type="monotone"
              dataKey="tickets"
              stackId="1"
              stroke="#8884d8"
              fill="#8884d8"
              fillOpacity={0.3}
              strokeWidth={2}
            />
            <Area
              type="monotone"
              dataKey="processed"
              stackId="2"
              stroke="#82ca9d"
              fill="#82ca9d"
              fillOpacity={0.3}
              strokeWidth={2}
            />
            <Area
              type="monotone"
              dataKey="failed"
              stackId="3"
              stroke="#ffc658"
              fill="#ffc658"
              fillOpacity={0.3}
              strokeWidth={2}
            />
          </AreaChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
};

/**
 * Processing Rate Chart Component
 *
 * Shows system processing efficiency and rates over time
 */
export const ProcessingRateChart: React.FC<ProcessingRateChartProps> = ({
  data,
  loading = false,
  height = 300
}) => {
  if (loading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <span>Processing Rate</span>
            <Badge variant="outline">Performance</Badge>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <ChartSkeleton height={height} />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <span>Processing Rate & Efficiency</span>
          <Badge variant="outline">Performance</Badge>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={height}>
          <LineChart data={data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
            <XAxis
              dataKey="name"
              className="text-xs"
              tick={{ fontSize: 12 }}
            />
            <YAxis className="text-xs" tick={{ fontSize: 12 }} />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            <Line
              type="monotone"
              dataKey="rate"
              stroke="#8884d8"
              strokeWidth={3}
              dot={{ fill: '#8884d8', strokeWidth: 2, r: 4 }}
              activeDot={{ r: 6 }}
            />
            <Line
              type="monotone"
              dataKey="efficiency"
              stroke="#82ca9d"
              strokeWidth={3}
              dot={{ fill: '#82ca9d', strokeWidth: 2, r: 4 }}
              activeDot={{ r: 6 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
};

/**
 * Status Distribution Chart Component
 *
 * Shows current status distribution across different categories
 */
export const StatusDistributionChart: React.FC<StatusDistributionChartProps> = ({
  data,
  loading = false,
  height = 300
}) => {
  if (loading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <span>Status Distribution</span>
            <Badge variant="outline">Current</Badge>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <ChartSkeleton height={height} />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <span>Ticket Status Distribution</span>
          <Badge variant="outline">Current</Badge>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={height}>
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              innerRadius={60}
              outerRadius={100}
              paddingAngle={5}
              dataKey="value"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip content={<CustomTooltip />} />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
};

// Sample data generators for development/demo
export const generateSampleTicketTrendData = (): TicketTrendData[] => [
  { name: 'Mon', tickets: 45, processed: 42, failed: 3 },
  { name: 'Tue', tickets: 52, processed: 48, failed: 4 },
  { name: 'Wed', tickets: 48, processed: 45, failed: 3 },
  { name: 'Thu', tickets: 55, processed: 52, failed: 3 },
  { name: 'Fri', tickets: 62, processed: 58, failed: 4 },
  { name: 'Sat', tickets: 38, processed: 36, failed: 2 },
  { name: 'Sun', tickets: 41, processed: 39, failed: 2 },
];

export const generateSampleProcessingData = (): ProcessingData[] => [
  { name: '00:00', rate: 12, efficiency: 85 },
  { name: '04:00', rate: 8, efficiency: 78 },
  { name: '08:00', rate: 25, efficiency: 92 },
  { name: '12:00', rate: 32, efficiency: 88 },
  { name: '16:00', rate: 28, efficiency: 90 },
  { name: '20:00', rate: 18, efficiency: 86 },
];

export const generateSampleStatusData = (): StatusData[] => [
  { name: 'Processing', value: 35, color: '#8884d8' },
  { name: 'Completed', value: 45, color: '#82ca9d' },
  { name: 'Pending', value: 15, color: '#ffc658' },
  { name: 'Failed', value: 5, color: '#ff7300' },
];

// Combined charts component for dashboard
export const DashboardCharts: React.FC<{ loading?: boolean }> = ({ loading = false }) => {
  const ticketData = generateSampleTicketTrendData();
  const processingData = generateSampleProcessingData();
  const statusData = generateSampleStatusData();

  return (
    <div className="grid gap-6 md:grid-cols-2">
      <TicketTrendChart data={ticketData} loading={loading} />
      <div className="space-y-6">
        <ProcessingRateChart data={processingData} loading={loading} height={200} />
        <StatusDistributionChart data={statusData} loading={loading} height={200} />
      </div>
    </div>
  );
};