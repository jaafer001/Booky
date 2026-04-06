package com.example.booky.data.api

fun BookDoc.getReadUrl(): String? {
    val iaId = ia?.firstOrNull() ?: return null
    return "https://archive.org/details/$iaId/mode/2up"
}

fun BookDoc.getDownloadUrl(): String? {
    val iaId = ia?.firstOrNull() ?: return null
    return "https://archive.org/download/$iaId/$iaId.pdf"
}
