package me.wjz.cquexpresslocker.ui.screens.courier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wjz.cquexpresslocker.viewmodels.courier.CourierDeliverViewModel
import me.wjz.cquexpresslocker.viewmodels.courier.ReceiverQueryState
import me.wjz.cquexpresslocker.viewmodels.courier.DeliverState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierDeliverScreen(
    viewModel: CourierDeliverViewModel = viewModel()
) {
    var trackingNo by remember { mutableStateOf("") }
    var receiverPhone by remember { mutableStateOf("") }
    var selectedLocker by remember { mutableStateOf<String?>(null) }
    var selectedCompartment by remember { mutableStateOf<String?>(null) }
    
    val lockers by viewModel.lockers.collectAsState()
    val receiverQueryState by viewModel.receiverQueryState.collectAsState()
    val deliverState by viewModel.deliverState.collectAsState()
    
    var showDeliverySuccess by remember { mutableStateOf(false) }
    var deliveryResult by remember { mutableStateOf<String?>(null) }
    
    if (showDeliverySuccess) {
        AlertDialog(
            onDismissRequest = { showDeliverySuccess = false },
            title = { Text("投递成功") },
            text = {
                Column {
                    Text("快递号: ${deliveryResult?.split("|")?.getOrNull(0) ?: ""}")
                    Text("仓门号: ${deliveryResult?.split("|")?.getOrNull(1) ?: ""}")
                    Text("取件码: ${deliveryResult?.split("|")?.getOrNull(2) ?: ""}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeliverySuccess = false
                        trackingNo = ""
                        receiverPhone = ""
                        selectedLocker = null
                        selectedCompartment = null
                        viewModel.resetDeliverState()
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }
    
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
                            Text("快递单号", fontWeight = FontWeight.Bold)
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
                                        Text("单号已识别", fontWeight = FontWeight.Medium)
                                        Text(
                                            trackingNo,
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
            
            // 第二步：查询收件人
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
                            Text("收件人信息", fontWeight = FontWeight.Bold)
                        }
                        
                        if (trackingNo.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = receiverPhone,
                                    onValueChange = { receiverPhone = it },
                                    label = { Text("收件人手机") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.queryReceiver(receiverPhone) },
                                    enabled = receiverPhone.isNotEmpty() && receiverQueryState !is ReceiverQueryState.Loading
                                ) {
                                    Text("查询")
                                }
                            }
                            
                            when (receiverQueryState) {
                                is ReceiverQueryState.Loading -> {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                                }
                                is ReceiverQueryState.Success -> {
                                    val receiver = (receiverQueryState as ReceiverQueryState.Success).receiver
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                        ) {
                                            Text("收件人: ${receiver.name}", fontWeight = FontWeight.Medium)
                                            Text("手机: ${receiver.phone}", fontSize = 14.sp)
                                            if (receiver.defaultLocker != null) {
                                                Text("默认柜: ${receiver.defaultLocker.lockerName}", fontSize = 14.sp)
                                            }
                                        }
                                    }
                                }
                                is ReceiverQueryState.Error -> {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        (receiverQueryState as ReceiverQueryState.Error).message,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 14.sp
                                    )
                                }
                                else -> Unit
                            }
                        }
                    }
                }
            }
            
            // 第三步：选择快递柜
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = if (receiverPhone.isNotEmpty() && receiverQueryState is ReceiverQueryState.Success) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "3",
                                        color = if (receiverPhone.isNotEmpty() && receiverQueryState is ReceiverQueryState.Success) 
                                            MaterialTheme.colorScheme.onPrimary 
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("选择快递柜", fontWeight = FontWeight.Bold)
                        }
                        
                        if (receiverPhone.isNotEmpty() && receiverQueryState is ReceiverQueryState.Success) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                lockers.forEach { locker ->
                                    Card(
                                        onClick = { selectedLocker = locker.lockerId },
                                        colors = if (selectedLocker == locker.lockerId) {
                                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                        } else {
                                            CardDefaults.cardColors()
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = selectedLocker == locker.lockerId,
                                                onClick = { selectedLocker = locker.lockerId }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(locker.lockerName, fontWeight = FontWeight.Medium)
                                                Text(
                                                    "ID: ${locker.lockerId}",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 第四步：选择仓门大小
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
                                        "4",
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
                                    size = "小",
                                    isSelected = selectedCompartment == "small",
                                    onClick = { selectedCompartment = "small" },
                                    modifier = Modifier.weight(1f)
                                )
                                CompartmentSizeChip(
                                    size = "中",
                                    isSelected = selectedCompartment == "medium",
                                    onClick = { selectedCompartment = "medium" },
                                    modifier = Modifier.weight(1f)
                                )
                                CompartmentSizeChip(
                                    size = "大",
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
                val receiver = if (receiverQueryState is ReceiverQueryState.Success) {
                    (receiverQueryState as ReceiverQueryState.Success).receiver
                } else null
                
                Button(
                    onClick = {
                        if (selectedLocker != null && selectedCompartment != null && receiver != null) {
                            // 从 "L001" 提取数字部分 "1"
                            val numericLockerId = selectedLocker!!.filter { it.isDigit() }
                            viewModel.deliverExpress(
                                lockerId = numericLockerId,
                                compartmentSize = selectedCompartment!!,
                                trackingNo = trackingNo,
                                receiverPhone = receiverPhone,
                                receiverName = receiver.name
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = trackingNo.isNotEmpty() && selectedLocker != null && selectedCompartment != null
                        && receiverQueryState is ReceiverQueryState.Success
                        && deliverState !is DeliverState.Loading
                ) {
                    if (deliverState is DeliverState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.LockOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("开柜投递", fontSize = 16.sp)
                    }
                }
            }
            
            // 显示投递结果
            item {
                when (deliverState) {
                    is DeliverState.Success -> {
                        val response = (deliverState as DeliverState.Success).response
                        LaunchedEffect(deliverState) {
                            deliveryResult = "${response.expressId}|${response.compartmentNo}|${response.pickupCode}"
                            showDeliverySuccess = true
                        }
                    }
                    is DeliverState.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                (deliverState as DeliverState.Error).message,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun CompartmentSizeChip(
    size: String,
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
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(size, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
