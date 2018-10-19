package com.stuartsoft.rotorai.ui.welcome

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stuartsoft.rotorai.ui.BaseFragment

import com.stuartsoft.rotorai.R

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

        return binding.root
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }
}
