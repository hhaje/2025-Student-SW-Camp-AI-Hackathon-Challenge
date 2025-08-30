package com.cashride.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.cashride.app.data.RidingSession
import com.cashride.app.data.SafetyAction
import com.cashride.app.data.SafetyActionType
import com.cashride.app.data.SafetyActions
import com.cashride.app.ui.theme.*
import kotlin.random.Random

@Composable
fun RidingScreen(
    ridingSession: RidingSession,
    onEndRiding: (RidingSession) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentScore by remember { mutableIntStateOf(100) }
    var elapsedTime by remember { mutableLongStateOf(0L) }
    var distance by remember { mutableDoubleStateOf(0.0) }
    
    // 시간과 거리 시뮬레이션
    LaunchedEffect(ridingSession) {
        while (ridingSession.isActive) {
            delay(1000) // 1초마다
            elapsedTime += 1
            distance += Random.nextDouble(0.01, 0.05) // 0.01~0.05km 증가
            ridingSession.distance = distance
            ridingSession.averageSpeed = if (elapsedTime > 0) (distance / (elapsedTime / 3600.0)) else 0.0
            currentScore = ridingSession.calculateSafetyScore()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 상단 스코어 카드
        ScoreCard(
            score = currentScore,
            elapsedTime = elapsedTime,
            distance = distance,
            averageSpeed = ridingSession.averageSpeed
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 액션 버튼들
        ActionButtonsSection(
            onActionSelected = { actionType ->
                val action = SafetyActions.actions[actionType]?.copy(
                    timestamp = System.currentTimeMillis()
                )
                action?.let { ridingSession.actions.add(it) }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 액션 히스토리
        Text(
            text = "🎬 이번 운행 액션",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ridingSession.actions.reversed()) { action ->
                ActionHistoryItem(action = action)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 운행 종료 버튼
        Button(
            onClick = {
                ridingSession.isActive = false
                ridingSession.endTime = System.currentTimeMillis()
                onEndRiding(ridingSession)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SafetyRed
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "운행 종료"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "운행 종료",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScoreCard(
    score: Int,
    elapsedTime: Long,
    distance: Double,
    averageSpeed: Double,
    modifier: Modifier = Modifier
) {
    val scoreColor = when {
        score >= 90 -> SafetyGreen
        score >= 70 -> SafetyYellow
        else -> SafetyRed
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "현재 안전 점수",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "${score}점",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
            
            Text(
                text = when {
                    score >= 100 -> "완벽한 운행! 🏆"
                    score >= 90 -> "훌륭한 안전운전! 🌟"
                    score >= 80 -> "좋은 운행입니다! 👍"
                    score >= 70 -> "조금 더 주의하세요! ⚠️"
                    else -> "안전운전에 집중하세요! 🚨"
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 운행 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn("⏱️", "운행시간", "${elapsedTime / 60}:${String.format("%02d", elapsedTime % 60)}")
                StatColumn("🛣️", "거리", "${String.format("%.2f", distance)}km")
                StatColumn("⚡", "평균속도", "${String.format("%.1f", averageSpeed)}km/h")
            }
        }
    }
}

@Composable
fun StatColumn(
    emoji: String,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, style = MaterialTheme.typography.headlineLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ActionButtonsSection(
    onActionSelected: (SafetyActionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🎮 이벤트 시뮬레이션",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "⚠️ 위험 행동",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = SafetyRed,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "과속\n(-15점)",
                    color = SafetyRed,
                    modifier = Modifier.weight(1f)
                ) { onActionSelected(SafetyActionType.ZONE_SPEEDING) }
                
                ActionButton(
                    text = "급감속\n(-10점)",
                    color = SafetyRed,
                    modifier = Modifier.weight(1f)
                ) { onActionSelected(SafetyActionType.SUDDEN_BRAKE) }
                
                ActionButton(
                    text = "폰 사용\n(-20점)",
                    color = SafetyRed,
                    modifier = Modifier.weight(1f)
                ) { onActionSelected(SafetyActionType.PHONE_USAGE) }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "✅ 안전 행동",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = SafetyGreen,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "완벽한 정지\n(+10점)",
                    color = SafetyGreen,
                    modifier = Modifier.weight(1f)
                ) { onActionSelected(SafetyActionType.PERFECT_STOP) }
                
                ActionButton(
                    text = "안전 서행\n(+5점)",
                    color = SafetyGreen,
                    modifier = Modifier.weight(1f)
                ) { onActionSelected(SafetyActionType.SAFE_ZONE_SLOW) }
                
                ActionButton(
                    text = "추천 주차\n(+15점)",
                    color = SafetyGreen,
                    modifier = Modifier.weight(1f)
                ) { onActionSelected(SafetyActionType.RECOMMENDED_PARKING) }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ActionHistoryItem(
    action: SafetyAction,
    modifier: Modifier = Modifier
) {
    val isPositive = action.scoreChange > 0
    val backgroundColor = if (isPositive) SafetyGreen.copy(alpha = 0.1f) else SafetyRed.copy(alpha = 0.1f)
    val icon = if (isPositive) Icons.Default.CheckCircle else Icons.Default.Warning
    val iconColor = if (isPositive) SafetyGreen else SafetyRed
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = action.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${if (isPositive) "+" else ""}${action.scoreChange}점",
                    style = MaterialTheme.typography.bodyMedium,
                    color = iconColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RidingScreenPreview() {
    CashRideTheme {
        RidingScreen(
            ridingSession = RidingSession(),
            onEndRiding = {}
        )
    }
}
