package com.pcforgeek.audiophile.home

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.di.ViewModelFactory
import com.pcforgeek.audiophile.util.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }
    private val tabsAdapter: TabsAdapter by lazy {
        TabsAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!PermissionUtils.isPermissionGranted(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.FOREGROUND_SERVICE
                )
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_READ_PERMISSION
            )
        }
        App.component.inject(this)
        volumeControlStream = AudioManager.STREAM_MUSIC

        viewpager.adapter = tabsAdapter
        viewpager.offscreenPageLimit = 2
        tabs.setupWithViewPager(viewpager)

        viewModel.rootMediaId.observe(this, Observer {
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == EXTERNAL_STORAGE_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val EXTERNAL_STORAGE_READ_PERMISSION = 1
    }
}
