package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "billing_history")
data class BillingHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val billedBy: String,
    val heading: String,
    val itemsJson: String,
    val itemCount: Int,
    val totalAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
