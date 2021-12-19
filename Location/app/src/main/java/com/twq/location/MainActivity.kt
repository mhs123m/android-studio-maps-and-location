package com.twq.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var textView2: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btn = findViewById<Button>(R.id.button)
        textView = findViewById(R.id.textView)
        textView2 = findViewById(R.id.textView2)


        btn.setOnClickListener {

            checkPermissionForLocation()
        }
    }

    fun checkPermissionForLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            // request permision dialog
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )

        } else { //when location permission is granted
            showLocation()
        }
    }


    @SuppressLint("MissingPermission")
    fun showLocation() {

        var locationManager = getSystemService(LOCATION_SERVICE) as? LocationManager
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
            0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    textView.text = "${location.latitude}   ${location.longitude}"


                   Thread {
                       var geocoder = Geocoder(this@MainActivity)
                       var l = geocoder.getFromLocation(
                           location.latitude,
                           location.longitude, 10
                       )

                       val address = l[0]
                       println("${address.countryName}, ${address.adminArea}")
                       println(address.getAddressLine(0) + " " +
                               address.getAddressLine(1)+ " " +
                               address.getAddressLine(2))


                       runOnUiThread {
                           textView2.text = (address.getAddressLine(0) + " " +
                                   address.getAddressLine(1))
                       }

                   }.start()
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showLocation()
        } else {
            AlertDialog.Builder(this).apply {
                title = "Warning"
                setMessage("To access Location, allow location from settings")
                setPositiveButton("Ok", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val intentSetting = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intentSetting.data = uri
                        startActivity(intentSetting)

                    }


                })

            }.show()
        }
    }
}