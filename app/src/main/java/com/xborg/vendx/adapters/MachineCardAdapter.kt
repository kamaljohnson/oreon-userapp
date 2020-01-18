package com.xborg.vendx.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.Machine
import kotlinx.android.synthetic.main.machine_card.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

private var TAG = "MachineCard"

class MachineCardAdapter(
    private val machineCards: List<Machine>,
    val context: Context,
    machineCardListener: OnMachineCardListener
) : RecyclerView.Adapter<MachineCardAdapter.MachineCardViewHolder>() {

    private val _machineCardListener: OnMachineCardListener = machineCardListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.machine_card, parent, false)
        return MachineCardViewHolder(view, _machineCardListener)
    }

    override fun getItemCount(): Int {
        return machineCards.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MachineCardViewHolder, position: Int) {
        val machine = machineCards[position]
        holder.machineCode.text = machine.code

        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        holder.distance.text = "${df.format(machine.distance)}Km"

        holder.machineId = machine.id
    }

    class MachineCardViewHolder(view: View, cardListener: OnMachineCardListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val machineCode: TextView = view.machine_code
        val distance: TextView = view.distance

        var machineId: String = ""
        private val onMachineCardListener: OnMachineCardListener = cardListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onCardClicked()
            //TODO: change the current machine to clicked
        }

        private fun onCardClicked() {
            onMachineCardListener.onCardClicked(machineId)
        }
    }

    interface OnMachineCardListener {
        fun onCardClicked(machineId: String)
    }
}
