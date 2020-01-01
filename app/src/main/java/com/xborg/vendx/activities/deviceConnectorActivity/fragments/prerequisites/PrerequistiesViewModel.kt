package com.xborg.vendx.activities.deviceConnectorActivity.fragments.prerequisites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class Permissions {
    Bluetooth,
    FineLocation
}

class PrerequistiesViewModel: ViewModel() {

    var permissionsGranted =  MutableLiveData<MutableMap<Permissions, Boolean>>()
    var currentConnectionModeRequirement =  MutableLiveData<MutableMap<Permissions, Boolean>>()

    var grantComplete =  MutableLiveData<Boolean>()

    init {
        grantComplete.value = false
        permissionsGranted.value = mutableMapOf(
            Permissions.Bluetooth to false,
            Permissions.FineLocation to false
        )
    }

    fun checkPermissionRequirement() {
        var allPermissionsGranted = true
        currentConnectionModeRequirement.value!!.forEach { (permission, status) ->
            var permissionStatus = permissionsGranted.value!![permission]!!
            currentConnectionModeRequirement.value!![permission] = permissionStatus
            if(!permissionStatus) {
                allPermissionsGranted = false
            }
        }

        grantComplete.value = allPermissionsGranted
    }

}