package com.clothesmatcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clothesmatcher.R
import com.clothesmatcher.data.FavoriteStyle
import com.clothesmatcher.data.FavoritesRepository
import com.clothesmatcher.ui.theme.ClothesMatcherTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onNavigateToDetails: (String, List<String>) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { FavoritesRepository(context) }
    val favorites by repository.favorites.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    var styleToRemove by remember { mutableStateOf<FavoriteStyle?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        text = "My Favorites",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No styles saved yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Start matching to save your favorites!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(favorites, key = { it.id }) { style ->
                    FavoriteStyleCard(
                        style = style,
                        onRemoveClick = { styleToRemove = style },
                        onClick = { onNavigateToDetails(style.paletteId, style.categories) }
                    )
                }
            }
        }

        if (styleToRemove != null) {
            AlertDialog(
                onDismissRequest = { styleToRemove = null },
                title = { Text("Remove Favorite") },
                text = { Text("Are you sure you want to remove this style from your favorites?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            styleToRemove?.let { style ->
                                scope.launch {
                                    repository.toggleFavorite(style.paletteId, style.categories)
                                    styleToRemove = null
                                }
                            }
                        }
                    ) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { styleToRemove = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun FavoriteStyleCard(
    style: FavoriteStyle,
    onRemoveClick: () -> Unit,
    onClick: () -> Unit
) {
    val palette = Palettes.getById(style.paletteId) ?: Palettes.all.first()
    
    // SSOT Sort for consistent order
    val sortedCategories = remember(style.categories) {
        CategoryOrder.sort(style.categories)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = palette.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onRemoveClick) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sortedCategories.forEach { catId ->
                    val color = when (catId) {
                        "shirts" -> palette.colors[0]
                        "trousers" -> palette.colors[1]
                        "shoes" -> palette.colors[2]
                        "accessories" -> palette.colors[3 % palette.colors.size]
                        else -> palette.colors[0]
                    }
                    val iconRes = when(catId) {
                        "shirts" -> R.drawable.ic_tshirt
                        "trousers" -> R.drawable.ic_pants
                        "shoes" -> R.drawable.ic_sneakers
                        "accessories" -> R.drawable.ic_watch
                        else -> R.drawable.ic_tshirt
                    }
                    
                    val backgroundColor = ContrastRules.getBackgroundColorForIcon(color)
                    val borderColor = ContrastRules.getBorderColorForIcon(color)
                    
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .border(
                                width = 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Phone")
@Composable
fun FavoritesScreenPreview() {
    ClothesMatcherTheme {
        FavoritesScreen(onBack = {}, onNavigateToDetails = { _, _ -> })
    }
}

@Preview(
    showBackground = true, 
    device = "spec:width=1280dp,height=720dp,dpi=240", 
    name = "Store Tablet 10 (16:9 Landscape)"
)
@Composable
fun FavoritesScreenStoreLandscapePreview() {
    ClothesMatcherTheme {
        FavoritesScreen(onBack = {}, onNavigateToDetails = { _, _ -> })
    }
}

@Preview(
    showBackground = true, 
    device = "spec:width=1440dp,height=2560dp,dpi=240",
    name = "Store Tablet 10 (9:16 Portrait)"
)
@Composable
fun FavoritesScreenStorePortraitPreview() {
    ClothesMatcherTheme {
        FavoritesScreen(onBack = {}, onNavigateToDetails = { _, _ -> })
    }
}
