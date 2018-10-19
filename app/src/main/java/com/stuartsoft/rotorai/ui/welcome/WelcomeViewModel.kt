package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.databinding.Bindable
import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import com.stuartsoft.rotorai.BR
import com.stuartsoft.rotorai.data.BTVehicleConnector
import com.stuartsoft.rotorai.data.RotorUtils
import com.stuartsoft.rotorai.data.VehicleConnectionState
import com.stuartsoft.rotorai.ui.BaseViewModel
import com.stuartsoft.rotorai.ui.SingleLiveEvent
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

open class WelcomeViewModel @Inject constructor(
        private val app: Application,
        private val btVehicleConnector: BTVehicleConnector)
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    @Parcelize
    class State() : Parcelable

    var shouldShowBTDialog = SingleLiveEvent<Boolean>()

    @Bindable
    fun getWelcomeScreenStep(): WelcomeScreenStep =
        if(btVehicleConnector.currentConnectionState() == VehicleConnectionState.READY_VEHICLE_CONNECTED){
            WelcomeScreenStep.CONNECTED
        } else {
            WelcomeScreenStep.SELECT_VEHICLE
        }

    override fun setupViewModel() {

    }

    fun onReceiveBroadcast(intent: Intent?) {
        intent?.let {
            it.extras?.let { extraz ->
                if (extraz.containsKey(EXTRA_STATE)) {
                    notifyPropertyChanged(BR.welcomeScreenStep)
                }
            }
        }
    }

    fun onClickNeedsBT() {
        shouldShowBTDialog.value = true
    }

    fun forceRefreshAllBindings() {
        notifyChange()
    }

    enum class WelcomeScreenStep(val i: Int) {
        SELECT_VEHICLE(0),
        CONNECTED(1)
    }

    companion object {
        private const val STATE_KEY = "WelcomeViewModelState"  // NON-NLS
    }
}
