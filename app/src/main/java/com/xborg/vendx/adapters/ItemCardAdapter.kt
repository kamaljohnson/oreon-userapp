package com.xborg.vendx.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xborg.vendx.R
import com.xborg.vendx.database.*
import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private var TAG = "ItemCardAdapter"

private lateinit var itemDetailDao: ItemDetailDao
private lateinit var cartItemDao: CartItemDao

private val viewModelJob = Job()
private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
private val uiScope = CoroutineScope(Dispatchers.Main)

private val cart = MutableLiveData<List<CartItem>>()

class ItemCardAdapter(
    private val paidItemGroup: Boolean,
    val context: Context
) : ListAdapter<InventoryItem, ItemCardAdapter.ItemViewHolder>(ItemCardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)

        itemDetailDao = ItemDetailDatabase.getInstance(context).itemDetailDatabaseDao
        cartItemDao = CartItemDatabase.getInstance(context).cartItemDao()

        return ItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val inventoryItem = getItem(position)

        val itemDetailId = inventoryItem.ItemDetailId

        ioScope.launch {
            val itemDetail = itemDetailDao.get(itemDetailId)

            holder.itemId.text = itemDetail!!.Id
            holder.name.text = itemDetail.Name
            holder.cost.text = "₹ " + itemDetail.Cost.toString()

            uiScope.launch {
                Glide
                    .with(context)
                    .load(itemDetail.ForegroundAsset)
                    .into(holder.packageImage)
                Glide
                    .with(context)
                    .load(itemDetail.BackgroundAsset)
                    .into(holder.cardBg)
            }
        }

        holder.paid = paidItemGroup

        holder.quantity.text = inventoryItem.Quantity.toString()

        cartItemDao.getLiveCartItem(itemDetailId, paidItemGroup).observe(holder.itemView.context as LifecycleOwner, Observer { item ->
            if(item != null) {
                Log.i(TAG, "updated card item $item")

                val count = item.Count

                holder.purchaseCount.text = count.toString()

                holder.purchaseCount.visibility = View.VISIBLE
                holder.itemRemoveButton.visibility = View.VISIBLE

            } else {
                holder.purchaseCount.visibility = View.INVISIBLE
                holder.itemRemoveButton.visibility = View.INVISIBLE
            }
        })

    }

    @SuppressLint("SetTextI18n")
    class ItemViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemId: TextView = view.item_id
        val name: TextView = view.name
        val cost: TextView = view.cost
        val packageImage: ImageView = view.package_image
        val cardBg: ImageView = view.card_bg
        val purchaseCount: TextView = view.purchase_count
        val quantity: TextView = view.quantity

        val itemRemoveButton: ImageView = view.remove_button

        var paid: Boolean = false

        init {
            purchaseCount.visibility = View.INVISIBLE
            itemRemoveButton.visibility = View.INVISIBLE

            itemView.setOnClickListener(this)
            itemRemoveButton.setOnClickListener {
                removeItemFromCart()
            }
        }

        override fun onClick(v: View?) {
            addItemToCart()
        }

        private fun addItemToCart() {

            Log.i(TAG, "paid : $paid")

            if(paid) {

                val purchaseCountInt = purchaseCount.text.toString().toInt()
                val quantityInt = quantity.text.toString().toInt()

                if( purchaseCountInt == quantityInt ){
                    return
                }

            }

            ioScope.launch {

                val status = cartItemDao.addItem(itemId.text.toString(), paid)

                uiScope.launch {
                    when(status) {

                        CartStatusCode.ItemNotInMachine -> {

                            Toast.makeText(itemView.context, "Item not in machine", Toast.LENGTH_SHORT).show()
                        }

                        CartStatusCode.MachineNotSelected -> {

                            Toast.makeText(itemView.context, "No machine selected", Toast.LENGTH_SHORT).show()

                        }

                        CartStatusCode.ItemNotRemainingInMachine -> {

                            Toast.makeText(itemView.context, "Item not remaining in machine", Toast.LENGTH_SHORT).show()

                        }

                        CartStatusCode.ItemAddedSuccessfully -> {



                        }
                    }
                }
            }
        }

        private fun removeItemFromCart() {
            ioScope.launch {
                cartItemDao.removeItem(itemId.text.toString(), paid)
            }
        }

    }
}

class ItemCardDiffCallback: DiffUtil.ItemCallback<InventoryItem>() {
    override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
        return oldItem.ItemDetailId == newItem.ItemDetailId
    }

    override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
        return oldItem == newItem
    }
}

