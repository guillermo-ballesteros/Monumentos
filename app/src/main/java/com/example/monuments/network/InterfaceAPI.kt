package com.example.monuments.network

import retrofit2.http.GET

private const val monumentsURL = "monuments"

private const val favoritesURL = "favorites"

private const val commentsURL = "comments"


interface InterfaceAPI {
    @GET(monumentsURL)
    suspend fun listMonuments(): MonumentApiDTO

    @GET(favoritesURL)
    suspend fun listFavorites(): FavoriteApiDTO

    @GET(commentsURL)
    suspend fun listComments(): CommentApiDTO
}