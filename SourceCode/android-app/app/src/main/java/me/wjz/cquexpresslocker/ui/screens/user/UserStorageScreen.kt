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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserStorageScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("存物品", "我的寄存")
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("寄存") }
        )
        
        // Tab切换
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        when (selectedTab) {
            0 -> StorageNewScreen()
            1 -> StorageListScreen()
        }
    }
}

@Composable
private fun StorageNewScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 步骤说明
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("寄存流程", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                StorageStep(1, "扫描快递柜二维码")
                StorageStep(2, "选择空闲仓门")
                StorageStep(3, "放入物品并关门")
                StorageStep(4, "分享取件码给朋友")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 扫码按钮
        Button(
            onClick = { /* 扫码开始寄存 */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("扫码寄存", fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 仓位选择（模拟）
        Text(
            "附近可用仓位",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("重庆大学A区1号门", fontWeight = FontWeight.Medium)
                        Text(
                            "距离约100米",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("空闲 5 个", color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CompartmentChip("小仓", "2个", true)
                    CompartmentChip("中仓", "2个", true)
                    CompartmentChip("大仓", "1个", true)
                }
            }
        }
    }
}

@Composable
private fun StorageStep(number: Int, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "$number",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}

@Composable
private fun CompartmentChip(size: String, count: String, available: Boolean) {
    AssistChip(
        onClick = { },
        label = { Text("$size $count") },
        colors = if (available) {
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        } else {
            AssistChipDefaults.assistChipColors()
        }
    )
}

@Composable
private fun StorageListScreen() {
    val storageItems = listOf(
        StorageItem("S001", "重庆大学A区1号门", "L001-C02", "998877", "2026-01-18 11:00", "寄存中"),
        StorageItem("S002", "重庆大学B区食堂", "L003-C04", "556677", "2026-01-17 15:30", "已取出")
    )
    
    if (storageItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Archive,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("暂无寄存记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(storageItems) { item ->
                StorageCard(item)
            }
        }
    }
}

data class StorageItem(
    val id: String,
    val location: String,
    val compartment: String,
    val pickupCode: String,
    val time: String,
    val status: String
)

@Composable
private fun StorageCard(item: StorageItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.location, fontWeight = FontWeight.Medium)
                AssistChip(
                    onClick = { },
                    label = { Text(item.status) },
                    colors = if (item.status == "寄存中") {
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    } else {
                        AssistChipDefaults.assistChipColors()
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("柜门", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(item.compartment)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("取件码", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        item.pickupCode,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "寄存时间: ${item.time}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (item.status == "寄存中") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { /* 分享取件码 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("分享取件码")
                }
            }
        }
    }
}
