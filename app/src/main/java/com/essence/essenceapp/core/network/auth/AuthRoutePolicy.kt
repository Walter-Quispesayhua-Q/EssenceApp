package com.essence.essenceapp.core.network.auth

object AuthRoutePolicy {

    private val publicExactPaths = setOf(
        "/api/v1/home",
        "/api/v1/search",
        "/api/v1/search/categories"
    )

    private val publicPrefixes = listOf(
        "/api/v1/login",
        "/api/v1/register"
    )

    fun isPublic(path: String): Boolean {
        return path in publicExactPaths || matchesAnyPrefix(path, publicPrefixes)
    }

    fun requiresAuth(path: String): Boolean = !isPublic(path)

    fun isAuthRoute(path: String): Boolean = matchesAnyPrefix(path, publicPrefixes)

    private fun matchesAnyPrefix(path: String, prefixes: List<String>): Boolean {
        return prefixes.any { prefix ->
            path == prefix || path.startsWith("$prefix/") || path.startsWith("$prefix?")
        }
    }
}