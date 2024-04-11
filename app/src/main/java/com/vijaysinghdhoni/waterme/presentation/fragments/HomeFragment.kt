package com.vijaysinghdhoni.waterme.presentation.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.vijaysinghdhoni.waterme.R
import com.vijaysinghdhoni.waterme.data.model.DailyWaterLog
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeitem
import com.vijaysinghdhoni.waterme.databinding.FragmentHomeBinding
import com.vijaysinghdhoni.waterme.presentation.adapters.WaterIntakeRvAdapter
import com.vijaysinghdhoni.waterme.presentation.services.WaterWorker
import com.vijaysinghdhoni.waterme.util.Constants.SLEEP_TIME_KEY
import com.vijaysinghdhoni.waterme.util.Constants.WAKEUP_TIME_KEY
import com.vijaysinghdhoni.waterme.viewmodels.WaterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }
    private val waterIntakeRvAdapter: WaterIntakeRvAdapter by lazy {
        WaterIntakeRvAdapter()
    }

    private var usrWaterIntakeAmmnt: WaterIntakeitem? = null

    private val viewModel by activityViewModels<WaterViewModel>()

    private var usersTodaysWaterLog: DailyWaterLog? = null

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
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notification permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        askNotificationPermission()
        setRv()
        scheduleNotification()
        viewModel.getTodayDailyWaterLog()
        viewModel.getUserLastWaterIntake()
        observeUserLastWaterIntake()
        observeDailyWaterLog()
        //notification sleep and wakeup time
        val list = viewModel.getListOfWaterIntakeItems()
        waterIntakeRvAdapter.differ.submitList(list)
        onRvItemClick()
        onAddWtrClick()
    }

    private fun observeUserLastWaterIntake() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lastWaterIntake.collectLatest {
                    if (it != null) {
                        binding.lastWtrAmount.text = "${it.waterAmount} ml"
                        binding.lastWtrTime.text = it.time
                        val imageResource = when (it.waterAmount) {
                            in 0..50 -> R.drawable.small_cup_ic
                            in 51..150 -> R.drawable.water_glass
                            in 151..300 -> R.drawable.water_bottle
                            else -> R.drawable.water_jug
                        }
                        binding.lastWtrIcon.setImageResource(imageResource)
                    } else {
                        binding.lastWtrAmount.text = ""
                    }
                }
            }
        }
    }

    private fun observeDailyWaterLog() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todayDailyWaterLog.collect {
                    Log.d("WATER", it.toString())
                    if (it == null) {
                        Log.d("WATER", "this is null")
                        val dailyWaterLog = DailyWaterLog(
                            waterGoalOfDay = viewModel.getWaterGoalOfTheDay(),
                            waterDrunk = 0,
                            date = SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            ).format(Date())
                        )
                        viewModel.insertDailyWaterLog(dailyWaterLog)
                    } else {
                        Log.d("WATER", "this is not null")
                        usersTodaysWaterLog = it
                        addDataInPieChart()
                        Log.d("WATER", usersTodaysWaterLog.toString())
                    }
                }
            }
        }
    }

    private fun addDataInPieChart() {
        val pieChart = binding.usrWtrPieChart
        usersTodaysWaterLog?.let { dailyWaterLog ->

            pieChart.setUsePercentValues(true)
            pieChart.description.isEnabled = false
            pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
            pieChart.dragDecelerationFrictionCoef = 0.95f

            pieChart.isDrawHoleEnabled = true
            pieChart.setHoleColor(Color.WHITE)
            pieChart.setTransparentCircleColor(Color.WHITE)
            pieChart.setTransparentCircleAlpha(110)

            pieChart.holeRadius = 70f
            pieChart.transparentCircleRadius = 56f

            pieChart.setDrawCenterText(true)

            pieChart.rotationAngle = 0f

            pieChart.isRotationEnabled = true
            pieChart.isHighlightPerTapEnabled = true
            pieChart.animateY(1400, Easing.EaseInOutQuad)

            pieChart.legend.isEnabled = false
            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.setEntryLabelTextSize(12f)
            var part = dailyWaterLog.waterDrunk
            val whole = dailyWaterLog.waterGoalOfDay
            if (part > 2500) {
                part = 2500
            }
            val waterDrunk = ((part.toFloat() / whole) * 100).toInt()
            val waterleft: Int = 100 - waterDrunk

            val entries: ArrayList<PieEntry> = ArrayList()
            entries.add(PieEntry(waterDrunk.toFloat()))
            entries.add(PieEntry(waterleft.toFloat()))

            val dataSet = PieDataSet(entries, "Water Entries")

            dataSet.setDrawIcons(false)

            val centerText = "$part/$whole ml \n \n $waterDrunk% of daily target"

            val centerSpannable = SpannableString(centerText)
            centerSpannable.setSpan(RelativeSizeSpan(1.5f), 0, centerText.indexOf("\n"), 0)

            pieChart.setCenterTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.wblue_color
                )
            )
            pieChart.setCenterTextSize(14f)
            pieChart.centerText = centerSpannable

            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0f, 40f)
            dataSet.selectionShift = 5f

            val colors: ArrayList<Int> = ArrayList()
            colors.add(ContextCompat.getColor(requireContext(), R.color.wblue_color))
            colors.add(ContextCompat.getColor(requireContext(), R.color.white))


            dataSet.colors = colors

            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(15f)
            data.setValueTypeface(Typeface.DEFAULT_BOLD)
            data.setValueTextColor(Color.WHITE)
            data.setDrawValues(false)
            pieChart.data = data

            pieChart.highlightValues(null)


            pieChart.invalidate()

        }
    }


    @SuppressLint("InflateParams")
    private fun onAddWtrClick() {
        binding.addWaterIntake.setOnClickListener {
            if (usrWaterIntakeAmmnt != null) {
                Log.d("WATER", usrWaterIntakeAmmnt.toString())
                viewModel.insertWaterIntake(usrWaterIntakeAmmnt!!.amount)
            } else {

                val builder = AlertDialog.Builder(requireContext())
                val inflater = requireActivity().layoutInflater
                val dialogView = inflater.inflate(R.layout.custom_input_box_layout, null)
                val editText = dialogView.findViewById<EditText>(R.id.custom_water_input)

                builder.setTitle("Enter Custom Input")
                    .setView(dialogView)
                    .setPositiveButton("Ok") { dialog, _ ->
                        val customInputText = editText.text.toString()
                        if (customInputText.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Custom water intake is empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.insertWaterIntake(customInputText.toInt())
                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun onRvItemClick() {
        waterIntakeRvAdapter.onClick = {
            usrWaterIntakeAmmnt = it
        }
    }

    private fun setRv() {
        binding.waterIntakeRv.apply {
            adapter = waterIntakeRvAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun scheduleNotification() {
        val timeInterval = viewModel.getTimeIntervalForNotification()
        val waterWorkRequest =
            PeriodicWorkRequestBuilder<WaterWorker>(
                timeInterval,
                java.util.concurrent.TimeUnit.HOURS
            )
                .setInitialDelay(timeInterval, java.util.concurrent.TimeUnit.HOURS)
                .build()
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        workManager.enqueueUniquePeriodicWork(
            "NotificationWorkTag",
            ExistingPeriodicWorkPolicy.KEEP,
            waterWorkRequest
        )
    }

    private fun createInputData(wakeupTime: Long, sleepTime: Long): Data {
        return Data.Builder()
            .putLong(WAKEUP_TIME_KEY, wakeupTime)
            .putLong(SLEEP_TIME_KEY, sleepTime)
            .build()
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
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

                Toast.makeText(
                    requireContext(),
                    "Please grant Notification permission from setting",
                    Toast.LENGTH_SHORT
                ).show()
                //     requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}