package com.myapps.toualbiamine.bsuweatherstation;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import im.delight.android.location.SimpleLocation;

public class Menu extends AppCompatActivity {

    SimpleLocation myLocation;
    public static double deviceLongitude;
    public static double deviceLatitude;
    public static TextView longitudeTv;
    public static TextView latitudeTv;


    public static Dialog infoPopupDialog;
    public static TextView messageTv;
    public static Dialog locationPopupDialog;

    public static SharedPreferences mPrefs;

    private static long firstReadingUpdateTime;

    private static CountDownTimer cTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //SharedPreferences to save variables in phone memory.
        mPrefs = getApplicationContext().getSharedPreferences("SavedData", MODE_PRIVATE);

        myLocation = new SimpleLocation(this);

        infoPopupDialog = new Dialog(this);
        messageTv = (TextView) findViewById(R.id.messageTv);

        locationPopupDialog = new Dialog(this);
        locationPopupDialog.findViewById(R.layout.popup_location);
        longitudeTv = (TextView) locationPopupDialog.findViewById(R.id.longitudeTv);
        latitudeTv = (TextView) locationPopupDialog.findViewById(R.id.latitudeTv);

        cTimer = null;


        // if we can't access the location yet => ask user to enable location access.
        if (!myLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }

        //Instantiate all cards in the menu view.
        CardView dataCard = (CardView) findViewById(R.id.dataCard);
        CardView locationCard = (CardView) findViewById(R.id.locationCard);
        CardView timeCard = (CardView) findViewById(R.id.timeCard);
        CardView websiteCard = (CardView) findViewById(R.id.websiteCard);
        CardView emailCard = (CardView) findViewById(R.id.emailCard);
        CardView infoCard = (CardView) findViewById(R.id.infoCard);

        infoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showInfoPopup();
            }
        });

        dataCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dataActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(dataActivity);
            }
        });

        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final double longit = roundCoordinates(myLocation.getLongitude());
                final double latit = roundCoordinates(myLocation.getLatitude());


                   deviceLongitude = longit;
                   deviceLatitude = latit;



                MainActivity.showLocationPopup();

            }
        });

        timeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long currentTime = Calendar.getInstance().getTimeInMillis();
                final long nextUpdateTime = 900000 - (currentTime - MainActivity.updateTime); //If 15 minute-interval.
                long nextUpdateTimeInMin = nextUpdateTime / 1000 / 60;
                long nextUpdateTimeInSec = nextUpdateTime / 1000;
                if(nextUpdateTimeInSec < 1 || MainActivity.nextDataPresent == true){
                    Toast.makeText(Menu.this, "New data to be refreshed", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(Menu.this, "Next update in " + nextUpdateTimeInMin + " min", Toast.LENGTH_SHORT).show();
                }

            }
        });

        websiteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.url));
                startActivity(openWebsite);
            }
        });

        emailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Weather Station Data Report");
                intent.putExtra(Intent.EXTRA_TEXT, MainActivity.dataFromURL); //Add string variable holding entire data here.
                intent.setData(Uri.parse("mailto:RHellstrom@bridgew.edu"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(intent);

            }
        });
    }


    public double roundCoordinates(double coordinate){    //function to round to 2 decimal places our GPS coordinates.

        String result = String.format("%.2f", coordinate);
        double roundedValue = 0;
        try{
             roundedValue = Double.parseDouble(result);
        }
        catch(NumberFormatException ex){
            Toast.makeText(Menu.this, "Error in determining location. Try again later.", Toast.LENGTH_LONG).show();
        }

        return roundedValue;

    }


    public void closePopup(View v){     //Close icon = onClick fct.

        infoPopupDialog.dismiss();
        locationPopupDialog.dismiss();

    }

    public void updateCoordinates(View v){      //Update button = onClick fct.

        longitudeTv = (TextView) locationPopupDialog.findViewById(R.id.longitudeTv);
        latitudeTv = (TextView) locationPopupDialog.findViewById(R.id.latitudeTv);
        latitudeTv.setText("Latitude: " + Menu.deviceLatitude + "°");
        longitudeTv.setText("Longitude: " + Menu.deviceLongitude + "°");

        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString("longitude", longitudeTv.getText().toString().trim());
        edit.putString("latitude", latitudeTv.getText().toString().trim());
        edit.apply();

    }

    void cancelTimer() {              //Cancel timer.
        if(cTimer!=null)
            cTimer.cancel();
    }

    @Override
    protected void onDestroy() {    //Cancel timer here to avoid memory leak of the activity.
        super.onDestroy();
        cancelTimer();
    }


}
