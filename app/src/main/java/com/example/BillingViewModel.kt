package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BillingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class BillingHistoryItem(
    val id: Long,
    val billedBy: String,
    val heading: String,
    val items: List<BillingItem>,
    val itemCount: Int,
    val totalAmount: Double,
    val formattedDate: String,
    val timestamp: Long
)

data class BillingUiState(
    val billedBy: String = "",
    val heading: String = "Ghar Ka Rashan / Monthly Grocery Bill",
    val items: List<BillingItem> = BillingDefaults.initialItems(),
    val isAddEditOpen: Boolean = false,
    val editingItem: BillingItem? = null,
    val generatedPdfFile: File? = null,
    val pdfMessage: String? = null,
    val isHistoryOpen: Boolean = false,
    val history: List<BillingHistoryItem> = emptyList(),
    val saveBillMessage: String? = null
) {
    val totalCount: Int get() = items.size
    val totalPrice: Double get() = items.sumOf { it.totalPrice }
}

class BillingViewModel(
    private val repository: BillingRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    init {
        repository?.let { repo ->
            viewModelScope.launch {
                repo.history.collect { entities ->
                    val historyItems = entities.map { entity ->
                        BillingHistoryItem(
                            id = entity.id,
                            billedBy = entity.billedBy,
                            heading = entity.heading,
                            items = BillingRepository.jsonToItems(entity.itemsJson),
                            itemCount = entity.itemCount,
                            totalAmount = entity.totalAmount,
                            formattedDate = dateFormatter.format(Date(entity.timestamp)),
                            timestamp = entity.timestamp
                        )
                    }
                    _uiState.update { it.copy(history = historyItems) }
                }
            }
        }
    }

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

    fun openHistory() {
        _uiState.update { it.copy(isHistoryOpen = true) }
    }

    fun closeHistory() {
        _uiState.update { it.copy(isHistoryOpen = false) }
    }

    fun toggleHistory() {
        _uiState.update { it.copy(isHistoryOpen = !it.isHistoryOpen) }
    }

    fun saveCurrentBill(onSuccess: (() -> Unit)? = null) {
        val state = _uiState.value
        if (state.items.isEmpty()) return
        viewModelScope.launch {
            repository?.saveBill(
                billedBy = state.billedBy,
                heading = state.heading,
                items = state.items,
                totalAmount = state.totalPrice
            )
            _uiState.update { it.copy(saveBillMessage = "Bill successfully saved to History!") }
            onSuccess?.invoke()
        }
    }

    fun loadBillFromHistory(item: BillingHistoryItem) {
        _uiState.update {
            it.copy(
                billedBy = item.billedBy,
                heading = item.heading,
                items = item.items,
                isHistoryOpen = false
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository?.deleteBill(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository?.clearAllHistory()
        }
    }

    fun clearSaveBillMessage() {
        _uiState.update { it.copy(saveBillMessage = null) }
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

        // Also save to history automatically
        viewModelScope.launch {
            repository?.saveBill(
                billedBy = state.billedBy,
                heading = state.heading,
                items = state.items,
                totalAmount = state.totalPrice
            )
        }

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

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = application as? CalculatorApplication
                    return BillingViewModel(app?.billingRepository) as T
                }
            }
    }
}
