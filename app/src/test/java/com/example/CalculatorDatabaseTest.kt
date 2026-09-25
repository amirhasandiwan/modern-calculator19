package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.CalculationHistoryDao
import com.example.data.CalculationHistoryEntity
import com.example.data.CalculatorDatabase
import com.example.data.CalculatorRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CalculatorDatabaseTest {

    private lateinit var db: CalculatorDatabase
    private lateinit var dao: CalculationHistoryDao
    private lateinit var repository: CalculatorRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CalculatorDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.historyDao()
        repository = CalculatorRepository(dao)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveCalculation() = runBlocking {
        repository.addCalculation("25 × 4", "100")
        val history = repository.history.first()

        assertEquals(1, history.size)
        assertEquals("25 × 4", history[0].expression)
        assertEquals("100", history[0].result)
    }

    @Test
    fun testDeleteCalculationById() = runBlocking {
        val id1 = repository.addCalculation("10 + 5", "15")
        repository.addCalculation("20 ÷ 2", "10")

        var history = repository.history.first()
        assertEquals(2, history.size)

        repository.deleteHistoryItem(id1)
        history = repository.history.first()
        assertEquals(1, history.size)
        assertEquals("20 ÷ 2", history[0].expression)
    }

    @Test
    fun testClearAllHistory() = runBlocking {
        repository.addCalculation("1 + 1", "2")
        repository.addCalculation("2 + 2", "4")
        repository.addCalculation("3 + 3", "6")

        var history = repository.history.first()
        assertEquals(3, history.size)

        repository.clearHistory()
        history = repository.history.first()
        assertTrue(history.isEmpty())
    }
}
