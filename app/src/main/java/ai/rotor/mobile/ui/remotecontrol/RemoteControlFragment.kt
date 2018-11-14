package ai.rotor.mobile.ui.remotecontrol

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.rotor.mobile.ui.BaseFragment

import ai.rotor.mobile.R

class RemoteControlFragment : BaseFragment() {
    interface RemoteControlFragmentHost

    private lateinit var viewModel: RemoteControlViewModel
    private lateinit var binding: RemoteControlFragmentBinding
    private var host: RemoteControlFragmentHost? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        host = context as RemoteControlFragmentHost
    }

    override fun onDetach() {
        host = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = getViewModel(RemoteControlViewModel::class)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_remote_control, container, false)
        binding.vm = viewModel

        return binding.root
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }
}
