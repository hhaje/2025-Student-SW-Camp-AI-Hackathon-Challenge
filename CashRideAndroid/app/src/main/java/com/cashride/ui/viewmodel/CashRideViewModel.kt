package com.cashride.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cashride.data.models.*
import com.cashride.data.repository.CashRideRepository
import com.cashride.data.repository.PurchaseResult
import com.cashride.data.repository.SafetyEventResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CashRideViewModel(private val repository: CashRideRepository) : ViewModel() {
    
    private val userId = "demo_user"
    
    // User 관련 State
    val user = repository.getUser(userId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    // Ride 관련 State
    private val _currentRideId = MutableStateFlow<String?>(null)
    val currentRideId: StateFlow<String?> = _currentRideId.asStateFlow()
    
    private val _rideStartTime = MutableStateFlow<Long?>(null)
    val rideStartTime: StateFlow<Long?> = _rideStartTime.asStateFlow()
    
    private val _currentSpeed = MutableStateFlow(0)
    val currentSpeed: StateFlow<Int> = _currentSpeed.asStateFlow()
    
    private val _currentDistance = MutableStateFlow(0.0)
    val currentDistance: StateFlow<Double> = _currentDistance.asStateFlow()
    
    private val _currentScore = MutableStateFlow(100)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()
    
    // UI State
    private val _showReportPage = MutableStateFlow(false)
    val showReportPage: StateFlow<Boolean> = _showReportPage.asStateFlow()
    
    private val _currentReportStage = MutableStateFlow(0)
    val currentReportStage: StateFlow<Int> = _currentReportStage.asStateFlow()
    
    private val _ridingReport = MutableStateFlow<RidingReport?>(null)
    val ridingReport: StateFlow<RidingReport?> = _ridingReport.asStateFlow()
    
    // Events
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()
    
    init {
        viewModelScope.launch {
            repository.initializeUser(userId)
        }
    }
    
    fun startRide() {
        viewModelScope.launch {
            val rideId = repository.startRide(userId)
            _currentRideId.value = rideId
            _rideStartTime.value = System.currentTimeMillis()
            _currentScore.value = 100
            _currentDistance.value = 0.0
            _events.emit(UiEvent.RideStarted)
        }
    }
    
    fun addSafetyEvent(eventType: String) {
        val rideId = _currentRideId.value ?: return
        
        viewModelScope.launch {
            when (val result = repository.addSafetyEvent(rideId, eventType)) {
                is SafetyEventResult.Success -> {
                    _currentScore.value = result.currentScore
                    val message = "${result.description} (${if (result.scoreChange > 0) "+" else ""}${result.scoreChange}점)"
                    _events.emit(UiEvent.ShowMessage(message))
                }
                SafetyEventResult.RideNotFound -> {
                    _events.emit(UiEvent.ShowMessage("운행을 찾을 수 없습니다"))
                }
            }
        }
    }
    
    fun endRide() {
        val rideId = _currentRideId.value ?: return
        
        viewModelScope.launch {
            val report = repository.endRide(rideId)
            if (report != null) {
                _ridingReport.value = report
                _showReportPage.value = true
                _currentReportStage.value = 0
                _currentRideId.value = null
                _rideStartTime.value = null
            }
        }
    }
    
    fun nextReportStage() {
        val currentStage = _currentReportStage.value
        if (currentStage < 2) {
            _currentReportStage.value = currentStage + 1
        } else {
            // 마지막 단계에서 메인으로 돌아가기
            _showReportPage.value = false
            _currentReportStage.value = 0
            _ridingReport.value = null
        }
    }
    
    fun purchaseCoupon(couponId: String) {
        viewModelScope.launch {
            when (val result = repository.purchaseCoupon(userId, couponId)) {
                is PurchaseResult.Success -> {
                    _events.emit(UiEvent.ShowMessage("${result.purchase.couponName} 구매 완료!"))
                }
                PurchaseResult.InsufficientPoints -> {
                    _events.emit(UiEvent.ShowMessage("포인트가 부족합니다"))
                }
                PurchaseResult.CouponNotFound -> {
                    _events.emit(UiEvent.ShowMessage("쿠폰을 찾을 수 없습니다"))
                }
                PurchaseResult.UserNotFound -> {
                    _events.emit(UiEvent.ShowMessage("사용자를 찾을 수 없습니다"))
                }
            }
        }
    }
    
    // 실시간 시뮬레이션 업데이트
    fun updateRideSimulation() {
        if (_currentRideId.value != null) {
            _currentSpeed.value = (10..35).random()
            _currentDistance.value += (0.01..0.1).random()
        }
    }
    
    fun getUserLevel(): UserLevel {
        val totalPoints = user.value?.totalPoints ?: 0
        return getUserLevel(totalPoints)
    }
}

class CashRideViewModelFactory(private val repository: CashRideRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashRideViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CashRideViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class UiEvent {
    object RideStarted : UiEvent()
    data class ShowMessage(val message: String) : UiEvent()
}
