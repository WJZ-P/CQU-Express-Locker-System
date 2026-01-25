package me.wjz.cquexpresslocker.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import me.wjz.cquexpresslocker.network.HistoryItemData
import me.wjz.cquexpresslocker.viewmodels.user.HistoryViewModel
import me.wjz.cquexpresslocker.viewmodels.user.HistoryUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    
    val tabs = listOf("全部" to "all", "取件" to "pickup", "寄存" to "storage", "发件" to "send")
    val selectedTabIndex = tabs.indexOfFirst { it.second == selectedType }.coerceAtLeast(0)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("历史记录") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab切换
            ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, (title, type) ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { viewModel.changeTab(type) },
                        text = { Text(title) }
                    )
                }
            }
            
            // 内容区域
            when (val state = uiState) {
                is HistoryUiState.Initial, is HistoryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                is HistoryUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inbox,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(bottom = 16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "暂无历史记录",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                is HistoryUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.items) { item ->
                                HistoryCard(item)
                            }
                        }
                        
                        // 分页控制
                        PaginationBar(
                            currentPage = state.currentPage,
                            total = state.total,
                            pageSize = state.pageSize,
                            onPreviousClick = { viewModel.loadPreviousPage() },
                            onNextClick = { viewModel.loadNextPage() }
                        )
                    }
                }
                
                is HistoryUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(bottom = 16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                state.message,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { viewModel.refresh() },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("重试")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(item: HistoryItemData) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (item.type) {
                    "pickup" -> MaterialTheme.colorScheme.primaryContainer
                    "storage" -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.tertiaryContainer
                },
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when (item.type) {
                            "pickup" -> Icons.Default.Inventory
                            "storage" -> Icons.Default.Archive
                            else -> Icons.Default.Send
                        },
                        contentDescription = null,
                        tint = when (item.type) {
                            "pickup" -> MaterialTheme.colorScheme.primary
                            "storage" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Medium)
                Text(
                    "单号: ${item.trackingNo}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "柜: ${item.lockerName} / ${item.compartmentNo}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    item.time,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            AssistChip(
                onClick = { },
                label = { 
                    Text(
                        when (item.status) {
                            "pending" -> "待处理"
                            "completed" -> "已完成"
                            "expired" -> "已过期"
                            else -> item.status
                        },
                        fontSize = 12.sp
                    )
                }
            )
        }
    }
}

@Composable
private fun PaginationBar(
    currentPage: Int,
    total: Int,
    pageSize: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val totalPages = (total + pageSize - 1) / pageSize
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPreviousClick,
            enabled = currentPage > 1,
            modifier = Modifier.weight(1f)
        ) {
            Text("上一页")
        }
        
        Text(
            "第 $currentPage / $totalPages 页",
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 14.sp
        )
        
        Button(
            onClick = onNextClick,
            enabled = currentPage < totalPages,
            modifier = Modifier.weight(1f)
        ) {
            Text("下一页")
        }
    }
}

