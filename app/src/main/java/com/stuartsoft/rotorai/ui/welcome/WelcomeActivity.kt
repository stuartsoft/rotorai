package com.stuartsoft.rotorai.ui.welcome

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle

import com.stuartsoft.rotorai.R
import com.stuartsoft.rotorai.ui.BaseActivity
import com.stuartsoft.rotorai.ui.welcome.WelcomeFragment.WelcomeFragmentHost

import javax.inject.Inject

class WelcomeActivity : BaseActivity(), WelcomeFragmentHost {
    @Inject lateinit var viewModel: WelcomeViewModel
    private lateinit var binding: WelcomeActivityBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(WelcomeViewModel::class)
        viewModel.restoreState(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        binding.vm = viewModel
        binding.executePendingBindings()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "Welcome"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(outState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }
}
