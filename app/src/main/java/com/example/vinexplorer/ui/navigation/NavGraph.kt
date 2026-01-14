package com.example.vinexplorer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.vinexplorer.data.repository.VinRepository
import com.example.vinexplorer.ui.screens.VinDetailScreen
import com.example.vinexplorer.ui.screens.VinListScreen
import com.example.vinexplorer.ui.viewmodel.VinDetailViewModel
import com.example.vinexplorer.ui.viewmodel.VinListViewModel
import com.example.vinexplorer.ui.viewmodel.ViewModelFactory

/**
 * Main navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    repository: VinRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.VinList.route,
        modifier = modifier
    ) {
        composable(route = Screen.VinList.route) {
            val viewModel: VinListViewModel = viewModel(
                factory = ViewModelFactory(repository)
            )
            VinListScreen(
                viewModel = viewModel,
                onVinClick = { vin ->
                    navController.navigate(Screen.VinDetail.createRoute(vin))
                }
            )
        }

        composable(
            route = Screen.VinDetail.route,
            arguments = listOf(
                navArgument("vin") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vin = backStackEntry.arguments?.getString("vin") ?: ""
            val viewModel: VinDetailViewModel = viewModel(
                factory = ViewModelFactory(repository, vin)
            )
            VinDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

