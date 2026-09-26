package com.example

import android.app.Application
import com.example.data.BillingRepository
import com.example.data.CalculatorDatabase
import com.example.data.CalculatorRepository

class CalculatorApplication : Application() {
    val database: CalculatorDatabase by lazy { CalculatorDatabase.getDatabase(this) }
    val repository: CalculatorRepository by lazy { CalculatorRepository(database.historyDao()) }
    val billingRepository: BillingRepository by lazy { BillingRepository(database.billingHistoryDao()) }
}
