package me.wjz.cqu_express_locker.ui.screens.courier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierDeliverScreen() {
    var trackingNo by remember { mutableStateOf("") }
    var selectedLocker by remember { mutableStateOf<String?>(null) }
    var selectedCompartment by remember { mutableStateOf<String?>(null) }
    var showLockerSelection by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("投递快递") }
        )
        
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 第一步：扫描/输入快递单号
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("1", color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("扫描/输入快递单号", fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = trackingNo,
                                onValueChange = { trackingNo = it },
                                label = { Text("快递单号") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledTonalIconButton(
                                onClick = { /* 扫码 */ },
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "扫码")
                            }
                        }
                        
                        if (trackingNo.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("收件人: 李明", fontWeight = FontWeight.Medium)
                                        Text(
                                            "手机: 138****1234",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 第二步：选择快递柜
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = if (trackingNo.isNotEmpty()) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "2",
                                        color = if (trackingNo.isNotEmpty()) 
                                            MaterialTheme.colorScheme.onPrimary 
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("选择快递柜", fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 快递柜列表
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LockerSelectionItem(
                                name = "重庆大学A区1号门",
                                lockerId = "L001",
                                emptySmall = 2,
                                emptyMedium = 2,
                                emptyLarge = 1,
                                isSelected = selectedLocker == "L001",
                                onSelect = { selectedLocker = "L001" }
                            )
                            LockerSelectionItem(
                                name = "重庆大学A区2号门",
                                lockerId = "L002",
                                emptySmall = 1,
                                emptyMedium = 1,
                                emptyLarge = 0,
                                isSelected = selectedLocker == "L002",
                                onSelect = { selectedLocker = "L002" }
                            )
                        }
                    }
                }
            }
            
            // 第三步：选择仓门
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = if (selectedLocker != null) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "3",
                                        color = if (selectedLocker != null) 
                                            MaterialTheme.colorScheme.onPrimary 
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("选择仓门大小", fontWeight = FontWeight.Bold)
                        }
                        
                        if (selectedLocker != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CompartmentSizeChip(
                                    size = "小仓",
                                    count = 2,
                                    isSelected = selectedCompartment == "small",
                                    onClick = { selectedCompartment = "small" },
                                    modifier = Modifier.weight(1f)
                                )
                                CompartmentSizeChip(
                                    size = "中仓",
                                    count = 2,
                                    isSelected = selectedCompartment == "medium",
                                    onClick = { selectedCompartment = "medium" },
                                    modifier = Modifier.weight(1f)
                                )
                                CompartmentSizeChip(
                                    size = "大仓",
                                    count = 1,
                                    isSelected = selectedCompartment == "large",
                                    onClick = { selectedCompartment = "large" },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            
            // 投递按钮
            item {
                Button(
                    onClick = { /* 投递 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = trackingNo.isNotEmpty() && selectedLocker != null && selectedCompartment != null
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("开柜投递", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun LockerSelectionItem(
    name: String,
    lockerId: String,
    emptySmall: Int,
    emptyMedium: Int,
    emptyLarge: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        },
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onSelect)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Medium)
                Text(
                    "小:$emptySmall 中:$emptyMedium 大:$emptyLarge",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                lockerId,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompartmentSizeChip(
    size: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        },
        enabled = count > 0
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(size, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            Text(
                "${count}个空闲",
                fontSize = 12.sp,
                color = if (count > 0) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.error
            )
        }
    }
}
