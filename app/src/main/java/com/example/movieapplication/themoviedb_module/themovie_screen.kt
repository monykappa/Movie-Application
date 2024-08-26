package com.example.movieapplication.themoviedb_module

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.movieapplication.R
import androidx.compose.material.Text
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument


@Composable
fun SortingOptions(vm: TheMovieViewModel) {
    var selectedSortBy by remember { mutableStateOf("popularity") }
    var isAscending by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        DropdownMenu(sortOptions = listOf("popularity", "release date", "vote average"), selectedSortBy) { newSortBy ->
            selectedSortBy = newSortBy
        }
        Row {
            RadioButton(selected = isAscending, onClick = { isAscending = true })
            Text("Ascending")
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(selected = !isAscending, onClick = { isAscending = false })
            Text("Descending")
        }
        Button(onClick = { vm.sortMovies(selectedSortBy, isAscending) }) {
            Text("Sort")
        }
    }
}

@Composable
fun DropdownMenu(sortOptions: List<String>, selectedSortBy: String, onSortSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(text = selectedSortBy, modifier = Modifier.clickable { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            sortOptions.forEach { sortOption ->
                DropdownMenuItem(onClick = {
                    onSortSelected(sortOption)
                    expanded = false
                }) {
                    Text(text = sortOption)
                }
            }
        }
    }
}

@Composable
fun CustomTopBar(title: String, vm: TheMovieViewModel, isGridLayout: Boolean, onLayoutToggle: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedSortBy by remember { mutableStateOf("popularity") }
    var isAscending by remember { mutableStateOf(true) }
    val sortOptions = listOf("popularity", "release date", "vote average")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(Color(0xFF27272A))
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
        // Dropdown menu for sorting
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.Sort, contentDescription = "Sort", tint = Color.White)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sortOptions.forEach { sortOption ->
                    DropdownMenuItem(onClick = {
                        selectedSortBy = sortOption
                        expanded = false
                        vm.sortMovies(sortOption, isAscending)
                    }) {
                        Text(sortOption)
                    }
                }
            }
        }
        IconButton(onClick = {
            isAscending = !isAscending
            vm.sortMovies(selectedSortBy, isAscending)
        }) {
            Icon(
                if (isAscending) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                contentDescription = "Toggle Ascending/Descending",
                tint = Color.White
            )
        }

        Image(
            painter = painterResource(id = if (isGridLayout) R.drawable.list else R.drawable.grid),
            contentDescription = if (isGridLayout) "Switch to List View" else "Switch to Grid View",
            modifier = Modifier
                .size(20.dp)
                .clickable { onLayoutToggle() },
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieApp(vm: TheMovieViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) {
        NavHost(navController = navController, startDestination = "movieList") {
            composable("movieList") {
                TheMovieScreen(vm = vm, navController = navController)
            }
            composable("search") {
                SearchScreen(vm = vm, navController = navController)
            }
//            composable("favorites") {
//                FavoriteScreen(vm = vm)
//            }
            composable(
                "movieDetail/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.LongType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getLong("movieId") ?: -1
                MovieDetailScreen(
                    movieId = movieId,
                    vm = vm,
                    onBackClicked = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Favorites
        )
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(item.icon, contentDescription = item.title)
                },
                label = { Text(item.title) },
                selected = navController.currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("movieList", Icons.Default.Home, "Home")
    object Search : BottomNavItem("search", Icons.Default.Search, "Search")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favorites")
}




@Composable
fun TheMovieScreen(vm: TheMovieViewModel, navController: NavHostController) {
    var isGridLayout by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        vm.getMovies()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF27272A))
    ) {
        CustomTopBar(
            title = "TheMovieDB",
            vm = vm,
            isGridLayout = isGridLayout,
            onLayoutToggle = { isGridLayout = !isGridLayout }
        )
        TheMovieBody(vm, isGridLayout, navController)
    }
}

@Composable
fun TheMovieBody(vm: TheMovieViewModel, isGridLayout: Boolean, navController: NavHostController) {
    if (vm.isLoading) {
        CircularProgressIndicator(color = Color.Blue)
    } else if (vm.errorMessage.isNotEmpty()) {
        Text(vm.errorMessage, color = Color.Red)
    } else {
        if (isGridLayout) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                items(vm.theMovieList) { movie ->
                    TheMovieItem(movie, isGridLayout, navController)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                items(vm.theMovieList) { movie ->
                    TheMovieItem(movie, isGridLayout, navController)
                }
            }
        }
    }
}



@Composable
fun TheMovieItem(item: Result, isGridLayout: Boolean, navController: NavHostController) {
    val navigateToDetail = {
        Log.d("TheMovieItem", "Navigating to movieDetail/${item.id}")
        navController.navigate("movieDetail/${item.id}")
    }

    if (isGridLayout) {
        Column(
            modifier = Modifier
                .clickable(onClick = navigateToDetail)
                .padding(8.dp)
                .background(Color(0xFF27272A), RoundedCornerShape(8.dp))
                .fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(item.fullPosterPath),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(4.dp)) {
                Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.releaseDate, color = Color.White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = "Rating Star",
                        modifier = Modifier.size(12.dp)
                    )
                    Text("${item.voteAverage}/10", color = Color.Yellow, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .clickable(onClick = navigateToDetail)
                .padding(0.dp)
                .background(Color(0xFF27272A), RoundedCornerShape(8.dp))
                .fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(item.fullPosterPath),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Top),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxHeight()
                    .align(Alignment.Top)
            ) {
                Text(
                    item.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    item.releaseDate,
                    color = Color.White,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = "Rating Star",
                        modifier = Modifier.size(12.dp)
                    )
                    Text("${item.voteAverage}/10", color = Color.Yellow, fontSize = 12.sp)
                }
            }
        }
    }
}
