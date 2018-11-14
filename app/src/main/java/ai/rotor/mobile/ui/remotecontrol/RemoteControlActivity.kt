package ai.rotor.mobile.ui.remotecontrol

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle

import ai.rotor.mobile.R
import ai.rotor.mobile.ui.BaseActivity
import ai.rotor.mobile.ui.remotecontrol.RemoteControlFragment.RemoteControlFragmentHost

import javax.inject.Inject

class RemoteControlActivity : BaseActivity(), RemoteControlFragmentHost {
    @Inject lateinit var viewModel: RemoteControlViewModel
    private lateinit var binding: RemoteControlActivityBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(RemoteControlViewModel::class)
        viewModel.restoreState(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_control)
        binding.vm = viewModel
        binding.executePendingBindings()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "RemoteControl"
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
            return Intent(context, RemoteControlActivity::class.java)
        }
    }
}
