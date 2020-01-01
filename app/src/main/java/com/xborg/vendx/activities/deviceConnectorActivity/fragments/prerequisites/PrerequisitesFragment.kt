package com.xborg.vendx.activities.deviceConnectorActivity.fragments.prerequisites

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.deviceConnectorActivity.SharedViewModel

const val REQUEST_ENABLE_BLUETOOTH = 1
const val REQUEST_ENABLE_BLUETOOTH_ADMIN = 2
const val REQUEST_ENABLE_LOC = 3

class PrerequisitesFragment : Fragment() {

    private lateinit var viewModel: PrerequisitesViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        })

        viewModel.currentPermissionToBeGranted.observe(this, Observer {
            checkRequiredPermissions()
        })

        viewModel.grantComplete.observe(this, Observer { grantStatus ->
            if (grantStatus) {
                sharedViewModel.jumpToNextStep()
            }
        })
    }

    private fun checkRequiredPermissions() {
        when (viewModel.currentPermissionToBeGranted) {
            Permissions.Bluetooth -> {
                if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH)
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
                        ActivityCompat.requestPermissions(
                            activity!!,
                            arrayOf(Manifest.permission.BLUETOOTH),
                            REQUEST_ENABLE_BLUETOOTH
                        )
                    }
                } else {
                    viewModel.updatePermissionsGranted(Permissions.Bluetooth)
                }
            }
            Permissions.BluetoothAdmin -> {
                if (ContextCompat.checkSelfPermission(
                        activity!!,
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
                                ActivityCompat.requestPermissions(
                                    activity!!,
                                    arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                                    REQUEST_ENABLE_BLUETOOTH_ADMIN
                                )
                            }
                        builder.create()
                        builder.show()
                        // endregion
                    } else {
                        ActivityCompat.requestPermissions(
                            activity!!,
                            arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                            REQUEST_ENABLE_BLUETOOTH_ADMIN
                        )
                    }
                } else {
                    viewModel.updatePermissionsGranted(Permissions.BluetoothAdmin)
                }
            }
            Permissions.FineLocation -> {
                if (ContextCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        // region Showing Message
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Location permission required for connecting to the machine")
                            .setPositiveButton(R.string.Ok) { _, _ ->
                                ActivityCompat.requestPermissions(
                                    activity!!,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    REQUEST_ENABLE_LOC
                                )
                            }
                        builder.create()
                        builder.show()
                        // endregion
                    } else {
                        ActivityCompat.requestPermissions(
                            activity!!,
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
        when (requestCode) {
            REQUEST_ENABLE_BLUETOOTH -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.updatePermissionsGranted(Permissions.Bluetooth)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            REQUEST_ENABLE_BLUETOOTH_ADMIN -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.updatePermissionsGranted(Permissions.BluetoothAdmin)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            REQUEST_ENABLE_LOC -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.updatePermissionsGranted(Permissions.FineLocation)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }
}
