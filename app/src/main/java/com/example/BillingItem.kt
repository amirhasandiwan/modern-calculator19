package com.example

import java.util.Locale
import java.util.UUID

enum class ItemUnit(
    val symbol: String,
    val title: String,
    val subUnit: String
) {
    KG(symbol = "kg", title = "1 kg (1000g)", subUnit = "g"),
    LITRE(symbol = "L", title = "1 L (1000ml)", subUnit = "ml"),
    PIECE(symbol = "Pc", title = "Piece (1=1)", subUnit = "pc");

    companion object {
        fun fromSymbol(symbol: String): ItemUnit = when (symbol.lowercase(Locale.ROOT)) {
            "l", "litre", "liter", "ml" -> LITRE
            "pc", "piece", "pieces", "pkt" -> PIECE
            else -> KG
        }
    }
}

data class BillingItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Double = 1.0,
    val unit: ItemUnit = ItemUnit.KG,
    val unitPrice: Double = 0.0
) {
    val hasPrice: Boolean get() = unitPrice > 0.0
    val totalPrice: Double get() = quantity * unitPrice

    // Backward-compatibility property
    val price: Double get() = totalPrice

    val formattedQuantity: String
        get() = when (unit) {
            ItemUnit.KG -> {
                if (quantity < 1.0 && quantity > 0.0) {
                    val grams = (quantity * 1000).toInt()
                    "${grams}g"
                } else if (quantity % 1.0 == 0.0) {
                    "${quantity.toInt()}kg"
                } else {
                    "${String.format(Locale.US, "%.2f", quantity).trimEnd('0').trimEnd('.')}kg"
                }
            }
            ItemUnit.LITRE -> {
                if (quantity < 1.0 && quantity > 0.0) {
                    val ml = (quantity * 1000).toInt()
                    "${ml}ml"
                } else if (quantity % 1.0 == 0.0) {
                    "${quantity.toInt()}L"
                } else {
                    "${String.format(Locale.US, "%.2f", quantity).trimEnd('0').trimEnd('.')}L"
                }
            }
            ItemUnit.PIECE -> {
                if (quantity % 1.0 == 0.0) {
                    "${quantity.toInt()}Pc"
                } else {
                    "${quantity}Pc"
                }
            }
        }

    val rateLabel: String
        get() = if (unitPrice <= 0.0) {
            "- / ${unit.symbol}"
        } else {
            String.format(Locale.US, "₹%.1f/%s", unitPrice, unit.symbol)
        }
}

object BillingDefaults {
    fun initialItems(): List<BillingItem> = listOf(
        BillingItem(name = "Doodh", quantity = 1.0, unit = ItemUnit.LITRE, unitPrice = 66.0),
        BillingItem(name = "Cheeni", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 45.0),
        BillingItem(name = "Chawal", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 90.0),
        BillingItem(name = "Aata", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 380.0),
        BillingItem(name = "Tel", quantity = 1.0, unit = ItemUnit.LITRE, unitPrice = 150.0),
        BillingItem(name = "Chai Patti", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 120.0),
        BillingItem(name = "Aalu", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 35.0),
        BillingItem(name = "Pyaz", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 40.0),
        BillingItem(name = "Sabun", quantity = 1.0, unit = ItemUnit.PIECE, unitPrice = 30.0),
        BillingItem(name = "Namak", quantity = 1.0, unit = ItemUnit.KG, unitPrice = 25.0)
    )
}
