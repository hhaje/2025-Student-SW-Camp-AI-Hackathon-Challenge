package com.cashride.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.cashride.app.data.RidingSession
import com.cashride.app.data.SafetyActionType
import com.cashride.app.data.SafetyActions
import com.cashride.app.data.UserProfile
import com.cashride.app.ui.theme.*

@Composable
fun RidingReportScreen(
    ridingSession: RidingSession,
    earnedPoints: Int,
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safetyScore = ridingSession.calculateSafetyScore()
    var showAnimation by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        showAnimation = true
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 헤더
        Text(
            text = "🎉 RIDING REPORT",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "안전한 주행을 완료해주셔서 감사합니다.\n당신의 라이딩 리포트가 도착했어요!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
        
        // 주행 요약
        RidingSummaryCard(ridingSession = ridingSession)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 안전 점수
        SafetyScoreCard(
            score = safetyScore,
            showAnimation = showAnimation
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 포인트 획득
        if (earnedPoints > 0) {
            PointsEarnedCard(
                earnedPoints = earnedPoints,
                ridingSession = ridingSession,
                userProfile = userProfile,
                showAnimation = showAnimation
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 액션 상세
        ActionDetailsCard(ridingSession = ridingSession)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 완료 버튼
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "확인",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RidingSummaryCard(
    ridingSession: RidingSession,
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
                text = "📊 주행 요약",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem("총 주행거리", "${String.format("%.1f", ridingSession.distance)} km")
                SummaryItem("평균 시속", "${String.format("%.1f", ridingSession.averageSpeed)} km/h")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val positiveActions = ridingSession.actions.filter { it.scoreChange > 0 }
            if (positiveActions.isNotEmpty()) {
                val mainAction = positiveActions.groupBy { it.type }.maxByOrNull { it.value.size }
                mainAction?.let { (type, actions) ->
                    val actionName = when(type) {
                        SafetyActionType.PERFECT_STOP -> "완벽한 멈춤"
                        SafetyActionType.SAFE_ZONE_SLOW -> "안전 서행"
                        SafetyActionType.RECOMMENDED_PARKING -> "추천 주차"
                        else -> "안전 행동"
                    }
                    Text(
                        text = "• 주요 안전 행동: $actionName (${actions.size}회) ✅",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SafetyGreen
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: String
) {
    Column {
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

@Composable
fun SafetyScoreCard(
    score: Int,
    showAnimation: Boolean,
    modifier: Modifier = Modifier
) {
    val scoreColor = when {
        score >= 100 -> PointGold
        score >= 90 -> SafetyGreen
        score >= 70 -> SafetyYellow
        else -> SafetyRed
    }
    
    val message = when {
        score >= 100 -> "완벽한 운행이었어요! 🏆"
        score >= 90 -> "아주 훌륭한 주행이었어요! 🌟"
        score >= 80 -> "좋은 안전운전이었습니다! 👍"
        score >= 70 -> "평균적인 운행이었어요! 😊"
        score >= 60 -> "조금 더 주의가 필요해요! ⚠️"
        else -> "다음엔 더 안전하게 운행해주세요! 🚨"
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = scoreColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💯 이번 운행 안전 점수",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (showAnimation) {
                Text(
                    text = "${score}점",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )
            }
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun PointsEarnedCard(
    earnedPoints: Int,
    ridingSession: RidingSession,
    userProfile: UserProfile,
    showAnimation: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = PointGold.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showAnimation) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PointGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$earnedPoints 포인트 획득!",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = PointGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PointGold,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 포인트 상세 계산
            val safetyScore = ridingSession.calculateSafetyScore()
            val basePoints = (safetyScore * 0.5).toInt()
            val actionBonus = ridingSession.getPositiveActionCount() * 2
            val distanceMultiplier = if (ridingSession.distance > 1.0) 1.5 else 1.0
            val comboMultiplier = if (userProfile.consecutiveSafeRides >= 5) 1.5 else 1.0
            
            Column {
                PointBredownItem("점수 포인트", "$basePoints SP", "${safetyScore}점 × 0.5")
                if (actionBonus > 0) {
                    PointBredownItem("액션 보너스", "+$actionBonus SP", "${ridingSession.getPositiveActionCount()}회 안전행동")
                }
                if (distanceMultiplier > 1.0) {
                    PointBredownItem("거리 보너스", "× $distanceMultiplier", "${String.format("%.1f", ridingSession.distance)}km 운행")
                }
                if (comboMultiplier > 1.0 && userProfile.consecutiveSafeRides > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        PointBredownItem("연속 탑승 콤보", "× $comboMultiplier", "${userProfile.consecutiveSafeRides}회 연속")
                    }
                }
            }
        }
    }
}

@Composable
fun PointBredownItem(
    label: String,
    value: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "• $label:",
            style = MaterialTheme.typography.bodyMedium
        )
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = PointGold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionDetailsCard(
    ridingSession: RidingSession,
    modifier: Modifier = Modifier
) {
    if (ridingSession.actions.isEmpty()) return
    
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
                text = "📋 운행 중 액션 상세",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            val groupedActions = ridingSession.actions.groupBy { it.type }
            groupedActions.forEach { (type, actions) ->
                val action = SafetyActions.actions[type]
                action?.let {
                    ActionDetailItem(
                        emoji = when(type) {
                            SafetyActionType.PERFECT_STOP -> "🛑"
                            SafetyActionType.SAFE_ZONE_SLOW -> "🐌"
                            SafetyActionType.RECOMMENDED_PARKING -> "🅿️"
                            SafetyActionType.ZONE_SPEEDING -> "🚨"
                            SafetyActionType.SUDDEN_BRAKE -> "⚠️"
                            SafetyActionType.SUDDEN_START -> "⚠️"
                            SafetyActionType.PHONE_USAGE -> "📱"
                            SafetyActionType.ILLEGAL_PARKING -> "🚧"
                        },
                        name = it.description,
                        count = actions.size,
                        scoreChange = it.scoreChange,
                        isPositive = it.scoreChange > 0
                    )
                }
            }
        }
    }
}

@Composable
fun ActionDetailItem(
    emoji: String,
    name: String,
    count: Int,
    scoreChange: Int,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${count}회",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = "${if (isPositive) "+" else ""}${scoreChange * count}점",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) SafetyGreen else SafetyRed
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RidingReportScreenPreview() {
    CashRideTheme {
        val session = RidingSession().apply {
            distance = 2.5
            averageSpeed = 15.2
            actions.add(SafetyActions.actions[SafetyActionType.PERFECT_STOP]!!.copy())
            actions.add(SafetyActions.actions[SafetyActionType.PERFECT_STOP]!!.copy())
            actions.add(SafetyActions.actions[SafetyActionType.SAFE_ZONE_SLOW]!!.copy())
        }
        
        RidingReportScreen(
            ridingSession = session,
            earnedPoints = 146,
            userProfile = UserProfile(),
            onDismiss = {}
        )
    }
}
