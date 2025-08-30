package com.cashride.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashride.app.data.UserProfile
import com.cashride.app.ui.theme.CashRideTheme
import com.cashride.app.ui.theme.PointGold

@Composable
fun UserProfileCard(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    val level = userProfile.getUserLevel()
    val pointsToNext = userProfile.getPointsToNextLevel()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "안녕하세요! 👋",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = level.displayName,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // 레벨 아이콘
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when(level) {
                                com.cashride.app.data.UserLevel.NEWBIE -> "🌱"
                                com.cashride.app.data.UserLevel.SAFE -> "🛡️"
                                com.cashride.app.data.UserLevel.VETERAN -> "⭐"
                                com.cashride.app.data.UserLevel.MASTER -> "👑"
                                com.cashride.app.data.UserLevel.LEGEND -> "🏆"
                            },
                            fontSize = 24.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 포인트 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "총 포인트",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${userProfile.totalPoints}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = PointGold
                        )
                        Text(
                            text = " SP",
                            style = MaterialTheme.typography.headlineLarge,
                            color = PointGold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "연속 안전운행",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${userProfile.consecutiveSafeRides}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "회 🔥",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
            
            // 다음 레벨까지 진행률
            if (pointsToNext > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Column {
                    Text(
                        text = "다음 레벨까지 ${pointsToNext}P",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val progress = userProfile.totalPoints.toFloat() / (userProfile.totalPoints + pointsToNext)
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileCardPreview() {
    CashRideTheme {
        UserProfileCard(
            userProfile = UserProfile().apply {
                totalPoints = 250
                consecutiveSafeRides = 7
            }
        )
    }
}
