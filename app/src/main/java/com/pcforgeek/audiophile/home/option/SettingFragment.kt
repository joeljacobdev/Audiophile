package com.pcforgeek.audiophile.home.option


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pcforgeek.audiophile.App
import com.pcforgeek.audiophile.R
import com.pcforgeek.audiophile.data.model.BlacklistPath
import com.pcforgeek.audiophile.di.ViewModelFactory
import com.pcforgeek.audiophile.util.FileUtils
import kotlinx.android.synthetic.main.fragment_setting.*
import javax.inject.Inject


class SettingFragment : Fragment(R.layout.fragment_setting),
    BlacklistPathAdapter.BlacklistPathOnClickListener {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    var isBlacklistUpdated = false
    private val settingViewModel by viewModels<SettingViewModel> { viewModelFactory }
    private val blacklistPathAdapter: BlacklistPathAdapter by lazy { BlacklistPathAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.component.inject(this)
        addBlacklistPath.setOnClickListener { selectFolder() }

        blacklistedPathsRecyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = blacklistPathAdapter
        }
        settingViewModel.blacklistPaths.observe(viewLifecycleOwner, Observer { blacklistPaths ->
            blacklistPathAdapter.setData(blacklistPaths)
        })
    }

    private fun selectFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(
            intent,
            FOLDER_PATH
        )
    }

    override fun removePath(blacklistPath: BlacklistPath) {
        isBlacklistUpdated = true
        settingViewModel.removeBlacklistPath(blacklistPath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FOLDER_PATH -> {
                val treeUri = data?.data ?: return
                val path = FileUtils.getFullPathFromTreeUri(treeUri)
                isBlacklistUpdated = true
                settingViewModel.addBlacklistPath(path)
            }
        }
    }

    companion object {

        private const val FOLDER_PATH = 132
        @JvmStatic
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }
}
