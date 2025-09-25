package com.miapp.cheapshark.api

import retrofit2.Response
import retrofit2.http.GET

interface MindicadorApiService {
    @GET("api")
    suspend fun getDolarPrice(): Response<MindicadorResponse>
}