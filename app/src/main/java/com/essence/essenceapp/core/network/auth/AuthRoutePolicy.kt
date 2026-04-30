package com.essence.essenceapp.core.network.auth

object AuthRoutePolicy {

    private val publicExactPaths = setOf(
        "/api/v1/login",
        "/api/v1/register",
        "/api/v1/home",
        "/api/v1/search"
    )

    fun isPublic(path: String): Boolean {
        return path in publicExactPaths
    }

    fun requiresAuth(path: String): Boolean = !isPublic(path)
}