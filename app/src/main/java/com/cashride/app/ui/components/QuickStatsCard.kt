package com.cashride.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cashride.app.data.UserProfile
import com.cashride.app.ui.theme.CashRideTheme

@Composable
fun QuickStatsCard(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "📊 나의 라이딩 통계",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    emoji = "🚴",
                    label = "총 운행",
                    value = "${userProfile.totalRides}회"
                )
                
                StatItem(
                    emoji = "🛣️",
                    label = "총 거리",
                    value = "${String.format("%.1f", userProfile.totalDistance)}km"
                )
                
                StatItem(
                    emoji = "🏆",
                    label = "획득 배지",
                    value = "${userProfile.badges.size}개"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.displaySmall
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
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

@Preview(showBackground = true)
@Composable
fun QuickStatsCardPreview() {
    CashRideTheme {
        QuickStatsCard(
            userProfile = UserProfile().apply {
                totalRides = 15
                totalDistance = 47.3
                badges.add(com.cashride.app.data.BadgeType.POINT_MASTER)
                badges.add(com.cashride.app.data.BadgeType.SAFE_STREAK_5)
            }
        )
    }
}
