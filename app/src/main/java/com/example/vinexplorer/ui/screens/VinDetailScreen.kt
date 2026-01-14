package com.example.vinexplorer.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vinexplorer.data.model.DecodedVinEntity
import com.example.vinexplorer.data.model.ModelResult
import com.example.vinexplorer.ui.components.*
import com.example.vinexplorer.ui.viewmodel.UiState
import com.example.vinexplorer.ui.viewmodel.VinDetailViewModel

import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.vinexplorer.data.remote.CarImageApi

/**
 * Detail screen showing full vehicle information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VinDetailScreen(
    viewModel: VinDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val vinDetails by viewModel.vinDetails.collectAsStateWithLifecycle()
    val modelsState by viewModel.modelsState.collectAsStateWithLifecycle()
    val expandedSections by viewModel.expandedSections.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vehicle Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    vinDetails?.let { entity ->
                        // Favorite button
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (entity.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (entity.isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (entity.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Share button
                        IconButton(onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check out this vehicle: ${entity.displayName}\nVIN: ${entity.vin}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share VIN"))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (vinDetails == null) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val entity = vinDetails!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                // Hero Card with VIN
                VinHeroCard(
                    entity = entity,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Collapsible Sections
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Basic Information
                    SectionCard(
                        title = "Basic Information",
                        icon = Icons.Default.Info,
                        isExpanded = "basic" in expandedSections,
                        onToggle = { viewModel.toggleSection("basic") }
                    ) {
                        InfoGrid(
                            items = listOf(
                                "Make" to entity.make,
                                "Model" to entity.model,
                                "Year" to entity.year,
                                "Trim" to entity.trim,
                                "Series" to entity.series,
                                "Body Class" to entity.bodyClass,
                                "Vehicle Type" to entity.vehicleType,
                                "Drive Type" to entity.driveType,
                                "Doors" to entity.doors
                            )
                        )
                    }

                    // Engine & Performance
                    SectionCard(
                        title = "Engine & Performance",
                        icon = Icons.Default.Speed,
                        isExpanded = "engine" in expandedSections,
                        onToggle = { viewModel.toggleSection("engine") }
                    ) {
                        InfoGrid(
                            items = listOf(
                                "Cylinders" to entity.engineCylinders,
                                "Displacement" to entity.engineDisplacement?.let { "${it}L" },
                                "Horsepower" to entity.engineHP?.let { "${it} hp" },
                                "Fuel Type" to entity.fuelType,
                                "Transmission" to entity.transmissionStyle,
                                "GVWR" to entity.gvwr?.let { "${it} lbs" }
                            )
                        )
                    }

                    // Safety Features
                    SectionCard(
                        title = "Safety Features",
                        icon = Icons.Default.Security,
                        isExpanded = "safety" in expandedSections,
                        onToggle = { viewModel.toggleSection("safety") }
                    ) {
                        InfoGrid(
                            items = listOf(
                                "ABS" to entity.abs,
                                "TPMS" to entity.tpms,
                                "Front Airbags" to entity.airBagLocFront,
                                "Side Airbags" to entity.airBagLocSide,
                                "Curtain Airbags" to entity.airBagLocCurtain
                            )
                        )
                    }

                    // Manufacturing
                    SectionCard(
                        title = "Manufacturing",
                        icon = Icons.Default.Factory,
                        isExpanded = "manufacturing" in expandedSections,
                        onToggle = { viewModel.toggleSection("manufacturing") }
                    ) {
                        InfoGrid(
                            items = listOf(
                                "Manufacturer" to entity.manufacturer,
                                "Plant City" to entity.plantCity,
                                "Plant State" to entity.plantState,
                                "Plant Country" to entity.plantCountry
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Models Section
                if (entity.make != null && entity.year != null) {
                    ModelsSection(
                        make = entity.make,
                        year = entity.year,
                        currentModel = entity.model,
                        modelsState = modelsState,
                        onRetry = { viewModel.retryLoadModels() },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Hero card showing VIN and vehicle name with car image
 */
@Composable
private fun VinHeroCard(
    entity: DecodedVinEntity,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val logoUrl = CarImageApi.getManufacturerLogoUrl(entity.make)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Manufacturer Logo
            if (logoUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(logoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${entity.make} logo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            } else {
                // Fallback icon if no logo URL
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vehicle Name
            Text(
                text = entity.displayName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // VIN Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VIN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entity.vin,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Error indicator if applicable
            entity.errorText?.let { errorText ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section showing other models from the same manufacturer/year
 */
@Composable
private fun ModelsSection(
    make: String,
    year: String,
    currentModel: String?,
    modelsState: UiState<List<ModelResult>>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Other $make Models",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Available in $year",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (modelsState) {
            is UiState.Loading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            is UiState.Error -> {
                ErrorBanner(
                    message = modelsState.message,
                    onRetry = onRetry
                )
            }

            is UiState.Success -> {
                val models = modelsState.data
                    .filter { it.modelName != currentModel }
                    .distinctBy { it.modelName }
                    .take(20)

                if (models.isEmpty()) {
                    Text(
                        text = "No other models found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(models) { model ->
                            ModelChip(modelName = model.modelName ?: "Unknown")
                        }
                    }
                }
            }

            is UiState.Idle -> {
                // Nothing to show
            }
        }
    }
}

/**
 * Chip for displaying model name
 */
@Composable
private fun ModelChip(
    modelName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = modelName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

