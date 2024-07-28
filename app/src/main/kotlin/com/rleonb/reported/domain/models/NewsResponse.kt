package com.rleonb.reported.domain.models

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

data class NewsResponse(

    @field:SerializedName("data")
    val data: Data? = null
)

data class NewsItem(

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
) {
    fun toOverview(): News {
        return News(
            title = title.toString(),
            address = address.toString(),
            imageUrl = imageUrl.toString(),
            createdAt = parseToIndonesianLocale(createdAt.toString())
        )
    }
}

data class Data(

    @field:SerializedName("news")
    val news: List<NewsItem?>? = null
)

fun parseToIndonesianLocale(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS ZZZZZ", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    val date = inputFormat.parse(dateString)
    return outputFormat.format(date!!)
}