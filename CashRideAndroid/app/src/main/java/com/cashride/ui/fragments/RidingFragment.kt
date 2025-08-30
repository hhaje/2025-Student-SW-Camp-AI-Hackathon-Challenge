package com.cashride.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cashride.databinding.FragmentRidingBinding
import com.cashride.ui.viewmodel.CashRideViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RidingFragment : Fragment() {
    
    private var _binding: FragmentRidingBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CashRideViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRidingBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // 안전 이벤트 버튼들
        binding.perfectStopBtn.setOnClickListener { viewModel.addSafetyEvent("perfect_stop") }
        binding.safeZoneSlowBtn.setOnClickListener { viewModel.addSafetyEvent("safe_zone_slow") }
        binding.signalObeyBtn.setOnClickListener { viewModel.addSafetyEvent("signal_obey") }
        
        binding.zoneSpeedingBtn.setOnClickListener { viewModel.addSafetyEvent("zone_speeding") }
        binding.phoneUsageBtn.setOnClickListener { viewModel.addSafetyEvent("phone_usage") }
        binding.speedingBtn.setOnClickListener { viewModel.addSafetyEvent("speeding") }
        
        // 운행 종료 버튼
        binding.endRideBtn.setOnClickListener {
            viewModel.endRide()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.currentSpeed.collectLatest { speed ->
                binding.rideSpeed.text = speed.toString()
            }
        }
        
        lifecycleScope.launch {
            viewModel.currentDistance.collectLatest { distance ->
                binding.rideDistance.text = String.format("%.1f", distance)
            }
        }
        
        lifecycleScope.launch {
            viewModel.currentScore.collectLatest { score ->
                binding.currentScore.text = score.toString()
            }
        }
        
        lifecycleScope.launch {
            viewModel.rideStartTime.collectLatest { startTime ->
                startTime?.let { updateRideTime(it) }
            }
        }
    }
    
    private fun updateRideTime(startTime: Long) {
        // 실시간 시간 업데이트는 별도 타이머로 처리
        val elapsed = (System.currentTimeMillis() - startTime) / 1000
        val minutes = elapsed / 60
        val seconds = elapsed % 60
        binding.rideTime.text = String.format("%02d:%02d", minutes, seconds)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
