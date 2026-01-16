# VIN Explorer

A modern Android application for decoding Vehicle Identification Numbers (VINs) with detailed vehicle specifications, history tracking, and favorites management.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Integration](#api-integration)
- [Database Schema](#database-schema)
- [Testing](#testing)

---

## Overview

**VIN Explorer** is a feature-rich Android application that allows users to decode Vehicle Identification Numbers (VINs) to retrieve comprehensive vehicle information. The app leverages the NHTSA (National Highway Traffic Safety Administration) vPIC API to provide accurate and detailed vehicle specifications.

### What is a VIN?

A Vehicle Identification Number (VIN) is a unique 17-character code assigned to every motor vehicle. VIN Explorer decodes this number to reveal:

- **Manufacturer Information** - Make, model, and manufacturing details
- **Vehicle Specifications** - Engine type, transmission, body class, and more
- **Safety Features** - ABS, airbags, traction control, and other safety equipment
- **Technical Details** - Displacement, cylinders, fuel type, and drivetrain

---

## Features

### Core Functionality

| Feature | Description |
|---------|-------------|
| **VIN Decoding** | Decode any valid 17-character VIN to retrieve detailed vehicle information |
| **Search History** | Automatically save and manage previously decoded VINs |
| **Favorites** | Mark vehicles as favorites for quick access |
| **Offline Support** | Access previously decoded vehicles without internet connection |

### User Experience

- **Dark/Light Theme** - System-aware theming with Material 3 design
- **Smart Search** - Search through your history by VIN, make, or model
- **Organized Data** - Vehicle specs grouped into logical categories
- **Modern UI** - Clean, intuitive interface built with Jetpack Compose

---

## Architecture

VIN Explorer follows **Clean Architecture** principles with **MVVM** (Model-View-ViewModel) pattern, ensuring separation of concerns, testability, and maintainability.

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Screens   │  │  ViewModels │  │    UI Components    │  │
│  │  (Compose)  │  │   (State)   │  │  (Reusable Views)   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Models    │  │  Use Cases  │  │  Repository Ifaces  │  │
│  │  (Entities) │  │  (Business) │  │   (Abstractions)    │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Remote Data │  │ Local Data  │  │    Repositories     │  │
│  │  (Retrofit) │  │   (Room)    │  │ (Implementation)    │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Key Architecture Components

#### Presentation Layer
- **Screens**: Jetpack Compose UI screens (`VinListScreen`, `VinDetailScreen`, `FavoritesScreen`)
- **ViewModels**: State holders managing UI state and business logic orchestration
- **Navigation**: Route-based navigation using Navigation Compose

#### Domain Layer
- **Models**: Core business entities (`VehicleInfo`, `DecodedVinEntity`)
- **Repository Interfaces**: Abstract definitions for data operations

#### Data Layer
- **Remote**: Retrofit-based API client for NHTSA vPIC API
- **Local**: Room database for offline storage and history
- **Repository Implementations**: Concrete implementations coordinating data sources

---

## Tech Stack

### Core Technologies

| Technology | Purpose | Version |
|------------|---------|---------|
| **Kotlin** | Primary programming language | 2.0.21 |
| **Jetpack Compose** | Modern declarative UI toolkit | BOM 2024.12.01 |
| **Material 3** | Design system and components | Latest |

### Android Jetpack

| Library | Purpose |
|---------|---------|
| **Navigation Compose** | Screen navigation |
| **Room** | Local SQLite database abstraction |
| **ViewModel** | Lifecycle-aware state management |
| **Lifecycle Runtime** | Coroutines integration with lifecycle |

### Networking & Serialization

| Library | Purpose |
|---------|---------|
| **Retrofit 2** | Type-safe HTTP client |
| **OkHttp 4** | HTTP client with logging interceptor |
| **Gson** | JSON parsing and serialization |

### Image Loading

| Library | Purpose |
|---------|---------|
| **Coil** | Image loading for Compose |

### Asynchronous Programming

| Library | Purpose |
|---------|---------|
| **Kotlin Coroutines** | Asynchronous programming |
| **Kotlin Flow** | Reactive streams |

---

## Project Structure

```
app/src/main/java/com/example/vinexplorer/
├── data/
│   ├── local/
│   │   ├── VinDao.kt                 # Room DAO for database operations
│   │   └── VinDatabase.kt            # Room database configuration
│   ├── model/
│   │   ├── DecodedVinEntity.kt       # Room entity for stored VINs
│   │   ├── MakeModelsResponse.kt     # API response models
│   │   ├── VehicleInfo.kt            # Parsed vehicle information
│   │   └── VinDecodeResponse.kt      # VIN decode API response
│   ├── remote/
│   │   ├── CarImageApi.kt            # Car image API service
│   │   ├── NhtsaApiService.kt        # NHTSA API interface
│   │   ├── RetrofitClient.kt         # Retrofit client setup
│   │   └── RetrofitInstance.kt       # Retrofit instance provider
│   └── repository/
│       └── VinRepository.kt          # Repository implementation
│
├── ui/
│   ├── components/
│   │   ├── EmptyState.kt             # Empty state placeholder
│   │   ├── ErrorBanner.kt            # Error display component
│   │   ├── LoadingShimmer.kt         # Loading animation
│   │   ├── SectionCard.kt            # Section card component
│   │   ├── SwipeToDeleteCard.kt      # Swipeable card with delete
│   │   └── VinHistoryCard.kt         # VIN history item card
│   │
│   ├── navigation/
│   │   ├── NavGraph.kt               # Navigation graph definition
│   │   └── Screen.kt                 # Screen route definitions
│   │
│   ├── screens/
│   │   ├── FavoritesScreen.kt        # Favorites management screen
│   │   ├── VinDetailScreen.kt        # Vehicle details screen
│   │   └── VinListScreen.kt          # Main VIN list screen
│   │
│   ├── theme/
│   │   ├── Color.kt                  # Color definitions
│   │   ├── Theme.kt                  # Material theme configuration
│   │   └── Type.kt                   # Typography definitions
│   │
│   └── viewmodel/
│       ├── FavoritesViewModel.kt     # Favorites state management
│       ├── UiState.kt                # UI state sealed class
│       ├── ViewModelFactory.kt       # ViewModel factory
│       ├── VinDetailViewModel.kt     # Detail screen state
│       └── VinListViewModel.kt       # Main list state management
│
├── MainActivity.kt                   # Application entry point
└── VinExplorerApplication.kt         # Application class
```

---

## Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2.1) or newer
- **JDK 11** or higher
- **Android SDK** with minimum API level 26 (Android 8.0)
- **Gradle** 8.x (included via wrapper)


---

## API Integration

### NHTSA vPIC API

VIN Explorer integrates with the [NHTSA Vehicle Product Information Catalog (vPIC) API](https://vpic.nhtsa.dot.gov/api/), a free public API provided by the U.S. Department of Transportation.

#### Endpoints Used

| Endpoint | Purpose |
|----------|---------|
| `GET /vehicles/DecodeVin/{vin}` | Decode VIN to get vehicle information |
| `GET /vehicles/DecodeVinExtended/{vin}` | Decode VIN with extended details |
| `GET /vehicles/GetModelsForMakeYear/make/{make}/modelyear/{year}` | Get models for a specific make and year |

#### Response Processing

The API returns an array of variable-value pairs that are mapped to the `VehicleInfo` domain model:

```kotlin
// Example mapping
data class VehicleInfo(
    val vin: String,
    val make: String?,           // Variable ID: 26
    val model: String?,          // Variable ID: 28
    val year: String?,           // Variable ID: 29
    val manufacturer: String?,   // Variable ID: 27
    // ... additional fields
)
```

#### Rate Limiting

The NHTSA API is free and does not require authentication. However, please be mindful of usage to ensure availability for all users.

### Car Logos

Manufacturer logos are loaded from [Car Logos](https://www.carlogos.org/) as a visual enhancement for vehicle display.

```
GET https://www.carlogos.org/car-logos/{make}-logo.png
```

---

## Database Schema

### DecodedVinEntity Table (decoded_vins)

| Column | Type | Description |
|--------|------|-------------|
| `vin` | TEXT (PK) | 17-character VIN |
| `make` | TEXT | Vehicle manufacturer |
| `model` | TEXT | Vehicle model name |
| `year` | TEXT | Model year |
| `bodyClass` | TEXT | Body style |
| `vehicleType` | TEXT | Type classification |
| `driveType` | TEXT | Drivetrain configuration |
| `doors` | TEXT | Number of doors |
| `trim` | TEXT | Trim level |
| `engineCylinders` | TEXT | Cylinder count |
| `engineDisplacement` | TEXT | Engine displacement (L) |
| `engineHP` | TEXT | Engine horsepower |
| `fuelType` | TEXT | Fuel type |
| `transmissionStyle` | TEXT | Transmission type |
| `manufacturer` | TEXT | Manufacturing company |
| `plantCity` | TEXT | Manufacturing city |
| `plantCountry` | TEXT | Country of manufacture |
| `plantState` | TEXT | Manufacturing state |
| `abs` | TEXT | ABS availability |
| `tpms` | TEXT | Tire pressure monitoring |
| `airBagLocFront` | TEXT | Front airbag location |
| `airBagLocSide` | TEXT | Side airbag location |
| `airBagLocCurtain` | TEXT | Curtain airbag location |
| `series` | TEXT | Vehicle series |
| `gvwr` | TEXT | Gross vehicle weight rating |
| `errorCode` | TEXT | Decode error code |
| `errorText` | TEXT | Decode error message |
| `timestamp` | INTEGER | Decode timestamp |
| `isFavorite` | INTEGER | Favorite flag |

---

## Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

### Test Structure

```
app/src/
├── test/                    # Unit tests
│   └── java/
│       └── com/example/vinexplorer/
│           ├── model/       # Model tests
│           └── viewmodel/   # ViewModel tests
│
└── androidTest/             # Instrumented tests
    └── java/
        └── com/example/vinexplorer/
            └── ui/          # Compose UI tests
```

---
