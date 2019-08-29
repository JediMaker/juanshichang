package com.example.juanshichang.bean

class MessageBean {
    data class MessageBeans(
        val `data`: Data,
        val errmsg: String,
        val errno: Int
    )

    data class Data(
        val message_list: List<Message>
    )

    data class Message(
        val content: String,
        val create_time: Long,
        val message_id: Int,
        val title: String,
        val type: String
    )
}