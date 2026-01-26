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
import me.wjz.cquexpresslocker.network.BindLockerData
import me.wjz.cquexpresslocker.viewmodels.courier.CourierScanState
import me.wjz.cquexpresslocker.viewmodels.courier.CourierScanViewModel
import me.wjz.cquexpresslocker.viewmodels.courier.ReceiverQueryState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierScanScreen(
    viewModel: CourierScanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val availabilityData by viewModel.availabilityData.collectAsState()
    val receiverQueryState by viewModel.receiverQueryState.collectAsState()
    
    var showDeliveryForm by remember { mutableStateOf(false) }
    var trackingNo by remember { mutableStateOf("") }
    var receiverPhone by remember { mutableStateOf("") }
    var receiverName by remember { mutableStateOf("") }
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedLocker by remember { mutableStateOf<BindLockerData?>(null) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("扫码") }
        )
        
        when (val state = uiState) {
            is CourierScanState.Idle -> {
                IdleScreen(viewModel)
            }
            is CourierScanState.ScanningLocker -> {
                LoadingDialog()
            }
            is CourierScanState.LockerScanned -> {
                LockerScannedDialog(
                    locker = state.locker,
                    onSelectCompartment = {
                        selectedLocker = state.locker
                        selectedSize = null
                        showDeliveryForm = false
                        viewModel.selectCompartmentSize("", state.locker)
                    },
                    onCancel = { viewModel.reset() }
                )
            }
            is CourierScanState.CompartmentSelected -> {
                CompartmentSelectionScreen(
                    locker = state.locker,
                    availabilityData = availabilityData,
                    onSizeSelected = { size ->
                        selectedSize = size
                        selectedLocker = state.locker
                        showDeliveryForm = true
                    },
                    onBack = { viewModel.goBack() }
                )
            }
            is CourierScanState.Delivering -> {
                LoadingDialog()
            }
            is CourierScanState.DeliverySuccess -> {
                DeliverySuccessDialog(
                    compartmentNo = state.compartmentNo,
                    pickupCode = state.pickupCode,
                    onClose = {
                        trackingNo = ""
                        receiverPhone = ""
                        receiverName = ""
                        selectedSize = null
                        showDeliveryForm = false
                        viewModel.reset()
                    }
                )
            }
            is CourierScanState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onClose = { viewModel.reset() }
                )
            }
            else -> {}
        }
    }
    
    // 投递表单对话框
    if (showDeliveryForm && selectedLocker != null && selectedSize != null) {
        DeliveryFormDialog(
            viewModel = viewModel,
            locker = selectedLocker!!,
            compartmentSize = selectedSize!!,
            trackingNo = trackingNo,
            receiverPhone = receiverPhone,
            receiverName = receiverName,
            receiverQueryState = receiverQueryState,
            onTrackingNoChange = { trackingNo = it },
            onReceiverPhoneChange = { receiverPhone = it },
            onReceiverNameChange = { receiverName = it },
            onQueryReceiver = { phone ->
                viewModel.queryReceiver(phone)
            },
            onDeliver = {
                viewModel.deliverExpress(
                    locker = selectedLocker!!,
                    compartmentSize = selectedSize!!,
                    trackingNo = trackingNo,
                    receiverPhone = receiverPhone,
                    receiverName = receiverName
                )
            },
            onCancel = {
                showDeliveryForm = false
                viewModel.goBack()
            }
        )
    }
}

@Composable
private fun IdleScreen(viewModel: CourierScanViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "将二维码放入框内扫描",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.simulateScanLocker() }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("打开相机")
                    }
                }
            }
        }
    }
}

@Composable
private fun LockerScannedDialog(
    locker: BindLockerData,
    onSelectCompartment: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("扫码结果") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoRow("快递柜", locker.lockerName)
                Spacer(modifier = Modifier.height(16.dp))
                Text("请选择快递仓体大小", fontWeight = FontWeight.Medium)
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("取消")
                }
                Button(
                    onClick = onSelectCompartment,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("选择仓体")
                }
            }
        }
    )
}

@Composable
private fun CompartmentSelectionScreen(
    locker: BindLockerData,
    availabilityData: me.wjz.cquexpresslocker.network.LockerAvailabilityResponse?,
    onSizeSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("选择仓体大小", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        item {
            CompartmentCard(
                size = "小仓",
                available = availabilityData?.smallCount ?: 0,
                enabled = (availabilityData?.smallCount ?: 0) > 0,
                onSelect = { onSizeSelected("small") }
            )
        }
        
        item {
            CompartmentCard(
                size = "中仓",
                available = availabilityData?.mediumCount ?: 0,
                enabled = (availabilityData?.mediumCount ?: 0) > 0,
                onSelect = { onSizeSelected("medium") }
            )
        }
        
        item {
            CompartmentCard(
                size = "大仓",
                available = availabilityData?.largeCount ?: 0,
                enabled = (availabilityData?.largeCount ?: 0) > 0,
                onSelect = { onSizeSelected("large") }
            )
        }
        
        item {
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("返回")
            }
        }
    }
}

@Composable
private fun CompartmentCard(
    size: String,
    available: Int,
    enabled: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(size, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("可用: $available 个", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onSelect,
                enabled = enabled
            ) {
                Text("选择")
            }
        }
    }
}

@Composable
private fun DeliveryFormDialog(
    viewModel: CourierScanViewModel,
    locker: BindLockerData,
    compartmentSize: String,
    trackingNo: String,
    receiverPhone: String,
    receiverName: String,
    receiverQueryState: ReceiverQueryState,
    onTrackingNoChange: (String) -> Unit,
    onReceiverPhoneChange: (String) -> Unit,
    onReceiverNameChange: (String) -> Unit,
    onQueryReceiver: (String) -> Unit,
    onDeliver: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("投递快递") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = trackingNo,
                        onValueChange = onTrackingNoChange,
                        label = { Text("快递单号") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = receiverPhone,
                            onValueChange = onReceiverPhoneChange,
                            label = { Text("收件人手机号") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = { onQueryReceiver(receiverPhone) },
                            enabled = receiverPhone.isNotEmpty() && receiverQueryState !is ReceiverQueryState.Loading,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("查询")
                        }
                    }
                }
                
                item {
                    when (receiverQueryState) {
                        is ReceiverQueryState.Loading -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("查询中...", fontSize = 12.sp)
                            }
                        }
                        is ReceiverQueryState.Success -> {
                            onReceiverNameChange(receiverQueryState.receiver.name)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("收件人信息", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    InfoRow("姓名", receiverQueryState.receiver.name)
                                    InfoRow("手机", receiverQueryState.receiver.phone)
                                }
                            }
                        }
                        is ReceiverQueryState.Error -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    receiverQueryState.message,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                        else -> {}
                    }
                }
                
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            InfoRow("快递柜", locker.lockerName)
                            InfoRow("仓体大小", when (compartmentSize) {
                                "small" -> "小仓"
                                "medium" -> "中仓"
                                "large" -> "大仓"
                                else -> compartmentSize
                            })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("取消")
                }
                Button(
                    onClick = onDeliver,
                    modifier = Modifier.weight(1f),
                    enabled = trackingNo.isNotEmpty() && receiverPhone.isNotEmpty() && receiverName.isNotEmpty()
                ) {
                    Text("投递")
                }
            }
        }
    )
}

@Composable
private fun DeliverySuccessDialog(
    compartmentNo: String,
    pickupCode: String,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("投递成功") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("快递投递成功", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                InfoRow("格口号", compartmentNo)
                InfoRow("取件码", pickupCode)
            }
        },
        confirmButton = {
            Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun ErrorDialog(
    message: String,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("错误") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun LoadingDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("加载中...") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ScanFunctionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(icon, contentDescription = label)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp)
    }
}
