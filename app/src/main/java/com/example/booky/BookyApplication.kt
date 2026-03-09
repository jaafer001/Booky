package com.example.booky

import android.app.Application
import androidx.room.Room
import com.example.booky.data.api.OpenLibraryApi
import com.example.booky.data.local.AppDatabase
import com.example.booky.data.pref.UserPreferences
import com.example.booky.data.repository.BookRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookyApplication : Application() {

    lateinit var repository: BookRepository
    lateinit var userPreferences: UserPreferences

    override fun onCreate() {
        super.onCreate()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(OpenLibraryApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(OpenLibraryApi::class.java)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "booky-db"
        )
        .fallbackToDestructiveMigration()
        .build()

        userPreferences = UserPreferences(this)
        repository = BookRepository(api, db.bookDao())
    }
}
