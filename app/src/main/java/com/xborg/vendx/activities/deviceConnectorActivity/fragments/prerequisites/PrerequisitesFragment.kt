package com.xborg.vendx.activities.deviceConnectorActivity.fragments.prerequisites

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.deviceConnectorActivity.SharedViewModel
import com.xborg.vendx.activities.mainActivity.MainActivity

const val REQUEST_ENABLE_BLUETOOTH = 1
const val REQUEST_ENABLE_BLUETOOTH_ADMIN = 2
const val REQUEST_ENABLE_LOC = 3

private const val TAG = "PrerequisitesFragment"

class PrerequisitesFragment : Fragment() {

    private lateinit var viewModel: PrerequisitesViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prerequisites, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(PrerequisitesViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.currentConnectionModePermissionRequirements.observe(this, Observer { permissionRequirements ->
            viewModel.currentConnectionModePermissionRequirements.value = permissionRequirements
            Log.i(TAG, " ---> $permissionRequirements")
            viewModel.checkPermissionRequirement()
        })

        viewModel.currentPermissionToBeGranted.observe(this, Observer {
            checkRequiredPermissions()
        })

        viewModel.grantComplete.observe(this, Observer { grantStatus ->
            if (grantStatus) {
                Log.i(TAG, "calling jump from prerequisites")
                sharedViewModel.jumpToNextStep()
            }
        })
    }

    fun checkRequiredPermissions() {
        when (viewModel.currentPermissionToBeGranted.value) {
            Permissions.Bluetooth -> {
                if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity!!,
                            Manifest.permission.BLUETOOTH
                        )
                    ) {
                        // region Showing Message
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Bluetooth permission required for connecting to the machine")
                            .setPositiveButton(R.string.Ok) { _, _ ->
                                ActivityCompat.requestPermissions(
                                    activity!!,
                                    arrayOf(Manifest.permission.BLUETOOTH),
                                    REQUEST_ENABLE_BLUETOOTH
                                )
                            }
                        builder.create()
                        builder.show()
                        // endregion
                    } else {
                        requestPermissions(
                            arrayOf(Manifest.permission.BLUETOOTH),
                            REQUEST_ENABLE_BLUETOOTH
                        )
                    }
                } else {
                    viewModel.updatePermissionsGranted(Permissions.Bluetooth)
                }
            }
            Permissions.BluetoothAdmin -> {
                if (ActivityCompat.checkSelfPermission(activity!!,
                        Manifest.permission.BLUETOOTH_ADMIN
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity!!,
                            Manifest.permission.BLUETOOTH_ADMIN
                        )
                    ) {
                        // region Showing Message
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Bluetooth permission required for connecting to the machine")
                            .setPositiveButton(R.string.Ok) { _, _ ->
                                requestPermissions(
                                    arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                                    REQUEST_ENABLE_BLUETOOTH_ADMIN
                                )
                            }
                        builder.create()
                        builder.show()
                        // endregion
                    } else {
                        requestPermissions(
                            arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                            REQUEST_ENABLE_BLUETOOTH_ADMIN
                        )
                    }
                } else {
                    viewModel.updatePermissionsGranted(Permissions.BluetoothAdmin)
                }
            }
            Permissions.FineLocation -> {
                if (ActivityCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        val builder = AlertDialog.Builder(context)
                        // region Showing Message
                        builder.setMessage("Location permission required for connecting to the machine")
                            .setPositiveButton(R.string.Ok) { _, _ ->
                                Log.i(TAG, "here 1")
                                requestPermissions(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    REQUEST_ENABLE_LOC
                                )
                            }
                        builder.create()
                        builder.show()
                        // endregion
                    } else {
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_ENABLE_LOC
                        )
                    }
                } else {
                    viewModel.updatePermissionsGranted(Permissions.FineLocation)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        Log.i(TAG, "here 2")
        when (requestCode) {
            REQUEST_ENABLE_BLUETOOTH -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.updatePermissionsGranted(Permissions.Bluetooth)
                } else {
                    backToHome()
                }
                return
            }
            REQUEST_ENABLE_BLUETOOTH_ADMIN -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.updatePermissionsGranted(Permissions.BluetoothAdmin)
                } else {
                    backToHome()
                }
                return
            }
            REQUEST_ENABLE_LOC -> {
                Log.i(TAG, "here 3")
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.updatePermissionsGranted(Permissions.FineLocation)
                } else {
                    backToHome()
                }
                return
            }
        }
    }

    private fun backToHome() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Sorry, could'nt connect to device")
            .setPositiveButton(R.string.Ok) { _, _ ->
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        builder.create()
        builder.show()
    }
}
