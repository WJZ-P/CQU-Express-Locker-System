package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wjz.cquexpresslocker.viewmodels.user.SendExpressUiState
import me.wjz.cquexpresslocker.viewmodels.user.SendExpressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendExpressScreen(
    onNavigateBack: () -> Unit,
    onSendExpressSuccess: (orderId: String) -> Unit = {},
    viewModel: SendExpressViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var senderName by remember { mutableStateOf("张三") }
    var senderPhone by remember { mutableStateOf("13812341234") }
    var senderAddress by remember { mutableStateOf("") }
    var receiverName by remember { mutableStateOf("") }
    var receiverPhone by remember { mutableStateOf("") }
    var receiverAddress by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var selectedCompany by remember { mutableStateOf("顺丰快递") }
    var showCompanyMenu by remember { mutableStateOf(false) }
    
    val companies = listOf("顺丰快递", "圆通快递", "中通快递", "申通快递", "京东快递")
    
    // 监听提交成功状态
    LaunchedEffect(uiState) {
        if (uiState is SendExpressUiState.Success) {
            val orderId = (uiState as SendExpressUiState.Success).data.orderId
            onSendExpressSuccess(orderId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发快递") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState is SendExpressUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("提交寄件信息中...", fontSize = 16.sp)
                }
            }
        } else if (uiState is SendExpressUiState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = (uiState as SendExpressUiState.Error).message,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.resetState() }) {
                        Text("返回")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 快递公司选择
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("快递公司", fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ExposedDropdownMenuBox(
                            expanded = showCompanyMenu,
                            onExpandedChange = { showCompanyMenu = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCompany,
                                onValueChange = {},
                                label = { Text("选择快递公司") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCompanyMenu) }
                            )
                            ExposedDropdownMenu(
                                expanded = showCompanyMenu,
                                onDismissRequest = { showCompanyMenu = false }
                            ) {
                                companies.forEach { company ->
                                    DropdownMenuItem(
                                        text = { Text(company) },
                                        onClick = {
                                            selectedCompany = company
                                            showCompanyMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            // 寄件人信息
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("寄件人", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = senderName,
                            onValueChange = { senderName = it },
                            label = { Text("姓名") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = senderPhone,
                            onValueChange = { senderPhone = it },
                            label = { Text("手机号") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = senderAddress,
                        onValueChange = { senderAddress = it },
                        label = { Text("寄件地址") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
            
            // 收件人信息
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PersonPin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("收件人", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = receiverName,
                            onValueChange = { receiverName = it },
                            label = { Text("姓名") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = receiverPhone,
                            onValueChange = { receiverPhone = it },
                            label = { Text("手机号") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = receiverAddress,
                        onValueChange = { receiverAddress = it },
                        label = { Text("收件地址") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
            
            // 物品描述
            OutlinedTextField(
                value = itemDescription,
                onValueChange = { itemDescription = it },
                label = { Text("物品描述（选填）") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 提交按钮
            Button(
                onClick = {
                    viewModel.sendExpress(
                        company = selectedCompany,
                        senderName = senderName,
                        senderPhone = senderPhone,
                        senderAddress = senderAddress,
                        receiverName = receiverName,
                        receiverPhone = receiverPhone,
                        receiverAddress = receiverAddress,
                        remark = itemDescription
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = senderName.isNotEmpty() && senderPhone.isNotEmpty() &&
                         receiverName.isNotEmpty() && receiverPhone.isNotEmpty() &&
                         receiverAddress.isNotEmpty()
            ) {
                Text("提交寄件", fontSize = 16.sp)
            }
            }
        }
    }
}
