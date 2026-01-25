package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wjz.cquexpresslocker.viewmodels.user.EditProfileViewModel
import me.wjz.cquexpresslocker.viewmodels.user.EditProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentNickname: String,
    onNavigateBack: () -> Unit,
    onUpdateSuccess: () -> Unit,
    viewModel: EditProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var nickname by remember { mutableStateOf(currentNickname) }

    LaunchedEffect(uiState) {
        if (uiState is EditProfileUiState.Success) {
            onUpdateSuccess()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏
        TopAppBar(
            title = { Text("编辑资料") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "昵称",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { newValue ->
                            if (newValue.length <= 20) {
                                nickname = newValue
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入昵称") },
                        singleLine = true,
                        supportingText = {
                            Text(
                                "${nickname.length}/20",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "提示",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "昵称长度不超过20个字符，仅支持中文、英文、数字和下划线",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                when (uiState) {
                    is EditProfileUiState.Initial -> {
                        Button(
                            onClick = { viewModel.updateProfile(nickname) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = nickname.isNotBlank() && nickname != currentNickname
                        ) {
                            Text("保存", fontSize = 16.sp)
                        }
                    }
                    is EditProfileUiState.Loading -> {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = false
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("保存中...", fontSize = 16.sp)
                        }
                    }
                    is EditProfileUiState.Success -> {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("保存成功", fontSize = 16.sp)
                        }
                    }
                    is EditProfileUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    (uiState as EditProfileUiState.Error).message,
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.updateProfile(nickname) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("重试", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
