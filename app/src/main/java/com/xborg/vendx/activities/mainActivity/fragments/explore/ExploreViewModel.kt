package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xborg.vendx.database.Location


class ExploreViewModel(
    application: Application
) : AndroidViewModel(application) {

    val userLocation = MutableLiveData<Location>()

}