package org.chornobyl.hamdash.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.chornobyl.hamdash.database.dao.BandDao
import org.chornobyl.hamdash.database.dao.DigitalModeDao
import org.chornobyl.hamdash.database.dao.PropagationDao
import org.chornobyl.hamdash.database.dao.RepeaterDao
import org.chornobyl.hamdash.database.dao.SyncMetadataDao
import org.chornobyl.hamdash.database.dao.TalkgroupDao
import org.chornobyl.hamdash.database.entity.BandEntity
import org.chornobyl.hamdash.database.entity.DigitalModeEntity
import org.chornobyl.hamdash.database.entity.PropagationEntity
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.database.entity.SyncMetadataEntity
import org.chornobyl.hamdash.database.entity.TalkgroupEntity

@Database(
    entities = [
        RepeaterEntity::class,
        BandEntity::class,
        DigitalModeEntity::class,
        TalkgroupEntity::class,
        PropagationEntity::class,
        SyncMetadataEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class HamDashDatabase : RoomDatabase() {
    abstract fun repeaterDao(): RepeaterDao
    abstract fun bandDao(): BandDao
    abstract fun digitalModeDao(): DigitalModeDao
    abstract fun talkgroupDao(): TalkgroupDao
    abstract fun propagationDao(): PropagationDao
    abstract fun syncMetadataDao(): SyncMetadataDao

    companion object {
        @Volatile private var instance: HamDashDatabase? = null

        fun getInstance(context: Context): HamDashDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HamDashDatabase::class.java,
                    "chornobyl_hamdash.db",
                ).build().also { instance = it }
            }
    }
}
