package ru.gidevent.androidapp.data.model.mainRecyclerviewModels

data class AdvertPreviewCard(
    val id: Long,
    val isFavourite: Boolean,
    val name: String,
    val categories: List<String>,
    val price: Int,
    val photoUrl: String
)
