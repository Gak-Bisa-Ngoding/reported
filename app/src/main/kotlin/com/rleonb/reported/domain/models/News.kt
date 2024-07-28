package com.rleonb.reported.domain.models

data class News(
    val title: String,
    val address: String,
    val imageUrl: String,
    val createdAt: String,
)

data class CreatingNews(
    val title: String,
    val description: String,
    val address: String,
    val image: String,
)