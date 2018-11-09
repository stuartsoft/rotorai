package ai.rotor.mobile.ui.welcome

import ai.rotor.commonstuff.GenericBTDevice
import ai.rotor.mobile.GenericBTDeviceBinding
import ai.rotor.mobile.R
import ai.rotor.mobile.ui.BaseFragment
import ai.rotor.mobile.util.recyclerview.ArrayAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL

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
        binding.uiBtdevices.addItemDecoration(DividerItemDecoration(this.context, HORIZONTAL))

        return binding.root
    }

    private inner class BTDeviceListAdapter : ArrayAdapter<GenericBTDevice, GenericBTDeviceViewHolder>() {
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

    private inner class GenericBTDeviceViewHolder(private val binding: GenericBTDeviceBinding):
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GenericBTDevice) {
            binding.item = item
            binding.executePendingBindings()
            this.itemView.setOnClickListener({viewModel.btDeviceClicked(item)})
        }
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }
}
