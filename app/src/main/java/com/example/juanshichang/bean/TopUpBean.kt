package com.example.juanshichang.bean

class TopUpBean {
    constructor()
    constructor(topPrice:String,priPrice:String ="",id:String="",botPrice:String = ""){
        this.topPrice = topPrice
        this.botPrice = botPrice
        this.priPrice = priPrice
        this.id = id
    }
    var topPrice:String = ""
    var botPrice:String = ""
    var priPrice:String = ""
    var id:String =""
}