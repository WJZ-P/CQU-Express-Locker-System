package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wjz.cquexpresslocker.viewmodels.user.PickupUiState
import me.wjz.cquexpresslocker.viewmodels.user.PickupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupScreen(
    expressId: String,
    onPickupSuccess: (compartmentNo: String, lockerName: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: PickupViewModel = viewModel()
    
    val uiState by viewModel.uiState.collectAsState()
    var pickupCode by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = { Text("取件验证") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            }
        )
        
        // 内容
        when (uiState) {
            is PickupUiState.Initial, is PickupUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState is PickupUiState.Loading) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("正在验证...", fontSize = 16.sp)
                    } else {
                        Text(
                            "请输入取件码",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                        
                        OutlinedTextField(
                            value = pickupCode,
                            onValueChange = { pickupCode = it },
                            label = { Text("6位数字取件码") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            singleLine = true,
                            isError = pickupCode.length > 6
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = { viewModel.pickup(pickupCode) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 16.dp),
                            enabled = pickupCode.length == 6
                        ) {
                            Text("验证并取件", fontSize = 16.sp)
                        }
                    }
                }
            }
            is PickupUiState.Success -> {
                val data = (uiState as PickupUiState.Success).data
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "取件成功！",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "格口号：${data.compartmentNo}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "快递柜：${data.lockerName}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Button(
                        onClick = { viewModel.openCompartment() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(bottom = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("一键开柜", fontSize = 16.sp)
                    }
                    
                    OutlinedButton(
                        onClick = { onPickupSuccess(data.compartmentNo, data.lockerName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("返回", fontSize = 16.sp)
                    }
                }
            }
            is PickupUiState.OpeningCompartment -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("正在开柜...", fontSize = 16.sp)
                }
            }
            is PickupUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "取件失败",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                (uiState as PickupUiState.Error).message,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                    
                    Button(
                        onClick = { 
                            pickupCode = ""
                            viewModel.resetState()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        Text("重试", fontSize = 16.sp)
                    }
                    
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("返回", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
