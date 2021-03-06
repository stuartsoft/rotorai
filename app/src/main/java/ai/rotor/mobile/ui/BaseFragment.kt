package ai.rotor.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

abstract class BaseFragment: androidx.fragment.app.Fragment() {
    fun <VM: ViewModel> getViewModel(viewModelClass: KClass<VM>): VM {
        return ViewModelProviders.of(activity!!).get(viewModelClass.java)
    }
}