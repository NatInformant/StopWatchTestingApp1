package com.example.healthypetsadvisor.stopwatchtestingapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.UnknownHostException


object NetworkUtils : ConnectivityManager.NetworkCallback() {

    private val networkLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getNetworkLiveData(context: Context): LiveData<Boolean> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(this)

        CoroutineScope(Dispatchers.IO).launch {
            networkLiveData.postValue(isInternetAvailable())
        }

        return networkLiveData
    }

    override fun onAvailable(network: Network) {
        CoroutineScope(Dispatchers.IO).launch {
            networkLiveData.postValue(isInternetAvailable())
        }
    }

    override fun onLost(network: Network) {
        networkLiveData.postValue(false)
    }

    private fun isInternetAvailable(): Boolean {
        try {
            val address = InetAddress.getByName("www.google.com")
            return !address.equals("")
        } catch (_: UnknownHostException) {}

        return false
    }
}