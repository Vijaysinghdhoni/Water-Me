package com.vijaysinghdhoni.waterme.presentation.fragments

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.vijaysinghdhoni.waterme.databinding.FragmentSettingBinding
import com.vijaysinghdhoni.waterme.viewmodels.WaterViewModel
import kotlinx.coroutines.launch


class SettingFragment : Fragment() {
    private val binding: FragmentSettingBinding by lazy {
        FragmentSettingBinding.inflate(layoutInflater)
    }
    private val viewModel by activityViewModels<WaterViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Notification permission granted",
                    Toast.LENGTH_SHORT
                ).show()
                binding.notifySwitch.isChecked = true
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notification permission denied",
                    Toast.LENGTH_SHORT
                ).show()
                binding.notifySwitch.isChecked = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserWakeupTime()
        viewModel.getUserSleepTime()
        setUi()
        updateBttn()
        checkSelfPermision()
        notificationUpdate()
    }

    private fun notificationUpdate() {
        binding.notifySwitch.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                askNotificationPermission()
            }
        }
    }

    private fun updateBttn() {
        val picker1 = createTimePicker()
        val picker2 = createTimePicker()


        binding.morningTimeLay.setOnClickListener {
            picker1.show(requireActivity().supportFragmentManager, "Morning Time")
            picker1.addOnPositiveButtonClickListener {
                val hour = picker1.hour
                val minute = picker1.minute
                binding.wakeupTime.setText(String.format("%02d:%02d", hour, minute))
            }
        }
        binding.sleepLay.setOnClickListener {
            picker2.show(requireActivity().supportFragmentManager, "Sleep Time")
            picker2.addOnPositiveButtonClickListener {
                val hour = picker2.hour
                val minute = picker2.minute
                binding.sleepTime.setText(String.format("%02d:%02d", hour, minute))
            }
        }


        binding.upsateBttn.setOnClickListener {
            val userWakeupTime = binding.wakeupTime.text.toString()
            val userSleepTime = binding.sleepTime.text.toString()
            val waterGoal = binding.etWaterGoal.text.toString()

            if (userSleepTime.isNotEmpty() && userWakeupTime.isNotEmpty() && waterGoal.isNotEmpty()) {
                val wakeupTimeParts = userWakeupTime.split(":")
                val sleepTimeParts = userSleepTime.split(":")

                if (wakeupTimeParts.size == 2 && sleepTimeParts.size == 2) {

                    val wakeupHour = wakeupTimeParts[0].toInt()
                    val wakeupMinute = wakeupTimeParts[1].toInt()
                    val sleepHour = sleepTimeParts[0].toInt()
                    val sleepMinute = sleepTimeParts[1].toInt()

                    val wakeupTimeInMillis =
                        wakeupHour * 60 * 60 * 1000L + wakeupMinute * 60 * 1000L
                    val sleepTimeInMillis = sleepHour * 60 * 60 * 1000L + sleepMinute * 60 * 1000L

                    viewModel.setUserSleepTime(sleepTimeInMillis)
                    viewModel.setUserWakeupTime(wakeupTimeInMillis)
                    viewModel.setWaterGoalOfTheDay(waterGoal.toInt())
                    viewModel.getUserSleepTime()
                    viewModel.getUserWakeupTime()
                    Log.d("Tag", "waterGoal time is ${waterGoal.toInt()}")
                    Log.d("Tag", "sleep time is $userSleepTime")
                    Log.d("Tag", "wakeupTime  is $userWakeupTime")
                    Log.d("Tag", "waterGoal time is $sleepTimeInMillis")
                    Log.d("Tag", "sleep time is $wakeupTimeInMillis")
                } else {
                    Log.e("Tag", "Incorrect time format")
                }
            } else {
                Log.d("Tag", "Both wakeup and sleep times are required")
                Toast.makeText(context, "Please enter wakeup and sleep times", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setUi() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userSleepTime.collect {
                    val time = formatTimeFromMillis(it)
                    binding.sleepTime.setText("$time pm")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userWakeupTime.collect {
                    val time = formatTimeFromMillis(it)
                    binding.wakeupTime.setText("$time am")
                }
            }
        }

        binding.apply {
            etWaterGoal.setText(viewModel.getWaterGoalOfTheDay().toString())
        }
    }

    private fun createTimePicker() = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_12H)
        .setHour(12)
        .setMinute(10)
        .setTitleText("Select Time")
        .build()


    private fun formatTimeFromMillis(milliseconds: Long): String {
        val hours = milliseconds / (60 * 60 * 1000)
        val minutes = (milliseconds % (60 * 60 * 1000)) / (60 * 1000)
        return String.format("%02d:%02d", hours, minutes)
    }

    private fun checkSelfPermision() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // alredy granted
                Toast.makeText(
                    requireContext(),
                    "Notification is alredy  granted",
                    Toast.LENGTH_SHORT
                ).show()
                binding.notifySwitch.isChecked = true
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // alredy granted
                Toast.makeText(
                    requireContext(),
                    "Notification is alredy  granted",
                    Toast.LENGTH_SHORT
                ).show()
                binding.notifySwitch.isChecked = true
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

                Toast.makeText(
                    requireContext(),
                    "Please grant Notification permission",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }else{
            Toast.makeText(
                requireContext(),
                "Notification permission are granted by default",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}