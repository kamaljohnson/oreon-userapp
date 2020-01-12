package com.xborg.vendx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.Machine
import kotlinx.android.synthetic.main.machine_card.view.*

class MachineCardAdapter(
    val machineCards: List<Machine>,
    val context: Context
) : RecyclerView.Adapter<MachineCardAdapter.MachineCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.machine_card, parent, false)
        return MachineCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return machineCards.size
    }

    override fun onBindViewHolder(holder: MachineCardViewHolder, position: Int) {
        val machine = machineCards[position]
        holder.machineCode.text = machine.code
    }

    class MachineCardViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val machineCode: TextView = view.machine_code
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //TODO: change the current machine to clicked
        }
    }
}
