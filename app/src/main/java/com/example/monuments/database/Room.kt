package com.example.monuments.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface MonumentsDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMonuments(monuments: List<MonumentDao>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllImages(images: List<ImageDao>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentDao>)

    @Query("SELECT * FROM comments WHERE monumentId == :id")
    fun getComments(id: String): Flow<List<CommentDao>>

    @Transaction
    @Query("SELECT * FROM monuments WHERE user == :user")
    fun getMyMonuments(user: String): Flow<List<MonumentWithImagesDao>>

    @Query("DELETE FROM monuments")
    fun deleteAllMonuments()

    @Query("DELETE FROM monuments WHERE id == :id")
    fun deleteMonument(id: String)

    @Query("SELECT * FROM monuments WHERE id == :id")
    fun getMonument(id: String): MonumentWithImagesDao

    @Query("DELETE FROM images_monument")
    fun deleteAllImages()

    @Query("DELETE FROM comments")
    fun deleteAllComments()

    @Transaction
    @Query("SELECT * FROM monuments")
    fun getMonumentsWithImages(): List<MonumentWithImagesDao>

    @Query("SELECT * FROM monuments WHERE isFavorite == 1")
    fun getFavorites(): Flow<List<MonumentWithImagesDao>>

}

@Database(entities = [MonumentDao::class, ImageDao::class, CommentDao::class], version = 13)
@TypeConverters(Converters::class)
abstract class MonumentsRoomDatabase: RoomDatabase() {

    abstract fun monumentsDao(): MonumentsDao
}