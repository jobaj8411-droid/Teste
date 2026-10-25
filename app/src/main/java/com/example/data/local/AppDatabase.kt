package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [VideoProjectEntity::class, CreditLedgerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoProjectDao(): VideoProjectDao
    abstract fun creditDao(): CreditDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cinecraft_video_ai.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial 150 welcome credits and sample initial showcases
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            database.creditDao().insertTransaction(
                                CreditLedgerEntity(
                                    type = "INITIAL_GRANT",
                                    amount = 150,
                                    description = "Welcome Creator Gift — 150 Free Credits"
                                )
                            )
                            // Seed a completed sample project to showcase features
                            database.videoProjectDao().insertProject(
                                VideoProjectEntity(
                                    title = "Cyberpunk Neo Metropolis",
                                    prompt = "A high-speed neon cyber vehicle drifting through rain-soaked Tokyo 2099 towers with volumetric holographic billboards",
                                    creationType = "TEXT_TO_VIDEO",
                                    style = "CYBERPUNK",
                                    aspectRatio = "PORTRAIT_9_16",
                                    durationSeconds = 6,
                                    resolution = "RES_1080P",
                                    cameraMotion = "CINEMATIC_DOLLY",
                                    modelUsed = "Google Veo 2",
                                    creditCost = 25,
                                    status = "COMPLETED",
                                    progressPercent = 100,
                                    thumbnailUri = "",
                                    videoUri = "sample_cyberpunk",
                                    createdAtTimestamp = System.currentTimeMillis() - 3600000L
                                )
                            )
                            database.videoProjectDao().insertProject(
                                VideoProjectEntity(
                                    title = "Ancient Dragon of the Mist",
                                    prompt = "A majestic emerald dragon emerging from mountain clouds at golden hour sunrise, hyperrealistic scales and cinematic lighting",
                                    creationType = "TEXT_TO_VIDEO",
                                    style = "CINEMATIC",
                                    aspectRatio = "LANDSCAPE_16_9",
                                    durationSeconds = 8,
                                    resolution = "RES_4K",
                                    cameraMotion = "ORBIT_360",
                                    modelUsed = "CineCraft Turbo",
                                    creditCost = 30,
                                    status = "COMPLETED",
                                    progressPercent = 100,
                                    thumbnailUri = "",
                                    videoUri = "sample_dragon",
                                    createdAtTimestamp = System.currentTimeMillis() - 7200000L
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
