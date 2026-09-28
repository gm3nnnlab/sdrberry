package org.chornobyl.hamdash.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "digital_modes")
data class DigitalModeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val howItWorks: String,
    val useCases: String,
    val equipmentNeeded: String,
    val docUrl: String,
)
