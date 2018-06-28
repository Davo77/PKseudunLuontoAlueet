package com.example.sampo.luontoalueet

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth


class MainMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val test: ArrayList<String> = arrayListOf()

    private var mLocationPermissionGranted: Boolean = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1234
    // ^Number isnt definitive, as long as its unique inside the application
    private val DEFAULT_ZOOM: Float = 15.0F

    private var mLastKnownLocation: Location? = null

    private val mDefaultLocation = LatLng(60.312491, 24.484248)

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_maps)

        mAuth = FirebaseAuth.getInstance()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        // Construct a GeoDataClient.
        //mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        //mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Do other setup activities here too, as described elsewhere in this tutorial.

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

       //PRESSING WILL ADD MARKER
        mMap.setOnMapClickListener(GoogleMap.OnMapClickListener { point ->
            val builder: AlertDialog.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            } else {
                builder = AlertDialog.Builder(this)
            }
            val marker = MarkerOptions().position(point)


            builder.setTitle("Are you sure you want to add a map location here?")

                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                        mMap.addMarker(marker)
                                //CUSTOM MARKER
                                .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pinetree_foreground))




                    })

                    .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                        // do nothing
                    })
                    .show()

            true
/*
//Adding info window to marker
         /  mMap.addMarker(MarkerOptions().position(point))
                    //CUSTOM MARKER
                    .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pinetree_foreground))

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL)
            mMap.getUiSettings().setZoomControlsEnabled(true)



            val markerOpt = MarkerOptions()
            markerOpt.position(point)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pinetree_foreground)) */



                val mapInfoWindowFragment = supportFragmentManager.findFragmentById(R.id.infoWindowMap) as? MapInfoWindowFragment
                //Set Custom InfoWindow
                val infoWindow = InfoWindow(point, InfoWindow.MarkerSpecification(0, 0), mapInfoWindowFragment)
                // Shows the InfoWindow or hides it if it is already opened.
                mapInfoWindowFragment?.infoWindowManager()?.toggle(infoWindow, false)

                mapInfoWindowFragment?.infoWindowManager()?.show(infoWindow)




            })



           val infoWindowManager = InfoWindowManager(supportFragmentManager)

            infoWindowManager.setWindowShowListener(object : InfoWindowManager.WindowShowListener {
                override fun onWindowShowStarted(infoWindow: InfoWindow) {Log.e("testi", "onWindowShowStarted")}

                override fun onWindowShown(infoWindow: InfoWindow) {Log.e("testi", "onWindowShown")}

                override fun onWindowHideStarted(infoWindow: InfoWindow) {Log.e("testi", "onWindowHideStarted")}

                override fun onWindowHidden(infoWindow: InfoWindow) {Log.e("testi", "onWindowHidden")}
            })




        //PRESSING ALREADY VISIBLE MARKER SHOWS OPTION TO DELETE IT
        mMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
            val builder: AlertDialog.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            } else {
                builder = AlertDialog.Builder(this)
            }
            builder.setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                        marker.remove()
                    })

                    .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                        // do nothing
                    })
                    .show()

            true
        })
    }



    private fun getLocationPermission() {
        /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true

            updateLocationUI()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }


    private fun updateLocationUI() {
        try {
            if (mLocationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                mLastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }


    private fun getDeviceLocation() {
        /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient!!.getLastLocation()
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.result as Location?

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(mLastKnownLocation!!.getLatitude(),
                                        mLastKnownLocation!!.getLongitude()), DEFAULT_ZOOM))
                    } else {
                        Log.d("luontoAlueet", "Current location is null. Using defaults.")
                        Log.e("luontoAlueet", "Exception: %s", task.exception)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }


    }

}









