package com.example.juanshichang.bean

class MainRecyclerBean {
    data class MainRecyclerBeans(
        var `data`: Data = Data(),
        var errmsg: String = "",
        var errno: Int = 0
    )

    data class Data(
        var theme_list: List<Theme> = listOf()
    )

    data class Theme(
        var theme_goods_list: List<ThemeGoods> = listOf(),
        var theme_goods_servicer: String = "",
        var theme_id: Int = 0,
        var theme_image_url: String = "",
        var theme_name: String = "",
        var theme_order: Int = 0
    )

    data class ThemeGoods(
        var activity_type: Any? = null,
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
        var coupon_min_order_amount: Int = 0,
        var coupon_remain_quantity: Int = 0,
        var coupon_start_time: Int = 0,
        var coupon_total_quantity: Int = 0,
        var cps_sign: Any? = null,
        var create_at: Any? = null,
        var crt_rf_ordr_rto1m: Any? = null,
        var desc_txt: String? = null,
        var goods_desc: String = "",
        var goods_gallery_urls: Any? = null,
        var goods_id: Long = 0,
        var goods_image_url: String = "",
        var goods_name: String = "",
        var goods_thumbnail_url: String = "",
        var has_coupon: Boolean = false,
        var has_mall_coupon: Boolean = false,
        var lgst_txt: String? = null,
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
        var promotion_rate: Int = 0,
        var sales_tip: String = "",
        var serv_txt: String? = null,
        var service_tags: Any? = null,
        var zs_duo_id: Int = 0
    )
}
