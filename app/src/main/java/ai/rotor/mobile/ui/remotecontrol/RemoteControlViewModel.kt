package ai.rotor.mobile.ui.remotecontrol

import android.app.Application
import android.os.Parcelable
import ai.rotor.mobile.ui.BaseViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class RemoteControlViewModel @Inject constructor(
        private val app: Application)
    : BaseViewModel<RemoteControlViewModel.State>(app, STATE_KEY, State()) {

    @Parcelize
    class State() : Parcelable

    override fun setupViewModel() {

    }

    companion object {
        private const val STATE_KEY = "RemoteControlViewModelState"  // NON-NLS
    }
}
