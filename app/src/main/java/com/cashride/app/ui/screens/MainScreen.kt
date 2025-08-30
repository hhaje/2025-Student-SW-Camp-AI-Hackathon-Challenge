package com.cashride.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cashride.app.data.UserProfile
import com.cashride.app.ui.components.UserProfileCard
import com.cashride.app.ui.components.QuickStatsCard
import com.cashride.app.ui.theme.CashRideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var currentRidingSession by remember { mutableStateOf(null as com.cashride.app.data.RidingSession?) }
    var showRidingReport by remember { mutableStateOf(false) }
    var completedSession by remember { mutableStateOf(null as com.cashride.app.data.RidingSession?) }
    var earnedPoints by remember { mutableIntStateOf(0) }
    
    // 라이딩 리포트 화면
    if (showRidingReport && completedSession != null) {
        RidingReportScreen(
            ridingSession = completedSession!!,
            earnedPoints = earnedPoints,
            userProfile = userProfile,
            onDismiss = {
                showRidingReport = false
                completedSession = null
                earnedPoints = 0
            }
        )
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🛴 CashRide",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "라이딩") },
                    label = { Text("라이딩") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "배지") },
                    label = { Text("배지") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "상점") },
                    label = { Text("상점") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "프로필") },
                    label = { Text("프로필") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> {
                    if (currentRidingSession == null) {
                        DashboardScreen(
                            userProfile = userProfile,
                            onStartRiding = { session ->
                                currentRidingSession = session
                            }
                        )
                    } else {
                        RidingScreen(
                            ridingSession = currentRidingSession!!,
                            onEndRiding = { session ->
                                val points = userProfile.addSafetyPoints(session)
                                completedSession = session
                                earnedPoints = points
                                currentRidingSession = null
                                showRidingReport = true
                            }
                        )
                    }
                }
                1 -> BadgeScreen(userProfile = userProfile)
                2 -> StoreScreen(userProfile = userProfile)
                3 -> ProfileScreen(userProfile = userProfile)
            }
        }
    }
}

@Composable
fun DashboardScreen(
    userProfile: UserProfile,
    onStartRiding: (com.cashride.app.data.RidingSession) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 사용자 프로필 카드
        UserProfileCard(userProfile = userProfile)
        
        // 빠른 통계
        QuickStatsCard(userProfile = userProfile)
        
        // 라이딩 시작 버튼
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "안전한 라이딩으로\n포인트를 획득하세요!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val newSession = com.cashride.app.data.RidingSession()
                        onStartRiding(newSession)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "🚀 라이딩 시작",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // 최근 라이딩 기록
        if (userProfile.ridingHistory.isNotEmpty()) {
            Text(
                text = "최근 라이딩",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            userProfile.ridingHistory.takeLast(3).forEach { session ->
                RecentRideCard(session = session)
            }
        }
    }
}

@Composable
fun RecentRideCard(
    session: com.cashride.app.data.RidingSession,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "안전 점수: ${session.calculateSafetyScore()}점",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${session.distance}km",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Text(
                text = "긍정적 행동: ${session.getPositiveActionCount()}회",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CashRideTheme {
        MainScreen(userProfile = UserProfile())
    }
}
