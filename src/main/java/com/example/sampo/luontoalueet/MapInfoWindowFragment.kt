package com.example.sampo.luontoalueet

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.appolica.mapanimations.R
import com.example.sampo.luontoalueet.customview.TouchInterceptFrameLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapInfoWindowFragment : Fragment() {

    private var googleMap: GoogleMap? = null
    private var infoWindowManager: InfoWindowManager? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_map_infowindow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoWindowManager = InfoWindowManager(childFragmentManager)
        infoWindowManager!!.onParentViewCreated(view as TouchInterceptFrameLayout, savedInstanceState)
    }

    private fun setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

            mapFragment
                    .getMapAsync { googleMap ->
                        this@MapInfoWindowFragment.googleMap = googleMap
                        setUpMap()
                    }
        }
    }

    private fun setUpMap() {
        infoWindowManager!!.onMapReady(googleMap!!)
    }

    /**
     * Get the [InfoWindowManager], used for showing/hiding and positioning the
     * [InfoWindow].
     *
     * @return The [InfoWindowManager]
     */
    fun infoWindowManager(): InfoWindowManager? {
        return infoWindowManager
    }

    override fun onResume() {
        super.onResume()
        setUpMapIfNeeded()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        infoWindowManager!!.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        infoWindowManager!!.onDestroy()
    }

    /**
     * Use this method to get the [GoogleMap] object asynchronously from our fragment.
     *
     * @param onMapReadyCallback The callback that will be called providing you the GoogleMap
     * object.
     */
    fun getMapAsync(onMapReadyCallback: OnMapReadyCallback) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    companion object {

        private val TAG = "MapInfoWindowFragment"
    }
}