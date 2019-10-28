package com.xborg.vendx.MainActivityFragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xborg.vendx.R
import com.xborg.vendx.SupportClasses.Item
import com.xborg.vendx.SupportClasses.ItemAdapter
import kotlinx.android.synthetic.main.fragment_home.*

private var TAG = "ShelfFragment"

class ShelfFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_shelf, container, false)

    /**
     * adds all the items to the recycler view
     * as item cards
     */
    private fun addItemsToRV(items: ArrayList<Item>){
        rv_items_list.layoutManager = LinearLayoutManager(context)
        rv_items_list.layoutManager = GridLayoutManager(context, 2)
        rv_items_list.adapter = context?.let { ItemAdapter(items, it) }
    }
}