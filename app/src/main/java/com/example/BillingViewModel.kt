package com.example

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import kotlin.math.roundToInt

data class BillingUiState(
    val billedBy: String = "",
    val heading: String = "Ghar Ka Rashan / Monthly Grocery Bill",
    val items: List<BillingItem> = BillingDefaults.initialItems(),
    val isAddEditOpen: Boolean = false,
    val editingItem: BillingItem? = null,
    val generatedPdfFile: File? = null,
    val pdfMessage: String? = null
) {
    val totalCount: Int get() = items.size
    val totalPrice: Double get() = items.sumOf { it.totalPrice }
}

class BillingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    fun updateBilledBy(newName: String) {
        _uiState.update { it.copy(billedBy = newName) }
    }

    fun updateHeading(newHeading: String) {
        _uiState.update { it.copy(heading = newHeading) }
    }

    fun openAddDialog() {
        _uiState.update { it.copy(isAddEditOpen = true, editingItem = null) }
    }

    fun openEditDialog(item: BillingItem) {
        _uiState.update { it.copy(isAddEditOpen = true, editingItem = item) }
    }

    fun closeDialog() {
        _uiState.update { it.copy(isAddEditOpen = false, editingItem = null) }
    }

    fun saveItem(name: String, quantity: Double, unit: ItemUnit, unitPrice: Double) {
        _uiState.update { state ->
            val cleanName = name.trim().ifBlank { "Item" }
            val cleanQty = if (quantity <= 0.0) 1.0 else quantity
            val cleanPrice = if (unitPrice < 0.0) 0.0 else unitPrice

            val editing = state.editingItem
            val updatedList = if (editing != null) {
                state.items.map {
                    if (it.id == editing.id) it.copy(name = cleanName, quantity = cleanQty, unit = unit, unitPrice = cleanPrice) else it
                }
            } else {
                state.items + BillingItem(name = cleanName, quantity = cleanQty, unit = unit, unitPrice = cleanPrice)
            }

            state.copy(items = updatedList, isAddEditOpen = false, editingItem = null)
        }
    }

    fun incrementQuantity(id: String) {
        _uiState.update { state ->
            state.copy(
                items = state.items.map { item ->
                    if (item.id == id) {
                        val step = when (item.unit) {
                            ItemUnit.PIECE -> 1.0
                            ItemUnit.KG, ItemUnit.LITRE -> if (item.quantity < 1.0) 0.25 else 0.5
                        }
                        val newQty = ((item.quantity + step) * 100).roundToInt() / 100.0
                        item.copy(quantity = newQty)
                    } else item
                }
            )
        }
    }

    fun decrementQuantity(id: String) {
        _uiState.update { state ->
            state.copy(
                items = state.items.map { item ->
                    if (item.id == id) {
                        val newQty = when (item.unit) {
                            ItemUnit.PIECE -> if (item.quantity > 1.0) item.quantity - 1.0 else 1.0
                            ItemUnit.KG, ItemUnit.LITRE -> {
                                val step = if (item.quantity <= 1.0) 0.25 else 0.5
                                val calculated = ((item.quantity - step) * 100).roundToInt() / 100.0
                                if (calculated >= 0.25) calculated else 0.25
                            }
                        }
                        item.copy(quantity = newQty)
                    } else item
                }
            )
        }
    }

    fun deleteItem(id: String) {
        _uiState.update { state ->
            state.copy(items = state.items.filterNot { it.id == id })
        }
    }

    fun resetDefaults() {
        _uiState.update {
            it.copy(
                billedBy = "",
                heading = "Ghar Ka Rashan / Monthly Grocery Bill",
                items = BillingDefaults.initialItems()
            )
        }
    }

    fun generatePdf(context: Context) {
        val state = _uiState.value
        val result = BillingPdfGenerator.generatePdf(
            context = context,
            billedBy = state.billedBy,
            heading = state.heading,
            items = state.items,
            totalPrice = state.totalPrice
        )

        _uiState.update {
            it.copy(
                generatedPdfFile = if (result.success) result.file else null,
                pdfMessage = result.message
            )
        }
    }

    fun clearPdfMessage() {
        _uiState.update { it.copy(pdfMessage = null) }
    }
}
