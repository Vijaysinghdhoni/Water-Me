package com.vijaysinghdhoni.waterme.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vijaysinghdhoni.waterme.R
import com.vijaysinghdhoni.waterme.databinding.WaterIntakeRvItemBinding
import com.vijaysinghdhoni.waterme.data.model.WaterIntakeitem

class WaterIntakeRvAdapter : RecyclerView.Adapter<WaterIntakeRvAdapter.WaterIntakeRvViewHolder>() {

    private var selectedPosition = -1

    private val callBack = object : DiffUtil.ItemCallback<WaterIntakeitem>() {
        override fun areItemsTheSame(oldItem: WaterIntakeitem, newItem: WaterIntakeitem): Boolean {
            return oldItem.amount == newItem.amount
        }

        override fun areContentsTheSame(
            oldItem: WaterIntakeitem,
            newItem: WaterIntakeitem
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, callBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterIntakeRvViewHolder {
        return WaterIntakeRvViewHolder(
            WaterIntakeRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: WaterIntakeRvViewHolder, position: Int) {
        val waterIntakeItem = differ.currentList[position]
        holder.bind(waterIntakeItem, position)
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onClick?.invoke(waterIntakeItem)
        }
    }

    var onClick: ((waterIntakeitem: WaterIntakeitem) -> Unit)? = null

    inner class WaterIntakeRvViewHolder(private val binding: WaterIntakeRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(waterIntakeitem: WaterIntakeitem, position: Int) {

            binding.wtrIntkAmnt.text = "${waterIntakeitem.amount} ml"
            binding.wtrIntkImg.setImageResource(waterIntakeitem.image)

            if (position == selectedPosition) {
                binding.root.setBackgroundResource(R.drawable.rv_item_bg)
            } else {
                binding.root.setBackgroundResource(R.color.white)
            }

        }


    }
}