package com.xborg.vendx.activities.mainActivity.fragments.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Location


class ExploreViewModel : ViewModel() {

    val userLocation = MutableLiveData<Location>()

}