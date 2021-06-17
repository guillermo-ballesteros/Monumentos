package com.example.monuments.network

import android.util.Log
import com.example.monuments.database.*
import com.example.monuments.domain.*
import com.example.monuments.repository.MainRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.annotations.SerializedName
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class MonumentApiDTO(
    @SerializedName("documents") val monuments: List<MonumentDTO>?
)

data class MonumentDTO(
    @SerializedName("name") val id: String?,
    @SerializedName("fields") val monument: MonumentDataDTO?,
    @SerializedName("createTime") val createTime: String?
)

data class MonumentDataDTO(
    @SerializedName("user") val user: StringType?,
    @SerializedName("name") val name: StringType?,
    @SerializedName("citY") val city: StringType?,
    @SerializedName("description") val description: StringType?,
    @SerializedName("urlExtraInformation") val urlExtraInformation: StringType?,
    @SerializedName("location") val location: LocationMapDTO?,
    @SerializedName("images") val images: ImagesDTO?
)


data class LocationDTO(
    @SerializedName("latitudE") val latitude: DoubleType?,
    @SerializedName("longitude") val longitude: DoubleType?
)

data class ImageDTO(
    @SerializedName("id") val id: StringType?,
    @SerializedName("url") val url: StringType?
)

data class FavoriteApiDTO(
    @SerializedName("documents") val favorites: List<FavoriteDTO>?
)

data class FavoriteDTO(
    @SerializedName("name") val id: String?,
    @SerializedName("fields") val favorite: FavoriteDataDTO

)

data class FavoriteDataDTO(
    @SerializedName("monumentId") val monumentId: StringType,
    @SerializedName("user") val user: StringType
)

data class CommentApiDTO(
    @SerializedName("documents") val comments: List<CommentDTO>?
)

data class CommentDTO(
    @SerializedName("name") val id: String?,
    @SerializedName("fields") val comment: CommentDataDTO,
    @SerializedName("createTime") val createTime: String?
)

data class CommentDataDTO(
    @SerializedName("rate") val rate: IntType,
    @SerializedName("monumentId") val monumentId: StringType,
    @SerializedName("comment") val comment: StringType,
    @SerializedName("user") val user: StringType
)

data class LocationMapDTO (
    @SerializedName("mapValue") val mapValue: LocationMapValueDTO?
)

data class LocationMapValueDTO (
    @SerializedName("fields") val fields: LocationDTO?
)

data class ValueDTO (
    @SerializedName("mapValue") val mapValue: ValueMapValueDTO?
)

data class ValueMapValueDTO (
    @SerializedName("fields") val fields: ImageDTO?
)


data class StringType (
    @SerializedName("stringValue") val stringValue: String?
)

data class IntType (
    @SerializedName("integerValue") val intValue: Int?
)

data class DoubleType (
    @SerializedName("doubleValue") val doubleValue: Double?
)

data class ImagesDTO (
    @SerializedName("arrayValue") val arrayValue: ArrayValueDTO?
)

data class ArrayValueDTO (
    @SerializedName("values") val values: List<ValueDTO?>?
)

fun toBO(monuments: List<MonumentDTO?>?, favorites: List<FavoriteDTO?>?, auth: FirebaseAuth): List<MonumentBO>{
    if (monuments != null) {
        return monuments.mapNotNull {
            MonumentBO(
                id = it?.id?.split("/")?.get(6)?: "Desconocido",
                user = it?.monument?.user?.stringValue?: "Desconocido",
                name = it?.monument?.name?.stringValue?: "Desconocido",
                city = it?.monument?.city?.stringValue?: "Desconocido",
                favorite = favoriteDTDToBO(favorites, it, auth),
                description = it?.monument?.description?.stringValue?: "Desconocido",
                urlExtraInformation = it?.monument?.urlExtraInformation?.stringValue,
                location = locationDTOToBO(it?.monument?.location?.mapValue?.fields),
                images = imagesDTOToBO(it?.monument?.images),
                createTime = stringToDate(it?.createTime)
            )
        }
    }
    return emptyList()
}

fun locationDTOToBO(location: LocationDTO?): LocationBO{
    return LocationBO(
        latitude = location?.latitude?.doubleValue?: -1.0,
        longitude = location?.longitude?.doubleValue?: -1.0
    )
}


fun imagesDTOToBO(images: ImagesDTO?): List<ImageBO> {
    return images?.arrayValue?.values.let {
        it?.mapNotNull {
            ImageBO(
                id = it?.mapValue?.fields?.id?.stringValue?: "Desconocido",
                url = it?.mapValue?.fields?.url?.stringValue?: "Desconocido"
            )
        }
    }?: emptyList()
}

fun toDao(monuments: List<MonumentDTO?>?, favorites: List<FavoriteDTO?>?, auth: FirebaseAuth): List<MonumentDao>{
    if (monuments != null) {
        return monuments.mapNotNull {
            MonumentDao(
                id = it?.id?.split("/")?.get(6)?: "Desconocido",
                user = it?.monument?.user?.stringValue?: "Desconocido",
                name = it?.monument?.name?.stringValue?: "Desconocido",
                city = it?.monument?.city?.stringValue?: "Desconocido",
                favorite =  favoriteDTDToDao(favorites, it, auth) ,
                description = it?.monument?.description?.stringValue?: "Desconocido",
                urlExtraInformation = it?.monument?.urlExtraInformation?.stringValue,
                location = locationDTOToDao(it?.monument?.location?.mapValue?.fields),
                createTime = stringToDate(it?.createTime)
            )
        }
    }
    return emptyList()
}


fun favoriteDTDToBO(favorites: List<FavoriteDTO?>?, monument: MonumentDTO?, auth: FirebaseAuth) :FavoriteBO {
    val list = favorites?.filter {
        val id = monument?.id?.split("/")?.get(6)
        it?.favorite?.monumentId?.stringValue == id
    }
    if (list != null) {
        val monumentFavorite = list.find {
            it?.favorite?.user?.stringValue == auth.currentUser?.email
        }
        if (monumentFavorite != null) {
            return FavoriteBO(
                id = monumentFavorite.id?.split("/")?.get(6)?: "Desconocido",
                isFavorite = true
            )
        }
    }

    return FavoriteBO(
        id = "Desconocido",
        isFavorite = false
    )
}

fun favoriteDTDToDao(favorites: List<FavoriteDTO?>?, monument: MonumentDTO?, auth: FirebaseAuth) : FavoriteDao {
    val list = favorites?.filter {
        val id = monument?.id?.split("/")?.get(6)
        it?.favorite?.monumentId?.stringValue == id
    }
    if (list != null) {
        val monumentFavorite = list.find {
            it?.favorite?.user?.stringValue == auth.currentUser?.email
        }
        if (monumentFavorite != null) {
            return FavoriteDao(
                favoriteId = monumentFavorite.id?.split("/")?.get(6)?: "Desconocido",
                isFavorite = true
            )
        }
    }

    return FavoriteDao(
        favoriteId = "Desconocido",
        isFavorite = false
    )
}
fun locationDTOToDao(location: LocationDTO?): LocationDao {
    return LocationDao(
        latitude = location?.latitude?.doubleValue?: -1.0,
        longitude = location?.longitude?.doubleValue?: -1.0
    )
}

fun imagesDTOToDao(monuments: List<MonumentDTO?>?): List<ImageDao> {
    val images: ArrayList<ImageDao> = ArrayList()
    if (monuments != null) {
        for (item in monuments){
            item?.monument?.images?.arrayValue?.values?.mapNotNull {
                images.add(
                    ImageDao(
                        id = it?.mapValue?.fields?.id?.stringValue?: "Desconocido",
                        monumentId = item.id?.split("/")?.get(6)?: "Desconocido",
                        url = it?.mapValue?.fields?.url?.stringValue?: "Desconocido"
                    )
                )
            }
        }
    }
    return images
}


fun commentsDTDToDao(comments: List<CommentDTO?>?): List<CommentDao> {
    if (comments != null) {
        return comments.mapNotNull {
            CommentDao(
                commentId = it?.id?.split("/")?.get(6)?: "Desconocido",
                user = it?.comment?.user?.stringValue?: "Desconocido",
                rate = it?.comment?.rate?.intValue?: -1,
                monumentId = it?.comment?.monumentId?.stringValue?: "Desconocido",
                comment = it?.comment?.comment?.stringValue?: "Desconocido",
                createTime = stringToDate(it?.createTime)
            )
        }
    }
    return emptyList()
}

fun stringToDate(date: String?): Date {
    if (date != null) {
        val initDate:  java.util.Date? = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault() )
            .parse(date)
        if (initDate != null) {
            Log.d(":::DATE", Date(initDate.time).toString())
            return Date(initDate.time)
        }
    }
    return Date(0)
}


