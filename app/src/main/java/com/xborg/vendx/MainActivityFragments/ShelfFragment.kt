package com.xborg.vendx.MainActivityFragments
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.SupportClasses.Item
import com.xborg.vendx.SupportClasses.ItemAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.rv_items_list
import com.xborg.vendx.MainActivity
import com.xborg.vendx.R
import kotlinx.android.synthetic.main.fragment_shelf.*

private var TAG = "ShelfFragment"

class ShelfFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()

    val items: ArrayList<Item> = ArrayList()               //all the items in the inventory list
    var temp_items: ArrayList<Item> = ArrayList()

    var is_visible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getShelfItems()

        val activity = activity as MainActivity?
        activity?.search_text?.addTextChangedListener{
            Log.e(TAG, "the searching string is ${it.toString()}")
            if(it.toString().isNotEmpty()) {
                search(it.toString())
            } else {
                rv_items_list.removeAllViews()
                addItemsToRV(items)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_shelf, container, false)

    @SuppressLint("ResourceAsColor")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser) {
            Log.e(TAG, "fragment Shelf is visible")
            is_visible = true
            val activity = activity as MainActivity?
            activity?.shelf_button?.setTextColor(Color.WHITE)
            activity?.home_button?.setTextColor(Color.parseColor("#FF9800"))
            activity?.home_button?.setBackgroundResource(R.color.fui_transparent)
            activity?.shelf_button?.setBackgroundResource(R.drawable.rounded_button_orange)
        } else {
            is_visible = false
        }
    }

    private fun getShelfItems(){
        db.document("Users/$uid")
            .get()
            .addOnSuccessListener { userSnap ->
                if(userSnap.data?.get("Shelf")  == null) {

                } else {
                    var shelfItems: Map<String, Number> = userSnap.data?.get("Shelf") as Map<String, Number>
                    if(shelfItems.count() == 0) {
                        shelf_empty_text.visibility = View.VISIBLE
                    }
                    for (shelfItem in shelfItems){
                        Log.d(TAG, shelfItem.key)
                        Log.d(TAG, shelfItem.value.toString())

                        var itemId = shelfItem.key
                        var quantity = shelfItem.value

                        db.document("Inventory/${itemId}")
                            .get()
                            .addOnSuccessListener { document ->
                                Log.d(TAG, "${document.id} => ${document.data}")

                                val item = Item()

                                item.item_id = document.id
                                item.name = document.data?.get("Name").toString()
                                item.quantity = document.data?.get("Quantity").toString()
                                item.cost = "-1"    // shelf items are already bought, no need to show the cost
                                item.item_limit = quantity.toString()
                                item.image_src = document.data?.get("Image").toString()

                                items.add(item)

                                if(items.size == shelfItems.size) {
                                    addItemsToRV(items)
                                }
                            }
                            .addOnFailureListener{exception ->
                                Log.w(TAG, "Error getting documents.", exception)
                            }
                    }
                }
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    /**
     * adds all the items to the recycler view
     * as item_card cards
     */
    private fun addItemsToRV(items: ArrayList<Item>){
        rv_items_list.layoutManager = LinearLayoutManager(context)
        rv_items_list.layoutManager = GridLayoutManager(context, 2)
        val block: (Context) -> ItemAdapter = { ItemAdapter(items, it) }
        rv_items_list.adapter = context?.let(block)
    }


    //    region item_card search

    private fun search(search_name: String) {
        temp_items = ArrayList()
        for (item in items) {
            Log.d(TAG, item.toString())
            var i = 0
            var j = 0
            while(i < item.name.length) {
                if(item.name[i].toUpperCase() == search_name[j].toUpperCase()) {
                    j++
                    if(j == search_name.length) {
                        temp_items.add(item)
                        break
                    }
                }
                i++
            }
            Log.d(TAG, temp_items.size.toString())
        }
        Log.e(TAG, MainActivity.cart_items.toString())
        rv_items_list.removeAllViews()
        addItemsToRV(temp_items)
    }

//    endregion
}