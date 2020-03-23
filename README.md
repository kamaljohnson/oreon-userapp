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

Here are the link to the example from which our code was "ported" for setting up bluetooth
communication with the vending machine.
project: [kai-morich/SimpleBluetoothLeTerminal](https://github.com/kai-morich/SimpleBluetoothLeTerminal)
 - [scanning](https://github.com/kai-morich/SimpleBluetoothLeTerminal/blob/master/app/src/main/java/de/kai_morich/simple_bluetooth_le_terminal/DevicesFragment.java) -> [deviceScanner](app/src/main/java/com/xborg/vendx/activities/vendingActivity/fragments/deviceScanner.java)
 - [communication](https://github.com/kai-morich/SimpleBluetoothLeTerminal/blob/master/app/src/main/java/de/kai_morich/simple_bluetooth_le_terminal/TerminalFragment.java) -> [deviceCommunicator](app/src/main/java/com/xborg/vendx/activities/vendingActivity/fragments/deviceCommunicator.java)