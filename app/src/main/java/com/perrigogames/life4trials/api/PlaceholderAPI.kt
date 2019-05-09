package com.perrigogames.life4trials.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

//Data Model for Post
data class PlaceholderPosts(
    val id: Int,
    val userId : Int,
    val title: String,
    val body: String
)

//A retrofit Network Interface for the Api
interface PlaceholderApi{
    @GET("/posts")
    fun getPosts() : Deferred<Response<List<PlaceholderPosts>>>
}