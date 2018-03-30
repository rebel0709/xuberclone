package com.xuber_for_services.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.xuber_for_services.app.Constants.URLHelper;
import com.xuber_for_services.app.Helper.ConnectionHelper;
import com.xuber_for_services.app.Helper.CustomDialog;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.Utils.Utilities;
import com.xuber_for_services.app.XuberServicesApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.xuber_for_services.app.XuberServicesApplication.trimMessage;

public class HistoryDetails extends AppCompatActivity {
    public JSONObject jsonObject;
    Activity activity;
    Context context;
    Boolean isInternet;
    ConnectionHelper helper;
    CustomDialog customDialog;
    TextView tripAmount;
    TextView tripDate;
    TextView paymentType;
    TextView tripComments,lblComments;
    TextView tripProviderName;
    TextView tripSource;
    TextView tripDestination;
    TextView lblTitle;
    TextView lblServiceType,lblRateStatus;
    TextView lblServiceAddress;
    ImageView tripImg, tripProviderImg, paymentTypeImg;
    RatingBar tripProviderRating;
    LinearLayout sourceAndDestinationLayout, lnrComments, lnrParent;
    CardView crdComments;
    View viewLayout;
    ImageView backArrow;
    LinearLayout parentLayout;
    String tag = "", strProviderId = "";
    Button btnCancelRide;
    ImageView imgInfo;
    CardView ServiceAddressCardView;

    String before_comment="",before_image="";
    String after_comment="",after_image="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_history_details);
        findViewByIdAndInitialize();
        try {
            Intent intent = getIntent();
            String post_details = intent.getStringExtra("post_value");
            tag = intent.getStringExtra("tag");
            jsonObject = new JSONObject(post_details);
        } catch (Exception e) {
            jsonObject = null;
        }

        if (jsonObject != null) {

            if(tag.equalsIgnoreCase("past_trips")){
                btnCancelRide.setVisibility(View.GONE);
                crdComments.setVisibility(View.VISIBLE);
                lnrComments.setVisibility(View.VISIBLE);
                getRequestDetails();
                imgInfo.setVisibility(View.VISIBLE);
                lblRateStatus.setText("Total Amount");
                lblTitle.setText("Past Services");
            }else{
                btnCancelRide.setVisibility(View.VISIBLE);
                crdComments.setVisibility(View.GONE);
                lnrComments.setVisibility(View.GONE);
                getUpcomingDetails();
                lblRateStatus.setText("Hourly Fare");
                imgInfo.setVisibility(View.GONE);
                lblTitle.setText("Upcoming Services");
            }
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void findViewByIdAndInitialize() {
        activity = HistoryDetails.this;
        context = HistoryDetails.this;
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        parentLayout.setVisibility(View.GONE);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        tripAmount = (TextView) findViewById(R.id.tripAmount);
        tripDate = (TextView) findViewById(R.id.tripDate);
        paymentType = (TextView) findViewById(R.id.paymentType);
        paymentTypeImg = (ImageView) findViewById(R.id.paymentTypeImg);
        tripProviderImg = (ImageView) findViewById(R.id.tripProviderImg);
        tripImg = (ImageView) findViewById(R.id.tripImg);
        tripComments = (TextView) findViewById(R.id.tripComments);
        tripProviderName = (TextView) findViewById(R.id.tripProviderName);
        tripProviderRating = (RatingBar) findViewById(R.id.tripProviderRating);
        tripSource = (TextView) findViewById(R.id.tripSource);
        tripDestination = (TextView) findViewById(R.id.tripDestination);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblServiceType = (TextView) findViewById(R.id.lblServiceType);
        lblRateStatus = (TextView) findViewById(R.id.lblRateStatus);
        lblServiceAddress = (TextView) findViewById(R.id.lblServiceAddress);
        btnCancelRide = (Button) findViewById(R.id.btnCancelRide);
        sourceAndDestinationLayout = (LinearLayout) findViewById(R.id.sourceAndDestinationLayout);
        lnrComments = (LinearLayout) findViewById(R.id.lnrComments);
        lnrParent = (LinearLayout) findViewById(R.id.lnrParent);
        ServiceAddressCardView = (CardView) findViewById(R.id.ServiceAddressCardView);

        crdComments = (CardView) findViewById(R.id.crdComments);

        imgInfo= (ImageView) findViewById(R.id.imgInfo);

        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.cencel_request))
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                cancelRequest();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               dialog.dismiss();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg) {
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    }
                });
                alert.show();
            }
        });

        tripProviderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent showProfileIntent = new Intent(context, ShowProviderProfile.class);
                    showProfileIntent.putExtra("provider_id",""+strProviderId);
                    startActivity(showProfileIntent);
            }
        });


        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(activity, HistoryService.class);
                mainIntent.putExtra("before_comment",""+before_comment);
                mainIntent.putExtra("after_comment",""+after_comment);
                mainIntent.putExtra("before_image",""+before_image);
                mainIntent.putExtra("after_image",""+after_image);
                startActivity(mainIntent);
            }
        });

    }


    public void getRequestDetails() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_HISTORY_DETAILS_API + "?request_id=" + jsonObject.optString("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {

                Log.v("GetPaymentList", response.toString());
                if (response != null && response.length() > 0) {

                    before_comment =response.optJSONObject(0).optString("before_comment");
                    after_comment =response.optJSONObject(0).optString("after_comment");
                    before_image =response.optJSONObject(0).optString("before_image");
                    after_image =response.optJSONObject(0).optString("after_image");

                    Glide.with(activity).load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
                    if (response.optJSONObject(0).optJSONObject("payment") != null){
                        tripAmount.setText("$"+response.optJSONObject(0).optJSONObject("payment").optString("total"));
                    }

                    String form = response.optJSONObject(0).optString("assigned_at");
                    try {
                        //tripDate.setText(getDate(form)+"th "+getMonth(form)+" "+getYear(form)+"\n"+getTime(form));
                        tripDate.setText(getDate(form)+"/"+getMonth(form)+"/"+getYear(form)+" at "+getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    strProviderId = response.optJSONObject(0).optString("provider_id");

                    try {
                        if (!response.optJSONObject(0).optString("s_address").equalsIgnoreCase("")){
                            lblServiceAddress.setText(response.getJSONObject(0).optString("s_address"));
                        }else {
                            Double lat = null;
                            Double lng = null;
                            if (!response.optJSONObject(0).optString("s_latitude").equalsIgnoreCase("")){
                                lat = Double.parseDouble(response.getJSONObject(0).optString("s_latitude"));
                            }
                            if (!response.optJSONObject(0).optString("s_longitude").equalsIgnoreCase("")){
                                lng = Double.parseDouble(response.getJSONObject(0).optString("s_longitude"));
                            }
                            if (lat != null && lng != null){
                                //lblServiceAddress.setText(response.getJSONObject(0).optString("s_latitude"));gfgdgdfgdfgdfg
                                if(response.getJSONObject(0).optString("s_address").equalsIgnoreCase(""))
                                {
                                    lblServiceAddress.setText("No Address Available!");

                                }
                                else
                                {
                                    lblServiceAddress.setText(response.getJSONObject(0).optString("s_address"));

                                }
                            }else{
                                lblServiceAddress.setText(response.getJSONObject(0).optString("s_latitude")+", "+response.getJSONObject(0).optString("s_longitude"));
                            }
                        }
                        //Go to Google Map and show loacation
                        ServiceAddressCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri naviUri2 = Uri.parse("http://maps.google.com/maps?"+ "q=loc:" + response.optJSONObject(0).optString("s_latitude") + "," + response.optJSONObject(0).optString("s_longitude"));
                                Intent intentMap = new Intent(Intent.ACTION_VIEW, naviUri2);
                                intentMap.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intentMap);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                    paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    if(response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")){
                        //paymentTypeImg.setImageResource(R.drawable.money);
                    }else {
                        //paymentTypeImg.setImageResource(R.drawable.visa);
                    }
                    Glide.with(activity).load(Utilities.getImageURL(response.optJSONObject(0).optJSONObject("provider").optString("avatar")))
                            .placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).dontAnimate().into(tripProviderImg);

                    SharedHelper.putKey(context, "provider_email", "");
                    SharedHelper.putKey(context, "provider_first_name", "");
                    SharedHelper.putKey(context, "provider_last_name", "");
                    SharedHelper.putKey(context, "provider_mobile", "");
                    SharedHelper.putKey(context, "provider_picture", ""+URLHelper.BASE_IMAGE_LOAD_URL +response.optJSONObject(0).optJSONObject("provider").optString("avatar"));

                    if (response.optJSONObject(0).optJSONObject("rating") != null) {
                        if (!response.optJSONObject(0).optJSONObject("rating").optString("user_comment").equalsIgnoreCase("")){
                            tripComments.setText(response.optJSONObject(0).optJSONObject("rating").optString("user_comment"));
                        }else{
                            tripComments.setText(getResources().getString(R.string.no_comments));
                        }
                    }
                    tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("rating").optString("user_rating")));
                    tripProviderName.setText(response.optJSONObject(0).optJSONObject("provider").optString("first_name") + " " + response.optJSONObject(0).optJSONObject("provider").optString("last_name"));
                    if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                        sourceAndDestinationLayout.setVisibility(View.GONE);
                    } else {
                        tripSource.setText(response.optJSONObject(0).optString("s_address"));
                        tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                    }

                    try{
                        JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                        if (serviceObj!=null){
                            //lblServiceType.setText("Service Type: "+serviceObj.optString("name"));
                            lblServiceType.setText(serviceObj.optString("name"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                customDialog.dismiss();
                parentLayout.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PAST_TRIPS");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));

                        } else {
                            displayMessage(getString(R.string.please_try_again));

                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));

                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    public void getUpcomingDetails() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject.optString("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {

                Log.v("GetPaymentList", response.toString());
                if (response != null && response.length() > 0) {
                    Glide.with(activity).load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
//                    tripDate.setText(response.optJSONObject(0).optString("assigned_at"));
                    paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    String form = response.optJSONObject(0).optString("assigned_at");
                    try {
                        //tripDate.setText(getDate(form)+"th "+getMonth(form)+" "+getYear(form)+"\n"+getTime(form));
                        tripDate.setText(getDate(form)+"/"+getMonth(form)+"/"+getYear(form)+" at "+getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")){
                        //paymentTypeImg.setImageResource(R.drawable.money);
                    }else {
                       // paymentTypeImg.setImageResource(R.drawable.visa);
                    }

                    strProviderId = response.optJSONObject(0).optString("provider_id");

                    if(response.optJSONObject(0).optJSONObject("provider").optString("avatar") != null)
                        Glide.with(activity).load(Utilities.getImageURL(response.optJSONObject(0).optJSONObject("provider").optString("avatar")))
                                .placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).dontAnimate().into(tripProviderImg);

                    tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("provider").optString("rating")));
                    tripProviderName.setText(response.optJSONObject(0).optJSONObject("provider").optString("first_name") + " " + response.optJSONObject(0).optJSONObject("provider").optString("last_name"));
                    if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                        sourceAndDestinationLayout.setVisibility(View.GONE);
                    } else {
                        tripSource.setText(response.optJSONObject(0).optString("s_address"));
                        tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                    }

                    try {
                        if (!response.optJSONObject(0).optString("s_address").equalsIgnoreCase("")){
                            lblServiceAddress.setText(response.getJSONObject(0).optString("s_address"));
                        }else {
                            Double lat = null;
                            Double lng = null;
                            if (!response.optJSONObject(0).optString("s_latitude").equalsIgnoreCase("")){
                                lat = Double.parseDouble(response.getJSONObject(0).optString("s_latitude"));
                            }
                            if (!response.optJSONObject(0).optString("s_longitude").equalsIgnoreCase("")){
                                lng = Double.parseDouble(response.getJSONObject(0).optString("s_longitude"));
                            }
                            if (lat != null && lng != null){
//                                lblServiceAddress.setText(getAddress(lat, lng));
                                if(response.getJSONObject(0).optString("s_address").equalsIgnoreCase(""))
                                {
                                    lblServiceAddress.setText("No Address Available!");

                                }
                                else
                                {
                                    lblServiceAddress.setText(response.getJSONObject(0).optString("s_address"));

                                }
                            }else{
                                lblServiceAddress.setText(response.getJSONObject(0).optString("s_latitude")+", "+response.getJSONObject(0).optString("s_longitude"));
                            }
                        }

                        //Go to Google Map and show loacation
                        ServiceAddressCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri naviUri2 = Uri.parse("http://maps.google.com/maps?"+ "saddr=&daddr=" + response.optJSONObject(0).optString("s_latitude") + "," + response.optJSONObject(0).optString("s_longitude"));
                                Intent intentMap = new Intent(Intent.ACTION_VIEW, naviUri2);
                                intentMap.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intentMap);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try{
                        JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                        if (serviceObj!=null){
                           // lblServiceType.setText("Service Type: "+serviceObj.optString("name"));
                            lblServiceType.setText(serviceObj.optString("name"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                        if (serviceObj!=null){
//                            holder.car_name.setText(serviceObj.optString("name"));
                            tripAmount.setText("$"+serviceObj.optString("price"));
                            Glide.with(activity).load(Utilities.getImageURL(serviceObj.optString("image")))
                                    .placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
                                    .dontAnimate().into(tripProviderImg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


        }
                customDialog.dismiss();
                parentLayout.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("UPCOMING_TRIPS");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));

                        } else {
                            displayMessage(getString(R.string.please_try_again));

                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));

                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                Log.v("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }



    private void refreshAccessToken(final String tag) {


        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.CLIENT_ID);
            object.put("client_secret", URLHelper.CLIENT_SECRET_KEY);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if(tag.equalsIgnoreCase("PAST_TRIPS")){
                    getRequestDetails();
                }else if(tag.equalsIgnoreCase("UPCOMING_TRIPS")){
                    getUpcomingDetails();
                }else if(tag.equalsIgnoreCase("CANCEL_REQUEST")){
                    cancelRequest();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    GoToBeginActivity();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void displayMessage(String toastString) {
        Snackbar.make(lnrParent, toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void cancelRequest() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", jsonObject.optString("id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("CancelRequestResponse", response.toString());
                //SharedHelper.putKey(context, "payment_mode", "CASH");
                customDialog.dismiss();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("CANCEL_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }
    private String getDate(String date) throws ParseException{
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }
    private String getYear(String date) throws ParseException{
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    private String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        String str = timeName.replace("a.m", "AM").replace("p.m","PM");
        return str;
    }


    public String getAddress(double latitude, double longitude) {
        StringBuilder strReturnedAddress = null;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                strReturnedAddress = new StringBuilder();
                for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Can't able to get the address!.Please try again", Toast.LENGTH_SHORT).show();
        }

        String strAddress = "";
        if (strReturnedAddress != null){
            strAddress = strReturnedAddress.toString();

            if(strAddress.equalsIgnoreCase(""))
                strAddress = "No Address Available";
        }
        return strAddress;
    }
}
