package com.cashride.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coupon(
    val id: String,
    val name: String,
    val cost: Int,
    val description: String
) : Parcelable

@Parcelize
data class PurchasedCoupon(
    val couponId: String,
    val couponName: String,
    val cost: Int,
    val purchaseDate: Long,
    val used: Boolean = false
) : Parcelable

val AVAILABLE_COUPONS = listOf(
    Coupon("coffee_1000", "카페 1000원 할인", 50, "전국 주요 카페에서 사용 가능"),
    Coupon("chicken_3000", "치킨 3000원 할인", 150, "배달 앱에서 사용 가능"),
    Coupon("convenience_2000", "편의점 2000원 할인", 100, "GS25, CU, 세븐일레븐에서 사용"),
    Coupon("movie_5000", "영화 5000원 할인", 250, "CGV, 롯데시네마, 메가박스"),
    Coupon("delivery_free", "배달비 무료", 80, "배달의민족, 요기요 배달비 면제")
)
