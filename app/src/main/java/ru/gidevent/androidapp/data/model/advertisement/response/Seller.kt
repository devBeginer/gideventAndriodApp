package ru.gidevent.RestAPI.model


data class Seller(
        val sellerId: Long,
        val firstName: String,
        val lastName: String,
        val photo: String,
        val about: String
)

//TODO сделать наследование от User (ссылку на User)
