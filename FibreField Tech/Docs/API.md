# FibreField Technician API Documentation

## 🌐 API Overview

The FibreField Technician API provides comprehensive endpoints for managing fiber optic installation workflows, technician operations, and data synchronization. The API follows REST principles with JSON payloads and implements robust authentication and error handling.

### 📋 Base Information

- **Base URL**: `https://api.fibreflow.com/v1/`
- **Protocol**: HTTPS only
- **Content Type**: `application/json`
- **Authentication**: Bearer Token + Request Signing
- **Rate Limiting**: 1000 requests/hour per technician
- **API Version**: v1.0

### 🔐 Authentication

All API requests require authentication using a Bearer token with optional request signing for enhanced security.

#### Authentication Headers

```http
Authorization: Bearer <access_token>
X-Timestamp: <unix_timestamp>
X-Nonce: <unique_request_id>
X-Signature: <hmac_sha256_signature>
Content-Type: application/json
```

#### Request Signing (Optional for Enhanced Security)

```kotlin
// Signature Generation
val data = "${method}${path}${timestamp}${nonce}${body}"
val signature = hmacSha256(data, apiSecret)
```

## 🔑 Authentication Endpoints

### Login

Authenticate technician and receive access tokens.

**POST** `/auth/login`

#### Request Body

```json
{
  "username": "technician001",
  "password": "secure_password",
  "device_id": "android_device_123",
  "device_info": {
    "model": "Samsung Galaxy S23",
    "os_version": "Android 13",
    "app_version": "1.0.0"
  },
  "biometric_enabled": true
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIs...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
    "expires_in": 3600,
    "token_type": "Bearer",
    "technician": {
      "id": "TECH001",
      "name": "John Smith",
      "email": "john.smith@contractor.com",
      "role": "SENIOR_TECHNICIAN",
      "certifications": ["FIBER_INSTALLATION", "ONT_ACTIVATION"],
      "active_projects": [1, 2, 3],
      "permissions": ["INSTALL", "ACTIVATE", "REMEDIATE"]
    }
  }
}
```

#### Error Response (401 Unauthorized)

```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "Username or password incorrect",
    "details": {
      "attempts_remaining": 2,
      "lockout_duration": null
    }
  }
}
```

### Refresh Token

Renew access token using refresh token.

**POST** `/auth/refresh`

#### Request Body

```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
  "device_id": "android_device_123"
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIs...",
    "expires_in": 3600,
    "token_type": "Bearer"
  }
}
```

### Logout

Invalidate current session and tokens.

**POST** `/auth/logout`

#### Request Body

```json
{
  "device_id": "android_device_123",
  "all_devices": false
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Successfully logged out"
}
```

## 📍 Project & Drop Management

### Get Available Projects

Retrieve projects accessible to the technician.

**GET** `/projects`

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `active` | boolean | `true` | Filter active projects |
| `limit` | integer | `50` | Maximum results |
| `offset` | integer | `0` | Pagination offset |

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "projects": [
      {
        "id": 1,
        "name": "Stellenbosch Phase 1",
        "description": "Residential fiber rollout in Stellenbosch",
        "status": "ACTIVE",
        "boundary": {
          "type": "Polygon",
          "coordinates": [[[18.8500, -33.9300], [18.8600, -33.9300], [18.8600, -33.9200], [18.8500, -33.9200], [18.8500, -33.9300]]]
        },
        "statistics": {
          "total_drops": 1250,
          "completed": 890,
          "in_progress": 45,
          "available": 315,
          "failed": 12
        },
        "assigned_technicians": ["TECH001", "TECH002", "TECH003"],
        "created_at": "2024-01-15T08:00:00Z",
        "updated_at": "2024-03-15T14:30:00Z"
      }
    ],
    "pagination": {
      "total": 3,
      "limit": 50,
      "offset": 0,
      "has_more": false
    }
  }
}
```

### Get Available Drops

Retrieve drops available for installation within a project.

**GET** `/projects/{project_id}/drops`

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `project_id` | integer | Project identifier |

#### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `status` | string | `AVAILABLE` | Filter by drop status |
| `assigned_to` | string | `null` | Filter by technician ID |
| `near_location` | string | `null` | "lat,lng,radius_meters" |
| `priority` | string | `null` | `HIGH`, `MEDIUM`, `LOW` |
| `limit` | integer | `100` | Maximum results |
| `offset` | integer | `0` | Pagination offset |

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "drops": [
      {
        "drop_number": "STELL-001-DROP-001",
        "project_id": 1,
        "location": {
          "latitude": -33.9249,
          "longitude": 18.4241,
          "altitude": 120.5,
          "accuracy": 3.2
        },
        "address": "123 Main Street, Stellenbosch, 7600",
        "status": "AVAILABLE",
        "priority": "MEDIUM",
        "customer": {
          "name": "Alice Johnson",
          "phone": "+27 82 123 4567",
          "email": "alice.johnson@email.com",
          "special_instructions": "Gate code: 1234. Friendly dog."
        },
        "assigned_technician": null,
        "estimated_duration": 120,
        "installation_date": null,
        "activation_status": "PENDING",
        "notes": "Standard residential installation",
        "created_at": "2024-01-20T09:15:00Z",
        "updated_at": "2024-01-20T09:15:00Z"
      }
    ],
    "pagination": {
      "total": 315,
      "limit": 100,
      "offset": 0,
      "has_more": true
    }
  }
}
```

### Get Drop Details

Retrieve detailed information for a specific drop.

**GET** `/drops/{drop_number}`

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "drop_number": "STELL-001-DROP-001",
    "project_id": 1,
    "location": {
      "latitude": -33.9249,
      "longitude": 18.4241,
      "altitude": 120.5,
      "accuracy": 3.2
    },
    "address": "123 Main Street, Stellenbosch, 7600",
    "status": "AVAILABLE",
    "priority": "MEDIUM",
    "customer": {
      "name": "Alice Johnson",
      "phone": "+27 82 123 4567",
      "email": "alice.johnson@email.com",
      "contact_preferences": ["SMS", "EMAIL"],
      "special_instructions": "Gate code: 1234. Friendly dog.",
      "availability": {
        "preferred_times": ["09:00-12:00", "14:00-17:00"],
        "unavailable_dates": ["2024-03-20", "2024-03-21"]
      }
    },
    "technical_info": {
      "fiber_type": "G.652.D",
      "splice_enclosure": "SE-001-A",
      "port_number": 12,
      "expected_signal_strength": -15.2,
      "service_tier": "1000/500"
    },
    "installation_history": [
      {
        "attempt_date": "2024-02-15T10:00:00Z",
        "technician": "TECH002",
        "status": "CANCELLED",
        "reason": "Customer unavailable",
        "notes": "Rescheduled for next week"
      }
    ],
    "created_at": "2024-01-20T09:15:00Z",
    "updated_at": "2024-02-15T15:30:00Z"
  }
}
```

### Assign Drop

Assign a drop to a technician.

**POST** `/drops/{drop_number}/assign`

#### Request Body

```json
{
  "technician_id": "TECH001",
  "scheduled_date": "2024-03-20T10:00:00Z",
  "estimated_duration": 120,
  "notes": "Standard installation, customer available all day"
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "drop_number": "STELL-001-DROP-001",
    "status": "ASSIGNED",
    "assigned_technician": "TECH001",
    "scheduled_date": "2024-03-20T10:00:00Z",
    "updated_at": "2024-03-15T14:30:00Z"
  }
}
```

### Validate Drop Location

Validate technician's proximity to drop location.

**POST** `/drops/{drop_number}/validate-location`

#### Request Body

```json
{
  "technician_location": {
    "latitude": -33.9248,
    "longitude": 18.4240,
    "accuracy": 5.0,
    "timestamp": "2024-03-20T10:15:00Z"
  }
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "valid": true,
    "distance_meters": 12.5,
    "within_threshold": true,
    "threshold_meters": 50,
    "confidence": "HIGH"
  }
}
```

#### Response (200 OK - Warning)

```json
{
  "success": true,
  "data": {
    "valid": false,
    "distance_meters": 125.8,
    "within_threshold": false,
    "threshold_meters": 50,
    "confidence": "LOW",
    "warning": "You are 125.8m away from the drop location. Please move closer to continue."
  }
}
```

## 🔧 Installation Management

### Start Installation

Begin a new installation workflow for a drop.

**POST** `/installations`

#### Request Body

```json
{
  "drop_number": "STELL-001-DROP-001",
  "technician_id": "TECH001",
  "start_location": {
    "latitude": -33.9249,
    "longitude": 18.4241,
    "accuracy": 3.2,
    "timestamp": "2024-03-20T10:30:00Z"
  },
  "installation_type": "NEW",
  "expected_duration": 120
}
```

#### Response (201 Created)

```json
{
  "success": true,
  "data": {
    "installation_id": 12345,
    "drop_number": "STELL-001-DROP-001",
    "technician_id": "TECH001",
    "status": "IN_PROGRESS",
    "start_time": "2024-03-20T10:30:00Z",
    "expected_completion": "2024-03-20T12:30:00Z",
    "workflow_steps": [
      {
        "step_number": 1,
        "step_type": "CABLE_SPAN",
        "name": "Cable Span Photo",
        "description": "Capture photo showing full cable span from pole to house",
        "required": true,
        "status": "PENDING",
        "validation_criteria": {
          "drop_number_visible": true,
          "full_span_visible": true,
          "no_damage_visible": true
        }
      },
      {
        "step_number": 2,
        "step_type": "HOME_ENTRY",
        "name": "Home Entry Point",
        "description": "Photo of cable entry point into building",
        "required": true,
        "status": "PENDING"
      }
      // ... 7 more steps
    ],
    "created_at": "2024-03-20T10:30:00Z"
  }
}
```

### Get Installation Status

Retrieve current status of an installation.

**GET** `/installations/{installation_id}`

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "installation_id": 12345,
    "drop_number": "STELL-001-DROP-001",
    "technician_id": "TECH001",
    "status": "IN_PROGRESS",
    "start_time": "2024-03-20T10:30:00Z",
    "current_step": 3,
    "total_steps": 9,
    "progress_percentage": 33.3,
    "photos_taken": 2,
    "photos_validated": 1,
    "photos_pending": 1,
    "estimated_completion": "2024-03-20T12:30:00Z",
    "issues_encountered": [],
    "last_activity": "2024-03-20T11:15:00Z"
  }
}
```

### Update Installation

Update installation progress or details.

**PUT** `/installations/{installation_id}`

#### Request Body

```json
{
  "status": "IN_PROGRESS",
  "current_step": 4,
  "ont_serial": "ONT123456789",
  "router_serial": "RTR987654321",
  "cable_length": 45.5,
  "signal_strength": -12.8,
  "notes": "Installation proceeding smoothly. Customer very helpful.",
  "issues_encountered": [
    {
      "type": "MINOR_DELAY",
      "description": "Had to wait for customer to move car",
      "resolution": "Customer moved vehicle, continued installation",
      "time_impact": 10
    }
  ]
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "installation_id": 12345,
    "updated_fields": ["current_step", "ont_serial", "router_serial", "cable_length", "signal_strength", "notes", "issues_encountered"],
    "updated_at": "2024-03-20T11:45:00Z"
  }
}
```

### Complete Installation

Mark an installation as completed and submit for review.

**POST** `/installations/{installation_id}/complete`

#### Request Body

```json
{
  "completion_time": "2024-03-20T12:15:00Z",
  "customer_satisfaction": "SATISFIED",
  "customer_feedback": "Very professional technician. Excellent work quality.",
  "customer_signature": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
  "final_notes": "Installation completed successfully. All tests passed.",
  "speed_test_results": {
    "download_mbps": 950.2,
    "upload_mbps": 480.5,
    "ping_ms": 4.2,
    "jitter_ms": 1.1,
    "test_server": "Cape Town",
    "test_time": "2024-03-20T12:10:00Z"
  },
  "quality_checks": {
    "signal_strength": -11.5,
    "optical_power": -8.2,
    "all_lights_green": true,
    "connectivity_verified": true
  }
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "installation_id": 12345,
    "status": "COMPLETED",
    "completion_time": "2024-03-20T12:15:00Z",
    "total_duration_minutes": 105,
    "quality_score": 9.2,
    "submission_id": "SUB-20240320-12345",
    "next_steps": [
      "Activation scheduled within 24 hours",
      "Customer will receive SMS notification",
      "Quality assurance review initiated"
    ]
  }
}
```

## 📸 Photo Management

### Upload Photo

Upload and validate installation photos.

**POST** `/installations/{installation_id}/photos`

#### Request (Multipart Form Data)

```http
Content-Type: multipart/form-data

--boundary123
Content-Disposition: form-data; name="photo"; filename="ont_active.jpg"
Content-Type: image/jpeg

[binary image data]
--boundary123
Content-Disposition: form-data; name="metadata"

{
  "photo_type": "ONT_ACTIVE",
  "sequence_number": 6,
  "capture_time": "2024-03-20T11:30:00Z",
  "capture_location": {
    "latitude": -33.9249,
    "longitude": 18.4241,
    "accuracy": 2.8
  },
  "device_orientation": 0,
  "camera_settings": {
    "flash_used": true,
    "focus_mode": "AUTO",
    "resolution": "4032x3024"
  }
}
--boundary123--
```

#### Response (201 Created)

```json
{
  "success": true,
  "data": {
    "photo_id": 98765,
    "installation_id": 12345,
    "photo_type": "ONT_ACTIVE",
    "sequence_number": 6,
    "file_url": "https://storage.fibreflow.com/photos/2024/03/20/98765.jpg",
    "thumbnail_url": "https://storage.fibreflow.com/photos/2024/03/20/98765_thumb.jpg",
    "validation_status": "VALIDATING",
    "processing_estimated_time": 5,
    "uploaded_at": "2024-03-20T11:30:15Z"
  }
}
```

### Get Photo Validation Result

Retrieve AI validation results for uploaded photo.

**GET** `/photos/{photo_id}/validation`

#### Response (200 OK - Validation Passed)

```json
{
  "success": true,
  "data": {
    "photo_id": 98765,
    "validation_status": "PASSED",
    "confidence_score": 0.94,
    "processing_time_ms": 1250,
    "validation_results": {
      "ont_detected": true,
      "ont_confidence": 0.96,
      "lights_detected": {
        "power": {"status": "ON", "confidence": 0.98},
        "los": {"status": "ON", "confidence": 0.95},
        "pon": {"status": "ON", "confidence": 0.93},
        "lan": {"status": "ON", "confidence": 0.91}
      },
      "drop_number_visible": true,
      "drop_number_text": "STELL-001-DROP-001",
      "quality_metrics": {
        "brightness": 0.72,
        "contrast": 0.85,
        "sharpness": 0.89,
        "overall_quality": 0.87
      }
    },
    "ai_metadata": {
      "model_version": "ont_detector_v3.2",
      "processing_node": "gpu_node_02",
      "detected_objects": [
        {"class": "ONT", "confidence": 0.96, "bbox": [120, 45, 340, 180]},
        {"class": "ethernet_cable", "confidence": 0.88, "bbox": [200, 160, 380, 200]}
      ]
    },
    "validated_at": "2024-03-20T11:30:20Z"
  }
}
```

#### Response (200 OK - Validation Failed)

```json
{
  "success": true,
  "data": {
    "photo_id": 98765,
    "validation_status": "FAILED",
    "confidence_score": 0.35,
    "processing_time_ms": 1180,
    "validation_issues": [
      {
        "type": "POWER_LIGHT_OFF",
        "severity": "CRITICAL",
        "message": "Power light appears to be off or not visible",
        "suggestion": "Ensure ONT is powered on and power light is clearly visible",
        "can_override": false
      },
      {
        "type": "PHOTO_QUALITY_LOW",
        "severity": "WARNING",
        "message": "Photo appears blurry or poorly lit",
        "suggestion": "Retake photo with better lighting and ensure device is steady",
        "can_override": true
      }
    ],
    "quality_metrics": {
      "brightness": 0.25,
      "contrast": 0.45,
      "sharpness": 0.32,
      "overall_quality": 0.34
    },
    "manual_override_allowed": true,
    "validated_at": "2024-03-20T11:30:18Z"
  }
}
```

### Manual Photo Override

Override AI validation with manual approval.

**POST** `/photos/{photo_id}/override`

#### Request Body

```json
{
  "override_reason": "CUSTOMER_EXPLANATION",
  "technician_notes": "Customer confirmed all lights are working. Photo angle makes power light appear dim, but it is actually on.",
  "supervisor_approval": false,
  "risk_acknowledgment": true
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "photo_id": 98765,
    "validation_status": "MANUALLY_APPROVED",
    "override_applied": true,
    "override_by": "TECH001",
    "override_time": "2024-03-20T11:35:00Z",
    "quality_flags": ["MANUAL_OVERRIDE_APPLIED"],
    "audit_trail": {
      "original_status": "FAILED",
      "override_reason": "CUSTOMER_EXPLANATION",
      "risk_level": "LOW"
    }
  }
}
```

## 🔄 Data Synchronization

### Sync Data

Synchronize local changes with the server.

**POST** `/sync/batch`

#### Request Body

```json
{
  "device_id": "android_device_123",
  "last_sync": "2024-03-20T09:00:00Z",
  "sync_items": [
    {
      "entity_type": "INSTALLATION",
      "entity_id": "12345",
      "operation": "UPDATE",
      "timestamp": "2024-03-20T11:45:00Z",
      "data": {
        "ont_serial": "ONT123456789",
        "current_step": 4,
        "notes": "Installation proceeding smoothly"
      },
      "checksum": "sha256:abc123..."
    },
    {
      "entity_type": "PHOTO",
      "entity_id": "98765",
      "operation": "CREATE",
      "timestamp": "2024-03-20T11:30:00Z",
      "data": {
        "photo_type": "ONT_ACTIVE",
        "validation_status": "PASSED",
        "file_url": "local://photos/98765.jpg"
      }
    }
  ]
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "sync_id": "SYNC-20240320-001",
    "processed_items": 2,
    "successful": [
      {
        "entity_type": "INSTALLATION",
        "entity_id": "12345",
        "server_timestamp": "2024-03-20T11:45:05Z"
      },
      {
        "entity_type": "PHOTO",
        "entity_id": "98765",
        "server_timestamp": "2024-03-20T11:45:06Z",
        "server_photo_id": "SPH-98765"
      }
    ],
    "failed": [],
    "conflicts": [],
    "server_changes": [
      {
        "entity_type": "DROP",
        "entity_id": "STELL-001-DROP-002",
        "operation": "UPDATE",
        "data": {
          "status": "AVAILABLE",
          "priority": "HIGH"
        },
        "timestamp": "2024-03-20T10:15:00Z"
      }
    ],
    "sync_completed_at": "2024-03-20T11:45:10Z"
  }
}
```

### Handle Sync Conflicts

Resolve data synchronization conflicts.

**POST** `/sync/resolve-conflicts`

#### Request Body

```json
{
  "conflicts": [
    {
      "conflict_id": "CONF-001",
      "entity_type": "INSTALLATION",
      "entity_id": "12345",
      "resolution": "USE_SERVER",
      "resolution_notes": "Server version has updated customer signature"
    }
  ]
}
```

#### Response (200 OK)

```json
{
  "success": true,
  "data": {
    "resolved_conflicts": 1,
    "resolution_summary": [
      {
        "conflict_id": "CONF-001",
        "resolution": "USE_SERVER",
        "resolved_at": "2024-03-20T11:50:00Z"
      }
    ]
  }
}
```

## 🚨 Error Responses

### Standard Error Format

All API errors follow a consistent format:

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": {
      "field": "Additional context or field-specific errors"
    },
    "request_id": "req_123456789",
    "timestamp": "2024-03-20T11:45:00Z"
  }
}
```

### Common Error Codes

| HTTP Status | Error Code | Description |
|-------------|------------|-------------|
| 400 | `INVALID_REQUEST` | Malformed request body or parameters |
| 400 | `VALIDATION_ERROR` | Request validation failed |
| 401 | `UNAUTHORIZED` | Invalid or missing authentication |
| 401 | `TOKEN_EXPIRED` | Access token has expired |
| 403 | `FORBIDDEN` | Insufficient permissions |
| 404 | `NOT_FOUND` | Requested resource not found |
| 409 | `CONFLICT` | Resource conflict (e.g., installation already exists) |
| 422 | `BUSINESS_RULE_VIOLATION` | Business logic validation failed |
| 429 | `RATE_LIMITED` | Too many requests |
| 500 | `INTERNAL_ERROR` | Server error |
| 502 | `SERVICE_UNAVAILABLE` | Downstream service unavailable |

### Example Error Responses

#### Validation Error (400)

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request validation failed",
    "details": {
      "drop_number": ["Drop number is required", "Drop number format invalid"],
      "technician_location": {
        "latitude": ["Latitude must be between -90 and 90"]
      }
    },
    "request_id": "req_123456789",
    "timestamp": "2024-03-20T11:45:00Z"
  }
}
```

#### Business Rule Violation (422)

```json
{
  "success": false,
  "error": {
    "code": "DROP_ALREADY_ASSIGNED",
    "message": "Drop is already assigned to another technician",
    "details": {
      "drop_number": "STELL-001-DROP-001",
      "assigned_to": "TECH002",
      "assigned_at": "2024-03-20T09:30:00Z"
    },
    "request_id": "req_123456789",
    "timestamp": "2024-03-20T11:45:00Z"
  }
}
```

#### Rate Limit Error (429)

```json
{
  "success": false,
  "error": {
    "code": "RATE_LIMITED",
    "message": "Too many requests. Please try again later.",
    "details": {
      "limit": 1000,
      "window": "1 hour",
      "reset_at": "2024-03-20T12:00:00Z",
      "retry_after": 900
    },
    "request_id": "req_123456789",
    "timestamp": "2024-03-20T11:45:00Z"
  }
}
```

## 📊 Rate Limiting

### Rate Limit Headers

All responses include rate limiting headers:

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 856
X-RateLimit-Reset: 1647781200
X-RateLimit-Window: 3600
```

### Rate Limits by Endpoint Type

| Endpoint Type | Limit | Window |
|---------------|-------|--------|
| **Authentication** | 10 requests | 15 minutes |
| **Read Operations** | 500 requests | 1 hour |
| **Write Operations** | 200 requests | 1 hour |
| **File Uploads** | 50 requests | 1 hour |
| **Sync Operations** | 100 requests | 1 hour |

## 🔧 Request/Response Examples

### cURL Examples

#### Login
```bash
curl -X POST https://api.fibreflow.com/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "technician001",
    "password": "secure_password",
    "device_id": "android_device_123"
  }'
```

#### Get Available Drops
```bash
curl -X GET "https://api.fibreflow.com/v1/projects/1/drops?status=AVAILABLE&limit=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  -H "Content-Type: application/json"
```

#### Upload Photo
```bash
curl -X POST https://api.fibreflow.com/v1/installations/12345/photos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  -F "photo=@ont_active.jpg" \
  -F 'metadata={"photo_type":"ONT_ACTIVE","sequence_number":6}'
```

### Kotlin/Android Examples

#### Authentication Service
```kotlin
class ApiAuthService @Inject constructor(
    private val apiService: FibreFlowApiService,
    private val secureStorage: SecureStorage
) {
    suspend fun login(username: String, password: String): Result<AuthToken> {
        return try {
            val request = LoginRequest(
                username = username,
                password = password,
                deviceId = getDeviceId(),
                deviceInfo = getDeviceInfo()
            )
            val response = apiService.login(request)
            secureStorage.saveAuthToken(response.data)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### Photo Upload Service
```kotlin
class PhotoUploadService @Inject constructor(
    private val apiService: FibreFlowApiService
) {
    suspend fun uploadPhoto(
        installationId: Long,
        photoFile: File,
        metadata: PhotoMetadata
    ): Result<PhotoUploadResponse> {
        return try {
            val photoPart = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val photoBody = MultipartBody.Part.createFormData("photo", photoFile.name, photoPart)
            
            val metadataJson = Json.encodeToString(metadata)
            val metadataBody = metadataJson.toRequestBody("application/json".toMediaTypeOrNull())
            
            val response = apiService.uploadPhoto(installationId, photoBody, metadataBody)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 🔐 Security Considerations

### API Security Features

1. **HTTPS Only**: All communication encrypted in transit
2. **Bearer Token**: JWT-based authentication
3. **Request Signing**: HMAC-SHA256 for critical operations
4. **Rate Limiting**: Prevent abuse and DoS attacks
5. **Input Validation**: Comprehensive server-side validation
6. **CORS Policy**: Restricted cross-origin requests

### Client Implementation Guidelines

1. **Token Storage**: Use Android Keystore for token storage
2. **Certificate Pinning**: Implement certificate pinning for API calls
3. **Request Timeout**: Set appropriate timeouts (30s for normal, 60s for uploads)
4. **Retry Logic**: Implement exponential backoff for retries
5. **Error Handling**: Handle all error codes gracefully
6. **Logging**: Never log sensitive data (tokens, passwords)

## 📱 SDK Integration

### Retrofit Service Interface

```kotlin
interface FibreFlowApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthToken>
    
    @GET("projects/{projectId}/drops")
    suspend fun getDrops(
        @Path("projectId") projectId: Int,
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ApiResponse<DropListResponse>
    
    @POST("installations")
    suspend fun startInstallation(@Body request: StartInstallationRequest): ApiResponse<Installation>
    
    @Multipart
    @POST("installations/{installationId}/photos")
    suspend fun uploadPhoto(
        @Path("installationId") installationId: Long,
        @Part photo: MultipartBody.Part,
        @Part("metadata") metadata: RequestBody
    ): ApiResponse<PhotoUploadResponse>
    
    @POST("sync/batch")
    suspend fun syncBatch(@Body request: BatchSyncRequest): ApiResponse<BatchSyncResponse>
}
```

## 🔍 Testing

### API Testing Tools

- **Postman Collection**: Complete API collection available
- **OpenAPI Spec**: Swagger documentation for automated testing
- **Mock Server**: Local mock server for development
- **Test Data**: Comprehensive test datasets

### Testing Environments

| Environment | Base URL | Purpose |
|-------------|----------|---------|
| **Development** | `https://dev-api.fibreflow.com/v1/` | Local development |
| **Staging** | `https://staging-api.fibreflow.com/v1/` | Pre-production testing |
| **Production** | `https://api.fibreflow.com/v1/` | Live production |

---

## 📞 Support

For API support, please contact:
- **Email**: api-support@fibreflow.com
- **Documentation**: [https://docs.fibreflow.com/api](https://docs.fibreflow.com/api)
- **Status Page**: [https://status.fibreflow.com](https://status.fibreflow.com)

---

**API Version**: 1.0  
**Last Updated**: March 2024  
**Next Review**: June 2024