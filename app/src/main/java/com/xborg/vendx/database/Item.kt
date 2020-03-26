package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("name") var Name: String
) {
    override fun toString(): String {
        return "category='$Name'"
    }
}

/*
*  This data class will store details of items in both user inventory and also
*  in machine inventory.
*  This is done to make the billing and item selection implementation simple
* */
data class Item(
    // item details
    @SerializedName("id") var Id: String,
    @SerializedName("name") var Name: String,
    @SerializedName("cost") var Cost: Long,
    @SerializedName("discounted_cost") var DiscountedCost: Long,
    @SerializedName("category") var Category: Category,

    // listing assets
    @SerializedName("foreground_asset") var ForegroundAsset: String,
    @SerializedName("background_asset") var BackgroundAsset: String,
    @SerializedName("content_asset") var ContentAsset: String,

    // required for billing and display
    @SerializedName("from_inventory") var FromInventory: Boolean = false,                  // if the current item is referring to item in users inventory
    @SerializedName("from_machine") var FromMachine: Boolean = false,                      //                        ...                  machine inventory
    @SerializedName("stock") var MachineStock: Int = -1,                                   // the number of items in the machine
    @SerializedName("quantity") var InventoryStock: Int = -1,                              //          ...        in user-inventory

    // required for billing
    var CartCount: Int = 0
) {
    override fun toString(): String {
        return "Item(id='$Id', name='$Name', inShelf=$FromInventory, inMachine=$FromMachine, remainingInMachine=$MachineStock, remainingInShelf=$InventoryStock)"
    }
}