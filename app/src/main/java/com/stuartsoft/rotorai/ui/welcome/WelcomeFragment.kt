package com.stuartsoft.rotorai.ui.welcome

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stuartsoft.rotorai.GenericBTDeviceBinding
import com.stuartsoft.rotorai.ui.BaseFragment

import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.data.GenericBTDevice
import com.stuartsoft.rotorai.util.recyclerview.ArrayAdapter

class WelcomeFragment : BaseFragment() {
    interface WelcomeFragmentHost

    private lateinit var viewModel: WelcomeViewModel
    private lateinit var binding: WelcomeFragmentBinding
    private var host: WelcomeFragmentHost? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        host = context as WelcomeFragmentHost
    }

    override fun onDetach() {
        host = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = getViewModel(WelcomeViewModel::class)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        binding.vm = viewModel

        binding.uiBtdevices.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        binding.uiBtdevices.adapter = BTDeviceListAdapter()

        return binding.root
    }


    private class BTDeviceListAdapter : ArrayAdapter<GenericBTDevice, GenericBTDeviceViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericBTDeviceViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: GenericBTDeviceBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_generic_bt_device, parent, false)
            return GenericBTDeviceViewHolder(binding)
        }

        override fun onBindViewHolder(holder: GenericBTDeviceViewHolder, position: Int) {
            val device = getItemAtPosition(position)
            holder.bind(device)
        }
    }

    private class GenericBTDeviceViewHolder(private val binding: GenericBTDeviceBinding):
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GenericBTDevice) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }
}
