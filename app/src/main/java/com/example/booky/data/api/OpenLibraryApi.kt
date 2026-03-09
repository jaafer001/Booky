package com.example.booky.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String? = null,
        @Query("title") title: String? = null,
        @Query("author") author: String? = null,
        @Query("subject") subject: String? = null,
        @Query("language") language: String = "ara", // Default to Arabic as requested
        @Query("page") page: Int = 1
    ): SearchResponse

    companion object {
        const val BASE_URL = "https://openlibrary.org/"
    }
}
