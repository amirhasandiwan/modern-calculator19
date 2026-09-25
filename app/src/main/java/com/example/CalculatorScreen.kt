package com.example

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.DarkActionKey
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkClearKey
import com.example.ui.theme.DarkEqualKey
import com.example.ui.theme.DarkNumberKey
import com.example.ui.theme.DarkOperatorKey
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.HeaderGold
import com.example.ui.theme.HeaderTag
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    onOpenBilling: () -> Unit = {},
    viewModel: CalculatorViewModel = run {
        val context = LocalContext.current.applicationContext as Application
        viewModel(factory = CalculatorViewModel.provideFactory(context))
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextPrimary
                ),
                navigationIcon = {
                    Surface(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onOpenBilling()
                            }
                            .testTag("btn_open_billing_left_corner"),
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "Billing / Item List",
                                tint = HeaderGold,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bill",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }
                },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.testTag("app_header_container")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = HeaderGold,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 6.dp)
                            )
                            Text(
                                text = "AMIR HASAN DIWAN",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.8.sp,
                                    color = TextPrimary
                                ),
                                modifier = Modifier.testTag("app_header_title")
                            )
                        }
                        Text(
                            text = "MODERN CALCULATOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = HeaderTag
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.toggleHistory()
                        },
                        modifier = Modifier.testTag("history_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.history.isNotEmpty()) {
                                    Badge(
                                        containerColor = DarkOperatorKey,
                                        contentColor = Color.Black
                                    ) {
                                        Text("${uiState.history.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Calculation History",
                                tint = if (uiState.history.isNotEmpty()) HeaderGold else TextSecondary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display Area
            CalculatorDisplay(
                expression = uiState.expression,
                previewResult = uiState.previewResult,
                isEvaluated = uiState.isEvaluated,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Keypad Grid
            CalculatorKeypad(
                onDigit = { digit ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onDigit(digit)
                },
                onOperator = { op ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onOperator(op)
                },
                onDecimal = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onDecimal()
                },
                onClear = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onClear()
                },
                onBackspace = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onBackspace()
                },
                onEquals = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onEquals()
                },
                onNegate = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onNegate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            )
        }

        // Room Database Persistent History Bottom Sheet
        if (uiState.isHistoryOpen) {
            HistoryBottomSheet(
                history = uiState.history,
                onDismiss = { viewModel.toggleHistory() },
                onSelect = { item ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onSelectHistory(item)
                },
                onDelete = { id ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onDeleteHistoryItem(id)
                },
                onClear = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onClearHistory()
                }
            )
        }
    }
}

@Composable
fun CalculatorDisplay(
    expression: String,
    previewResult: String,
    isEvaluated: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Auto-scroll to end as expression changes
    LaunchedEffect(expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Card(
        modifier = modifier.testTag("display_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            // Top line: Raw expression (or previous equation if evaluated)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    style = if (isEvaluated) {
                        MaterialTheme.typography.headlineMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    } else {
                        MaterialTheme.typography.titleLarge.copy(
                            color = if (expression.isEmpty()) TextMuted else TextSecondary,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 1.sp
                        )
                    },
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("expression_text")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom line: Live preview / Final evaluated result
            AnimatedVisibility(
                visible = previewResult.isNotEmpty() && !isEvaluated,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val isError = previewResult == "Error"
                Text(
                    text = if (isError) "Error" else "= $previewResult",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isError) DarkClearKey else HeaderTag,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("preview_result_text")
                )
            }

            if (previewResult.isEmpty() && !isEvaluated) {
                Text(
                    text = " ",
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
    }
}

@Composable
fun CalculatorKeypad(
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onEquals: () -> Unit,
    onNegate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.testTag("keypad_column"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: C, ⌫, %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "C",
                textColor = Color.White,
                backgroundColor = DarkClearKey,
                tag = "btn_clear",
                modifier = Modifier.weight(1f),
                onClick = onClear
            )
            CalculatorIconButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                iconColor = TextPrimary,
                backgroundColor = DarkActionKey,
                tag = "btn_backspace",
                contentDescription = "Backspace",
                modifier = Modifier.weight(1f),
                onClick = onBackspace
            )
            CalculatorButton(
                text = "%",
                textColor = TextPrimary,
                backgroundColor = DarkActionKey,
                tag = "btn_percent",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("%") }
            )
            CalculatorButton(
                text = "÷",
                textColor = Color.Black,
                backgroundColor = DarkOperatorKey,
                tag = "btn_divide",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("÷") }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(text = "7", tag = "btn_7", modifier = Modifier.weight(1f), onClick = { onDigit("7") })
            CalculatorButton(text = "8", tag = "btn_8", modifier = Modifier.weight(1f), onClick = { onDigit("8") })
            CalculatorButton(text = "9", tag = "btn_9", modifier = Modifier.weight(1f), onClick = { onDigit("9") })
            CalculatorButton(
                text = "×",
                textColor = Color.Black,
                backgroundColor = DarkOperatorKey,
                tag = "btn_multiply",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("×") }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(text = "4", tag = "btn_4", modifier = Modifier.weight(1f), onClick = { onDigit("4") })
            CalculatorButton(text = "5", tag = "btn_5", modifier = Modifier.weight(1f), onClick = { onDigit("5") })
            CalculatorButton(text = "6", tag = "btn_6", modifier = Modifier.weight(1f), onClick = { onDigit("6") })
            CalculatorButton(
                text = "−",
                textColor = Color.Black,
                backgroundColor = DarkOperatorKey,
                tag = "btn_subtract",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("−") }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(text = "1", tag = "btn_1", modifier = Modifier.weight(1f), onClick = { onDigit("1") })
            CalculatorButton(text = "2", tag = "btn_2", modifier = Modifier.weight(1f), onClick = { onDigit("2") })
            CalculatorButton(text = "3", tag = "btn_3", modifier = Modifier.weight(1f), onClick = { onDigit("3") })
            CalculatorButton(
                text = "+",
                textColor = Color.Black,
                backgroundColor = DarkOperatorKey,
                tag = "btn_add",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("+") }
            )
        }

        // Row 5: ±, 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorButton(
                text = "±",
                textColor = TextSecondary,
                backgroundColor = DarkActionKey,
                tag = "btn_negate",
                modifier = Modifier.weight(1f),
                onClick = onNegate
            )
            CalculatorButton(text = "0", tag = "btn_0", modifier = Modifier.weight(1f), onClick = { onDigit("0") })
            CalculatorButton(
                text = ".",
                tag = "btn_dot",
                modifier = Modifier.weight(1f),
                onClick = onDecimal
            )
            CalculatorButton(
                text = "=",
                textColor = Color.White,
                backgroundColor = DarkEqualKey,
                tag = "btn_equal",
                modifier = Modifier.weight(1f),
                onClick = onEquals
            )
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    tag: String,
    modifier: Modifier = Modifier,
    textColor: Color = TextPrimary,
    backgroundColor: Color = DarkNumberKey,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .testTag(tag),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = textColor
                )
            )
        }
    }
}

@Composable
fun CalculatorIconButton(
    icon: ImageVector,
    contentDescription: String,
    tag: String,
    modifier: Modifier = Modifier,
    iconColor: Color = TextPrimary,
    backgroundColor: Color = DarkActionKey,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .testTag(tag),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
    history: List<HistoryItem>,
    onDismiss: () -> Unit,
    onSelect: (HistoryItem) -> Unit,
    onDelete: (Long) -> Unit,
    onClear: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        modifier = Modifier.testTag("history_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = HeaderGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Calculation History",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                if (history.isNotEmpty()) {
                    TextButton(
                        onClick = onClear,
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = null,
                            tint = DarkClearKey,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Clear All",
                            color = DarkClearKey,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Text(
                text = if (history.isEmpty()) "Tap = on any equation to save to Room local storage" else "Saved locally via Room Database • Tap to restore into calculator",
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
                modifier = Modifier.padding(top = 2.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = DarkSurfaceVariant
            )

            if (history.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No saved calculations yet",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calculations are saved permanently to Room DB",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .testTag("history_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(history, key = { it.id }) { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onSelect(item) }
                                .testTag("history_item_${item.id}"),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = item.formattedTime,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextMuted,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Restore,
                                            contentDescription = "Restore",
                                            tint = HeaderTag,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(end = 6.dp)
                                        )
                                        IconButton(
                                            onClick = { onDelete(item.id) },
                                            modifier = Modifier
                                                .size(24.dp)
                                                .testTag("delete_history_${item.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete item",
                                                tint = DarkClearKey.copy(alpha = 0.8f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.expression,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextSecondary,
                                        letterSpacing = 0.5.sp
                                    ),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "= ${item.result}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HeaderTag,
                                        letterSpacing = 0.5.sp
                                    ),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
