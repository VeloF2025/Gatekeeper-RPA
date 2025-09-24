/**
 * Role-Based Access Control (RBAC) Implementation
 * Comprehensive permission management with hierarchical roles
 */

export interface Permission {
  id: string;
  name: string;
  description: string;
  resource: string;
  action: string;
  conditions?: PermissionCondition[];
}

export interface PermissionCondition {
  field: string;
  operator: 'equals' | 'contains' | 'startsWith' | 'endsWith' | 'in' | 'notIn';
  value: any;
}

export interface Role {
  id: string;
  name: string;
  description: string;
  permissions: string[];
  inherits?: string[]; // Role IDs to inherit permissions from
  isSystemRole?: boolean;
}

export interface User {
  id: string;
  email: string;
  roles: string[];
  directPermissions: string[];
  active: boolean;
  organizationId?: string;
  departmentId?: string;
}

export interface Resource {
  id: string;
  name: string;
  type: string;
  owner?: string;
  organizationId?: string;
  departmentId?: string;
  metadata?: Record<string, any>;
}

class RBACManager {
  private roles: Map<string, Role> = new Map();
  private permissions: Map<string, Permission> = new Map();
  private users: Map<string, User> = new Map();
  private roleHierarchy: Map<string, Set<string>> = new Map();

  constructor() {
    this.initializeDefaultRoles();
    this.initializeDefaultPermissions();
    this.buildRoleHierarchy();
  }

  /**
   * Initialize default system roles
   */
  private initializeDefaultRoles(): void {
    const defaultRoles: Role[] = [
      {
        id: 'super_admin',
        name: 'Super Administrator',
        description: 'Full system access with all permissions',
        permissions: ['*'],
        isSystemRole: true,
      },
      {
        id: 'admin',
        name: 'Administrator',
        description: 'Administrative access with most permissions',
        permissions: [
          'users.read', 'users.create', 'users.update', 'users.delete',
          'roles.read', 'roles.create', 'roles.update',
          'audits.read', 'audits.create', 'audits.update',
          'reports.read', 'reports.create',
          'settings.read', 'settings.update',
        ],
        inherits: ['auditor'],
      },
      {
        id: 'auditor',
        name: 'Auditor',
        description: 'Can perform audits and view reports',
        permissions: [
          'audits.read', 'audits.create', 'audits.update',
          'reports.read',
          'tickets.read',
          'whatsapp.read',
        ],
        inherits: ['analyst'],
      },
      {
        id: 'analyst',
        name: 'Analyst',
        description: 'Can analyze data and view reports',
        permissions: [
          'reports.read',
          'tickets.read',
          'whatsapp.read',
        ],
      },
      {
        id: 'manager',
        name: 'Manager',
        description: 'Department manager with team management',
        permissions: [
          'team.read', 'team.create', 'team.update',
          'tickets.read', 'tickets.create', 'tickets.update',
          'reports.read',
          'audits.read',
        ],
        inherits: ['user'],
      },
      {
        id: 'user',
        name: 'User',
        description: 'Basic user with limited permissions',
        permissions: [
          'profile.read', 'profile.update',
          'tickets.read', 'tickets.create',
          'whatsapp.read',
        ],
      },
      {
        id: 'guest',
        name: 'Guest',
        description: 'Read-only access for external users',
        permissions: [
          'public.read',
        ],
      },
    ];

    defaultRoles.forEach(role => {
      this.roles.set(role.id, role);
    });
  }

  /**
   * Initialize default permissions
   */
  private initializeDefaultPermissions(): void {
    const defaultPermissions: Permission[] = [
      // User Management
      { id: 'users.read', name: 'Read Users', description: 'View user information', resource: 'users', action: 'read' },
      { id: 'users.create', name: 'Create Users', description: 'Create new users', resource: 'users', action: 'create' },
      { id: 'users.update', name: 'Update Users', description: 'Update user information', resource: 'users', action: 'update' },
      { id: 'users.delete', name: 'Delete Users', description: 'Delete users', resource: 'users', action: 'delete' },

      // Role Management
      { id: 'roles.read', name: 'Read Roles', description: 'View role information', resource: 'roles', action: 'read' },
      { id: 'roles.create', name: 'Create Roles', description: 'Create new roles', resource: 'roles', action: 'create' },
      { id: 'roles.update', name: 'Update Roles', description: 'Update role information', resource: 'roles', action: 'update' },
      { id: 'roles.delete', name: 'Delete Roles', description: 'Delete roles', resource: 'roles', action: 'delete' },

      // Audit Management
      { id: 'audits.read', name: 'Read Audits', description: 'View audit information', resource: 'audits', action: 'read' },
      { id: 'audits.create', name: 'Create Audits', description: 'Create new audits', resource: 'audits', action: 'create' },
      { id: 'audits.update', name: 'Update Audits', description: 'Update audit information', resource: 'audits', action: 'update' },
      { id: 'audits.delete', name: 'Delete Audits', description: 'Delete audits', resource: 'audits', action: 'delete' },

      // Ticket Management
      { id: 'tickets.read', name: 'Read Tickets', description: 'View ticket information', resource: 'tickets', action: 'read' },
      { id: 'tickets.create', name: 'Create Tickets', description: 'Create new tickets', resource: 'tickets', action: 'create' },
      { id: 'tickets.update', name: 'Update Tickets', description: 'Update ticket information', resource: 'tickets', action: 'update' },
      { id: 'tickets.delete', name: 'Delete Tickets', description: 'Delete tickets', resource: 'tickets', action: 'delete' },

      // WhatsApp Integration
      { id: 'whatsapp.read', name: 'Read WhatsApp', description: 'View WhatsApp messages', resource: 'whatsapp', action: 'read' },
      { id: 'whatsapp.send', name: 'Send WhatsApp', description: 'Send WhatsApp messages', resource: 'whatsapp', action: 'send' },
      { id: 'whatsapp.manage', name: 'Manage WhatsApp', description: 'Manage WhatsApp settings', resource: 'whatsapp', action: 'manage' },

      // Report Management
      { id: 'reports.read', name: 'Read Reports', description: 'View reports', resource: 'reports', action: 'read' },
      { id: 'reports.create', name: 'Create Reports', description: 'Create new reports', resource: 'reports', action: 'create' },
      { id: 'reports.update', name: 'Update Reports', description: 'Update reports', resource: 'reports', action: 'update' },
      { id: 'reports.delete', name: 'Delete Reports', description: 'Delete reports', resource: 'reports', action: 'delete' },

      // Team Management
      { id: 'team.read', name: 'Read Team', description: 'View team information', resource: 'team', action: 'read' },
      { id: 'team.create', name: 'Create Team', description: 'Create new team members', resource: 'team', action: 'create' },
      { id: 'team.update', name: 'Update Team', description: 'Update team information', resource: 'team', action: 'update' },

      // Settings Management
      { id: 'settings.read', name: 'Read Settings', description: 'View system settings', resource: 'settings', action: 'read' },
      { id: 'settings.update', name: 'Update Settings', description: 'Update system settings', resource: 'settings', action: 'update' },

      // Profile Management
      { id: 'profile.read', name: 'Read Profile', description: 'View user profile', resource: 'profile', action: 'read' },
      { id: 'profile.update', name: 'Update Profile', description: 'Update user profile', resource: 'profile', action: 'update' },

      // Public Access
      { id: 'public.read', name: 'Public Read', description: 'Read public information', resource: 'public', action: 'read' },
    ];

    defaultPermissions.forEach(permission => {
      this.permissions.set(permission.id, permission);
    });
  }

  /**
   * Build role hierarchy for inheritance
   */
  private buildRoleHierarchy(): void {
    for (const role of this.roles.values()) {
      this.roleHierarchy.set(role.id, new Set());
    }

    for (const role of this.roles.values()) {
      if (role.inherits) {
        const parentRoles = this.roleHierarchy.get(role.id);
        if (parentRoles) {
          role.inherits.forEach(inheritedRoleId => {
            parentRoles.add(inheritedRoleId);
          });
        }
      }
    }
  }

  /**
   * Check if user has specific permission
   */
  hasPermission(userId: string, permissionId: string, resource?: Resource): boolean {
    const user = this.users.get(userId);
    if (!user || !user.active) {
      return false;
    }

    // Super admin has all permissions
    if (user.roles.includes('super_admin')) {
      return true;
    }

    // Check direct permissions
    if (user.directPermissions.includes('*') || user.directPermissions.includes(permissionId)) {
      return this.checkPermissionConditions(permissionId, resource);
    }

    // Check role permissions
    for (const roleId of user.roles) {
      if (this.roleHasPermission(roleId, permissionId, resource)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Check if role has specific permission
   */
  roleHasPermission(roleId: string, permissionId: string, resource?: Resource): boolean {
    const role = this.roles.get(roleId);
    if (!role) {
      return false;
    }

    // Check direct permissions
    if (role.permissions.includes('*') || role.permissions.includes(permissionId)) {
      return this.checkPermissionConditions(permissionId, resource);
    }

    // Check inherited permissions
    const parentRoles = this.roleHierarchy.get(roleId);
    if (parentRoles) {
      for (const parentRoleId of parentRoles) {
        if (this.roleHasPermission(parentRoleId, permissionId, resource)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Check permission conditions
   */
  private checkPermissionConditions(permissionId: string, resource?: Resource): boolean {
    const permission = this.permissions.get(permissionId);
    if (!permission || !permission.conditions || !resource) {
      return true;
    }

    for (const condition of permission.conditions) {
      if (!this.evaluateCondition(condition, resource)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Evaluate permission condition
   */
  private evaluateCondition(condition: PermissionCondition, resource: Resource): boolean {
    const fieldValue = this.getNestedValue(resource, condition.field);

    switch (condition.operator) {
      case 'equals':
        return fieldValue === condition.value;
      case 'contains':
        return typeof fieldValue === 'string' && fieldValue.includes(condition.value);
      case 'startsWith':
        return typeof fieldValue === 'string' && fieldValue.startsWith(condition.value);
      case 'endsWith':
        return typeof fieldValue === 'string' && fieldValue.endsWith(condition.value);
      case 'in':
        return Array.isArray(condition.value) && condition.value.includes(fieldValue);
      case 'notIn':
        return Array.isArray(condition.value) && !condition.value.includes(fieldValue);
      default:
        return false;
    }
  }

  /**
   * Get nested value from object
   */
  private getNestedValue(obj: any, path: string): any {
    return path.split('.').reduce((current, key) => {
      return current && current[key];
    }, obj);
  }

  /**
   * Get all user permissions
   */
  getUserPermissions(userId: string): string[] {
    const user = this.users.get(userId);
    if (!user || !user.active) {
      return [];
    }

    const permissions = new Set<string>();

    // Add direct permissions
    user.directPermissions.forEach(perm => permissions.add(perm));

    // Add role permissions
    for (const roleId of user.roles) {
      this.getRolePermissions(roleId).forEach(perm => permissions.add(perm));
    }

    return Array.from(permissions);
  }

  /**
   * Get all role permissions including inherited
   */
  getRolePermissions(roleId: string): string[] {
    const role = this.roles.get(roleId);
    if (!role) {
      return [];
    }

    const permissions = new Set<string>(role.permissions);

    // Add inherited permissions
    const parentRoles = this.roleHierarchy.get(roleId);
    if (parentRoles) {
      for (const parentRoleId of parentRoles) {
        this.getRolePermissions(parentRoleId).forEach(perm => permissions.add(perm));
      }
    }

    return Array.from(permissions);
  }

  /**
   * Create new role
   */
  createRole(role: Omit<Role, 'id'>): Role {
    const newRole: Role = {
      ...role,
      id: this.generateRoleId(),
    };

    this.roles.set(newRole.id, newRole);
    this.roleHierarchy.set(newRole.id, new Set());

    if (newRole.inherits) {
      const parentRoles = this.roleHierarchy.get(newRole.id);
      if (parentRoles) {
        newRole.inherits.forEach(inheritedRoleId => {
          parentRoles.add(inheritedRoleId);
        });
      }
    }

    return newRole;
  }

  /**
   * Update role
   */
  updateRole(roleId: string, updates: Partial<Role>): Role | null {
    const role = this.roles.get(roleId);
    if (!role) {
      return null;
    }

    const updatedRole = { ...role, ...updates };
    this.roles.set(roleId, updatedRole);

    // Update hierarchy if inherits changed
    if (updates.inherits) {
      const parentRoles = this.roleHierarchy.get(roleId);
      if (parentRoles) {
        parentRoles.clear();
        updates.inherits.forEach(inheritedRoleId => {
          parentRoles.add(inheritedRoleId);
        });
      }
    }

    return updatedRole;
  }

  /**
   * Delete role
   */
  deleteRole(roleId: string): boolean {
    if (!this.roles.has(roleId)) {
      return false;
    }

    // Check if any users have this role
    for (const user of this.users.values()) {
      if (user.roles.includes(roleId)) {
        return false; // Cannot delete role assigned to users
      }
    }

    this.roles.delete(roleId);
    this.roleHierarchy.delete(roleId);

    return true;
  }

  /**
   * Assign role to user
   */
  assignRole(userId: string, roleId: string): boolean {
    const user = this.users.get(userId);
    const role = this.roles.get(roleId);

    if (!user || !role) {
      return false;
    }

    if (!user.roles.includes(roleId)) {
      user.roles.push(roleId);
      this.users.set(userId, user);
    }

    return true;
  }

  /**
   * Remove role from user
   */
  removeRole(userId: string, roleId: string): boolean {
    const user = this.users.get(userId);
    if (!user) {
      return false;
    }

    user.roles = user.roles.filter(r => r !== roleId);
    this.users.set(userId, user);

    return true;
  }

  /**
   * Add user
   */
  addUser(user: Omit<User, 'id'>): User {
    const newUser: User = {
      ...user,
      id: this.generateUserId(),
    };

    this.users.set(newUser.id, newUser);
    return newUser;
  }

  /**
   * Generate unique role ID
   */
  private generateRoleId(): string {
    return `role_${Date.now()}_${Math.random().toString(36).substring(2, 10)}`;
  }

  /**
   * Generate unique user ID
   */
  private generateUserId(): string {
    return `user_${Date.now()}_${Math.random().toString(36).substring(2, 10)}`;
  }

  // Getters for testing and management
  getRoles(): Role[] {
    return Array.from(this.roles.values());
  }

  getPermissions(): Permission[] {
    return Array.from(this.permissions.values());
  }

  getUsers(): User[] {
    return Array.from(this.users.values());
  }
}

// Create singleton instance
export const rbac = new RBACManager();

// Export interfaces and classes
export { RBACManager };