package com.essence.essenceapp.core.network.security

import okhttp3.CertificatePinner

object NetworkSecurity {

    private const val BACKEND_HOST =
        "essence-backend-api.braveglacier-4eb99e73.brazilsouth.azurecontainerapps.io"

    private val PINS: List<String> = emptyList()

    fun buildPinner(): CertificatePinner {
        if (PINS.isEmpty()) {
            return CertificatePinner.DEFAULT
        }
        val builder = CertificatePinner.Builder()
        PINS.forEach { pin ->
            builder.add(BACKEND_HOST, pin)
        }
        return builder.build()
    }
}
