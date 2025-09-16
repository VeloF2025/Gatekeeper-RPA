// 🟢 WORKING: Proximity detection service without domain dependencies
package com.fibreflow.core.location

import com.fibreflow.core.common.result.Result
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for detecting proximity to locations
 * Validates if technician is within acceptable distance of target coordinates
 */
@Singleton
class ProximityDetector @Inject constructor(
    private val locationService: LocationService
) {

    companion object {
        // Default proximity thresholds in meters
        const val DEFAULT_PROXIMITY_RADIUS = 50f  // 50 meters
        const val STRICT_PROXIMITY_RADIUS = 25f   // 25 meters for critical validations
        const val LOOSE_PROXIMITY_RADIUS = 100f   // 100 meters for initial checks
    }

    /**
     * Check if technician is within proximity of a target location
     * @param targetLatitude Target location latitude
     * @param targetLongitude Target location longitude
     * @param radiusMeters Optional custom radius, defaults to DEFAULT_PROXIMITY_RADIUS
     * @return Result with proximity validation result
     */
    suspend fun validateProximity(
        targetLatitude: Double,
        targetLongitude: Double,
        radiusMeters: Float = DEFAULT_PROXIMITY_RADIUS
    ): Result<CoreProximityResult> {
        return try {
            val currentLocationResult = locationService.getCurrentLocation()

            if (currentLocationResult is Result.Error) {
                return Result.Error(currentLocationResult.exception)
            }

            val currentLocation = (currentLocationResult as Result.Success).data

            val distance = locationService.calculateDistance(
                currentLocation.latitude,
                currentLocation.longitude,
                targetLatitude,
                targetLongitude
            )

            val isWithinRadius = distance <= radiusMeters

            val result = CoreProximityResult(
                isWithinProximity = isWithinRadius,
                distanceMeters = distance,
                requiredRadiusMeters = radiusMeters,
                currentLatitude = currentLocation.latitude,
                currentLongitude = currentLocation.longitude,
                targetLatitude = targetLatitude,
                targetLongitude = targetLongitude,
                accuracy = currentLocation.accuracy
            )

            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Calculate distance between two locations
     * @param lat1 First location latitude
     * @param lon1 First location longitude
     * @param lat2 Second location latitude
     * @param lon2 Second location longitude
     * @return Distance in meters
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        return locationService.calculateDistance(lat1, lon1, lat2, lon2)
    }

    /**
     * Check proximity for multiple target locations
     * @param targets List of target locations with identifiers
     * @param radiusMeters Proximity radius
     * @return Map of identifiers to proximity results
     */
    suspend fun validateMultipleProximities(
        targets: List<TargetLocation>,
        radiusMeters: Float = DEFAULT_PROXIMITY_RADIUS
    ): Result<Map<String, CoreProximityResult>> {
        return try {
            val currentLocationResult = locationService.getCurrentLocation()

            if (currentLocationResult is Result.Error) {
                return Result.Error(currentLocationResult.exception)
            }

            val currentLocation = (currentLocationResult as Result.Success).data

            val results = targets.associate { target ->
                val distance = locationService.calculateDistance(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    target.latitude,
                    target.longitude
                )

                val isWithinRadius = distance <= radiusMeters

                target.identifier to CoreProximityResult(
                    isWithinProximity = isWithinRadius,
                    distanceMeters = distance,
                    requiredRadiusMeters = radiusMeters,
                    currentLatitude = currentLocation.latitude,
                    currentLongitude = currentLocation.longitude,
                    targetLatitude = target.latitude,
                    targetLongitude = target.longitude,
                    accuracy = currentLocation.accuracy
                )
            }

            Result.Success(results)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get the closest target from current location
     * @param targets List of target locations
     * @return Result with closest target and distance
     */
    suspend fun findClosestTarget(targets: List<TargetLocation>): Result<ClosestTargetResult> {
        return try {
            if (targets.isEmpty()) {
                return Result.Error(Exception("No targets provided"))
            }

            val currentLocationResult = locationService.getCurrentLocation()

            if (currentLocationResult is Result.Error) {
                return Result.Error(currentLocationResult.exception)
            }

            val currentLocation = (currentLocationResult as Result.Success).data

            var closestTarget: TargetLocation? = null
            var closestDistance = Float.MAX_VALUE

            for (target in targets) {
                val distance = locationService.calculateDistance(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    target.latitude,
                    target.longitude
                )

                if (distance < closestDistance) {
                    closestDistance = distance
                    closestTarget = target
                }
            }

            if (closestTarget == null) {
                return Result.Error(Exception("Unable to determine closest target"))
            }

            val result = ClosestTargetResult(
                target = closestTarget,
                distanceMeters = closestDistance,
                currentLatitude = currentLocation.latitude,
                currentLongitude = currentLocation.longitude
            )

            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Check if current location is within a specific geographic boundary
     * @param boundaries List of boundary points (lat, lon pairs)
     * @return Result with boundary check result
     */
    suspend fun isWithinBoundary(boundaries: List<Pair<Double, Double>>): Result<Boolean> {
        return try {
            val currentLocationResult = locationService.getCurrentLocation()

            if (currentLocationResult is Result.Error) {
                return Result.Error(currentLocationResult.exception)
            }

            val currentLocation = (currentLocationResult as Result.Success).data

            // Simple point-in-polygon check using ray casting algorithm
            val result = isPointInPolygon(
                currentLocation.latitude,
                currentLocation.longitude,
                boundaries
            )

            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Ray casting algorithm to check if point is inside polygon
     */
    private fun isPointInPolygon(
        lat: Double,
        lon: Double,
        boundaries: List<Pair<Double, Double>>
    ): Boolean {
        if (boundaries.size < 3) return false

        var inside = false
        var j = boundaries.size - 1

        for (i in boundaries.indices) {
            val (latI, lonI) = boundaries[i]
            val (latJ, lonJ) = boundaries[j]

            if (((lonI > lon) != (lonJ > lon)) &&
                (lat < (latJ - latI) * (lon - lonI) / (lonJ - lonI) + latI)) {
                inside = !inside
            }
            j = i
        }

        return inside
    }
}

/**
 * Target location with identifier
 */
data class TargetLocation(
    val identifier: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * Result of proximity validation (core version)
 */
data class CoreProximityResult(
    val isWithinProximity: Boolean,
    val distanceMeters: Float,
    val requiredRadiusMeters: Float,
    val currentLatitude: Double,
    val currentLongitude: Double,
    val targetLatitude: Double,
    val targetLongitude: Double,
    val accuracy: Float?
) {
    val distanceText: String
        get() = "%.1f meters".format(distanceMeters)

    val isAccurate: Boolean
        get() = accuracy != null && accuracy <= 20f  // Within 20 meters accuracy
}

/**
 * Result for finding closest target
 */
data class ClosestTargetResult(
    val target: TargetLocation,
    val distanceMeters: Float,
    val currentLatitude: Double,
    val currentLongitude: Double
) {
    val distanceText: String
        get() = "%.1f meters".format(distanceMeters)
}