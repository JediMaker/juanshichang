package com.example.juanshichang.bean

/**
 * @作者: yzq
 * @创建日期: 2019/7/26 10:33
 * @文件作用: 这是商品详情
 */
class SDB{
    data class SearchDetailBean(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var goods_detail_response: GoodsDetailResponse = GoodsDetailResponse()
    )

    data class GoodsDetailResponse(
        var goods_details: List<GoodsDetail> = listOf(),
        var request_id: String = ""
    )

    data class GoodsDetail(
        var avg_desc: Any? = null,
        var avg_lgst: Any? = null,
        var avg_serv: Any? = null,
        var cat_id: Any? = null,
        var cat_ids: List<Int> = listOf(),
        var category_id: Int = 0,
        var category_name: String = "",
        var clt_cpn_batch_sn: Any? = null,
        var clt_cpn_discount: Any? = null,
        var clt_cpn_end_time: Any? = null,
        var clt_cpn_min_amt: Any? = null,
        var clt_cpn_quantity: Any? = null,
        var clt_cpn_remain_quantity: Any? = null,
        var clt_cpn_start_time: Any? = null,
        var coupon_discount: Int = 0,
        var coupon_end_time: Int = 0,
        var coupon_id: Int = 0,
        var coupon_min_order_amount: Int = 0,
        var coupon_remain_quantity: Int = 0,
        var coupon_start_time: Int = 0,
        var coupon_total_quantity: Int = 0,
        var create_at: Any? = null,
        var crt_rf_ordr_rto1m: Double = 0.0,
        var desc_pct: Any? = null,
        var desc_txt: String = "",
        var goods_desc: String = "",
        var goods_eval_count: Int = 0,
        var goods_eval_score: Int = 0,
        var goods_gallery_urls: List<String> = listOf(),
        var goods_id: Long = 0,
        var goods_image_url: String = "",
        var goods_name: String = "",
        var goods_thumbnail_url: String = "",
        var has_coupon: Boolean = false,
        var has_mall_coupon: Boolean = false,
        var lgst_pct: Any? = null,
        var lgst_txt: String = "",
        var mall_coupon_discount_pct: Int = 0,
        var mall_coupon_end_time: Int = 0,
        var mall_coupon_id: Int = 0,
        var mall_coupon_max_discount_amount: Int = 0,
        var mall_coupon_min_order_amount: Int = 0,
        var mall_coupon_remain_quantity: Int = 0,
        var mall_coupon_start_time: Int = 0,
        var mall_coupon_total_quantity: Int = 0,
        var mall_cps: Int = 0,
        var mall_id: Int = 0,
        var mall_name: String = "",
        var mall_rate: Int = 0,
        var merchant_type: Int = 0,
        var min_group_price: Int = 0,
        var min_normal_price: Int = 0,
        var opt_id: Int = 0,
        var opt_ids: List<Int> = listOf(),
        var opt_name: String = "",
        var plan_type: Int = 0,
        var plan_type_all: Int = 0,
        var promotion_rate: Int = 0,
        var sales_tip: String = "",
        var serv_pct: Any? = null,
        var serv_txt: String = "",
        var service_tags: List<Int> = listOf()
    )
}
