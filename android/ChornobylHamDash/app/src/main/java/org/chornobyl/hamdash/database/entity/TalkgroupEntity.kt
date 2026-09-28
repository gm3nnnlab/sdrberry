package org.chornobyl.hamdash.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A publicly-published DMR talkgroup. */
@Entity(tableName = "talkgroups")
data class TalkgroupEntity(
    @PrimaryKey val id: String,
    val tgId: Int,
    val name: String,
    val description: String,
    val source: String,
)
