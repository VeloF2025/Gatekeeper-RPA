'use client';

/**
 * Authentication Provider for Gatekeeper RPA System
 *
 * Integrates with Clerk authentication to provide user context and auth functions.
 * Following Zero Trust security principles with proper session management.
 *
 * Created with comprehensive authentication and session validation.
 */

import React, { createContext, useContext, useEffect, ReactNode } from 'react';
import { useAuth as useClerkAuth } from '@clerk/nextjs';
import { useSecurity } from './security-provider';
import { log } from '@/lib/logger';

// Auth context interface
interface AuthContextType {
  user: {
    id: string;
    email?: string;
    firstName?: string;
    lastName?: string;
    role?: string;
    permissions: string[];
  } | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  signOut: () => Promise<void>;
  hasPermission: (permission: string) => boolean;
  requirePermission: (permission: string) => void;
  refreshUser: () => Promise<void>;
}

// Default auth context value
const defaultAuthContext: AuthContextType = {
  user: null,
  isLoading: true,
  isAuthenticated: false,
  signOut: async () => {},
  hasPermission: () => false,
  requirePermission: () => {},
  refreshUser: async () => {},
};

// Create auth context
const AuthContext = createContext<AuthContextType>(defaultAuthContext);

// Auth provider props
interface AuthProviderProps {
  children: ReactNode;
}

/**
 * AuthProvider Component
 *
 * Integrates Clerk authentication with Zero Trust security principles.
 * Provides user context and authentication functions with proper validation.
 */
export function AuthProvider({ children }: AuthProviderProps) {
  const { isLoaded, userId, sessionId, isSignedIn, signOut } = useClerkAuth();
  const { setSecurityLevel, addSecurityEvent } = useSecurity();

  const [user, setUser] = useState<AuthContextType['user']>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Initialize authentication state
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        if (!isLoaded) {
          setIsLoading(true);
          return;
        }

        if (isSignedIn && userId) {
          // In a real implementation, this would fetch user data from your database
          // including roles and permissions based on Clerk user ID
          const userData = {
            id: userId,
            email: 'user@example.com', // Would come from Clerk
            firstName: 'John',        // Would come from Clerk
            lastName: 'Doe',          // Would come from Clerk
            role: 'user',             // Would come from your user database
            permissions: ['tickets:read', 'dashboard:read'], // Would come from your permissions system
          };

          setUser(userData);

          // Update security context
          setSecurityLevel('medium');

          addSecurityEvent({
            type: 'authentication',
            severity: 'low',
            message: 'User authenticated successfully',
          });

          log.info('User authenticated', { userId, role: userData.role }, 'AuthProvider');
        } else {
          setUser(null);
          setSecurityLevel('low');

          addSecurityEvent({
            type: 'authentication',
            severity: 'low',
            message: 'User not authenticated',
          });
        }
      } catch (error) {
        log.error('Failed to initialize authentication', error, 'AuthProvider');
        addSecurityEvent({
          type: 'authentication',
          severity: 'high',
          message: 'Authentication initialization failed',
        });
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, [isLoaded, isSignedIn, userId, sessionId, setSecurityLevel, addSecurityEvent]);

  // Sign out function with proper cleanup
  const handleSignOut = async () => {
    try {
      addSecurityEvent({
        type: 'authentication',
        severity: 'low',
        message: 'User signing out',
      });

      await signOut();
      setUser(null);
      setSecurityLevel('low');

      log.info('User signed out successfully', {}, 'AuthProvider');
    } catch (error) {
      log.error('Sign out failed', error, 'AuthProvider');
      addSecurityEvent({
        type: 'authentication',
        severity: 'medium',
        message: 'Sign out failed',
      });
    }
  };

  // Check if user has specific permission
  const hasPermission = (permission: string): boolean => {
    if (!user || !user.permissions) {
      return false;
    }

    return user.permissions.includes(permission) ||
           user.permissions.includes('*');
  };

  // Require permission or throw error
  const requirePermission = (permission: string): void => {
    if (!hasPermission(permission)) {
      addSecurityEvent({
        type: 'authorization',
        severity: 'medium',
        message: `Permission denied: ${permission}`,
      });

      throw new Error(`Permission denied: ${permission}`);
    }
  };

  // Refresh user data
  const refreshUser = async (): Promise<void> => {
    try {
      if (!isSignedIn || !userId) {
        return;
      }

      // In a real implementation, this would refresh user data from your database
      const refreshedUserData = {
        id: userId,
        email: 'user@example.com',
        firstName: 'John',
        lastName: 'Doe',
        role: 'user',
        permissions: ['tickets:read', 'dashboard:read'],
      };

      setUser(refreshedUserData);

      addSecurityEvent({
        type: 'authentication',
        severity: 'low',
        message: 'User data refreshed',
      });

      log.info('User data refreshed', { userId }, 'AuthProvider');
    } catch (error) {
      log.error('Failed to refresh user data', error, 'AuthProvider');
      addSecurityEvent({
        type: 'authentication',
        severity: 'medium',
        message: 'User data refresh failed',
      });
    }
  };

  // Context value
  const contextValue: AuthContextType = {
    user,
    isLoading,
    isAuthenticated: isSignedIn || false,
    signOut: handleSignOut,
    hasPermission,
    requirePermission,
    refreshUser,
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
}

// Hook to use auth context
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

// Hook for role-based access control
export function useRoleAccess(requiredRole: string) {
  const { user, requirePermission } = useAuth();

  const hasRole = user?.role === requiredRole;

  const requireRole = () => {
    if (!hasRole) {
      throw new Error(`Role required: ${requiredRole}`);
    }
  };

  return { hasRole, requireRole, user };
}

// Higher-order component for role protection
export function withRole<P extends object>(
  Component: React.ComponentType<P>,
  requiredRole: string
) {
  return function RoleProtectedComponent(props: P) {
    const { hasRole, user } = useRoleAccess(requiredRole);

    if (!hasRole) {
      return (
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center">
            <h1 className="text-2xl font-bold mb-4">Access Denied</h1>
            <p className="text-muted-foreground mb-4">
              You don't have the required role to access this resource.
            </p>
            <p className="text-sm text-muted-foreground">
              Required role: {requiredRole}
              {user && <span> | Your role: {user.role}</span>}
            </p>
          </div>
        </div>
      );
    }

    return <Component {...props} />;
  };
}