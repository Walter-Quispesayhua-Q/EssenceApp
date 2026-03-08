package com.essence.essenceapp.feature.auth.login.data.mapper

import com.essence.essenceapp.feature.auth.login.data.dto.LoginApiDTO
import com.essence.essenceapp.feature.auth.login.domain.model.Login

fun Login.loginToDto(): LoginApiDTO {
    return LoginApiDTO(
        email = this.email,
        password = this.password
    )
}