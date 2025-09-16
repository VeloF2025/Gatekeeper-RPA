package com.fibreflow.core.database.dao

import androidx.room.*
import com.fibreflow.core.database.entities.AIConversationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for AI Conversation operations
 * Handles all database operations related to AI chat conversations
 */
@Dao
interface AIConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: AIConversationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<AIConversationEntity>): List<Long>

    @Update
    suspend fun updateConversation(conversation: AIConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: AIConversationEntity)

    @Query("SELECT * FROM ai_conversations WHERE conversation_id = :conversationId")
    suspend fun getConversationById(conversationId: Long): AIConversationEntity?

    @Query("SELECT * FROM ai_conversations WHERE installation_id = :installationId ORDER BY created_at ASC")
    suspend fun getConversationsByInstallation(installationId: Long): List<AIConversationEntity>

    @Query("SELECT * FROM ai_conversations WHERE installation_id = :installationId ORDER BY created_at ASC")
    fun getConversationsByInstallationFlow(installationId: Long): Flow<List<AIConversationEntity>>

    @Query("SELECT * FROM ai_conversations ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecentConversations(limit: Int = 50): List<AIConversationEntity>

    @Query("DELETE FROM ai_conversations WHERE created_at < :cutoffDate")
    suspend fun deleteOldConversations(cutoffDate: Long): Int

    @Query("SELECT COUNT(*) FROM ai_conversations")
    suspend fun getTotalConversationCount(): Int
}