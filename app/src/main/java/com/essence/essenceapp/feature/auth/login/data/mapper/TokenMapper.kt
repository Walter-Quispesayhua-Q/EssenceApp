package com.essence.essenceapp.feature.auth.login.data.mapper

import com.essence.essenceapp.feature.auth.login.data.dto.TokenDTO
import com.essence.essenceapp.feature.auth.login.domain.model.Token

fun TokenDTO.tokenToDomain(): Token {
    return Token(token = token)
}