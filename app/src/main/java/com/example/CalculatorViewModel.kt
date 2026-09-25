package com.example

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CalculationHistoryEntity
import com.example.data.CalculatorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryItem(
    val id: Long,
    val expression: String,
    val result: String,
    val formattedTime: String
)

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String = "",
    val isEvaluated: Boolean = false,
    val history: List<HistoryItem> = emptyList(),
    val isHistoryOpen: Boolean = false
)

class CalculatorViewModel(
    private val repository: CalculatorRepository
) : ViewModel() {

    private val _expressionState = MutableStateFlow("")
    private val _previewResultState = MutableStateFlow("")
    private val _isEvaluatedState = MutableStateFlow(false)
    private val _isHistoryOpenState = MutableStateFlow(false)

    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    val uiState: StateFlow<CalculatorUiState> = combine(
        _expressionState,
        _previewResultState,
        _isEvaluatedState,
        _isHistoryOpenState,
        repository.history
    ) { expr, preview, isEvaluated, isHistoryOpen, historyEntities ->
        CalculatorUiState(
            expression = expr,
            previewResult = preview,
            isEvaluated = isEvaluated,
            isHistoryOpen = isHistoryOpen,
            history = historyEntities.map { entity ->
                HistoryItem(
                    id = entity.id,
                    expression = entity.expression,
                    result = entity.result,
                    formattedTime = timeFormatter.format(Date(entity.timestamp))
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalculatorUiState()
    )

    fun onDigit(digit: String) {
        val currentExpr = _expressionState.value
        val isEvaluated = _isEvaluatedState.value

        if (currentExpr == "Error" || isEvaluated) {
            val newExpr = digit
            _expressionState.value = newExpr
            _previewResultState.value = CalculatorEngine.evaluate(newExpr)
            _isEvaluatedState.value = false
        } else {
            val newExpr = if (currentExpr == "0") digit else currentExpr + digit
            _expressionState.value = newExpr
            _previewResultState.value = CalculatorEngine.evaluate(newExpr)
        }
    }

    fun onOperator(op: String) {
        val currentExpr = _expressionState.value
        if (currentExpr == "Error") return

        if (currentExpr.isEmpty()) {
            if (op == "−" || op == "-") {
                _expressionState.value = "−"
                _isEvaluatedState.value = false
            }
            return
        }

        val lastChar = currentExpr.last()
        val isLastCharOp = lastChar == '+' || lastChar == '−' || lastChar == '×' || lastChar == '÷' || lastChar == '%'

        val newExpr = if (isLastCharOp) {
            currentExpr.dropLast(1) + op
        } else {
            currentExpr + op
        }

        _expressionState.value = newExpr
        _previewResultState.value = CalculatorEngine.evaluate(newExpr)
        _isEvaluatedState.value = false
    }

    fun onDecimal() {
        val currentExpr = _expressionState.value
        val isEvaluated = _isEvaluatedState.value

        if (currentExpr == "Error" || isEvaluated) {
            _expressionState.value = "0."
            _previewResultState.value = "0"
            _isEvaluatedState.value = false
            return
        }

        if (currentExpr.isEmpty()) {
            _expressionState.value = "0."
            _previewResultState.value = "0"
            return
        }

        val lastChar = currentExpr.last()
        if (lastChar == '+' || lastChar == '−' || lastChar == '×' || lastChar == '÷' || lastChar == '%' || lastChar == '-') {
            val newExpr = currentExpr + "0."
            _expressionState.value = newExpr
            _previewResultState.value = CalculatorEngine.evaluate(newExpr)
            return
        }

        if (CalculatorEngine.canAppendDecimal(currentExpr)) {
            val newExpr = currentExpr + "."
            _expressionState.value = newExpr
            _previewResultState.value = CalculatorEngine.evaluate(newExpr)
        }
    }

    fun onBackspace() {
        val currentExpr = _expressionState.value
        val isEvaluated = _isEvaluatedState.value

        if (currentExpr == "Error" || isEvaluated) {
            _expressionState.value = ""
            _previewResultState.value = ""
            _isEvaluatedState.value = false
            return
        }

        if (currentExpr.isNotEmpty()) {
            val newExpr = currentExpr.dropLast(1)
            _expressionState.value = newExpr
            _previewResultState.value = if (newExpr.isNotEmpty()) CalculatorEngine.evaluate(newExpr) else ""
        }
    }

    fun onClear() {
        _expressionState.value = ""
        _previewResultState.value = ""
        _isEvaluatedState.value = false
    }

    fun onEquals() {
        val currentExpr = _expressionState.value
        if (currentExpr.isBlank() || currentExpr == "Error") return

        val result = CalculatorEngine.evaluate(currentExpr)
        if (result == "Error") {
            _expressionState.value = "Error"
            _previewResultState.value = "Error"
            _isEvaluatedState.value = true
        } else if (result.isNotEmpty()) {
            val expressionToSave = currentExpr
            _expressionState.value = result
            _previewResultState.value = ""
            _isEvaluatedState.value = true

            // Persist to Room Database asynchronously
            viewModelScope.launch {
                repository.addCalculation(expressionToSave, result)
            }
        }
    }

    fun onNegate() {
        val currentExpr = _expressionState.value
        if (currentExpr.isBlank() || currentExpr == "Error") return

        val result = CalculatorEngine.evaluate(currentExpr)
        if (result.isNotEmpty() && result != "Error") {
            val negated = if (result.startsWith("-")) result.removePrefix("-") else "-$result"
            _expressionState.value = negated
            _previewResultState.value = CalculatorEngine.evaluate(negated)
        }
    }

    fun toggleHistory() {
        _isHistoryOpenState.update { !it }
    }

    fun onSelectHistory(item: HistoryItem) {
        _expressionState.value = item.result
        _previewResultState.value = ""
        _isEvaluatedState.value = true
        _isHistoryOpenState.value = false
    }

    fun onDeleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun onClearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = application as CalculatorApplication
                    return CalculatorViewModel(app.repository) as T
                }
            }
    }
}
