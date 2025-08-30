package com.cashride

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.cashride.data.database.CashRideDatabase
import com.cashride.data.repository.CashRideRepository
import com.cashride.databinding.ActivityMainBinding
import com.cashride.ui.fragments.*
import com.cashride.ui.viewmodel.CashRideViewModel
import com.cashride.ui.viewmodel.CashRideViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CashRideViewModel by viewModels {
        CashRideViewModelFactory(
            CashRideRepository(
                Room.databaseBuilder(
                    applicationContext,
                    CashRideDatabase::class.java,
                    "cashride_database"
                ).build()
            )
        )
    }
    
    private var rideTimer: Timer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        observeViewModel()
        startRideSimulation()
    }
    
    private fun setupUI() {
        // 사이드바 설정
        binding.menuToggle.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        
        // 사이드바 메뉴 클릭 리스너
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> showFragment(MainFragment())
                R.id.nav_rewards -> showFragment(RewardsFragment())
                R.id.nav_challenge -> showFragment(ChallengeFragment())
                R.id.nav_profile -> showFragment(ProfileFragment())
                R.id.nav_stats -> showFragment(StatsFragment())
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        
        // 초기 프래그먼트 설정
        showFragment(MainFragment())
    }
    
    private fun observeViewModel() {
        // 사용자 정보 관찰
        lifecycleScope.launch {
            viewModel.user.collectLatest { user ->
                user?.let { updateHeader(it) }
            }
        }
        
        // 리포트 페이지 표시 상태 관찰
        lifecycleScope.launch {
            viewModel.showReportPage.collectLatest { showReport ->
                if (showReport) {
                    showFragment(ReportFragment())
                }
            }
        }
        
        // UI 이벤트 관찰
        lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is com.cashride.ui.viewmodel.UiEvent.ShowMessage -> {
                        showMessage(event.message)
                    }
                    com.cashride.ui.viewmodel.UiEvent.RideStarted -> {
                        showFragment(RidingFragment())
                    }
                }
            }
        }
    }
    
    private fun updateHeader(user: com.cashride.data.models.User) {
        val userLevel = viewModel.getUserLevel()
        binding.totalPoints.text = user.totalPoints.toString()
        binding.levelInfo.text = "${userLevel.icon} ${userLevel.name} • 총 ${String.format("%.1f", user.totalDistance)}km 주행"
        
        // 사이드바 헤더 업데이트
        binding.navHeaderLevel.text = "${userLevel.icon} ${userLevel.name}"
        binding.navHeaderPoints.text = "${user.totalPoints} 포인트"
    }
    
    private fun showFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
    
    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
    
    private fun startRideSimulation() {
        rideTimer = Timer()
        rideTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    viewModel.updateRideSimulation()
                }
            }
        }, 0, 2000) // 2초마다 업데이트
    }
    
    override fun onDestroy() {
        super.onDestroy()
        rideTimer?.cancel()
    }
    
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
