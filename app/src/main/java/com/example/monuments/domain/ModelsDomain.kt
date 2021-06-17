package com.example.monuments.domain

import com.example.monuments.database.FavoriteDao
import com.example.monuments.database.LocationDao
import com.example.monuments.database.MonumentDao
import java.sql.Date

data class MonumentBO(
        val id: String,
        val user: String,
        val name: String,
        val city: String,
        val description: String,
        val urlExtraInformation: String?,
        val location: LocationBO,
        val images: List<ImageBO>,
        val favorite: FavoriteBO,
        val createTime: Date
)

data class LocationBO(
        val latitude: Double,
        val longitude: Double
)

data class ImageBO(
    val id: String,
    val url: String
)

data class FavoriteBO(
        val id: String,
        val isFavorite: Boolean
)

data class CommentBO(
        val id: String,
        val monumentId: String,
        val user: String,
        val rate: Int,
        val comment: String,
        val createTime: Date
)

fun MonumentBO.toDao(monument: MonumentBO): MonumentDao {
        return MonumentDao(
                id = monument.id,
                user = monument.user,
                name = monument.name,
                city = monument.city,
                favorite =  favoriteToDao(monument.favorite) ,
                description = monument.description,
                urlExtraInformation = monument.urlExtraInformation,
                location = locationToDao(monument.location),
                createTime = monument.createTime
        )
}

fun favoriteToDao(favorite: FavoriteBO): FavoriteDao {
        return FavoriteDao(
                favoriteId = favorite.id,
                isFavorite = favorite.isFavorite
        )
}

fun locationToDao(location: LocationBO): LocationDao {
        return LocationDao(
                latitude = location.latitude,
                longitude = location.longitude
        )
}


