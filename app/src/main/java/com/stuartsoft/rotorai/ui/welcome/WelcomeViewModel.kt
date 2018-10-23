package com.stuartsoft.rotorai.ui.welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter.*
import android.content.Intent
import android.os.Parcel
import androidx.databinding.Bindable
import android.os.Parcelable
import com.stuartsoft.rotorai.BR
import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.data.BTVehicleConnector
import com.stuartsoft.rotorai.data.VehicleConnectionState
import com.stuartsoft.rotorai.data.VehicleConnectionState.*
import com.stuartsoft.rotorai.ui.BaseViewModel
import com.stuartsoft.rotorai.ui.SingleLiveEvent
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

open class WelcomeViewModel @Inject constructor(
        private val app: Application,
        private val btvc: BTVehicleConnector)
    : BaseViewModel<WelcomeViewModel.State>(app, STATE_KEY, State()) {

    @Parcelize
    class State() : Parcelable

    var shouldShowBTDialog = SingleLiveEvent<Boolean>()

    @Bindable
    fun getHeaderMsg(): String? {
        return app.getString(when (btvc.currentConnectionState()) {
            UNAVAILABLE -> R.string.ui_welcome_doesnt_have_bt
            OFFLINE -> R.string.ui_welcome_header_enable_bt
            else -> R.string.ui_welcome_header_select_vehicle
        })
    }

    @Bindable
    fun getWelcomeScreenStep(): WelcomeScreenStep {
        return if(btvc.currentConnectionState() == READY_VEHICLE_CONNECTED){
            WelcomeScreenStep.CONNECTED
        } else {
            WelcomeScreenStep.SELECT_VEHICLE
        }
    }

    @Bindable
    fun isEnableBTLinkVisible(): Boolean {
        return btvc.currentConnectionState() == OFFLINE
    }

    override fun setupViewModel() {
    }

    fun onReceiveBroadcast(intent: Intent?) {
        intent?.let {
            it.extras?.let { extraz ->
                if (extraz.containsKey(EXTRA_STATE)) {
                    forceRefreshAllBindings()
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
