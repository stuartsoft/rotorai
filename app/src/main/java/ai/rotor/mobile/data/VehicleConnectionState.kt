package ai.rotor.mobile.data

enum class VehicleConnectionState {

    //this medium is unavailable (example: device doesn't have bluetooth radio)
    UNAVAILABLE,

    //device has supported medium, but it is not running or available at this time
    //(example: user has not granted bluetooth permission)
    OFFLINE,

    //we are connected to nothing, or we are connected to a non-vehicle device (example: earbuds)
    VEHICLE_NOT_CONNECTED,

    //good to go! let's roll
    READY_VEHICLE_CONNECTED,
}