package com.vijaysinghdhoni.waterme.presentation.fragments

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.vijaysinghdhoni.waterme.R
import com.vijaysinghdhoni.waterme.databinding.FragmentStatisticBinding
import com.vijaysinghdhoni.waterme.presentation.adapters.LastDaysAdapter
import com.vijaysinghdhoni.waterme.viewmodels.WaterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class StatisticFragment : Fragment() {
    private val binding: FragmentStatisticBinding by lazy {
        FragmentStatisticBinding.inflate(layoutInflater)
    }
    private val lastAdapter by lazy {
        LastDaysAdapter()
    }
    private val viewModel by activityViewModels<WaterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setlastGoalAdapter()
        viewModel.getLastSevenDaysDailyWaterLog()
        viewModel.getLast7DaysWaterIntakes()
        setBarChart()
        lastWaterIntakeObserve()
    }

    //average occurrence (or frequency) of water intake per day
    @SuppressLint("SetTextI18n")
    private fun lastWaterIntakeObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.last7daysWaterIntake.collectLatest { waterIntakeList ->

                    if (waterIntakeList.isEmpty()) {
                        Log.d("last", "last seven days water intake is empty")
                    } else {
                        Log.d("last", "last seven days water intake is $waterIntakeList")
                        Log.d("last", "last seven days water intake is ${waterIntakeList.size}")
                        val lastSevendaysWaterIntakeNumber = waterIntakeList.size
                        val waterPerDayFrequencyDecimal =
                            lastSevendaysWaterIntakeNumber.toDouble() / 7
                        val waterPerDayFrequency = "%.2f".format(waterPerDayFrequencyDecimal)
                        binding.frequencyNumber.text = "$waterPerDayFrequency Times/day"
                    }


                }
            }
        }
    }

    private fun setlastGoalAdapter() {
        binding.lastGoalsRv.apply {
            adapter = lastAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setBarChart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lastSevenDaysWaterLog.collectLatest { waterIntakeList ->
                    if (waterIntakeList.isEmpty()) {
                        binding.noAchivementTxt.visibility = View.VISIBLE
                        binding.lastGoalsRv.visibility = View.GONE
                        Log.d("bar", "is empty")
                    } else {
                        Log.d("bar", "last seven $waterIntakeList")
                        binding.noAchivementTxt.visibility = View.GONE
                        binding.lastGoalsRv.visibility = View.VISIBLE
                        val sum = waterIntakeList.sumOf {
                            it.waterDrunk
                        }
                        val averageWithAllDecimal = sum.toDouble() / waterIntakeList.size
                        val average = "%.2f".format(averageWithAllDecimal)
                        val sevenDaysWaterGoal = waterIntakeList[0].waterGoalOfDay * 7
                        val averageCompletionWithAllDecimal =
                            (sum.toDouble() / sevenDaysWaterGoal) * 100
                        val averageCompletion = "%.2f".format(averageCompletionWithAllDecimal)
                        binding.itemAverageNumber.text = "$average ml/day"
                        binding.completionNumber.text = "$averageCompletion %"
                        lastAdapter.differ.submitList(waterIntakeList)
                        val xLabels = ArrayList<String>()
                        for (i in waterIntakeList) {
                            val day = getDayOfWeek(i.date)
                            Log.d("bar", day)
                            xLabels.add(day)
                        }
                        val yValues = ArrayList<BarEntry>()
                        for (i in waterIntakeList.indices) {
                            yValues.add(
                                BarEntry(
                                    i.toFloat(),
                                    waterIntakeList[i].waterDrunk.toFloat()
                                )
                            )
                        }

                        val barDataSet = BarDataSet(yValues, "Last 7 Days Water Intake")
                        val xAxis = binding.barChart.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
                        val barData = BarData(barDataSet)
                        binding.barChart.data = barData
                        barDataSet.valueTextColor =
                            ContextCompat.getColor(requireContext(), R.color.wblue_color)
                        barDataSet.color =
                            ContextCompat.getColor(requireContext(), R.color.wblue_color)
                        binding.barChart.description.isEnabled = false
                        binding.barChart.axisRight.isEnabled = false
                        binding.barChart.animateY(1000, Easing.EaseInCubic)
                        binding.barChart.invalidate()
                    }

                }

            }

        }
    }


    private fun getDayOfWeek(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }

        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sun"
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thur"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            else -> ""
        }
    }
}