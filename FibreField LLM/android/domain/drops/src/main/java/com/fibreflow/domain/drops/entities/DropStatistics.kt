// 🟢 WORKING: Drop statistics data class
package com.fibreflow.domain.drops.entities

/**
 * Drop statistics data class containing summary information about drops
 */
data class DropStatistics(
    val totalDrops: Int,
    val availableDrops: Int,
    val assignedDrops: Int,
    val completedDrops: Int,
    val failedDrops: Int
) {

    /**
     * Get completion rate as percentage
     */
    val completionRate: Double
        get() = if (totalDrops > 0) (completedDrops.toDouble() / totalDrops) * 100 else 0.0

    /**
     * Get failure rate as percentage
     */
    val failureRate: Double
        get() = if (totalDrops > 0) (failedDrops.toDouble() / totalDrops) * 100 else 0.0

    /**
     * Get available drops percentage
     */
    val availableRate: Double
        get() = if (totalDrops > 0) (availableDrops.toDouble() / totalDrops) * 100 else 0.0

    /**
     * Get assigned drops percentage
     */
    val assignedRate: Double
        get() = if (totalDrops > 0) (assignedDrops.toDouble() / totalDrops) * 100 else 0.0

    /**
     * Check if there are any drops in progress (assigned but not completed)
     */
    val hasInProgressDrops: Boolean
        get() = assignedDrops > completedDrops
}