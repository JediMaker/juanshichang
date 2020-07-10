package com.example.juanshichang.bean

class TokenBean {
    data class TokenBean(
        var `data`: Data,
        var err_code: Any,
        var err_msg: Any,
        var status: Boolean
    )

    data class Data(
        var error: String? = null,
        var code: String? = null,
        var error_description: String? = null,
        var access_token: String,
        var expires_in: Int,
        var refresh_token: String,
        var scope: Any,
        var token_type: String? = null
    )
}
