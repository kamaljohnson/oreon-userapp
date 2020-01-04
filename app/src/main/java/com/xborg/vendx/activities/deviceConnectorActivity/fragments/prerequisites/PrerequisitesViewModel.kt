package com.xborg.vendx.activities.deviceConnectorActivity.fragments.prerequisites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "PrerequisitesViewModel"

enum class Permissions {
    None,
    Bluetooth,
    BluetoothAdmin,
    FineLocation
}

class PrerequisitesViewModel : ViewModel() {

    var permissionsGranted = MutableLiveData<MutableMap<Permissions, Boolean>>()
    var currentConnectionModePermissionRequirements =
        MutableLiveData<MutableList<Permissions>>()

    var grantComplete = MutableLiveData<Boolean>()

    var currentPermissionToBeGranted = MutableLiveData<Permissions>()

    init {
        grantComplete.value = false
        permissionsGranted.value = mutableMapOf(
            Permissions.Bluetooth to false,
            Permissions.BluetoothAdmin to false,
            Permissions.FineLocation to false
        )
    }

    fun updatePermissionsGranted(grantedPermission: Permissions) {
        permissionsGranted.value!![grantedPermission] = true
        Log.i(TAG, "permissions Granted : " + permissionsGranted.value.toString())
        checkPermissionRequirement()
    }

    fun checkPermissionRequirement() {
        currentConnectionModePermissionRequirements.value!!.forEach { permission ->
            var permissionStatus = permissionsGranted.value!![permission]!!
            if (!permissionStatus) {
                Log.i(TAG, "current permission req: $permission")
                currentPermissionToBeGranted.value = permission
                return
            }
        }
        grantComplete.value = true

    }
}