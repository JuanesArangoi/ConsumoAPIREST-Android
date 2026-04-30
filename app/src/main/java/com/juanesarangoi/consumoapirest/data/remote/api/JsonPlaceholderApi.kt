package com.juanesarangoi.consumoapirest.data.remote.api

import com.juanesarangoi.consumoapirest.data.remote.dto.PostDto
import com.juanesarangoi.consumoapirest.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

interface JsonPlaceholderApi {
    
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
    
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): PostDto
    
    @GET("users")
    suspend fun getUsers(): List<UserDto>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto
}
