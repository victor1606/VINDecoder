package com.example.vinexplorer.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vinexplorer.data.model.DecodedVinEntity
import com.example.vinexplorer.ui.components.*
import com.example.vinexplorer.ui.viewmodel.UiState
import com.example.vinexplorer.ui.viewmodel.VinListViewModel
import kotlinx.coroutines.launch

/**
 * Main screen showing VIN input and history list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VinListScreen(
    viewModel: VinListViewModel,
    onVinClick: (String) -> Unit
) {
    val vinInput by viewModel.vinInput.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val decodeState by viewModel.decodeState.collectAsStateWithLifecycle()
    val vinHistory by viewModel.vinHistory.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Handle decode success - navigate to detail
    LaunchedEffect(decodeState) {
        if (decodeState is UiState.Success) {
            val vin = (decodeState as UiState.Success).data.vin
            onVinClick(vin)
            viewModel.resetDecodeState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "VIN Explorer",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Decode vehicle information",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // VIN Input Section
            VinInputSection(
                vinInput = vinInput,
                onVinInputChange = viewModel::onVinInputChange,
                onDecode = {
                    focusManager.clearFocus()
                    viewModel.decodeVin()
                },
                isLoading = decodeState is UiState.Loading,
                isValid = viewModel.isValidVin(vinInput),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Error message
            AnimatedVisibility(
                visible = decodeState is UiState.Error,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (decodeState is UiState.Error) {
                    ErrorBanner(
                        message = (decodeState as UiState.Error).message,
                        onDismiss = viewModel::resetDecodeState,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar for history
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // History section header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${vinHistory.size} VINs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // VIN History List
            if (vinHistory.isEmpty()) {
                if (searchQuery.isNotBlank()) {
                    NoResultsState(
                        query = searchQuery,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    EmptyState(
                        icon = Icons.Default.DirectionsCar,
                        title = "No VINs decoded yet",
                        subtitle = "Enter a 17-character VIN above to decode vehicle information",
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = vinHistory,
                        key = { it.vin }
                    ) { entity ->
                        SwipeToDeleteCard(
                            entity = entity,
                            onClick = { onVinClick(entity.vin) },
                            onFavoriteClick = {
                                viewModel.toggleFavorite(entity.vin, !entity.isFavorite)
                            },
                            onDelete = {
                                viewModel.deleteVin(entity)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "VIN deleted",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Short
                                    )
                                    // Note: Undo would require keeping a reference to deleted item
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * VIN input section with text field and decode button
 */
@Composable
private fun VinInputSection(
    vinInput: String,
    onVinInputChange: (String) -> Unit,
    onDecode: () -> Unit,
    isLoading: Boolean,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = vinInput,
                onValueChange = onVinInputChange,
                label = { Text("Enter VIN") },
                placeholder = { Text("17-character VIN", fontFamily = FontFamily.Monospace) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { if (isValid) onDecode() }
                ),
                trailingIcon = {
                    if (vinInput.isNotEmpty()) {
                        IconButton(onClick = { onVinInputChange("") }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (vinInput.isNotEmpty() && !isValid && vinInput.length == 17) {
                            Text(
                                text = "Invalid VIN format",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text("")
                        }
                        Text("${vinInput.length}/17")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDecode,
                enabled = isValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Decode VIN")
                }
            }
        }
    }
}

/**
 * Search bar for filtering history
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search history...") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, "Clear search")
                }
            }
        },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )
}

/**
 * Swipe to delete wrapper for VIN card
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteCard(
    entity: DecodedVinEntity,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.error)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        },
        content = {
            VinHistoryCard(
                entity = entity,
                onClick = onClick,
                onFavoriteClick = onFavoriteClick
            )
        },
        enableDismissFromStartToEnd = false
    )
}

