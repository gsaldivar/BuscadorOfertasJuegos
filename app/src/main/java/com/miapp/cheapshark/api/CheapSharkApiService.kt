package com.miapp.cheapshark.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CheapSharkApiService {
    @GET("api/1.0/deals")
    suspend fun getGameDeals(
        @Query("title") gameTitle: String,
        @Query("limit") limit: Int = 1
    ): Response<List<GameDeal>>
}