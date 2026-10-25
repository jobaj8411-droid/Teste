package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val prompt: String,
    val negativePrompt: String = "",
    val creationType: String,
    val style: String,
    val aspectRatio: String,
    val durationSeconds: Int,
    val resolution: String,
    val cameraMotion: String = "NONE",
    val modelUsed: String = "Google Veo 2",
    val creditCost: Int = 15,
    val status: String, // PENDING, GENERATING, COMPLETED, FAILED
    val progressPercent: Int = 0,
    val thumbnailUri: String = "",
    val videoUri: String = "",
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val storyScenesJson: String = ""
)

@Entity(tableName = "credit_ledger")
data class CreditLedgerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // INITIAL_GRANT, DAILY_BONUS, GENERATION_COST, RECHARGE
    val amount: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface VideoProjectDao {
    @Query("SELECT * FROM video_projects ORDER BY createdAtTimestamp DESC")
    fun getAllProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): VideoProjectEntity?

    @Query("SELECT * FROM video_projects WHERE status = :status ORDER BY createdAtTimestamp DESC")
    fun getProjectsByStatus(status: String): Flow<List<VideoProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VideoProjectEntity): Long

    @Update
    suspend fun updateProject(project: VideoProjectEntity)

    @Delete
    suspend fun deleteProject(project: VideoProjectEntity)

    @Query("DELETE FROM video_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)
}

@Dao
interface CreditDao {
    @Query("SELECT SUM(amount) FROM credit_ledger")
    fun getTotalCredits(): Flow<Int?>

    @Query("SELECT * FROM credit_ledger ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CreditLedgerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CreditLedgerEntity): Long
}
