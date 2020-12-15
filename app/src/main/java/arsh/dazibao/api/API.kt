package com.example.arshkotlin9.api

import arsh.dazibao.dto.AuthRequestParams
import arsh.dazibao.dto.IdeaRequestDto
import arsh.dazibao.dto.RegistrationRequestParams
import arsh.dazibao.model.Attachment
import arsh.dazibao.model.Idea
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*


data class Token(val token: String)



interface API {
    @POST("api/v1/authentication")
    suspend fun authenticate(@Body authRequestParams: AuthRequestParams): Response<Token>

    @POST("api/v1/registration")
    suspend fun register(@Body registrationRequestParams: RegistrationRequestParams): Response<Token>

    @GET("api/v1/posts/recent")
    suspend fun getRecent(): Response<List<Idea>>

    @GET("api/v1/posts/after/{id}")
    suspend fun after(@Path("id") id: Long): Response<List<Idea>>

    @GET("api/v1/posts/before/{id}")
    suspend fun before(@Path("id") id: Long): Response<List<Idea>>

    @GET("api/v1/posts")
    suspend fun getAllPosts(): Response<List<Idea>>

    @POST("api/v1/posts/like/{id}")
    suspend fun likePost(@Path("id") id: Long): Response<Idea>

    @POST("api/v1/posts/dislike/{id}")
    suspend fun dislikePost(@Path("id") id: Long): Response<Idea>

    @POST("api/v1/posts")
    suspend fun createPost(@Body createPostRequest: IdeaRequestDto): Response<Idea>

    /*@POST("api/v1/posts/share/{id}")
    suspend fun sharePost(
        @Path("id") id: Long,
        @Body createPostRequest: PostRequest
    ): Response<Idea>*/

    @Multipart
    @POST("api/v1/media")
    suspend fun uploadImage(@Part file: MultipartBody.Part):
            Response<Attachment>

}