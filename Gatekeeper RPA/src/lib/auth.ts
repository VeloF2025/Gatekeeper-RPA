import { authMiddleware } from '@/middleware/auth';
import type { NextAuthOptions } from 'next-auth';

/**
 * Auth options for NextAuth compatibility layer
 * This provides a compatibility layer for APIs expecting NextAuth-style authentication
 */
export const authOptions: NextAuthOptions = {
  // Empty configuration - we're using our custom JWT middleware
  providers: [],
  session: {
    strategy: 'jwt',
  },
  callbacks: {
    async jwt({ token }) {
      return token;
    },
    async session({ session }) {
      return session;
    },
  },
};

/**
 * Helper function to get server session using our custom auth
 */
export async function getServerSession() {
  // This is a compatibility function
  // In a real implementation, this would extract session info from JWT token
  return null;
}

/**
 * Helper function to get current user from request
 */
export async function getCurrentUser(request: Request) {
  try {
    const authResult = await authMiddleware(request as any);

    if (authResult instanceof Response) {
      return null;
    }

    return {
      id: authResult.userId,
      email: authResult.email,
      role: authResult.role,
      permissions: authResult.permissions
    };
  } catch {
    return null;
  }
}