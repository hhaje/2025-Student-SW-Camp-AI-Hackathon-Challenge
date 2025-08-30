package com.cashride.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cashride.app.data.UserLevel
import com.cashride.app.data.UserProfile
import com.cashride.app.data.BadgeType
import com.cashride.app.ui.theme.CashRideTheme
import com.cashride.app.ui.theme.PointGold

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 사용자 레벨 카드
        UserLevelCard(userProfile = userProfile)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 상세 통계
        DetailedStatsCard(userProfile = userProfile)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 최근 배지
        RecentBadgesCard(userProfile = userProfile)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 레벨별 혜택
        LevelBenefitsCard(userProfile = userProfile)
    }
}

@Composable
fun UserLevelCard(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    val currentLevel = userProfile.getUserLevel()
    val pointsToNext = userProfile.getPointsToNextLevel()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 레벨 아이콘
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when(currentLevel) {
                            UserLevel.NEWBIE -> "🌱"
                            UserLevel.SAFE -> "🛡️"
                            UserLevel.VETERAN -> "⭐"
                            UserLevel.MASTER -> "👑"
                            UserLevel.LEGEND -> "🏆"
                        },
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = currentLevel.displayName,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "포인트 적립률 ${String.format("%.0f", (currentLevel.bonus * 100))}%",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 현재 포인트
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${userProfile.totalPoints}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = PointGold
                )
                Text(
                    text = " / ${if (pointsToNext > 0) userProfile.totalPoints + pointsToNext else "∞"} SP",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // 다음 레벨까지 진행률
            if (pointsToNext > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "다음 레벨까지",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${pointsToNext}P 남음",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val progress = userProfile.totalPoints.toFloat() / (userProfile.totalPoints + pointsToNext)
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = PointGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🏆 최고 레벨 달성!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = PointGold
                )
            }
        }
    }
}

@Composable
fun DetailedStatsCard(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "📈 상세 통계",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            DetailedStatItem(
                icon = Icons.Default.Home,
                label = "총 운행 횟수",
                value = "${userProfile.totalRides}회",
                description = "안전한 라이딩"
            )
            
            DetailedStatItem(
                icon = Icons.Default.LocationOn,
                label = "총 주행 거리",
                value = "${String.format("%.1f", userProfile.totalDistance)}km",
                description = "친환경 이동"
            )
            
            DetailedStatItem(
                icon = Icons.Default.Add,
                label = "연속 안전운행",
                value = "${userProfile.consecutiveSafeRides}회",
                description = "현재 연속 기록"
            )
            
            DetailedStatItem(
                icon = Icons.Default.Star,
                label = "획득 배지",
                value = "${userProfile.badges.size}개",
                description = "총 ${BadgeType.values().size}개 중"
            )
            
            // 안전 행동 통계
            if (userProfile.perfectStops > 0 || userProfile.safeZoneSlows > 0 || userProfile.recommendedParkings > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "🎯 안전 행동 기록",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (userProfile.perfectStops > 0) {
                    SafetyActionStat("🛑", "완벽한 정지", userProfile.perfectStops)
                }
                if (userProfile.safeZoneSlows > 0) {
                    SafetyActionStat("🐌", "안전 서행", userProfile.safeZoneSlows)
                }
                if (userProfile.recommendedParkings > 0) {
                    SafetyActionStat("🅿️", "추천 주차", userProfile.recommendedParkings)
                }
            }
        }
    }
}

@Composable
fun DetailedStatItem(
    icon: ImageVector,
    label: String,
    value: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SafetyActionStat(
    emoji: String,
    name: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${count}회",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun RecentBadgesCard(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    if (userProfile.badges.isEmpty()) return
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "🏆 획득한 배지",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                userProfile.badges.take(4).forEach { badge ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = badge.emoji,
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text(
                                text = badge.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
            
            if (userProfile.badges.size > 4) {
                Text(
                    text = "그 외 ${userProfile.badges.size - 4}개 배지",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun LevelBenefitsCard(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    val currentLevel = userProfile.getUserLevel()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "🎁 레벨 혜택",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "${currentLevel.displayName} 혜택",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            BenefitItem("💰", "포인트 적립률", "${String.format("%.0f", (currentLevel.bonus * 100))}% 보너스")
            
            when (currentLevel) {
                UserLevel.SAFE -> {
                    BenefitItem("🎫", "특별 쿠폰", "월 1회 할인 쿠폰 제공")
                }
                UserLevel.VETERAN -> {
                    BenefitItem("🎫", "특별 쿠폰", "월 2회 할인 쿠폰 제공")
                    BenefitItem("🅿️", "프리미엄 주차", "추천 주차구역 우선 이용")
                }
                UserLevel.MASTER -> {
                    BenefitItem("🎫", "특별 쿠폰", "월 3회 할인 쿠폰 제공")
                    BenefitItem("🅿️", "프리미엄 주차", "전용 주차구역 이용 가능")
                    BenefitItem("📞", "전용 고객센터", "우선 상담 서비스")
                }
                UserLevel.LEGEND -> {
                    BenefitItem("🎫", "VIP 쿠폰", "무제한 할인 혜택")
                    BenefitItem("🅿️", "VIP 주차", "모든 프리미엄 구역 무료")
                    BenefitItem("📞", "전용 매니저", "1:1 맞춤 서비스")
                    BenefitItem("🏆", "특별 리워드", "월간 특별 리워드 지급")
                }
                else -> {
                    BenefitItem("📱", "기본 서비스", "CashRide 기본 기능 이용")
                }
            }
        }
    }
}

@Composable
fun BenefitItem(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    CashRideTheme {
        ProfileScreen(
            userProfile = UserProfile().apply {
                totalPoints = 350
                totalRides = 25
                totalDistance = 67.5
                consecutiveSafeRides = 12
                perfectStops = 8
                safeZoneSlows = 15
                recommendedParkings = 3
                badges.add(BadgeType.POINT_MASTER)
                badges.add(BadgeType.SAFE_STREAK_5)
                badges.add(BadgeType.SAFE_STREAK_10)
                badges.add(BadgeType.STOP_MASTER)
            }
        )
    }
}
