package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.ConfigurationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Configuration operations
 * Handles all database operations related to app configuration settings
 */
@Dao
interface ConfigurationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfiguration(config: ConfigurationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigurations(configs: List<ConfigurationEntity>): List<Long>

    @Update
    suspend fun updateConfiguration(config: ConfigurationEntity)

    @Delete
    suspend fun deleteConfiguration(config: ConfigurationEntity)

    @Query("SELECT * FROM configuration WHERE config_key = :key")
    suspend fun getConfigurationByKey(key: String): ConfigurationEntity?

    @Query("SELECT * FROM configuration WHERE config_key = :key")
    fun getConfigurationByKeyFlow(key: String): Flow<ConfigurationEntity?>

    @Query("SELECT config_value FROM configuration WHERE config_key = :key")
    suspend fun getConfigValue(key: String): String?

    @Query("SELECT * FROM configuration ORDER BY config_key ASC")
    suspend fun getAllConfigurations(): List<ConfigurationEntity>

    @Query("SELECT * FROM configuration ORDER BY config_key ASC")
    fun getAllConfigurationsFlow(): Flow<List<ConfigurationEntity>>

    @Query("SELECT * FROM configuration WHERE config_type = :type ORDER BY config_key ASC")
    suspend fun getConfigurationsByType(type: String): List<ConfigurationEntity>

    @Query("SELECT * FROM configuration WHERE is_encrypted = 1 ORDER BY config_key ASC")
    suspend fun getEncryptedConfigurations(): List<ConfigurationEntity>

    @Query("UPDATE configuration SET config_value = :value, updated_at = :timestamp, updated_by = :updatedBy WHERE config_key = :key")
    suspend fun updateConfigValue(
        key: String,
        value: String,
        updatedBy: String?,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM configuration WHERE config_key = :key")
    suspend fun deleteConfigurationByKey(key: String)

    @Query("SELECT COUNT(*) FROM configuration")
    suspend fun getConfigurationCount(): Int

    @Query("SELECT COUNT(*) FROM configuration WHERE is_encrypted = 1")
    suspend fun getEncryptedConfigurationCount(): Int

    @Query("SELECT * FROM configuration WHERE config_key LIKE :pattern ORDER BY config_key ASC")
    suspend fun searchConfigurations(pattern: String): List<ConfigurationEntity>

    @Query("SELECT * FROM configuration WHERE updated_at BETWEEN :startDate AND :endDate ORDER BY updated_at DESC")
    suspend fun getConfigurationsInDateRange(startDate: Long, endDate: Long): List<ConfigurationEntity>
}