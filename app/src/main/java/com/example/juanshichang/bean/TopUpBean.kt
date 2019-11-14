package com.example.juanshichang.bean

class TopUpBean {
    constructor()
    constructor(topPrice:String,botPrice:String){
        this.topPrice = topPrice
        this.botPrice = botPrice
    }
    var topPrice:String = ""
    var botPrice:String = ""
}