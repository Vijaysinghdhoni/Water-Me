package com.vijaysinghdhoni.waterme.presentation.adapters

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vijaysinghdhoni.waterme.R
import com.vijaysinghdhoni.waterme.data.model.DailyWaterLog
import com.vijaysinghdhoni.waterme.databinding.LalstDaysGoalsRvItemBinding
import java.util.*

class LastDaysAdapter : RecyclerView.Adapter<LastDaysAdapter.LastDaysViewHolder>() {

    private val callBack = object : DiffUtil.ItemCallback<DailyWaterLog>() {
        override fun areItemsTheSame(oldItem: DailyWaterLog, newItem: DailyWaterLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DailyWaterLog, newItem: DailyWaterLog): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, callBack)

    inner class LastDaysViewHolder(private val binding: LalstDaysGoalsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dailyWaterLog: DailyWaterLog) {
            val imageResource = when (dailyWaterLog.waterDrunk) {
                in 0..1000 -> R.drawable.trophy_thrid
                in 1001..2000 -> R.drawable.trophy_second
                else -> R.drawable.trophyfirst
            }

            binding.goalItemImg.setImageResource(imageResource)
            val day = getDayOfWeek(dailyWaterLog.date)
            binding.dayTxt.text = day
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
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                else -> ""
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastDaysViewHolder {
        return LastDaysViewHolder(
            LalstDaysGoalsRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: LastDaysViewHolder, position: Int) {
       val waterLog = differ.currentList[position]
        holder.bind(waterLog)
    }

}