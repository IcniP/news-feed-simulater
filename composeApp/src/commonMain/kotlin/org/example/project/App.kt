package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class NewsItem(
    val id: Int,
    val title: String,
    val category: String
)

class NewsViewModel(private val scope: CoroutineScope) {
    private val categories = listOf("Tech", "Health", "Sport")

    // Flow
    val newsFlow: Flow<NewsItem> = flow {
        var id = 1
        while (true) {
            emit(NewsItem(id, "Berita Ke-$id", categories.random()))
            id++
            delay(2000)
        }
    }

    // StateFlow
    private val _readCount = MutableStateFlow(0)
    val readCount: StateFlow<Int> = _readCount.asStateFlow()

    // Filter & Transform
    fun getFilteredNews(category: String): Flow<String> {
        return newsFlow
            .filter { it.category == category || category == "All" }
            .map { "[$category] ${it.title} - Terbit sekarang!" }
    }

    // Coroutines Async
    fun markAsRead() {
        scope.launch {
            delay(300)
            _readCount.value += 1
        }
    }
}

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val viewModel = remember { NewsViewModel(scope) }

    var selectedCategory by remember { mutableStateOf("All") }
    val readCount by viewModel.readCount.collectAsState()
    val latestNews by viewModel.getFilteredNews(selectedCategory)
        .collectAsState(initial = "Mencari berita...")

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("News Feed Simulator", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Farisi Suyitno - 123140152", style = MaterialTheme.typography.bodySmall)

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Status: Online")
                        Text("Berita Dibaca: $readCount", style = MaterialTheme.typography.titleLarge)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Tech", "Sport").forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Update Terkini:", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = latestNews,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { viewModel.markAsRead() }) {
                    Text("Sudah Dibaca")
                }
            }
        }
    }
}