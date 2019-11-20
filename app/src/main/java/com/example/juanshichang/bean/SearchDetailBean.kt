package com.example.juanshichang.bean

/**
 * @作者: yzq
 * @创建日期: 2019/7/26 10:33
 * @文件作用: 这是拼多多商品详情
 */
class SDB {
    data class SearchDetailBean(
        val `data`: Data,
        val errmsg: String,
        val errno: Int
    )

    data class Data(
        val goods_detail_response: GoodsDetailResponse
    )

    data class GoodsDetailResponse(
        val goods_details: List<GoodsDetail>
    )

    data class GoodsDetail(
        val cat_id: Any,
        val cat_ids: List<Int>,
        val category_id: Int,
        val category_name: String,
        val clt_cpn_batch_sn: String,
        val clt_cpn_discount: Int,
        val clt_cpn_end_time: Int,
        val clt_cpn_min_amt: Int,
        val clt_cpn_quantity: Int,
        val clt_cpn_remain_quantity: Int,
        val clt_cpn_start_time: Int,
        val coupon_discount: Int,
        val coupon_end_time: Int,
        val coupon_id: Long,
        val coupon_min_order_amount: Int,
        val coupon_remain_quantity: Int,
        val coupon_start_time: Int,
        val coupon_total_quantity: Int,
        val create_at: Any,
        val crt_rf_ordr_rto1m: Double,
        val desc_txt: String,
        val goods_desc: String,
        val goods_gallery_urls: List<String>,
        val goods_id: Long,
        val goods_image_url: String,
        val goods_name: String,
        val goods_servicer: String,
        val goods_thumbnail_url: String,
        val has_coupon: Boolean,
        val has_mall_coupon: Boolean,
        val lgst_txt: String,
        val mall_coupon_discount_pct: Int,
        val mall_coupon_end_time: Int,
        val mall_coupon_id: Long,
        val mall_coupon_max_discount_amount: Int,
        val mall_coupon_min_order_amount: Int,
        val mall_coupon_remain_quantity: Int,
        val mall_coupon_start_time: Int,
        val mall_coupon_total_quantity: Int,
        val mall_cps: Int,
        val mall_goods_list: List<MallGoods>,
        val mall_id: Int,
        val mall_logo: String,
        val mall_name: String,
        val mall_rate: Int,
        val merchant_type: Int,
        val min_group_price: Int,
        val min_normal_price: Int,
        val only_scene_auth: Boolean,
        val opt_id: Int,
        val opt_ids: List<Int>,
        val opt_name: String,
        val plan_type: Int,
        val plan_type_all: Int,
        val promotion_rate: Int,
        val sales_tip: String,
        val serv_txt: String,
        val service_tags: List<Int>,
        val zs_duo_id: Int
    )

    data class MallGoods(
        val activity_type: Any,
        val cat_id: Any,
        val cat_ids: List<Int>,
        val category_id: Int,
        val category_name: String,
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
        val desc_txt: String,
        val goods_desc: Any,
        val goods_gallery_urls: Any,
        val goods_id: Long,
        val goods_image_url: String,
        val goods_name: String,
        val goods_thumbnail_url: String,
        val has_coupon: Boolean,
        val has_mall_coupon: Boolean,
        val lgst_txt: String,
        val mall_coupon_discount_pct: Int,
        val mall_coupon_end_time: Int,
        val mall_coupon_id: Long,
        val mall_coupon_max_discount_amount: Int,
        val mall_coupon_min_order_amount: Int,
        val mall_coupon_remain_quantity: Int,
        val mall_coupon_start_time: Int,
        val mall_coupon_total_quantity: Int,
        val mall_cps: Any,
        val mall_id: Int,
        val mall_name: String,
        val mall_rate: Int,
        val merchant_type: Int,
        val min_group_price: Int,
        val min_normal_price: Int,
        val only_scene_auth: Any,
        val opt_id: Int,
        val opt_ids: List<Int>,
        val opt_name: String,
        val plan_type: Int,
        val promotion_rate: Int,
        val sales_tip: String,
        val serv_txt: String,
        val service_tags: Any,
        val zs_duo_id: Any
    )
}
