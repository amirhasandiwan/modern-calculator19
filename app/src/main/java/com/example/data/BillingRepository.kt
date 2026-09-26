package com.example.data

import com.example.BillingItem
import com.example.ItemUnit
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class BillingRepository(private val dao: BillingHistoryDao) {

    val history: Flow<List<BillingHistoryEntity>> = dao.getAllBillingHistory()

    suspend fun saveBill(
        billedBy: String,
        heading: String,
        items: List<BillingItem>,
        totalAmount: Double
    ): Long {
        val json = itemsToJson(items)
        val entity = BillingHistoryEntity(
            billedBy = billedBy.trim(),
            heading = heading.trim().ifBlank { "Billing & Item List" },
            itemsJson = json,
            itemCount = items.size,
            totalAmount = totalAmount,
            timestamp = System.currentTimeMillis()
        )
        return dao.insertBilling(entity)
    }

    suspend fun deleteBill(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAllHistory() {
        dao.clearAll()
    }

    companion object {
        fun itemsToJson(items: List<BillingItem>): String {
            val arr = JSONArray()
            for (item in items) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("name", item.name)
                obj.put("quantity", item.quantity)
                obj.put("unit", item.unit.name)
                obj.put("unitPrice", item.unitPrice)
                arr.put(obj)
            }
            return arr.toString()
        }

        fun jsonToItems(json: String): List<BillingItem> {
            val list = mutableListOf<BillingItem>()
            if (json.isBlank()) return list
            try {
                val arr = JSONArray(json)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    list.add(
                        BillingItem(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            name = obj.optString("name", "Item"),
                            quantity = obj.optDouble("quantity", 1.0),
                            unit = try {
                                ItemUnit.valueOf(obj.optString("unit", "KG"))
                            } catch (e: Exception) {
                                ItemUnit.KG
                            },
                            unitPrice = obj.optDouble("unitPrice", 0.0)
                        )
                    )
                }
            } catch (e: Exception) {
                // Return whatever parsed or empty
            }
            return list
        }
    }
}
