package com.example.movieapplication.themoviedb_module

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class TheMovieViewModel : ViewModel() {
    private val _theMovieList = mutableStateListOf<Result>()
    var errorMessage: String by mutableStateOf("")
    var isLoading: Boolean by mutableStateOf(false)
    val theMovieList: List<Result>
        get() = _theMovieList

    // Fetch movies from the API
    fun getMovies() {
        viewModelScope.launch {
            isLoading = true
            val apiService = TheMovieService.getInstance()
            try {
                _theMovieList.clear()
                val movies = apiService.getMovies().results
                _theMovieList.addAll(movies)
                Log.d("TheMovieViewModel", "Movies loaded: ${movies.map { it.id }}")
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("TheMovieViewModel", "Error loading movies: $errorMessage")
            } finally {
                isLoading = false
            }
        }
    }

    // Search for movies by query
    fun searchMovies(query: String) {
        if (query.isBlank()) {
            errorMessage = "Search query cannot be empty"
            return
        }

        viewModelScope.launch {
            isLoading = true
            val apiService = TheMovieService.getInstance()
            try {
                _theMovieList.clear()
                val movies = apiService.searchMovies(query).results
                if (movies.isEmpty()) {
                    errorMessage = "No movies found"
                } else {
                    _theMovieList.addAll(movies)
                    errorMessage = ""
                }
                Log.d("TheMovieViewModel", "Search results: ${movies.map { it.id }}")
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.e("TheMovieViewModel", "Error searching movies: $errorMessage")
            } finally {
                isLoading = false
            }
        }
    }

    // Retrieve a movie by its ID
    fun getMovieById(movieId: Long): Result? {
        val movie = _theMovieList.find { it.id == movieId }
        Log.d("TheMovieViewModel", "getMovieById: movieId=$movieId, movie=$movie")
        return movie
    }

    // Sort movies based on specified criteria
    fun sortMovies(sortBy: String, ascending: Boolean) {
        val sortedList = when (sortBy) {
            "popularity" -> _theMovieList.sortedBy { it.popularity }
            "release date" -> _theMovieList.sortedBy { it.releaseDate }
            "vote average" -> _theMovieList.sortedBy { it.voteAverage }
            else -> _theMovieList.toList()
        }
        if (!ascending) {
            _theMovieList.clear()
            _theMovieList.addAll(sortedList.reversed())
        } else {
            _theMovieList.clear()
            _theMovieList.addAll(sortedList)
        }
    }
}
