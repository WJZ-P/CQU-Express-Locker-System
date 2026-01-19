package me.wjz.cquexpresslocker.ui.screens.courier

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
fun CourierPickupScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("待揽收", "待取件")
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("取件任务") }
        )
        
        // Tab切换
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(title)
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge { Text(if (index == 0) "5" else "3") }
                        }
                    }
                )
            }
        }
        
        when (selectedTab) {
            0 -> PendingPickupList() // 待揽收（用户发的快递）
            1 -> PendingCollectList() // 待取件（退回的快递）
        }
    }
}

@Composable
private fun PendingPickupList() {
    val items = listOf(
        PickupTaskItem("SEND001", "重庆大学A区1号门", "L001-C05", "张三", "138****1234", "2小时前"),
        PickupTaskItem("SEND002", "重庆大学A区2号门", "L002-C03", "李四", "139****5678", "3小时前"),
        PickupTaskItem("SEND003", "重庆大学B区食堂", "L003-C02", "王五", "137****9012", "5小时前")
    )
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            PickupTaskCard(
                item = item,
                type = "揽收"
            )
        }
    }
}

@Composable
private fun PendingCollectList() {
    val items = listOf(
        PickupTaskItem("SF1234567890", "重庆大学A区1号门", "L001-C02", "收件人拒收", "-", "1天前"),
        PickupTaskItem("YT9876543210", "重庆大学B区食堂", "L003-C06", "超时未取", "-", "2天前")
    )
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            PickupTaskCard(
                item = item,
                type = "取回"
            )
        }
    }
}

data class PickupTaskItem(
    val id: String,
    val location: String,
    val compartment: String,
    val sender: String,
    val phone: String,
    val time: String
)

@Composable
private fun PickupTaskCard(
    item: PickupTaskItem,
    type: String
) {
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
                Text(item.id, fontWeight = FontWeight.Bold)
                Text(
                    item.time,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    item.location,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Inbox,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "柜门: ${item.compartment}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (item.phone != "-") {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${item.sender} ${item.phone}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "原因: ${item.sender}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* 导航 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("导航")
                }
                Button(
                    onClick = { /* 开柜 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(type)
                }
            }
        }
    }
}
