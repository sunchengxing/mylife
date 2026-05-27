package com.mylife.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mylife.app.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(vm: ProfileViewModel = viewModel()) {
    val stats by vm.stats.collectAsState()
    val auth by vm.auth.collectAsState()
    var showAuthDialog by remember { mutableStateOf(false) }
    var authMode by remember { mutableStateOf("login") }

    LaunchedEffect(Unit) { vm.loadStats() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Auth section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                if (auth.isLoggedIn) {
                    Text("欢迎", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(auth.username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { vm.logout() }) { Text("退出登录") }
                } else {
                    Text("云同步 — 未开启", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { authMode = "login"; showAuthDialog = true }) { Text("登录") }
                        OutlinedButton(onClick = { authMode = "register"; showAuthDialog = true }) { Text("注册") }
                    }
                }
            }
        }

        // Stats
        Text("数据统计", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("总记录", "${stats.totalRecords}", Modifier.weight(1f))
            StatCard("总花费", "¥${stats.totalCost.toInt()}", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("穿搭", "${stats.clothingCount}", Modifier.weight(1f))
            StatCard("餐饮", "${stats.foodCount}", Modifier.weight(1f))
            StatCard("居住", "${stats.housingCount}", Modifier.weight(1f))
            StatCard("出行", "${stats.transportCount}", Modifier.weight(1f))
        }

        // Sync
        if (auth.isLoggedIn) {
            Text("设置", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("云同步")
                    Button(
                        onClick = { vm.sync() },
                        enabled = !auth.isSyncing
                    ) { Text(if (auth.isSyncing) "同步中..." else "立即同步") }
                }
                if (auth.lastSync.isNotBlank()) {
                    Text("上次同步: ${auth.lastSync}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 12.dp, bottom = 8.dp))
                }
            }
        }
    }

    // Auth Dialog
    if (showAuthDialog) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAuthDialog = false },
            title = { Text(if (authMode == "login") "登录" else "注册") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("用户名") }, singleLine = true)
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("密码") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (authMode == "login") vm.login(username, password) { showAuthDialog = false }
                    else vm.register(username, password) { showAuthDialog = false }
                }) { Text(if (authMode == "login") "登录" else "注册") }
            },
            dismissButton = { TextButton(onClick = { showAuthDialog = false }) { Text("取消") } }
        )
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
