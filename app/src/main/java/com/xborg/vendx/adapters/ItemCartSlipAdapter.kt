package com.xborg.vendx.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xborg.vendx.R
import com.xborg.vendx.database.*
import kotlinx.android.synthetic.main.item_cart_slip.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private lateinit var itemDetailDao: ItemDetailDao
private lateinit var cartItemDao: CartItemDao

private val viewModelJob = Job()
private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
private val uiScope = CoroutineScope(Dispatchers.Main)

private val cart = MutableLiveData<List<CartItem>>()

class ItemCartSlipAdapter(
    val cart: List<CartItem>,
    val context: Context
) : RecyclerView.Adapter<ItemCartSlipAdapter.ItemSlipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart_slip, parent, false)

        itemDetailDao = ItemDetailDatabase.getInstance(context).itemDetailDatabaseDao
        cartItemDao = CartItemDatabase.getInstance(context).cartItemDao()

        return ItemSlipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cart.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemSlipViewHolder, position: Int) {
        val cartItem = cart[position]

        val itemDetailId = cartItem.ItemDetailId

        holder.paidIcon.visibility = if(cartItem.Paid) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.cost.visibility = if(cartItem.Paid) {
            View.GONE
        } else {
            View.VISIBLE
        }

        holder.count.text = cartItem.Count.toString()

        ioScope.launch {
            val itemDetail = itemDetailDao.get(itemDetailId)

            uiScope.launch {

                holder.name.text = itemDetail!!.Name

                holder.cost.text = "₹ " + itemDetail.Cost.toString()

                Glide
                    .with(context)
                    .load(itemDetail.ForegroundAsset)
                    .into(holder.packageImage)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    class ItemSlipViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val name: TextView = view.name
        val cost: TextView = view.cost
        val packageImage: ImageView = view.package_image
        val count: TextView = view.count
        val paidIcon: ImageView = view.paid_icon

        init {

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //TODO: can be used in the future
        }
    }

}
