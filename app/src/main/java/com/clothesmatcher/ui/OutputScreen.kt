package com.clothesmatcher.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.clothesmatcher.data.FavoritesRepository
import com.clothesmatcher.ui.theme.ClothesMatcherTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutputScreen(
    categories: List<String>,
    paletteId: String,
    onBack: () -> Unit,
    onNavigateToMainMenu: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { FavoritesRepository(context) }
    val scope = rememberCoroutineScope()
    val isFavorite by repository.isFavorite(paletteId, categories).collectAsState(initial = false)

    val sortedCategories = remember(categories) {
        CategoryOrder.sort(categories)
    }

    val colorMatch = remember(paletteId, sortedCategories) {
        val palette = Palettes.getById(paletteId)
        val colors = palette?.colors ?: listOf(Color.White, Color.Gray, Color.Black, Color.DarkGray)

        sortedCategories.associateWith { cat ->
            when (cat) {
                "shirts" -> colors[0]
                "trousers" -> colors[1]
                "shoes" -> colors[2]
                "accessories" -> colors[3 % colors.size]
                else -> colors[0]
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        text = "Your Match",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Curated combination based on your mood",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                sortedCategories.forEach { catId ->
                    val color = colorMatch[catId] ?: Color.LightGray
                    val iconRes = when (catId) {
                        "shirts" -> R.drawable.ic_tshirt
                        "trousers" -> R.drawable.ic_pants
                        "shoes" -> R.drawable.ic_sneakers
                        "accessories" -> R.drawable.ic_watch
                        else -> R.drawable.ic_tshirt
                    }

                    MatchItemDisplay(
                        name = catId.replaceFirstChar { it.uppercase() },
                        iconRes = iconRes,
                        color = color
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            repository.toggleFavorite(paletteId, categories)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFavorite) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (isFavorite) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFavorite) "Remove from Favorites" else "Save to Favorites",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToMainMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Return to Main Menu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun MatchItemDisplay(name: String, iconRes: Int, color: Color, modifier: Modifier = Modifier) {
    val backgroundColor = ContrastRules.getBackgroundColorForIcon(color)
    val borderColor = ContrastRules.getBorderColorForIcon(color)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(width = 60.dp, height = 12.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.dp, Color.Black.copy(alpha = 0.1f), CircleShape)
            )
        }
    }
}

@Preview(showBackground = true, name = "Phone")
@Composable
fun OutputScreenPreview() {
    ClothesMatcherTheme {
        OutputScreen(
            categories = listOf("shirts", "trousers", "shoes", "accessories"),
            paletteId = "timeless_ivy",
            onBack = {},
            onNavigateToMainMenu = {}
        )
    }
}

@Preview(
    showBackground = true, 
    device = "spec:width=1280dp,height=720dp,dpi=240", 
    name = "Store Tablet 10 (16:9 Landscape)"
)
@Composable
fun OutputScreenStoreLandscapePreview() {
    ClothesMatcherTheme {
        OutputScreen(
            categories = listOf("shirts", "trousers", "shoes", "accessories"),
            paletteId = "timeless_ivy",
            onBack = {},
            onNavigateToMainMenu = {}
        )
    }
}

@Preview(
    showBackground = true, 
    device = "spec:width=1440dp,height=2560dp,dpi=240",
    name = "Store Tablet 10 (9:16 Portrait)"
)
@Composable
fun OutputScreenStorePortraitPreview() {
    ClothesMatcherTheme {
        OutputScreen(
            categories = listOf("shirts", "trousers", "shoes", "accessories"),
            paletteId = "timeless_ivy",
            onBack = {},
            onNavigateToMainMenu = {}
        )
    }
}
