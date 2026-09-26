package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.BillingHistoryDao
import com.example.data.BillingRepository
import com.example.data.CalculatorDatabase
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
class BillingDatabaseTest {

    private lateinit var db: CalculatorDatabase
    private lateinit var dao: BillingHistoryDao
    private lateinit var repository: BillingRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CalculatorDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.billingHistoryDao()
        repository = BillingRepository(dao)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testSaveAndRetrieveBillingHistory() = runBlocking {
        val sampleItems = listOf(
            BillingItem(name = "Doodh", quantity = 2.0, unit = ItemUnit.LITRE, unitPrice = 66.0),
            BillingItem(name = "Cheeni", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 45.0)
        )
        val id = repository.saveBill(
            billedBy = "Amir Hasan",
            heading = "Ration Bill",
            items = sampleItems,
            totalAmount = 177.0
        )
        assertTrue(id > 0)

        val history = repository.history.first()
        assertEquals(1, history.size)
        assertEquals("Amir Hasan", history[0].billedBy)
        assertEquals("Ration Bill", history[0].heading)
        assertEquals(2, history[0].itemCount)
        assertEquals(177.0, history[0].totalAmount, 0.01)

        val restoredItems = BillingRepository.jsonToItems(history[0].itemsJson)
        assertEquals(2, restoredItems.size)
        assertEquals("Doodh", restoredItems[0].name)
        assertEquals(2.0, restoredItems[0].quantity, 0.01)
        assertEquals(ItemUnit.LITRE, restoredItems[0].unit)
        assertEquals(66.0, restoredItems[0].unitPrice, 0.01)
    }

    @Test
    fun testDeleteBillingHistoryById() = runBlocking {
        val items1 = listOf(BillingItem(name = "Aata", quantity = 5.0, unit = ItemUnit.KG, unitPrice = 40.0))
        val id1 = repository.saveBill("User 1", "Bill 1", items1, 200.0)

        val items2 = listOf(BillingItem(name = "Chai", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 120.0))
        repository.saveBill("User 2", "Bill 2", items2, 120.0)

        var history = repository.history.first()
        assertEquals(2, history.size)

        repository.deleteBill(id1)
        history = repository.history.first()
        assertEquals(1, history.size)
        assertEquals("Bill 2", history[0].heading)
    }

    @Test
    fun testClearAllBillingHistory() = runBlocking {
        val items = listOf(BillingItem(name = "Namak", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 25.0))
        repository.saveBill("User", "Bill 1", items, 25.0)
        repository.saveBill("User", "Bill 2", items, 25.0)

        var history = repository.history.first()
        assertEquals(2, history.size)

        repository.clearAllHistory()
        history = repository.history.first()
        assertTrue(history.isEmpty())
    }
}
