package com.example.juanshichang.bean

/**
 *  商品列表Bean类
 *  搜索列表
 */
class CLB{
    data class CargoListBean(
        val `data`: Data,
        val errmsg: String,
        val errno: Int
    )

    data class Data(
        val goods_search_response: GoodsSearchResponse
    )

    data class GoodsSearchResponse(
        val goods_list: List<Goods>,
        val request_id: String,
        val total_count: Int
    )

    data class Goods(
        val activity_type: Int,
        val avg_desc: Any,
        val avg_lgst: Any,
        val avg_serv: Any,
        val cat_id: Any,
        val cat_ids: List<Int>,
        val category_id: Int,  //种类id
        val category_name: String, //种类名称
        val clt_cpn_batch_sn: Any,
        val clt_cpn_discount: Any,
        val clt_cpn_end_time: Any,
        val clt_cpn_min_amt: Any,
        val clt_cpn_quantity: Any,
        val clt_cpn_remain_quantity: Any,
        val clt_cpn_start_time: Any,
        val coupon_discount: Int,
        val coupon_end_time: Int,
        val coupon_min_order_amount: Int,
        val coupon_remain_quantity: Int,
        val coupon_start_time: Int,
        val coupon_total_quantity: Int,
        val cps_sign: Any,
        val create_at: Any,
        val crt_rf_ordr_rto1m: Double,
        val desc_pct: Any,
        val desc_txt: String,
        val goods_desc: String, //商品降序? 介绍
        val goods_eval_count: Int,
        val goods_eval_score: Int,
        val goods_gallery_urls: Any,
        val goods_id: Long,   //商品id
        val goods_image_url: String,
        val goods_name: String,  //商品 名称... 标题
        val goods_thumbnail_url: String,
        val has_coupon: Boolean,  // 有无优惠劵
        val has_mall_coupon: Boolean,
        val lgst_pct: Any,
        val lgst_txt: String,
        val mall_coupon_discount_pct: Int,
        val mall_coupon_end_time: Int,
        val mall_coupon_id: Int,
        val mall_coupon_max_discount_amount: Int,
        val mall_coupon_min_order_amount: Int,
        val mall_coupon_remain_quantity: Int,
        val mall_coupon_start_time: Int,
        val mall_coupon_total_quantity: Int,
        val mall_cps: Int,
        val mall_id: Int, //购物id
        val mall_name: String,//店铺名称
        val mall_rate: Int,
        val merchant_type: Int,
        val min_group_price: Int,
        val min_normal_price: Int,
        val opt_id: Int,
        val opt_ids: List<Int>,
        val opt_name: String,
        val promotion_rate: Int,
        val sales_tip: String,
        val serv_pct: Any,
        val serv_txt: String,
        val service_tags: List<Int>
    )
}

/*class CargoListBeans{
    companion object{
        data class CargoListBean(
            val `data`: Data,
            val errmsg: String,
            val errno: Int
        )

        data class Data(
            val goods_search_response: GoodsSearchResponse
        )

        data class GoodsSearchResponse(
            val goods_list: List<Goods>,
            val request_id: String,
            val total_count: Int
        )

        data class Goods(
            val activity_type: Int,
            val avg_desc: Any,
            val avg_lgst: Any,
            val avg_serv: Any,
            val cat_id: Any,
            val cat_ids: List<Int>,
            val category_id: Int,  //种类id
            val category_name: String, //种类名称
            val clt_cpn_batch_sn: Any,
            val clt_cpn_discount: Any,
            val clt_cpn_end_time: Any,
            val clt_cpn_min_amt: Any,
            val clt_cpn_quantity: Any,
            val clt_cpn_remain_quantity: Any,
            val clt_cpn_start_time: Any,
            val coupon_discount: Int,
            val coupon_end_time: Int,
            val coupon_min_order_amount: Int,
            val coupon_remain_quantity: Int,
            val coupon_start_time: Int,
            val coupon_total_quantity: Int,
            val cps_sign: Any,
            val create_at: Any,
            val crt_rf_ordr_rto1m: Double,
            val desc_pct: Any,
            val desc_txt: String,
            val goods_desc: String, //商品降序? 介绍
            val goods_eval_count: Int,
            val goods_eval_score: Int,
            val goods_gallery_urls: Any,
            val goods_id: Long,   //商品id
            val goods_image_url: String,
            val goods_name: String,  //商品 名称... 标题
            val goods_thumbnail_url: String,
            val has_coupon: Boolean,
            val has_mall_coupon: Boolean,
            val lgst_pct: Any,
            val lgst_txt: String,
            val mall_coupon_discount_pct: Int,
            val mall_coupon_end_time: Int,
            val mall_coupon_id: Int,
            val mall_coupon_max_discount_amount: Int,
            val mall_coupon_min_order_amount: Int,
            val mall_coupon_remain_quantity: Int,
            val mall_coupon_start_time: Int,
            val mall_coupon_total_quantity: Int,
            val mall_cps: Int,
            val mall_id: Int, //购物id
            val mall_name: String,//店铺名称
            val mall_rate: Int,
            val merchant_type: Int,
            val min_group_price: Int,
            val min_normal_price: Int,
            val opt_id: Int,
            val opt_ids: List<Int>,
            val opt_name: String,
            val promotion_rate: Int,
            val sales_tip: String,
            val serv_pct: Any,
            val serv_txt: String,
            val service_tags: List<Int>
        )
    }
}*/

