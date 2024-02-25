package ru.gidevent.RestAPI.auth

data class LoginBodyResponse(
        val type: String = "Bearer",
        val accessToken:String,
        val refreshToken:String
)