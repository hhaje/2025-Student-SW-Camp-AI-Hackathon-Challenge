package com.cashride.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cashride.app.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    userProfile: UserProfile,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPurchaseDialog by remember { mutableStateOf(false) }
    var selectedGiftCard by remember { mutableStateOf<GiftCard?>(null) }
    var showUseDialog by remember { mutableStateOf(false) }
    var selectedUserGiftCard by remember { mutableStateOf<UserGiftCard?>(null) }
    
    // 구매 다이얼로그
    if (showPurchaseDialog && selectedGiftCard != null) {
        PurchaseDialog(
            giftCard = selectedGiftCard!!,
            userProfile = userProfile,
            onConfirm = { success ->
                showPurchaseDialog = false
                selectedGiftCard = null
            },
            onDismiss = {
                showPurchaseDialog = false
                selectedGiftCard = null
            }
        )
    }
    
    // 사용 다이얼로그
    if (showUseDialog && selectedUserGiftCard != null) {
        UseGiftCardDialog(
            userGiftCard = selectedUserGiftCard!!,
            onConfirm = { success ->
                if (success) {
                    // UserProfile의 useGiftCard 메서드 호출
                    userProfile.useGiftCard(selectedUserGiftCard!!.id)
                }
                showUseDialog = false
                selectedUserGiftCard = null
            },
            onDismiss = {
                showUseDialog = false
                selectedUserGiftCard = null
            }
        )
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 상단 포인트 정보
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "보유 포인트",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${userProfile.totalPoints} SP",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "포인트",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        // 탭 선택
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("상점") },
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("내 기프티콘") },
                icon = { Icon(Icons.Default.CardGiftcard, contentDescription = null) }
            )
        }
        
        // 탭 내용
        when (selectedTab) {
            0 -> {
                // 상점 - 기프티콘 구매
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(GiftCardStore.availableGiftCards) { giftCard ->
                        GiftCardItem(
                            giftCard = giftCard,
                            userPoints = userProfile.totalPoints,
                            onClick = {
                                selectedGiftCard = giftCard
                                showPurchaseDialog = true
                            }
                        )
                    }
                }
            }
            1 -> {
                // 내 기프티콘 - 보유한 기프티콘
                val availableGiftCards = userProfile.getAvailableGiftCards()
                val usedGiftCards = userProfile.getUsedGiftCards()
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (availableGiftCards.isNotEmpty()) {
                        item {
                            Text(
                                text = "사용 가능한 기프티콘",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(availableGiftCards) { userGiftCard ->
                            OwnedGiftCardItem(
                                userGiftCard = userGiftCard,
                                isUsed = false,
                                onClick = {
                                    selectedUserGiftCard = userGiftCard
                                    showUseDialog = true
                                }
                            )
                        }
                    }
                    
                    if (usedGiftCards.isNotEmpty()) {
                        item {
                            Text(
                                text = "사용된 기프티콘",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(usedGiftCards) { userGiftCard ->
                            OwnedGiftCardItem(
                                userGiftCard = userGiftCard,
                                isUsed = true,
                                onClick = { }
                            )
                        }
                    }
                    
                    if (availableGiftCards.isEmpty() && usedGiftCards.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "보유한 기프티콘이 없습니다",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = "상점에서 기프티콘을 구매해보세요!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GiftCardItem(
    giftCard: GiftCard,
    userPoints: Int,
    onClick: () -> Unit
) {
    val canAfford = userPoints >= giftCard.price
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canAfford) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 기프티콘 아이콘
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = giftCard.type.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 기프티콘 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = giftCard.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = giftCard.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 가격 정보
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${giftCard.price} SP",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (canAfford) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
                if (!canAfford) {
                    Text(
                        text = "포인트 부족",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun OwnedGiftCardItem(
    userGiftCard: UserGiftCard,
    isUsed: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isUsed) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isUsed) 
                MaterialTheme.colorScheme.surfaceVariant 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 기프티콘 아이콘
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = if (isUsed) 
                            MaterialTheme.colorScheme.outline 
                        else 
                            MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userGiftCard.giftCard.type.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 기프티콘 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = userGiftCard.giftCard.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userGiftCard.giftCard.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "구매일: ${java.text.SimpleDateFormat("yyyy.MM.dd").format(java.util.Date(userGiftCard.purchaseDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (isUsed && userGiftCard.usedDate != null) {
                    Text(
                        text = "사용일: ${java.text.SimpleDateFormat("yyyy.MM.dd").format(java.util.Date(userGiftCard.usedDate!!))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            // 상태 표시
            if (isUsed) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "사용됨",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "사용하기",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun PurchaseDialog(
    giftCard: GiftCard,
    userProfile: UserProfile,
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("기프티콘 구매")
        },
        text = {
            Column {
                Text("다음 기프티콘을 구매하시겠습니까?")
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = giftCard.type.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = giftCard.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = giftCard.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "가격: ${giftCard.price} SP",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "보유 포인트: ${userProfile.totalPoints} SP",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (userProfile.totalPoints < giftCard.price) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "포인트가 부족합니다!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val success = userProfile.purchaseGiftCard(giftCard)
                    onConfirm(success)
                },
                enabled = userProfile.totalPoints >= giftCard.price
            ) {
                Text("구매")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun UseGiftCardDialog(
    userGiftCard: UserGiftCard,
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("기프티콘 사용")
        },
        text = {
            Column {
                Text("이 기프티콘을 사용하시겠습니까?")
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = userGiftCard.giftCard.type.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = userGiftCard.giftCard.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userGiftCard.giftCard.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "사용 후에는 다시 사용할 수 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val success = userGiftCard.giftCard.type.displayName != "방탄소년단" // 임시로 BTS만 사용 불가
                    onConfirm(success)
                }
            ) {
                Text("사용")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
