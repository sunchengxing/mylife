package com.mylife.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mylife.app.data.Record
import com.mylife.app.viewmodel.RecordViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothingScreen(vm: RecordViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editRecord by remember { mutableStateOf<Record?>(null) }

    LaunchedEffect(Unit) { vm.setStore("clothing") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Search bar
            var query by remember { mutableStateOf("") }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; vm.search(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索名称或备注...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))

            if (state.items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("还没有穿搭记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.items, key = { it.id }) { item ->
                        RecordCard(
                            item = item,
                            onEdit = { editRecord = item },
                            onDelete = { vm.deleteRecord(item.id) }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, null) }
    }

    if (showAddDialog) {
        RecordDialog(store = "clothing", onDismiss = { showAddDialog = false }) { record ->
            vm.saveRecord(record)
            showAddDialog = false
        }
    }

    editRecord?.let { rec ->
        RecordDialog(store = "clothing", existing = rec, onDismiss = { editRecord = null }) { record ->
            vm.saveRecord(record)
            editRecord = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodRecordScreen(vm: RecordViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editRecord by remember { mutableStateOf<Record?>(null) }

    LaunchedEffect(Unit) { vm.setStore("food") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            var query by remember { mutableStateOf("") }
            OutlinedTextField(
                value = query, onValueChange = { query = it; vm.search(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索名称或备注...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))

            if (state.items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("还没有餐饮记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.items, key = { it.id }) { item ->
                        RecordCard(item = item, onEdit = { editRecord = item }, onDelete = { vm.deleteRecord(item.id) })
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, null) }
    }

    if (showAddDialog) {
        RecordDialog(store = "food", onDismiss = { showAddDialog = false }) { vm.saveRecord(it); showAddDialog = false }
    }
    editRecord?.let {
        RecordDialog(store = "food", existing = it, onDismiss = { editRecord = null }) { vm.saveRecord(it); editRecord = null }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HousingScreen(vm: RecordViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editRecord by remember { mutableStateOf<Record?>(null) }

    LaunchedEffect(Unit) { vm.setStore("housing") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            var query by remember { mutableStateOf("") }
            OutlinedTextField(
                value = query, onValueChange = { query = it; vm.search(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索名称或备注...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))

            if (state.items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("还没有居住记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.items, key = { it.id }) { item ->
                        RecordCard(item = item, onEdit = { editRecord = item }, onDelete = { vm.deleteRecord(item.id) })
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, null) }
    }

    if (showAddDialog) {
        RecordDialog(store = "housing", onDismiss = { showAddDialog = false }) { vm.saveRecord(it); showAddDialog = false }
    }
    editRecord?.let {
        RecordDialog(store = "housing", existing = it, onDismiss = { editRecord = null }) { vm.saveRecord(it); editRecord = null }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(vm: RecordViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editRecord by remember { mutableStateOf<Record?>(null) }

    LaunchedEffect(Unit) { vm.setStore("transport") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            var query by remember { mutableStateOf("") }
            OutlinedTextField(
                value = query, onValueChange = { query = it; vm.search(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索名称或备注...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))

            if (state.items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("还没有出行记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.items, key = { it.id }) { item ->
                        RecordCard(item = item, onEdit = { editRecord = item }, onDelete = { vm.deleteRecord(item.id) })
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, null) }
    }

    if (showAddDialog) {
        RecordDialog(store = "transport", onDismiss = { showAddDialog = false }) { vm.saveRecord(it); showAddDialog = false }
    }
    editRecord?.let {
        RecordDialog(store = "transport", existing = it, onDismiss = { editRecord = null }) { vm.saveRecord(it); editRecord = null }
    }
}

// ─── Shared Components ───

@Composable
fun RecordCard(item: Record, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (item.category.isNotBlank()) AssistChip(onClick = {}, label = { Text(item.category, style = MaterialTheme.typography.labelSmall) })
                    if (item.season.isNotBlank()) AssistChip(onClick = {}, label = { Text(item.season, style = MaterialTheme.typography.labelSmall) })
                    if (item.cost.isNotBlank()) Text("¥${item.cost}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (item.note.isNotBlank()) {
                    Text(item.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                }
                val fmt = SimpleDateFormat("M/d HH:mm", Locale.getDefault())
                Text(fmt.format(item.createdAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.padding(top = 2.dp))
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp)) }
            }
        }
    }
}

@Composable
fun RecordDialog(
    store: String,
    existing: Record? = null,
    onDismiss: () -> Unit,
    onSave: (Record) -> Unit,
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "") }
    var season by remember { mutableStateOf(existing?.season ?: "") }
    var calories by remember { mutableStateOf(existing?.calories ?: "") }
    var cost by remember { mutableStateOf(existing?.cost ?: "") }
    var note by remember { mutableStateOf(existing?.note ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加记录") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("名称") }, singleLine = true)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("分类") }, singleLine = true)
                if (store == "clothing") OutlinedTextField(value = season, onValueChange = { season = it }, label = { Text("季节") }, singleLine = true)
                if (store == "food") OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("卡路里") }, singleLine = true)
                OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("花费") }, singleLine = true)
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("备注") }, maxLines = 3)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    onSave(Record(
                        id = existing?.id ?: (System.currentTimeMillis().toString(36) + (0..99999).random().toString(36)),
                        store = store, name = name.trim(), category = category.trim(),
                        season = season.trim(), calories = calories.trim(),
                        cost = cost.trim(), note = note.trim(),
                        createdAt = existing?.createdAt ?: Date(),
                        updatedAt = Date(),
                    ))
                }
            }) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
