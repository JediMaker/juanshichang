package com.example.juanshichang.bean
class TokenBean{
    data class TokenBean(
        var `data`: Data,
        var err_code: Any,
        var err_msg: Any,
        var status: Boolean
    )

    data class Data(
        var access_token: String,
        var expires_in: Int,
        var refresh_token: String,
        var scope: Any,
        var token_type: String
    )
}
