package dev.joeljacob.audiophile.home.option

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.joeljacob.audiophile.data.MusicSource
import dev.joeljacob.audiophile.data.StorageMediaSource
import dev.joeljacob.audiophile.data.model.BlacklistPath
import dev.joeljacob.audiophile.db.BlacklistPathDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SettingViewModel @Inject constructor(
    private val blacklistPathDao: BlacklistPathDao
) :
    ViewModel() {

    val blacklistPaths = blacklistPathDao.getAllBlacklistPathLiveData()

    fun addBlacklistPath(path: String?) {
        path?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val blacklistPath = BlacklistPath(path)
                blacklistPathDao.insertBlacklistPath(blacklistPath)
                Timber.d("$path added to blacklist paths")
            }
        }
    }

    fun removeBlacklistPath(blacklistPath: BlacklistPath) {
        viewModelScope.launch(Dispatchers.IO) {
            blacklistPathDao.deleteBlacklistPath(blacklistPath)
            Timber.d("${blacklistPath.path} removed from blacklist paths")
        }
    }
}