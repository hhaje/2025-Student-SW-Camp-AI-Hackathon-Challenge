package com.cashride.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cashride.databinding.FragmentMainBinding
import com.cashride.ui.viewmodel.CashRideViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CashRideViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        binding.startRideBtn.setOnClickListener {
            viewModel.startRide()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.user.collectLatest { user ->
                user?.let {
                    binding.consecutiveRides.text = "${it.consecutiveSafeRides}회 연속 안전운전"
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
