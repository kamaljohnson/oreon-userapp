# vendx-userapp

# App 

## [architecture](https://developer.android.com/topic/libraries/architecture)
We use MVVM architecture which is build on the above architecture components. 
here is a great tutorial for getting started with android [tutorial](https://classroom.udacity.com/courses/ud9012)

# Bluetooth

here is an overview about [bluetooth](https://developer.android.com/guide/topics/connectivity/bluetooth) in general.

We use both [Bluetooth classic](https://en.wikipedia.org/wiki/Bluetooth) and
[Bluetooth Low Energy](https://en.wikipedia.org/wiki/Bluetooth_Low_Energy) in our application.

Bluetooth classic is needed because the vending machine can't advertise when connected 
in LE mode. We need to keep receiving advertisements even when another user is vending
so that users can still "see" the vending machine in app. Thus the device will
keep sending advertisements in classic mode even when connected in LE mode.

there are two files dedicated for scanning and connection+communication respectively which was created referencing [SimpleBluetoothLeTerminal](https://github.com/kai-morich/SimpleBluetoothLeTerminal) 

| project file                                                                                                                                                                                                            | referenced from                                                                                                                                                            |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [DeviceScanner](https://github.com/kamaljohnson/vendx-userapp/blob/ble-connect-plus-communicate/app/src/main/java/com/xborg/vendx/activities/vendingActivity/fragments/deviceScanner/DeviceScanner.java)                | [DeviceFragment](https://github.com/kai-morich/SimpleBluetoothLeTerminal/blob/master/app/src/main/java/de/kai_morich/simple_bluetooth_le_terminal/DevicesFragment.java)    |
| [DeviceCommunicator](https://github.com/kamaljohnson/vendx-userapp/blob/ble-connect-plus-communicate/app/src/main/java/com/xborg/vendx/activities/vendingActivity/fragments/deviceCommunicator/DeviceCommunicator.java) | [TerminalFragment](https://github.com/kai-morich/SimpleBluetoothLeTerminal/blob/master/app/src/main/java/de/kai_morich/simple_bluetooth_le_terminal/TerminalFragment.java) |

The VendingActivity handles the Bluetooth classic scanning.

## Device communication states
There are multiple states which control the process flow and also used for better retry if required

| Vending State      | DeviceScannerState | Description                                         |
|--------------------|--------------------|-----------------------------------------------------|
| Init               |                    | ...                                                 |
| Scanning           |                    | Starts scanning                                     |
|                    | None               | ...                                                 |
|                    | DeviceInfoSet      | Device MAC address loaded                           |
|                    | ScanMode           | Scan mode switch on                                 |
|                    | DeviceNearby       | Device found nearby via classic Bluetooth scan      |
|                    | DeviceNotNearby    | Device not found ...                                |
|                    | DeviceBusy         | Device not found via BLE scan                       |
|                    | DeviceIdle         | Device found via BLE scan                           |
| DeviceDiscovered   |                    | ...                                   |
| ConnectionRequest  |                    | Connection request created                          |
| Connecting         |                    | Trying to connect to device                         |
| Connected          |                    | ...                                                 |
| ReceivedOtp        |                    | OTP received from device                            |
| ReceivedOtpWithBag |                    | OTP + BAG receive from server                       |
| Vending            |                    | The device starts vending..                         |
| VendingDone        |                    | Device vending done **this state is skipped for now** |
| ReceivedLog        |                    | LOG received from device                            |
| ReceivedLogAck     |                    | LOG ACK received from server                        |
| VendingComplete    |                    | ...                                                 |
