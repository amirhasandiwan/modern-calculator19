package com.example

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.DarkActionKey
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkClearKey
import com.example.ui.theme.DarkEqualKey
import com.example.ui.theme.DarkOperatorKey
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.HeaderGold
import com.example.ui.theme.HeaderTag
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    onBackToCalculator: () -> Unit,
    billingViewModel: BillingViewModel = viewModel()
) {
    val uiState by billingViewModel.uiState.collectAsState()
    val context = LocalContext.current
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
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onBackToCalculator()
                        },
                        modifier = Modifier.testTag("billing_back_to_calculator")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Calculator",
                            tint = HeaderTag
                        )
                    }
                },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                            .testTag("billing_header_container")
                    ) {
                        // Editable Name Box (Coloum)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .background(
                                    color = DarkSurfaceVariant.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = HeaderGold.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            BasicTextField(
                                value = uiState.billedBy,
                                onValueChange = { billingViewModel.updateBilledBy(it) },
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = HeaderGold,
                                    textAlign = TextAlign.Center
                                ),
                                singleLine = true,
                                cursorBrush = SolidColor(HeaderGold),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("top_bar_billed_by_input"),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.Center) {
                                        if (uiState.billedBy.isEmpty()) {
                                            Text(
                                                text = "NAME",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = HeaderGold.copy(alpha = 0.65f),
                                                    letterSpacing = 1.2.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                            if (uiState.billedBy.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Name",
                                    tint = TextSecondary,
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            billingViewModel.updateBilledBy("")
                                        }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "SMART BILLING & ITEM LIST INVOICE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp,
                                color = TextSecondary,
                                fontSize = 10.sp
                            ),
                            maxLines = 1
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            billingViewModel.resetDefaults()
                            Toast.makeText(context, "Sample items reset", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("reset_defaults_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset sample items",
                            tint = TextSecondary
                        )
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
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // 1. Heading Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("heading_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "LIST / BILL HEADING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HeaderTag,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = uiState.heading,
                        onValueChange = { billingViewModel.updateHeading(it) },
                        placeholder = { Text("Jaise: Ghar Ka Rashan / Grocery Bill") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HeaderTag,
                            unfocusedBorderColor = DarkSurfaceVariant,
                            focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.3f),
                            cursorColor = HeaderTag
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("main_heading_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Clean 4-Column Table:
            // 1st col: Saman, 2nd col: Quantity (1kg/1000g, 1L/1000ml, Piece 1=1), 3rd col: Price (Rate), 4th col: Kul Price
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("billing_table_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    // Table Header Row
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1st Column: Saman
                            Text(
                                text = "Saman",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                modifier = Modifier.weight(1.2f)
                            )
                            // 2nd Column: Quantity
                            Text(
                                text = "Quantity",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HeaderGold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(82.dp)
                            )
                            // 3rd Column: Price (Rate)
                            Text(
                                text = "Price",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                ),
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(62.dp)
                            )
                            // 4th Column: Kul Price
                            Text(
                                text = "Kul Price",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HeaderTag
                                ),
                                textAlign = TextAlign.End,
                                modifier = Modifier.width(72.dp)
                            )
                            Spacer(modifier = Modifier.width(26.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Table Scrollable Items List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("billing_items_list"),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        itemsIndexed(uiState.items, key = { _, item -> item.id }) { index, item ->
                            val isEven = index % 2 == 0
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        billingViewModel.openEditDialog(item)
                                    }
                                    .testTag("billing_item_$index"),
                                color = if (isEven) DarkSurfaceVariant.copy(alpha = 0.45f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1st Column: Saman (with S.N index)
                                    Row(
                                        modifier = Modifier.weight(1.2f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${index + 1}.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // 2nd Column: Quantity (with - and + buttons, showing 1kg/500g, 1L/500ml, 1Pc)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.width(82.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    billingViewModel.decrementQuantity(item.id)
                                                },
                                            color = DarkActionKey,
                                            shape = CircleShape
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Remove,
                                                    contentDescription = "Decrease Quantity",
                                                    tint = TextPrimary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = item.formattedQuantity,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = HeaderGold
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(42.dp)
                                        )

                                        Surface(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    billingViewModel.incrementQuantity(item.id)
                                                },
                                            color = DarkActionKey,
                                            shape = CircleShape
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Increase Quantity",
                                                    tint = TextPrimary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }

                                    // 3rd Column: Price (Rate per unit e.g. ₹45/kg, ₹66/L, ₹30/pc)
                                    Text(
                                        text = item.rateLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Normal,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        ),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.width(62.dp)
                                    )

                                    // 4th Column: Kul Price (Total item price)
                                    Text(
                                        text = String.format(Locale.US, "₹%.2f", item.totalPrice),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = HeaderTag
                                        ),
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.width(72.dp)
                                    )

                                    // Delete action button
                                    Box(
                                        modifier = Modifier.width(26.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        IconButton(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                billingViewModel.deleteItem(item.id)
                                            },
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete item",
                                                tint = DarkClearKey.copy(alpha = 0.7f),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        color = DarkSurfaceVariant,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    // 5. Total Row
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, HeaderGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .testTag("billing_total_row"),
                        color = DarkSurfaceVariant.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = HeaderGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Total Items: ${uiState.totalCount}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = HeaderGold
                                        ),
                                        modifier = Modifier.testTag("total_items_count")
                                    )
                                }
                                Text(
                                    text = "1kg(1000g) • 1L(1000ml) • Piece(1=1)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "TOTAL AMOUNT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = String.format(Locale.US, "₹ %.2f", uiState.totalPrice),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = HeaderTag
                                    ),
                                    modifier = Modifier.testTag("total_price_amount")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4 & 6. Action Buttons: "+ Naya Saman Jodein" & "Download PDF"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: "+ Naya Saman Jodein"
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        billingViewModel.openAddDialog()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_add_item"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkActionKey,
                        contentColor = TextPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkOperatorKey))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = HeaderGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ Naya Saman",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1
                    )
                }

                // Button 2: "Download PDF"
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        billingViewModel.generatePdf(context)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_download_pdf"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkEqualKey,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Download PDF",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        // Add / Edit Item Dialog with 1kg(1000g), 1L(1000ml), Piece (1=1)
        if (uiState.isAddEditOpen) {
            AddEditItemDialog(
                editingItem = uiState.editingItem,
                onDismiss = { billingViewModel.closeDialog() },
                onSave = { name, quantity, unit, price ->
                    billingViewModel.saveItem(name, quantity, unit, price)
                }
            )
        }

        // PDF Generation Result Dialog
        uiState.pdfMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { billingViewModel.clearPdfMessage() },
                containerColor = DarkSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = DarkEqualKey,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PDF Generated",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                        uiState.generatedPdfFile?.let { file ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Saved: ${file.name}",
                                style = MaterialTheme.typography.labelSmall.copy(color = HeaderTag)
                            )
                        }
                    }
                },
                confirmButton = {
                    uiState.generatedPdfFile?.let { file ->
                        Button(
                            onClick = {
                                billingViewModel.clearPdfMessage()
                                BillingPdfGenerator.openOrSharePdf(context, file, android.content.Intent.ACTION_VIEW)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkEqualKey)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open PDF")
                        }
                    }
                },
                dismissButton = {
                    Row {
                        uiState.generatedPdfFile?.let { file ->
                            OutlinedButton(
                                onClick = {
                                    billingViewModel.clearPdfMessage()
                                    BillingPdfGenerator.openOrSharePdf(context, file, android.content.Intent.ACTION_SEND)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        TextButton(onClick = { billingViewModel.clearPdfMessage() }) {
                            Text("Close", color = TextSecondary)
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemDialog(
    editingItem: BillingItem?,
    onDismiss: () -> Unit,
    onSave: (String, Double, ItemUnit, Double) -> Unit
) {
    var name by remember { mutableStateOf(editingItem?.name ?: "") }
    var selectedUnit by remember { mutableStateOf(editingItem?.unit ?: ItemUnit.KG) }
    var quantityText by remember {
        mutableStateOf(
            editingItem?.let {
                if (it.quantity % 1.0 == 0.0) it.quantity.toInt().toString() else it.quantity.toString()
            } ?: "1"
        )
    }
    var priceText by remember {
        mutableStateOf(
            editingItem?.unitPrice?.let {
                if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
            } ?: ""
        )
    }
    var isError by remember { mutableStateOf(false) }

    val parsedQty = quantityText.toDoubleOrNull() ?: 0.0
    val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
    val calculatedTotal = parsedQty * parsedPrice

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (editingItem != null) "Saman Edit Karein" else "Naya Saman Jodein",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Saman Ka Naam
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Saman Ka Naam") },
                    placeholder = { Text("Jaise: Cheeni, Doodh, Sabun") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HeaderTag,
                        unfocusedBorderColor = DarkSurfaceVariant,
                        cursorColor = HeaderTag
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_item_name")
                )

                // 2. Unit Selection: 1kg(1000g), 1L(1000ml), Piece(1=1)
                Column {
                    Text(
                        text = "Unit (Maap):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = HeaderGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ItemUnit.values().forEach { unit ->
                            val isSelected = unit == selectedUnit
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedUnit = unit },
                                label = {
                                    Text(
                                        text = unit.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DarkOperatorKey,
                                    selectedLabelColor = Color.Black,
                                    selectedLeadingIconColor = Color.Black,
                                    containerColor = DarkSurfaceVariant,
                                    labelColor = TextSecondary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 3. Quick Quantity Presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when (selectedUnit) {
                        ItemUnit.KG -> {
                            PresetChip("250g") { quantityText = "0.25" }
                            PresetChip("500g") { quantityText = "0.5" }
                            PresetChip("1 kg") { quantityText = "1" }
                            PresetChip("2 kg") { quantityText = "2" }
                            PresetChip("5 kg") { quantityText = "5" }
                        }
                        ItemUnit.LITRE -> {
                            PresetChip("250ml") { quantityText = "0.25" }
                            PresetChip("500ml") { quantityText = "0.5" }
                            PresetChip("1 L") { quantityText = "1" }
                            PresetChip("2 L") { quantityText = "2" }
                            PresetChip("5 L") { quantityText = "5" }
                        }
                        ItemUnit.PIECE -> {
                            PresetChip("1 Pc") { quantityText = "1" }
                            PresetChip("2 Pcs") { quantityText = "2" }
                            PresetChip("4 Pcs") { quantityText = "4" }
                            PresetChip("6 Pcs") { quantityText = "6" }
                            PresetChip("12 Pcs") { quantityText = "12" }
                        }
                    }
                }

                // 4. Quantity & Price Inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = {
                            quantityText = it
                            isError = false
                        },
                        label = { Text("Qty (${selectedUnit.symbol})") },
                        placeholder = { Text("1 ya 0.5") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HeaderTag,
                            unfocusedBorderColor = DarkSurfaceVariant,
                            cursorColor = HeaderTag
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_item_quantity")
                    )

                    OutlinedTextField(
                        value = priceText,
                        onValueChange = {
                            priceText = it
                            isError = false
                        },
                        label = { Text("Price/1${selectedUnit.symbol} (₹)") },
                        placeholder = { Text("45") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HeaderTag,
                            unfocusedBorderColor = DarkSurfaceVariant,
                            cursorColor = HeaderTag
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("input_item_price")
                    )
                }

                // 5. Live Calculation Preview Card
                if (parsedQty > 0 && parsedPrice > 0) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkSurfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val qtyDisplay = if (selectedUnit != ItemUnit.PIECE && parsedQty < 1.0) {
                                "${(parsedQty * 1000).toInt()}${selectedUnit.subUnit}"
                            } else {
                                "$quantityText ${selectedUnit.symbol}"
                            }
                            Text(
                                text = "$qtyDisplay × ₹${String.format(Locale.US, "%.1f", parsedPrice)}",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                            Text(
                                text = "Kul: ₹${String.format(Locale.US, "%.2f", calculatedTotal)}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HeaderTag
                                )
                            )
                        }
                    }
                }

                if (isError) {
                    Text(
                        text = "Kripya sahi naam, quantity aur price enter karein",
                        color = DarkClearKey,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceVal = priceText.toDoubleOrNull()
                    val qtyVal = quantityText.toDoubleOrNull()
                    if (name.isBlank() || priceVal == null || qtyVal == null || qtyVal <= 0.0) {
                        isError = true
                    } else {
                        onSave(name, qtyVal, selectedUnit, priceVal)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkEqualKey),
                modifier = Modifier.testTag("btn_save_item")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun PresetChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = DarkActionKey,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

