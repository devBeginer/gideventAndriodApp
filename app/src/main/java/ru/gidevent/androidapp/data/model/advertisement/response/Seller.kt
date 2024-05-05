package ru.gidevent.androidapp.data.model.advertisement.response


data class Seller(
        val sellerId: Long,
        val firstName: String,
        val lastName: String,
        val photo: String,
        val about: String,
        val isVerified: Boolean
)

//TODO сделать наследование от User (ссылку на User)
