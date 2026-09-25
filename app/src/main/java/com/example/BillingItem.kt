package com.example

import java.util.UUID

data class BillingItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double
)

object BillingDefaults {
    fun initialItems(): List<BillingItem> = listOf(
        BillingItem(name = "Doodh", price = 66.0),
        BillingItem(name = "Cheeni", price = 45.0),
        BillingItem(name = "Chawal", price = 90.0),
        BillingItem(name = "Aata", price = 380.0),
        BillingItem(name = "Tel", price = 150.0),
        BillingItem(name = "Chai Patti", price = 120.0),
        BillingItem(name = "Aalu", price = 35.0),
        BillingItem(name = "Pyaz", price = 40.0),
        BillingItem(name = "Sabun", price = 30.0),
        BillingItem(name = "Namak", price = 25.0)
    )
}
