package com.example.data

import kotlinx.coroutines.flow.Flow

class CalculatorRepository(private val dao: CalculationHistoryDao) {

    val history: Flow<List<CalculationHistoryEntity>> = dao.getAllHistory()

    suspend fun addCalculation(expression: String, result: String): Long {
        return dao.insertCalculation(
            CalculationHistoryEntity(
                expression = expression,
                result = result,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteHistoryItem(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}
