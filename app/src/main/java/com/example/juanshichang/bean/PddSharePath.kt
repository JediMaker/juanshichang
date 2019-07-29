package com.example.juanshichang.bean
/**
 * @作者: yzq
 * @创建日期: 2019/7/26 13:04
 * @文件作用: 分享链接
 */
class PSP{
    data class PddSharePath(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var goods_promotion_url_generate_response: GoodsPromotionUrlGenerateResponse = GoodsPromotionUrlGenerateResponse()
    )

    data class GoodsPromotionUrlGenerateResponse(
        var goods_promotion_url_list: List<GoodsPromotionUrl> = listOf(),
        var request_id: String = ""
    )

    data class GoodsPromotionUrl(
        var goods_detail: Any? = null,
        var mobile_short_url: String = "",  //唤醒拼多多app的推广短链接
        var mobile_url: String = "",        //唤醒拼多多app的推广长链接
        var short_url: String = "",            //推广短链接
        var url: String = "",                   // 推广长链接
        var we_app_info: Any? = null,
        var we_app_web_view_short_url: String = "",    //唤起微信app推广短链接
        var we_app_web_view_url: String = "",       //唤起微信app推广链接
        var weibo_app_web_view_short_url: Any? = null,//微博推广短链接
        var weibo_app_web_view_url: Any? = null   //微博推广链接
    )
}
