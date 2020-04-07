package com.xborg.vendx.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.machine.Machine
import kotlinx.android.synthetic.main.machine_card.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

private var TAG = "MachineCard"

class MachineCardAdapter(
    val context: Context
) : ListAdapter<Machine, MachineCardAdapter.MachineCardViewHolder>(MachineCardDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineCardViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.machine_card, parent, false)

        return MachineCardViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MachineCardViewHolder, position: Int) {
        Log.i(TAG, " here ")

        val machine = getItem(position)
        holder.machineCode.text = machine.Name

        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
//        holder.distance.text = "${df.format(machine.Distance)}Km"

        holder.machineId = machine.Name.toString()
    }

    class MachineCardViewHolder(view: View ) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val machineCode: TextView = view.machine_code
        val distance: TextView = view.distance

        var machineId: String = ""

        override fun onClick(v: View?) {

        }
    }
}

class MachineCardDiffCallback: DiffUtil.ItemCallback<Machine>() {
    override fun areItemsTheSame(oldItem: Machine, newItem: Machine): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: Machine, newItem: Machine): Boolean {
        return oldItem == newItem
    }
}

