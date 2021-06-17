package com.example.monuments.database

import androidx.room.*
import com.example.monuments.domain.*
import java.sql.Date


@Entity(tableName = "monuments")
data class MonumentDao(
        @PrimaryKey
        val id: String,
        val user: String,
        val name: String,
        val city: String,
        val description: String,
        val urlExtraInformation: String?,
        @Embedded
        val favorite: FavoriteDao,
        @Embedded
        val location: LocationDao,
        val createTime: Date
)

data class LocationDao(
        val latitude: Double,
        val longitude: Double
)

data class FavoriteDao(
        val favoriteId: String,
        val isFavorite: Boolean
)

@Entity(tableName = "images_monument")
data class ImageDao(
        @PrimaryKey
        val id: String,
        val monumentId: String,
        val url: String
)

data class MonumentWithImagesDao(
    @Embedded
    val monument: MonumentDao,
    @Relation(
            parentColumn = "id",
            entityColumn = "monumentId"
    )
    val images: List<ImageDao>
)

@Entity(tableName = "comments")
data class CommentDao(
        @PrimaryKey
        val commentId: String,
        val monumentId: String,
        val user: String,
        val rate: Int,
        val comment: String,
        val createTime: Date
)

class Converters {
        @TypeConverter
        fun fromTimestamp(value: Long): Date {
                return Date(value)
        }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long {
                return date?.time?: 0
        }
}


fun List<MonumentWithImagesDao>.toBO(): List<MonumentBO> {

        return mapNotNull {
                it.toBO()
        }
}

fun MonumentWithImagesDao.toBO(): MonumentBO {
        return MonumentBO(
                id = monument.id,
                user = monument.user,
                name = monument.name,
                city = monument.city,
                description = monument.description,
                favorite = favoriteEntityToBO(monument.favorite),
                urlExtraInformation = monument.urlExtraInformation,
                location = locationEntityToBO(monument.location),
                images = imagesEntityToBO(images),
                createTime = monument.createTime
        )
}

fun favoriteEntityToBO(favorite: FavoriteDao): FavoriteBO {
        return FavoriteBO(
                id = favorite.favoriteId,
                isFavorite = favorite.isFavorite
        )
}

fun locationEntityToBO(location: LocationDao): LocationBO {
        return LocationBO(
                latitude = location.latitude,
                longitude = location.longitude
        )
}

fun imagesEntityToBO(images: List<ImageDao>): List<ImageBO> {
        return images.map { entity ->
                        ImageBO(
                                id = entity.id,
                                url = entity.url
                        )

        }
}

fun List<CommentDao>.commentsToBO(): List<CommentBO> {
        return mapNotNull {
                CommentBO(
                        id = it.commentId,
                        user = it.user,
                        rate = it.rate,
                        monumentId = it.monumentId,
                        comment = it.comment,
                        createTime = it.createTime
                )
        }
}