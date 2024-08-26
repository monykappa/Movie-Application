package com.example.movieapplication.themoviedb_module

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter



@Composable
fun MovieDetailTopBar(title: String, onBackClicked: () -> Unit) {
    TopAppBar(
        title = { Text(title, color = Color.White) },
        navigationIcon = {
            IconButton(onClick = { onBackClicked() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        backgroundColor = Color(0xFF27272A)
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun MovieDetailScreen(movieId: Long, vm: TheMovieViewModel, onBackClicked: () -> Unit) {
    val movie = remember { vm.getMovieById(movieId) }
    Log.d("MovieDetailScreen", "Movie ID: $movieId, Movie: $movie")

    Scaffold(
        topBar = {
            MovieDetailTopBar(title = movie?.title ?: "Movie Details", onBackClicked = onBackClicked)
        },
        content = { padding ->
            if (movie != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF27272A))
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = rememberImagePainter(movie.fullPosterPath),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(Color(0x80000000))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = movie.title,
                                style = TextStyle(
                                    fontFamily = FontFamily.Cursive,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    shadow = Shadow(
                                        color = Color.Black,
                                        offset = Offset(2f, 2f),
                                        blurRadius = 4f
                                    )
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    MovieInfoItem("", movie.overview, isOverview = true)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 1.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            MovieInfoItem("Release Date", movie.releaseDate)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            MovieInfoItem("Language", movie.originalLanguage)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            MovieInfoItem("Popularity", movie.popularity.toString())
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            MovieInfoItem("Vote Count", movie.voteCount.toString(), true)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            MovieInfoItem("Vote Average", "${movie.voteAverage}/10")
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            MovieInfoItem("Adult", if (movie.adult) "Yes" else "No")
                        }


                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF27272A))
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Movie not found", style = MaterialTheme.typography.body1.copy(color = Color.White))
                }
            }
        }
    )
}

@Composable
fun MovieInfoItem(label: String, value: String, isOverview: Boolean = false) {
    Column(
        modifier = Modifier.padding(
            start = 10.dp,
            end = 10.dp,
            top = if (isOverview) 0.dp else 8.dp,
            bottom = 8.dp
        )
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE0E0E0)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Text(
            text = value,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                color = Color.White,
                lineHeight = 24.sp
            ),
            textAlign = if (isOverview) TextAlign.Justify else TextAlign.Start,
            modifier = Modifier
                .background(Color(0xFF181818), shape = RoundedCornerShape(5.dp))
                .padding(15.dp)
                .fillMaxWidth()
        )

    }
}
