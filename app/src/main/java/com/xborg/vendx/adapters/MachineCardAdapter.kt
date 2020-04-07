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
import com.xborg.vendx.activities.mainActivity.fragments.home.HomeViewModel
import com.xborg.vendx.database.machine.Machine
import com.xborg.vendx.database.machine.MachineDao
import com.xborg.vendx.database.machine.MachineDatabase
import kotlinx.android.synthetic.main.machine_card.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

private var TAG = "MachineCard"

private lateinit var machineDao: MachineDao

private val viewModelJob = Job()
private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

class MachineCardAdapter(
    val context: Context
) : ListAdapter<Machine, MachineCardAdapter.MachineCardViewHolder>(MachineCardDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineCardViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.machine_card, parent, false)
        machineDao = MachineDatabase.getInstance(context).machineDao()

        return MachineCardViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MachineCardViewHolder, position: Int) {

        val machine = getItem(position)
        holder.machineName.text = machine.Name

        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
//        holder.distance.text = "${df.format(machine.Distance)}Km"

    }

    class MachineCardViewHolder(view: View ) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val machineName: TextView = view.machine_name
        val distance: TextView = view.distance

        init {

            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View?) {

            Log.i(TAG, "machine card clicked")

            ioScope.launch {

                HomeViewModel.selectedMachine.postValue(machineDao.get(machineName.text.toString()))

            }

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

