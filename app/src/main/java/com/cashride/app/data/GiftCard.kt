package com.cashride.app.data

/**
 * 기프티콘 타입
 */
enum class GiftCardType(val displayName: String, val emoji: String, val description: String) {
    STARBUCKS("스타벅스", "☕", "아메리카노 1잔"),
    MCDONALDS("맥도날드", "🍔", "빅맥 세트"),
    LOTTERIA("롯데리아", "🍟", "치킨버거 세트"),
    SUBWAY("서브웨이", "🥪", "샌드위치 + 음료"),
    GONGCHA("공차", "🧋", "버블티 1잔"),
    BANGLADESH("방탄소년단", "💜", "BTS 굿즈"),
    MOVIE("영화관", "🎬", "영화 티켓 1매"),
    BOOK("도서", "📚", "베스트셀러 1권"),
    TRANSPORT("교통카드", "🚇", "교통카드 충전"),
    CONVENIENCE("편의점", "🏪", "편의점 상품권")
}

/**
 * 기프티콘 데이터
 */
data class GiftCard(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: GiftCardType,
    val name: String,
    val description: String,
    val price: Int, // 포인트 가격
    val imageUrl: String? = null,
    val isAvailable: Boolean = true
)

/**
 * 사용자 기프티콘 보유 정보
 */
data class UserGiftCard(
    val id: String = java.util.UUID.randomUUID().toString(),
    val giftCard: GiftCard,
    val purchaseDate: Long = System.currentTimeMillis(),
    val isUsed: Boolean = false,
    val usedDate: Long? = null
)

/**
 * 기프티콘 샘플 데이터
 */
object GiftCardStore {
    val availableGiftCards = listOf(
        GiftCard(
            type = GiftCardType.STARBUCKS,
            name = "스타벅스 아메리카노",
            description = "아메리카노 1잔 (Tall 사이즈)",
            price = 50
        ),
        GiftCard(
            type = GiftCardType.MCDONALDS,
            name = "맥도날드 빅맥 세트",
            description = "빅맥 + 감자튀김 + 콜라",
            price = 80
        ),
        GiftCard(
            type = GiftCardType.LOTTERIA,
            name = "롯데리아 치킨버거 세트",
            description = "치킨버거 + 감자튀김 + 음료",
            price = 70
        ),
        GiftCard(
            type = GiftCardType.SUBWAY,
            name = "서브웨이 샌드위치",
            description = "샌드위치 + 음료 선택",
            price = 90
        ),
        GiftCard(
            type = GiftCardType.GONGCHA,
            name = "공차 버블티",
            description = "버블티 1잔 (기본 사이즈)",
            price = 60
        ),
        GiftCard(
            type = GiftCardType.BANGLADESH,
            name = "BTS 굿즈",
            description = "BTS 공식 굿즈 (랜덤)",
            price = 150
        ),
        GiftCard(
            type = GiftCardType.MOVIE,
            name = "영화 티켓",
            description = "영화관 티켓 1매 (전국 사용 가능)",
            price = 120
        ),
        GiftCard(
            type = GiftCardType.BOOK,
            name = "베스트셀러 도서",
            description = "베스트셀러 도서 1권 (랜덤)",
            price = 100
        ),
        GiftCard(
            type = GiftCardType.TRANSPORT,
            name = "교통카드 충전",
            description = "교통카드 충전권 5,000원",
            price = 75
        ),
        GiftCard(
            type = GiftCardType.CONVENIENCE,
            name = "편의점 상품권",
            description = "편의점 상품권 3,000원",
            price = 45
        )
    )
}
