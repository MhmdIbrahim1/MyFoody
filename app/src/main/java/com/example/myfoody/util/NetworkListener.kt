package com.example.myfoody.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

@ExperimentalCoroutinesApi
class NetworkListener : ConnectivityManager.NetworkCallback() {

    // MutableStateFlow is a type of flow that represents a value with a state that can be updated
    private val isNetworkAvailable = MutableStateFlow(false)

    /**
     * This function registers the network callback and returns a MutableStateFlow of Boolean
     * @param context
     * @return isNetworkAvailable
     */
    fun registerNetworkCallback(context: Context): MutableStateFlow<Boolean> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(this)
        return isNetworkAvailable
    }


    /**
     * This function is called when the network is available
     * @param network
     */
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        isNetworkAvailable.value = true
    }

    /**
     * This function is called when the network is lost
     * @param network
     */
    override fun onLost(network: Network) {
        super.onLost(network)
        isNetworkAvailable.value = false
    }
}