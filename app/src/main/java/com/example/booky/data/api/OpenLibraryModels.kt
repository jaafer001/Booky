package com.example.booky.data.api

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("numFound") val numFound: Int,
    @SerializedName("docs") val docs: List<BookDoc>
)

data class BookDoc(
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("author_name") val authorNames: List<String>?,
    @SerializedName("first_publish_year") val firstPublishYear: Int?,
    @SerializedName("subject") val subjects: List<String>?,
    @SerializedName("cover_i") val coverI: Int?,
    @SerializedName("language") val languages: List<String>?,
    @SerializedName("ia") val ia: List<String>?,
    @SerializedName("format") val formats: List<String>?
) {
    val coverUrl: String?
        get() = coverI?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
    
    val authorName: String
        get() = authorNames?.joinToString(", ") ?: "Unknown Author"
}
