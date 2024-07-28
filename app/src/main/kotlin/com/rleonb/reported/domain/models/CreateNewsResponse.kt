package com.rleonb.reported.domain.models

import com.google.gson.annotations.SerializedName

data class CreateNewsResponse(

    @field:SerializedName("data")
    val data: CreatedData? = null
)

data class CreatedData(

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
)
