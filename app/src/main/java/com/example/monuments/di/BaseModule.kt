package com.example.monuments.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.monuments.database.MonumentsDao
import com.example.monuments.database.MonumentsRoomDatabase
import com.example.monuments.network.InterfaceAPI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BaseModule {

    private val baseURL = "https://firestore.googleapis.com/v1/projects/monuments-e4047/databases/(default)/documents/"

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    fun provideGsonConverter(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun providesRetrofit(gsonConverter: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(gsonConverter)
            .build()
    }

    @Singleton
    @Provides
    fun apiProvider(retrofit: Retrofit): InterfaceAPI {
        return retrofit.create(InterfaceAPI::class.java)
    }

    @Singleton
    @Provides
    fun roomDatabaseProvider(@ApplicationContext context: Context): MonumentsRoomDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MonumentsRoomDatabase::class.java,
            "monument_database"
        ).fallbackToDestructiveMigration().build()
    }


    @Singleton
    @Provides
    fun providesDao(db: MonumentsRoomDatabase): MonumentsDao {
        return db.monumentsDao()
    }

    @Singleton
    @Provides
    fun firestoreDatabaseProvider(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun storageProvider(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }

    @Singleton
    @Provides
    fun authProvider(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}