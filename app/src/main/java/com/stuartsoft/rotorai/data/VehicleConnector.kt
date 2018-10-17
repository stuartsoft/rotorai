package com.stuartsoft.rotorai.data

abstract class VehicleConnector {

    private lateinit var connectionState: VehicleConnectionState


    fun isVehicalConnected() = (connectionState == VehicleConnectionState.READY_VEHICLE_CONNECTED)


    abstract fun currentConnectionState(): VehicleConnectionState

}