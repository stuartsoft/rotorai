package com.stuartsoft.rotorai.ui.main

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.ui.BaseActivity
import com.stuartsoft.rotorai.ui.SimpleSnackbarMessage
import com.stuartsoft.rotorai.ui.main.MainFragment.MainFragmentHost

class MainActivity : BaseActivity(), MainFragmentHost {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainActivityBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(MainViewModel::class)
        viewModel.restoreState(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.executePendingBindings()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = applicationContext.packageName

        viewModel.snackbarMessage.observe(this, object : SimpleSnackbarMessage.SnackbarObserver {
            override fun onNewMessage(message: String) {
                Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(outState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }
}