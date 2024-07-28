package com.rleonb.reported.api

import com.rleonb.reported.BuildConfig
import com.rleonb.reported.domain.models.CreateNewsResponse
import com.rleonb.reported.domain.models.CreatingNews
import com.rleonb.reported.domain.models.NewsResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

object RetrofitClient {
    private const val BASE_URL = BuildConfig.baseUrl

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}

interface ApiService {
    @GET("news")
    fun getNews(): Call<NewsResponse>

    @POST("news")
    fun postNews(
        @Body news: CreatingNews
    ): Call<CreateNewsResponse>
}