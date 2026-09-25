package com.example

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

data class BillingUiState(
    val heading: String = "Ghar Ka Rashan / Monthly Grocery Bill",
    val items: List<BillingItem> = BillingDefaults.initialItems(),
    val isAddEditOpen: Boolean = false,
    val editingItem: BillingItem? = null,
    val generatedPdfFile: File? = null,
    val pdfMessage: String? = null
) {
    val totalCount: Int get() = items.size
    val totalPrice: Double get() = items.sumOf { it.price }
}

class BillingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

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

    fun saveItem(name: String, price: Double) {
        _uiState.update { state ->
            val cleanName = name.trim().ifBlank { "Item" }
            val cleanPrice = if (price < 0) 0.0 else price

            val editing = state.editingItem
            val updatedList = if (editing != null) {
                state.items.map { if (it.id == editing.id) it.copy(name = cleanName, price = cleanPrice) else it }
            } else {
                state.items + BillingItem(name = cleanName, price = cleanPrice)
            }

            state.copy(items = updatedList, isAddEditOpen = false, editingItem = null)
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
                heading = "Ghar Ka Rashan / Monthly Grocery Bill",
                items = BillingDefaults.initialItems()
            )
        }
    }

    fun generatePdf(context: Context) {
        val state = _uiState.value
        val result = BillingPdfGenerator.generatePdf(
            context = context,
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
