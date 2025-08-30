package com.cashride.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashride.app.data.BadgeType
import com.cashride.app.data.UserProfile
import com.cashride.app.ui.theme.CashRideTheme

@Composable
fun BadgeScreen(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 헤더
        Text(
            text = "🏆 나의 배지",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "안전운전으로 획득한 배지들을 확인해보세요!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 배지 통계
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BadgeStatItem("획득한 배지", "${userProfile.badges.size}")
                BadgeStatItem("전체 배지", "${BadgeType.values().size}")
                BadgeStatItem("달성률", "${(userProfile.badges.size * 100 / BadgeType.values().size)}%")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 배지 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(BadgeType.values().toList()) { badgeType ->
                BadgeCard(
                    badgeType = badgeType,
                    isEarned = userProfile.badges.contains(badgeType),
                    progress = getBadgeProgress(badgeType, userProfile)
                )
            }
        }
    }
}

@Composable
fun BadgeStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BadgeCard(
    badgeType: BadgeType,
    isEarned: Boolean,
    progress: Pair<Int, Int>,
    modifier: Modifier = Modifier
) {
    val (current, target) = progress
    val progressPercentage = if (target > 0) (current.toFloat() / target).coerceAtMost(1f) else 0f
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isEarned) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isEarned) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 배지 이모지
            Text(
                text = badgeType.emoji,
                fontSize = 36.sp,
                color = if (isEarned) Color.Unspecified else Color.Gray.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 배지 이름
            Text(
                text = badgeType.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (isEarned) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                },
                modifier = Modifier.height(40.dp)
            )
            
            // 진행률 또는 완료 표시
            if (isEarned) {
                Text(
                    text = "✅ 획득완료",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$current / $target",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (target > 0) {
                        LinearProgressIndicator(
                            progress = progressPercentage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 요구사항
            Text(
                text = badgeType.requirement,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

/**
 * 배지별 진행률 계산
 */
private fun getBadgeProgress(badgeType: BadgeType, userProfile: UserProfile): Pair<Int, Int> {
    return when (badgeType) {
        BadgeType.SAFE_STREAK_5 -> userProfile.consecutiveSafeRides to 5
        BadgeType.SAFE_STREAK_10 -> userProfile.consecutiveSafeRides to 10
        BadgeType.SAFE_STREAK_20 -> userProfile.consecutiveSafeRides to 20
        
        BadgeType.POINT_MASTER -> userProfile.totalPoints to 100
        BadgeType.POINT_KING -> userProfile.totalPoints to 500
        BadgeType.POINT_LEGEND -> userProfile.totalPoints to 1000
        
        BadgeType.STOP_MASTER -> userProfile.perfectStops to 10
        BadgeType.SAFE_ZONE_GUARDIAN -> userProfile.safeZoneSlows to 20
        BadgeType.PARKING_EXPERT -> userProfile.recommendedParkings to 15
    }
}

@Preview(showBackground = true)
@Composable
fun BadgeScreenPreview() {
    CashRideTheme {
        BadgeScreen(
            userProfile = UserProfile().apply {
                totalPoints = 250
                consecutiveSafeRides = 7
                perfectStops = 5
                badges.add(BadgeType.POINT_MASTER)
                badges.add(BadgeType.SAFE_STREAK_5)
            }
        )
    }
}
