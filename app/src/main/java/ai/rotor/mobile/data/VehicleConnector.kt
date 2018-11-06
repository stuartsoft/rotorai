package ai.rotor.mobile.data

/*
   The definition for a class that can connect a phone to the vehicle

   The medium of connectivity is not defined here...
   it could be Bluetooth, HTTP requests, web sockets, carrier pigeon, whatever
 */

abstract class VehicleConnector {

    private lateinit var connectionState: VehicleConnectionState

    fun isVehicleConnected() = (connectionState == VehicleConnectionState.READY_VEHICLE_CONNECTED)

    abstract fun currentConnectionState(): VehicleConnectionState

}