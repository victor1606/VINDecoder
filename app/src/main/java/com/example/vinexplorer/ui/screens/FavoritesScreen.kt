package com.example.vinexplorer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vinexplorer.ui.components.EmptyState
import com.example.vinexplorer.ui.components.SwipeToDeleteCard
import com.example.vinexplorer.ui.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch

/**
 * Screen displaying VINs saved as favorites
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onVinClick: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val favoriteVins by viewModel.favoriteVins.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Favorites",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "${favoriteVins.size} saved VINs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (favoriteVins.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Favorite,
                title = "No favorites yet",
                subtitle = "Tap the heart icon on any VIN to save it here",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = favoriteVins,
                    key = { it.vin }
                ) { entity ->
                    SwipeToDeleteCard(
                        entity = entity,
                        onClick = { onVinClick(entity.vin) },
                        onFavoriteClick = {
                            viewModel.removeFromFavorites(entity.vin)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Removed from favorites",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        onDelete = {
                            viewModel.deleteVin(entity)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "VIN deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

