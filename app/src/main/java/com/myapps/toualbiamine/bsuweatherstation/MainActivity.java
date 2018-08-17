package com.myapps.toualbiamine.bsuweatherstation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements Tab1.OnFragmentInteractionListener, Tab2.OnFragmentInteractionListener,
Tab3.OnFragmentInteractionListener{

    final static String url = "http://159.203.78.94/rpilog/weatherstation.txt"; //URL to retrieve data from.

    //Data TextViews in the tabs.
    public static TextView dataTextTab1;
    public static TextView dataTextTab2;
    public static TextView dataTextTab3;

    public static Context context;

    //Tab creation.
    public static TabLayout tabLayout;
    public static int tabPosition;

    //Track state of tabs.
    public static boolean tab1Used = false;
    public static boolean tab2Used = false;
    public static boolean tab3Used = false;

    //Data retrieval variables => positionFactor is determined using the data's substring indexes.
    public static int positionFactor = 0;
    public static int finalPositionFactor = positionFactor;
    public static boolean endOfData = false;
    private static int newLineFix = 0;      //If there's weird \n at beginning of text file, this changes indexes of substring.
    public static boolean firstReading = true;     //Track state of app.
    public static boolean nextDataPresent;

    public static String dataFromURL;       //String to send through email with data.

    public static long updateTime = 0;      //Variable to get time when we receive data.

    public static String TAG;               //Debugging purposes => log.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Text placeholders for the data received from the URL.
        dataTextTab1 = (TextView) findViewById(com.myapps.toualbiamine.bsuweatherstation.R.id.dataTextTab1);
        dataTextTab2 = (TextView) findViewById(com.myapps.toualbiamine.bsuweatherstation.R.id.dataTextTab2);
        dataTextTab3 = (TextView) findViewById(com.myapps.toualbiamine.bsuweatherstation.R.id.dataTextTab3);

        context = getApplicationContext();

        //Creating the tabs.
        tabLayout = (TabLayout) findViewById(com.myapps.toualbiamine.bsuweatherstation.R.id.tablayout);

        tabLayout.addTab(tabLayout.newTab().setText("1"));
        tabLayout.addTab(tabLayout.newTab().setText("2"));
        tabLayout.addTab(tabLayout.newTab().setText("3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Retrieve only latest data.
        // positionFactor = Menu.mPrefs.getInt("positionFactor", 0);

        //ViewPager & PagerAdapter object to allow sliding tabs.
        final ViewPager viewPager = (ViewPager) findViewById(com.myapps.toualbiamine.bsuweatherstation.R.id.pager);
        viewPager.setOffscreenPageLimit(3);     //Prevents NullPointerException on third tab elements.
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    //Requests a String response from URL w/ Volley library.
    public static void receiveString(){
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dataFromURL = response; //Save data into string that will be content of email.

                        //If top of file has a new line, adjust indexes of substrings.
                        if (firstReading == true && response.length() > 0) {
                            if(response.charAt(0) == 0xd && response.charAt(1) == 0xa) {
                                newLineFix = 4;
                            }
                        }

                        //If there's data to be retrieved.
                        if (positionFactor * 168 + 16 + 3 + 143 <= response.length())  {

                            updateTime = Calendar.getInstance().getTimeInMillis();
                            firstReading = false;

                            int titleStart = positionFactor * 167 + newLineFix;
                            int titleEnd = positionFactor * 167 + 16 + newLineFix;
                            int dataStart = positionFactor * 167 + 16 + 3 + newLineFix;
                            int dataEnd = positionFactor * 167 + 16 + 3 + 143 + newLineFix;

                            String title = response.substring(titleStart, titleEnd);
                            String data = response.substring(dataStart, dataEnd);

                            if(tab1Used == false) {     //If tab1 is empty.

                                tabPosition = 0;
                                TabLayout.Tab currentTab = tabLayout.getTabAt(tabPosition);
                                currentTab.setText(title);
                                dataTextTab1.setText(data);
                                tab1Used = true;

                            }

                            else if(tab1Used == true && tab2Used == false){     //If tab1 has data & tab2 empty.

                                tabPosition = 1;
                                TabLayout.Tab currentTab = tabLayout.getTabAt(tabPosition);
                                currentTab.setText(title);
                                dataTextTab2.setText(data);
                                tab2Used = true;

                            }

                            else if(tab1Used == true && tab2Used == true && tab3Used == false){     //If tab1 & tab2 have data & tab3 empty.

                                tabPosition = 2;
                                TabLayout.Tab currentTab = tabLayout.getTabAt(tabPosition);
                                currentTab.setText(title);
                                dataTextTab3.setText(data);
                                tab3Used = true;

                            }

                            else if(tab1Used == true && tab2Used == true && tab3Used == true){  //If there's data in all tabs => overwrite oldest.

                                tabPosition = 0;
                                TabLayout.Tab currentTab = tabLayout.getTabAt(tabPosition);
                                currentTab.setText(title);
                                dataTextTab1.setText(data);
                                tab2Used = false;
                                tab3Used = false;

                            }

                            finalPositionFactor = positionFactor;
                            Toast.makeText(context, "Page refreshed", Toast.LENGTH_SHORT).show();
                            positionFactor++;

                            if(positionFactor * 167 + 16 + 3 + 143 + newLineFix < response.length()){
                                nextDataPresent = true;
                            }

                        }

                        else{

                            endOfData = true;
                            Toast.makeText(context, "No new data", Toast.LENGTH_SHORT).show();

                        }


                    }



                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error in data retrieval", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);

    }


    public void refresh(View view){             //Refresh action for FAB = onClick fct.

        receiveString();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static void showInfoPopup() {        //Show popup info = onClick fct.
        if (Menu.infoPopupDialog != null) {

            Menu.infoPopupDialog.setContentView(com.myapps.toualbiamine.bsuweatherstation.R.layout.popup_layout);
            Menu.infoPopupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Menu.infoPopupDialog.show();

        }
    }

    public static void showLocationPopup(){     //Show popup location = onClick fct.

        if(Menu.locationPopupDialog != null){

            Menu.locationPopupDialog.setContentView(com.myapps.toualbiamine.bsuweatherstation.R.layout.popup_location);

            Menu.locationPopupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Menu.locationPopupDialog.show();

            //Retrieve saved longitude & latitude.
            String savedLongit = Menu.mPrefs.getString("longitude", "");
            String savedLatit = Menu.mPrefs.getString("latitude", "");

            //Have to re-instantiate to avoid weird NPE.
            Menu.longitudeTv = (TextView) Menu.locationPopupDialog.findViewById(com.myapps.toualbiamine.bsuweatherstation.R.id.longitudeTv);
            Menu.latitudeTv = (TextView) Menu.locationPopupDialog.findViewById(com.myapps.toualbiamine.bsuweatherstation.R.id.latitudeTv);

            Menu.latitudeTv.setText("Latitude: " + Menu.deviceLatitude + "°");
            Menu.longitudeTv.setText("Longitude: " + Menu.deviceLongitude + "°");

            Menu.longitudeTv.setText(savedLongit);
            Menu.latitudeTv.setText(savedLatit);

        }
    }

    @Override
    public void onBackPressed() {   //If user presses back arrow & no more data to be refreshed => show 3 latest data sets.

        tab1Used = false;
        tab2Used = false;
        tab3Used = false;
        if(endOfData == true){          //If we've reached end of data, show 3 last blocks of data when app is reopened.
            positionFactor = finalPositionFactor - 2;
        }
        super.onBackPressed();
    }


}
