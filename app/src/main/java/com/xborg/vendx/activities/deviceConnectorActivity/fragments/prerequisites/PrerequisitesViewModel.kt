package com.xborg.vendx.activities.deviceConnectorActivity.fragments.prerequisites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class Permissions {
    None,
    Bluetooth,
    BluetoothAdmin,
    FineLocation
}

class PrerequisitesViewModel : ViewModel() {

    var permissionsGranted = MutableLiveData<MutableMap<Permissions, Boolean>>()
    var currentConnectionModePermissionRequirements =
        MutableLiveData<MutableMap<Permissions, Boolean>>()

    var grantComplete = MutableLiveData<Boolean>()

    var currentPermissionToBeGranted = MutableLiveData<Permissions>()

    init {
        grantComplete.value = false
        permissionsGranted.value = mutableMapOf(
            Permissions.Bluetooth to false,
            Permissions.BluetoothAdmin to false,
            Permissions.FineLocation to false
        )
        checkPermissionRequirement()
    }

    fun updatePermissionsGranted(grantedPermission: Permissions) {
        permissionsGranted.value!![grantedPermission] = true
        checkPermissionRequirement()
    }

    fun checkPermissionRequirement() {
        var flag = true
        var nextPermissionToBeRequested = Permissions.None
        currentConnectionModePermissionRequirements.value!!.forEach loop@{ (permission, status) ->
            var permissionStatus = permissionsGranted.value!![permission]!!
            currentConnectionModePermissionRequirements.value!![permission] = permissionStatus
            if (!permissionStatus) {
                flag = false
                nextPermissionToBeRequested = permission
                return@loop
            }
        }

        currentPermissionToBeGranted.value = nextPermissionToBeRequested
        grantComplete.value = flag
    }

}