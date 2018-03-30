package com.xuber_for_services.app.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.app.Activity.AddCard;
import com.xuber_for_services.app.Activity.BeginScreen;
import com.xuber_for_services.app.Activity.CustomGooglePlacesSearch;
import com.xuber_for_services.app.Activity.Home;
import com.xuber_for_services.app.Activity.ShowProviderProfile;
import com.xuber_for_services.app.Constants.URLHelper;
import com.xuber_for_services.app.Helper.ConnectionHelper;
import com.xuber_for_services.app.Helper.CustomDialog;
import com.xuber_for_services.app.Helper.DirectionsJSONParser;
import com.xuber_for_services.app.Helper.Keyname;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.Models.CardInfo;
import com.xuber_for_services.app.Models.PlacePredictions;
import com.xuber_for_services.app.Models.ServiceListModel;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.Utils.MapAnimator;
import com.xuber_for_services.app.Utils.Utilities;
import com.xuber_for_services.app.XuberServicesApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.xuber_for_services.app.XuberServicesApplication.trimMessage;

public class ServiceFlowFragment extends Fragment implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "ServiceFlowFragment";
    Context context;
    Utilities utils = new Utilities();
    Activity thisActivity;
    View view;
    int value = 0;
    Double crtLat, crtLng;

    boolean checkwallet = false;

    boolean time_choose = false, date_choose = false;

    String new_date_choose = "", new_time_choose = "";

    private double userLatitude = 0;
    private double userLongitude = 0;
    private double provLatitude = 0;
    private double provLongitude = 0;

    int selectedPosition = 0;

    public static final int FLOW_REQUEST = 1;
    public static final int FLOW_LOOKING_FOR_PROVIDERS = 2;
    public static final int FLOW_PROVIDER_ACCEPTED = 3;
    public static final int FLOW_PROVIDER_ARRIVED = 4;
    public static final int FLOW_PROVIDER_STARTED = 5;
    public static final int FLOW_INVOICE = 6;
    public static final int FLOW_RATING = 7;
    public static final int FLOW_SCHEDULE = 8;
    public int SERVICE_FLOW = 0;
    private static final int REQUEST_LOCATION = 1450;
    private static final int ENABLE_LOCATION = 55;
    boolean afterToday = false;

    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;

    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;

    private final int ADD_CARD_CODE = 435;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE = 18945;
    public static final int MY_PERMISSION_PHONE_CALL = 1;
    public static final int MY_PERMISSIONS_ACCESS_FINE = 2;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ArrayList<CardInfo> cardInfoArrayList = new ArrayList<>();
    ArrayList<Marker> lstProviderMarkers = new ArrayList<Marker>();

    ServiceFlowFgmtListener mListener;
    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    AlertDialog alert;
    Handler handleCheckStatus;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LinearLayout lnrZoomPhoto;

    private LatLng userLatLng;
    private LatLng provLatLng;
    private Marker sourceMarker;
    private Marker userMarker, providerMarker;
    ArrayList<LatLng> points = new ArrayList<LatLng>();

    // service flow

    public String PreviousStatus = "";
    public String CurrentStatus = "";
    public String feedBackRating = "";
    String isPaid = "", paymentMode = "";

    CustomDialog customDialog;
    String source_lat = "", source_lng = "", source_address = "", dest_lat = "", dest_lng = "", dest_address = "";

    String scheduledDate = "", scheduledDateNew = "", scheduledDateNewTxt = "";
    String scheduledTime = "", scheduledTimeNew = "", scheduledTimeNewTxt = "";
    DatePickerDialog datePickerDialog;

    BroadcastReceiver mReceiver;

    //UI Elements

    LinearLayout lnrMap, lnrSearch, lnrServicePhoto, lnrRequestService, lnrLookingForProviders, lnrProviderAccepted, lnrInvoice, lnrRateProvider,
            lnrBeforeService, lnrCallCancelRequest, lnrAfterService, lnrBeforeServiceInvoice, lnrAfterServiceInvoice, lnrScheduleLayout;

    //  RelativeLayout lnrWorkStatus;

    LinearLayout lnrTimeConsumed, lnrCancelTrip;
    FrameLayout frmDestination;

    ImageView imgchkWallet;

    LinearLayout lnrWallet;

    View viewWallet;

    TextView lblwalletAmt;

    String strFlow = "";

    Button btnRequestRides, btnCancelRequest02, btnPayNow, btnSubmitReview, btnSchedule;

    TextView serviceSource, lblServiceType, lblHourlyFare, lblPaymentChange, lblBeforeService, lblAfterService, lblBeforeServiceInvoice, lblAfterServiceInvoice, lblStatus, lblStatusServiceRequested,
            lblPaymentType, lblProviderName, lblServiceRequested, lblBasePrice, lblHourlyFareInvoice, lblTaxFare, lblWalletDetection, lblPromotionApplied,
            lblTotal, lblAmountPaid, lblPaymentTypeInvoice, lblProviderNameRate, lblScheduleDate, lblScheduleTime;

    Chronometer lblTimerText;

    EditText txtComments;

    ImageView imgAfterService, imgBeforeService, imgAfterServiceInvoice, imgBeforeServiceInvoice, imgProvider, imgPaymentTypeInvoice, imgPaymentType, imgProviderRate, imgBack, imgPickAddress, backArrow,
            imgGotoPhoto, imgMenu, imgCurrentLocation, imgSchedule, imgBeforeComments, imgAfterComments;

    Button btnScheduleRequest;

    RatingBar ratingProvider, ratingProviderRate;

    RippleBackground rippleBackground;

    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down, bounce_anim;

    GoogleMap mMap;
    SupportMapFragment mapFragment;
    Marker current_marker, currentMarker;
    CameraPosition position;
    ServiceListModel serviceListModel;

    LinearLayout lnrServiceDate, lnrWalletDetection;
    View viewServiceDate;
    TextView lblschdeduleDateMain;
    LinearLayout lnrDateImageTick, lnrStatusBar;
    Button btnConfirmRide;

    boolean schedulebtnClick = false;

    ImageView imgCall, imgCancelTrip;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String tabTitles[];

    Activity activity;

    String strStatusServiceRequested = "";

    String strProviderId = "", StrFlowStatus = "";

    CardView crdStatusBar;

    SampleFragmentPagerAdapter adapterPager;

    public ServiceFlowFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        thisActivity = getActivity();
    }

    public static ServiceFlowFragment newInstance() {
        ServiceFlowFragment fragment = new ServiceFlowFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = getActivity();

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_serviceflow, container, false);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            serviceListModel = bundle.getParcelable(Keyname.KEY_SERVICE_OBJECT);
        }

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(view);
                //permission to access location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Android M Permission check
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE);
                } else {
                    initMap();
                    MapsInitializer.initialize(ServiceFlowFragment.this.getContext());
                }
            }
        }, 500);


        return view;
    }


    @SuppressWarnings("MissingPermission")
    void initMap() {
        if (mMap == null) {
            if (isAdded()){
                FragmentManager fm = getChildFragmentManager();
                mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
                mapFragment.getMapAsync(this);
            }
        }
//        if (mMap != null) {
//            setupMap();
//        }
    }

    // initializing all views
    @SuppressWarnings("MissingPermission")
    private void findViewById(View view) {

        lnrMap = (LinearLayout) view.findViewById(R.id.lnrMap);
        lnrSearch = (LinearLayout) view.findViewById(R.id.lnrSearch);
        lnrScheduleLayout = (LinearLayout) view.findViewById(R.id.lnrScheduleLayout);
        lnrServicePhoto = (LinearLayout) view.findViewById(R.id.lnrServicePhoto);
        lnrRequestService = (LinearLayout) view.findViewById(R.id.lnrRequestProviders);
        lnrLookingForProviders = (LinearLayout) view.findViewById(R.id.lnrLookingForProviders);
        //lnrWorkStatus = (RelativeLayout) view.findViewById(R.id.lnrWorkStatus);
        lnrTimeConsumed = (LinearLayout) view.findViewById(R.id.lnrTimeConsumed);
        lnrCancelTrip = (LinearLayout) view.findViewById(R.id.lnrCancelTrip);
        imgchkWallet = (ImageView) view.findViewById(R.id.imgchkWallet);
        viewWallet = (View) view.findViewById(R.id.viewWallet);
        lnrWallet = (LinearLayout) view.findViewById(R.id.lnrWallet);
        lblwalletAmt = (TextView) view.findViewById(R.id.lblwalletAmt);
        lnrProviderAccepted = (LinearLayout) view.findViewById(R.id.lnrProviderAccepted);
        lnrInvoice = (LinearLayout) view.findViewById(R.id.lnrInvoice);
        lnrRateProvider = (LinearLayout) view.findViewById(R.id.lnrRateProvider);
        lnrBeforeService = (LinearLayout) view.findViewById(R.id.lnrBeforeService);
        lnrAfterService = (LinearLayout) view.findViewById(R.id.lnrAfterService);
        lnrBeforeServiceInvoice = (LinearLayout) view.findViewById(R.id.lnrBeforeServiceInvoice);
        lnrAfterServiceInvoice = (LinearLayout) view.findViewById(R.id.lnrAfterServiceInvoice);
//        lnrCallCancelRequest = (LinearLayout) view.findViewById(R.id.lnrCallCancelRequest);
        lnrServiceDate = (LinearLayout) view.findViewById(R.id.lnrServiceDate);
        lnrWalletDetection = (LinearLayout) view.findViewById(R.id.lnrWalletDetection);
        lnrZoomPhoto = (LinearLayout) view.findViewById(R.id.lnrZoomPhoto);

        lnrStatusBar = (LinearLayout) view.findViewById(R.id.lnrStatusBar);

        crdStatusBar = (CardView) view.findViewById(R.id.crdStatusBar);


        btnRequestRides = (Button) view.findViewById(R.id.btnRequestRides);
        btnCancelRequest02 = (Button) view.findViewById(R.id.btnCancelRequest02);
        imgCall = (ImageView) view.findViewById(R.id.imgCall);
        imgCancelTrip = (ImageView) view.findViewById(R.id.imgCancelTrip);
        btnPayNow = (Button) view.findViewById(R.id.btnPayNow);
        btnSubmitReview = (Button) view.findViewById(R.id.btnSubmitReview);
        btnSchedule = (Button) view.findViewById(R.id.btnSchedule);

        lblServiceType = (TextView) view.findViewById(R.id.lblServiceType);
        lblScheduleDate = (TextView) view.findViewById(R.id.lblScheduleDate);
        lblScheduleTime = (TextView) view.findViewById(R.id.lblScheduleTime);
        lblHourlyFare = (TextView) view.findViewById(R.id.lblHourlyFare);
        serviceSource = (TextView) view.findViewById(R.id.serviceSource);
        lblPaymentChange = (TextView) view.findViewById(R.id.lblPaymentChange);
        lblBeforeService = (TextView) view.findViewById(R.id.lblBeforeService);
        lblAfterService = (TextView) view.findViewById(R.id.lblAfterService);
        lblStatus = (TextView) view.findViewById(R.id.lblStatus);
        lblStatusServiceRequested = (TextView) view.findViewById(R.id.lblStatusServiceRequested);
        lblProviderName = (TextView) view.findViewById(R.id.lblProviderName);
        lblServiceRequested = (TextView) view.findViewById(R.id.lblServiceRequested);
        lblBasePrice = (TextView) view.findViewById(R.id.lblBasePrice);
        lblHourlyFareInvoice = (TextView) view.findViewById(R.id.lblHourlyFareInvoice);
        lblTaxFare = (TextView) view.findViewById(R.id.lblTaxFare);
        lblWalletDetection = (TextView) view.findViewById(R.id.lblWalletDetection);
        lblPromotionApplied = (TextView) view.findViewById(R.id.lblPromotionApplied);
        lblAmountPaid = (TextView) view.findViewById(R.id.lblAmountPaid);
        lblTotal = (TextView) view.findViewById(R.id.lblTotal);
        lblPaymentTypeInvoice = (TextView) view.findViewById(R.id.lblPaymentTypeInvoice);
        lblProviderNameRate = (TextView) view.findViewById(R.id.lblProviderNameRate);
        lblPaymentType = (TextView) view.findViewById(R.id.lblPaymentType);
        lblschdeduleDateMain = (TextView) view.findViewById(R.id.lblschdeduleDateMain);
        lblBeforeServiceInvoice = (TextView) view.findViewById(R.id.lblBeforeServiceInvoice);
        lblAfterServiceInvoice = (TextView) view.findViewById(R.id.lblAfterServiceInvoice);

        lblTimerText = (Chronometer) view.findViewById(R.id.lblTimerText);

        lblTimerText.setBase(SystemClock.elapsedRealtime());

        txtComments = (EditText) view.findViewById(R.id.txtComments);

        imgAfterService = (ImageView) view.findViewById(R.id.imgAfterService);
        imgBeforeService = (ImageView) view.findViewById(R.id.imgBeforeService);
        imgAfterServiceInvoice = (ImageView) view.findViewById(R.id.imgAfterServiceInvoice);
        imgBeforeServiceInvoice = (ImageView) view.findViewById(R.id.imgBeforeServiceInvoice);
        imgProvider = (ImageView) view.findViewById(R.id.imgProvider);
        imgPaymentTypeInvoice = (ImageView) view.findViewById(R.id.imgPaymentTypeInvoice);
        imgPaymentType = (ImageView) view.findViewById(R.id.imgPaymentType);
        imgProviderRate = (ImageView) view.findViewById(R.id.imgProviderRate);
        imgPickAddress = (ImageView) view.findViewById(R.id.imgPickAddress);
        imgBack = (ImageView) view.findViewById(R.id.imgBack);
        imgGotoPhoto = (ImageView) view.findViewById(R.id.imgGotoPhoto);
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        imgMenu = (ImageView) view.findViewById(R.id.imgMenu);
        imgCurrentLocation = (ImageView) view.findViewById(R.id.imgCurrentLocation);
        btnScheduleRequest = (Button) view.findViewById(R.id.btnScheduleRequest);
        imgBeforeComments = (ImageView) view.findViewById(R.id.imgBeforeComments);
        imgAfterComments = (ImageView) view.findViewById(R.id.imgAfterComments);
        lnrDateImageTick = (LinearLayout) view.findViewById(R.id.lnrDateImageTick);
        frmDestination = (FrameLayout) view.findViewById(R.id.frmDestination);


        btnConfirmRide = (Button) view.findViewById(R.id.btnConfirmRide);

        ratingProvider = (RatingBar) view.findViewById(R.id.ratingProvider);
        ratingProviderRate = (RatingBar) view.findViewById(R.id.ratingProviderRate);

        rippleBackground = (RippleBackground) view.findViewById(R.id.content);

        //Load animation
        slide_down = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(context, R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(context, R.anim.slide_up_down);
        bounce_anim = AnimationUtils.loadAnimation(context, R.anim.bounce_anim);

        viewServiceDate = (View) view.findViewById(R.id.viewServiceDate);

//        imgPickAddress.startAnimation(bounce_anim);

        lnrSearch.setOnClickListener(new OnClick());
        btnRequestRides.setOnClickListener(new OnClick());
        btnConfirmRide.setOnClickListener(new OnClick());
        btnCancelRequest02.setOnClickListener(new OnClick());
        lblScheduleDate.setOnClickListener(new OnClick());
        lblScheduleTime.setOnClickListener(new OnClick());
        btnSchedule.setOnClickListener(new OnClick());
        lnrServiceDate.setOnClickListener(new OnClick());
        imgCall.setOnClickListener(new OnClick());
        imgCancelTrip.setOnClickListener(new OnClick());
        btnPayNow.setOnClickListener(new OnClick());
        btnSubmitReview.setOnClickListener(new OnClick());
        imgBeforeService.setOnClickListener(new OnClick());
        imgAfterService.setOnClickListener(new OnClick());
        imgBeforeServiceInvoice.setOnClickListener(new OnClick());
        imgAfterServiceInvoice.setOnClickListener(new OnClick());
        imgPickAddress.setOnClickListener(new OnClick());
        imgBack.setOnClickListener(new OnClick());
        imgGotoPhoto.setOnClickListener(new OnClick());
        btnScheduleRequest.setOnClickListener(new OnClick());
        imgBeforeComments.setOnClickListener(new OnClick());
        imgAfterComments.setOnClickListener(new OnClick());
        backArrow.setOnClickListener(new OnClick());
        imgMenu.setOnClickListener(new OnClick());
        imgCurrentLocation.setOnClickListener(new OnClick());
        lblPaymentChange.setOnClickListener(new OnClick());
        imgchkWallet.setOnClickListener(new OnClick());
        lnrProviderAccepted.setOnClickListener(new OnClick());

        lnrRequestService.setOnClickListener(new OnClick());
        lnrScheduleLayout.setOnClickListener(new OnClick());
        lnrLookingForProviders.setOnClickListener(new OnClick());
        lnrRateProvider.setOnClickListener(new OnClick());

//        // set the layout
        if (!SharedHelper.getKey(context, "status").equalsIgnoreCase("onride")) {
            serviceFlow(FLOW_REQUEST);
        }

        // get the added card details
        getCards();

        // handler for checking the status
        startCheckStatus();

        // enabling location icon
        statusCheck();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    utils.print("", "Back key pressed!");
                    if (lnrServicePhoto.getVisibility() == View.VISIBLE) {
                        lnrServicePhoto.setVisibility(View.GONE);
                        lnrProviderAccepted.setVisibility(View.VISIBLE);
                        /*lnrWorkStatus.setVisibility(View.VISIBLE);
                        if (lnrWorkStatus.getVisibility() == View.GONE) {
                            imgMenu.setVisibility(View.VISIBLE);
                        }*/

                        lnrTimeConsumed.setVisibility(View.VISIBLE);
                        if (strFlow.equalsIgnoreCase(""))
                            crdStatusBar.setVisibility(View.VISIBLE);
                        else
                            crdStatusBar.setVisibility(View.GONE);

                        lnrStatusBar.setVisibility(View.VISIBLE);
                        if (lnrTimeConsumed.getVisibility() == View.GONE) {
                            imgMenu.setVisibility(View.VISIBLE);
                        }
                    } else if (lnrRequestService.getVisibility() == View.VISIBLE) {
                        getFragmentManager().popBackStackImmediate();
                    } else if (lnrScheduleLayout.getVisibility() == View.VISIBLE) {
                        serviceFlow(FLOW_REQUEST);
                    } else {
                        if (SERVICE_FLOW > 1) {
                            getActivity().finish();
                        } else {
                            getFragmentManager().popBackStackImmediate();
                        }
                    }
                    return true;
                } else {
                    getFragmentManager().popBackStackImmediate();
                }
                return false;
            }
        });


        //enable/disable service date here

        if (date_choose == true && time_choose == true) {
            lnrServiceDate.setVisibility(View.VISIBLE);
            viewServiceDate.setVisibility(View.VISIBLE);
        } else {
            lnrServiceDate.setVisibility(View.GONE);
            viewServiceDate.setVisibility(View.GONE);
        }


        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        tabTitles = new String[]{"", ""};
        if(isAdded()){
            adapterPager = new SampleFragmentPagerAdapter(tabTitles, getFragmentManager(), thisActivity);
            viewPager.setAdapter(adapterPager);
        }
    }

    public class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[];
        private Context context;

        public SampleFragmentPagerAdapter(String tabTitles[], FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
            this.tabTitles = tabTitles;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new BeforeService();
                case 1:
                    return new AfterService();
                default:
                    return new BeforeService();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }


    public void statusCheck() {
        try{
            final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableLoc();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void getProvidersList() {
//        customDialog = new CustomDialog(context);
//        customDialog.setCancelable(false);
//        customDialog.show();
        String providers_request = URLHelper.GET_PROVIDERS_LIST_API + "?" +
                "latitude=" + SharedHelper.getKey(context, "current_lat") +
                "&longitude=" + SharedHelper.getKey(context, "current_lng") +
                "&service=" + SharedHelper.getKey(context, "service_type");

        utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));

        for (int i = 0; i < lstProviderMarkers.size(); i++) {
            lstProviderMarkers.get(i).remove();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(providers_request, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                utils.print("GetProvidersList", response.toString());
//                customDialog.dismiss();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObj = response.getJSONObject(i);
                        if (!jsonObj.getString("latitude").equalsIgnoreCase("") && !jsonObj.getString("longitude").equalsIgnoreCase("")) {

                            Double proLat = Double.parseDouble(jsonObj.getString("latitude"));
                            Double proLng = Double.parseDouble(jsonObj.getString("longitude"));


                            Float rotation = 0.0f;

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(proLat, proLng))
                                    .rotation(rotation)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));

//                        availableProviders = mMap.addMarker(markerOptions);
                            lstProviderMarkers.add(mMap.addMarker(markerOptions));
                            builder.include(new LatLng(proLat, proLng));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (lstProviderMarkers.size() > 0) {
                    for (int i = 0; i < lstProviderMarkers.size(); i++) {
                        // dropPinEffect(lstProviderMarkers.get(i));
                    }
                }

                CameraUpdate cu = null;
                LatLngBounds bounds = builder.build();

//                cu = CameraUpdateFactory.newLatLngBounds(bounds, mapLayout.getWidth(), mapLayout.getWidth(), context.getResources()
//                        .getDimensionPixelSize(R.dimen._50sdp));
//                mMap.moveCamera(cu);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                customDialog.dismiss();
                String json = null;
                String Message;

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
//                                utils.showAlert(context, errorObj.optString("message"));
                            } catch (Exception e) {
//                                utils.showAlert(context, getString(R.string.something_went_wrong));

                            }

                        } else if (response.statusCode == 401) {
//                            refreshAccessToken("PAST_TRIPS");
                            Toast.makeText(context,getString(R.string.session_timeout),Toast.LENGTH_SHORT).show();
                            SharedHelper.putKey(context, "current_status", "");
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            Intent mainIntent = new Intent(context, BeginScreen.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            activity.finish();
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
//                                utils.showAlert(context, json);
                            } else {
//                                utils.showAlert(context, context.getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
//                            utils.showAlert(context, context.getString(R.string.please_try_again));
                        } else {
//                            utils.showAlert(context, context.getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
//                        utils.showAlert(context, context.getString(R.string.something_went_wrong));

                    }

                } else {
//                    utils.showAlert(context, context.getString(R.string.no_drivers_found));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), ENABLE_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        Log.v("", "Enabled");
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.v("", "Unavailable");
                        break;
                    case LocationSettingsStatusCodes.CANCELED:
                        Log.v("", "Cancelled");
                        break;
                }
            }
        });
    }

    // on map ready
    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

            if (!success) {
                utils.print("Map:Style", "Style parsing failed.");
            } else {
                utils.print("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            utils.print("Map:Style", "Can't find style. Error: ");
        }

        customDialog.dismiss();

        mMap = googleMap;

        setupMap();
        if (!SharedHelper.getKey(context, "current_lat").equalsIgnoreCase("") && !SharedHelper.getKey(context, "current_lng").equalsIgnoreCase("")) {
            LatLng location = new LatLng(Double.parseDouble(SharedHelper.getKey(context, "current_lat")),
                    Double.parseDouble(SharedHelper.getKey(context, "current_lng")));

            if (location != null) {
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(location)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
//
//            current_marker = mMap.addMarker(markerOptions);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    // on location changed
    @Override
    public void onLocationChanged(Location location) {
        if (value == 0) {
            value = 1;
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        crtLat = location.getLatitude();
        crtLng = location.getLongitude();

//        LatLng currentLoc = new LatLng(crtLat, crtLng);
//
//        if (currentMarker != null){
//            currentMarker.remove();
//        }
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(currentLoc)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_loc_marker));
//        currentMarker = mMap.addMarker(markerOptions);
    }

    @Override
    public void onCameraMove() {
        if (SERVICE_FLOW == 1) {
            position = mMap.getCameraPosition();
            imgPickAddress.setVisibility(View.VISIBLE);
            if (current_marker != null) {
                current_marker.remove();
            }
            mMap.clear();
            serviceSource.setText("Getting Address");
        }
    }

    @Override
    public void onCameraIdle() {
        if (SERVICE_FLOW == 1) {
            position = mMap.getCameraPosition();
            imgPickAddress.setVisibility(View.GONE);
            LatLng myLocation = position.target;
            SharedHelper.putKey(context, "current_lat", "" + myLocation.latitude);
            SharedHelper.putKey(context, "current_lng", "" + myLocation.longitude);

            getAddress(myLocation.latitude, myLocation.longitude);

            // getting near by providers
            getProvidersList();

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position.target)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
            current_marker = mMap.addMarker(markerOptions);
        }
    }

    public void getAddress(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String strCountry = addresses.get(0).getCountryName();
            String strPostalCode = addresses.get(0).getPostalCode();
            String strAddress1 = addresses.get(0).getAddressLine(0);
            String strAddress2 = addresses.get(0).getAddressLine(1);
            String strCity = addresses.get(0).getLocality();
            String strState = addresses.get(0).getAdminArea();
            String strLandmark = addresses.get(0).getSubLocality();
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                //  System.out.println("Test==>"+returnedAddress.getMaxAddressLineIndex());
                if (returnedAddress.getMaxAddressLineIndex() > 0) {
                    for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                    }
                } else {
                    strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("");
                }

                //      Toast.makeText(context,strReturnedAddress.toString(),Toast.LENGTH_SHORT).show();
                serviceSource.setText("" + strReturnedAddress.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // on click
    class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgBack:
                    if (schedulebtnClick == true) {
                        schedulebtnClick = false;
                        serviceFlow(FLOW_REQUEST);
                    } else {
                        getFragmentManager().popBackStackImmediate();
                    }
                    break;
                case R.id.imgMenu:
                    mListener.handleDrawer();
                    break;
                case R.id.btnScheduleRequest:
                case R.id.lnrServiceDate:
                    schedulebtnClick = true;
                    serviceFlow(FLOW_SCHEDULE);
                    break;
                case R.id.imgBeforeComments:
                    showCommentsDialog(imgBeforeComments, "before");
                    break;
                case R.id.imgAfterComments:
                    showCommentsDialog(imgAfterComments, "after");
                    break;
                case R.id.lblScheduleDate:
                    // calender class's instance and get current date , month and year from calender
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    int mMonth = c.get(Calendar.MONTH); // current month
                    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    datePickerDialog = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    // set day of month , month and year value in the edit text
                                    String choosedMonth = "";
                                    String choosedDate = "";
                                    String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                    scheduledDate = choosedDateFormat;
                                    scheduledDateNew = "";
                                    try {
                                        choosedMonth = utils.getMonth(choosedDateFormat);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (dayOfMonth < 10) {
                                        choosedDate = "0" + dayOfMonth;
                                    } else {
                                        choosedDate = "" + dayOfMonth;
                                    }
                                    afterToday = utils.isAfterToday(year, monthOfYear, dayOfMonth);
                                    lblScheduleDate.setText(choosedDate + " " + choosedMonth + " " + year);
                                    date_choose = true;
                                    new_date_choose = choosedDate + "/" + choosedMonth + "/" + year;
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
                    datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg) {
                            datePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.btn_color_blue));
                            datePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.btn_color_blue));
                        }
                    });
                    datePickerDialog.show();
                    break;
                case R.id.lblScheduleTime:
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    final TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        int callCount = 0;   //To track number of calls to onTimeSet()

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (callCount == 0) {
                                String choosedHour = "";
                                String choosedMinute = "";
                                String choosedTimeZone = "";
                                String choosedTime = "";

                                scheduledTime = selectedHour + ":" + selectedMinute;
                                scheduledTimeNew = "";

                                if (selectedHour > 12) {
                                    choosedTimeZone = "PM";
                                    selectedHour = selectedHour - 12;
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                } else {
                                    choosedTimeZone = "AM";
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                }

                                if (selectedMinute < 10) {
                                    choosedMinute = "0" + selectedMinute;
                                } else {
                                    choosedMinute = "" + selectedMinute;
                                }
                                choosedTime = choosedHour + ":" + choosedMinute + " " + choosedTimeZone;

                                if (scheduledDate != "" && scheduledTime != "") {
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long milliseconds = date.getTime();
                                    if (!DateUtils.isToday(milliseconds)) {
                                        lblScheduleTime.setText(choosedTime);
                                        time_choose = true;
                                        new_time_choose = choosedTime;
                                    } else {
                                        if (utils.checktimings(scheduledTime)) {
                                            lblScheduleTime.setText(choosedTime);
                                            time_choose = true;
                                            new_time_choose = choosedTime;
                                        } else {
                                            Toast toast = new Toast(context);
                                            toast.makeText(context, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                                }
                            }
                            callCount++;
                        }
                    }, hour, minute, false);
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg) {
                            mTimePicker.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.btn_color_blue));
                            mTimePicker.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.btn_color_blue));
                        }
                    });
                    mTimePicker.show();
                    break;
                case R.id.btnSchedule:


                    System.out.println("==>NEW=====>" + scheduledDateNew + "<====>" + scheduledTimeNew);


                    if (scheduledDateNew != "") {
                        scheduledDate = scheduledDateNew;
                        new_date_choose = scheduledDateNewTxt;
                        date_choose = true;


                        //ServiceDateSetFlow();
                    }

                    if (scheduledTimeNew != "") {

                        scheduledTime = scheduledTimeNew;
                        new_time_choose = scheduledTimeNewTxt;

                        time_choose = true;
                    }

                    System.out.println("==>OLD=====>" + scheduledDate + "<====>" + scheduledTime);


                    if (scheduledDate != "" && scheduledTime != "") {
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long milliseconds = date.getTime();
                        if (!DateUtils.isToday(milliseconds)) {
                            //sendRequest();
                            ServiceDateSetFlow();
                            schedulebtnClick = false;

                        } else {
                            if (utils.checktimings(scheduledTime)) {
                                //sendRequest();
                                ServiceDateSetFlow();
                                schedulebtnClick = false;

                            } else {
                                Toast.makeText(context, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    /*else if(scheduledDateNew != "" && scheduledTimeNew != "")
                    {
                        scheduledDate =scheduledDateNew;
                        scheduledTime =scheduledTimeNew;
                        ServiceDateSetFlow();
                    }*/
                    else {
                        Toast.makeText(context, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.lnrSearch:
                    Intent intent = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent.putExtra("cursor", "source");
                    intent.putExtra("s_address", "");
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE);
                    break;
                case R.id.btnRequestRides:
                case R.id.btnConfirmRide:
                    /*scheduledDate = "";
                    scheduledTime = "";*/
                    strFlow = "";
                    StrFlowStatus = "";
                    SharedHelper.putKey(context, "after_comment", "");
                    SharedHelper.putKey(context, "before_comment", "");
                    sendRequest();
                    break;
                case R.id.lblPaymentChange:
                    if (cardInfoArrayList.size() > 0)
                        showChooser();
                    else
                        gotoAddCard();
                    break;
                case R.id.btnCancelRequest02:

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(getString(R.string.are_you_sure_cancel))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    cancelRequest();
                                    StrFlowStatus = "cancel";
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog dialog = builder.create();
                    //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        }
                    });
                    dialog.show();


                    //cancelRequest();
                    break;
                case R.id.btnPayNow:
                    payNow();
                    break;
                case R.id.btnSubmitReview:
                    submitReviewCall();
                    break;
                case R.id.imgCancelTrip:
                    //

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage(getString(R.string.are_you_sure_cancel))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    cancelRequest();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog dialog1 = builder1.create();
                    //dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg) {
                            dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            dialog1.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        }
                    });
                    dialog1.show();


                    break;
                case R.id.imgCall:

                    AlertDialog.Builder builder12 = new AlertDialog.Builder(context);
                    builder12.setMessage(getString(R.string.are_you_sure_call))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_PHONE_CALL);
                                    } else {
                                        if(SharedHelper.getKey(context, "provider_mobile_no") != null && SharedHelper.getKey(context, "provider_mobile_no") != ""){
                                            Intent intentCall = new Intent(Intent.ACTION_CALL);
                                            intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                                            startActivity(intentCall);
                                        }else {
                                            Toast.makeText(context,""+getString(R.string.no_mobile),Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog dialog12 = builder12.create();
                    //dialog12.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialog12.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg) {
                            dialog12.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            dialog12.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        }
                    });
                    dialog12.show();


                    break;
                case R.id.backArrow:
                    lnrServicePhoto.setVisibility(View.GONE);
                    if (strFlow.equalsIgnoreCase(""))
                        crdStatusBar.setVisibility(View.VISIBLE);
                    else
                        crdStatusBar.setVisibility(View.GONE);
                    lnrStatusBar.setVisibility(View.VISIBLE);
                    lnrProviderAccepted.setVisibility(View.VISIBLE);
                    break;
                case R.id.imgGotoPhoto:
                    lnrServicePhoto.setVisibility(View.VISIBLE);
                    imgMenu.setVisibility(View.GONE);
                    crdStatusBar.setVisibility(View.GONE);
                    lnrStatusBar.setVisibility(View.GONE);
                    lnrProviderAccepted.setVisibility(View.GONE);
                    break;
                case R.id.imgBeforeService:
                    zoomImageFromThumb(imgBeforeService, Utilities.getImageURL(SharedHelper.getKey(context, "before_image")));
                    mShortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
                    break;
                case R.id.imgAfterService:
                    zoomImageFromThumb(imgAfterService, SharedHelper.getKey(context, "after_image"));
                    mShortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
                    break;
                case R.id.imgBeforeServiceInvoice:
                    zoomImageFromThumb(imgBeforeServiceInvoice, SharedHelper.getKey(context, "before_image"));
                    mShortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
                    break;
                case R.id.imgAfterServiceInvoice:
                    zoomImageFromThumb(imgAfterServiceInvoice, SharedHelper.getKey(context, "after_image"));
                    mShortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
                    break;
                case R.id.imgCurrentLocation:
                    if (crtLat != null && crtLng != null) {
                        LatLng loc = new LatLng(crtLat, crtLng);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                    break;

                case R.id.imgchkWallet:

                    if (checkwallet == false) {
                        checkwallet = true;
                        imgchkWallet.setBackground(getResources().getDrawable(R.drawable.check_tick));
                    } else {
                        checkwallet = false;
                        imgchkWallet.setBackground(getResources().getDrawable(R.drawable.check_untick));
                    }

                    break;

                case R.id.lnrProviderAccepted:

                    System.out.println("lnrProviderAccepted===> clicked==>");

                    break;
            }
        }
    }


    void ServiceDateSetFlow() {
        serviceFlow(FLOW_REQUEST);
        lblschdeduleDateMain.setText(new_date_choose + " at " + new_time_choose);
        lnrDateImageTick.setVisibility(View.VISIBLE);
        btnScheduleRequest.setVisibility(View.GONE);
        btnRequestRides.setVisibility(View.GONE);
        btnConfirmRide.setVisibility(View.VISIBLE);

        if (date_choose == true && time_choose == true) {
            lnrServiceDate.setVisibility(View.VISIBLE);
            viewServiceDate.setVisibility(View.VISIBLE);
        } else {
            lnrServiceDate.setVisibility(View.GONE);
            viewServiceDate.setVisibility(View.GONE);
        }
    }

    private void showCommentsDialog(View imgView, String strTag) {
        int[] viewCoords = new int[2];
        imgView.getLocationInWindow(viewCoords);
        final Dialog dialog = new Dialog(thisActivity, R.style.CommentsDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final View view = LayoutInflater.from(thisActivity).inflate(R.layout.comments_layout, null);
        TextView lblComments = (TextView) view.findViewById(R.id.lblComments);
        if (strTag.equalsIgnoreCase("before")) {
            lblComments.setText("" + SharedHelper.getKey(context, "before_comment"));
        } else {
            lblComments.setText("" + SharedHelper.getKey(context, "after_comment"));
        }
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        int imageX = viewCoords[0];
        int imageY = viewCoords[1];
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x = (int) imageX;
        wmlp.y = (int) imageY;
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setContentView(view);
        if (dialog.isShowing()) {
            dialog.cancel();
        } else {
            dialog.show();
        }
    }

    // method for initializing start check status
    private void startCheckStatus() {
        handleCheckStatus = new Handler();
        helper = new ConnectionHelper(context);
        //check status every 3 sec
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {
                    if (!isAdded()) {
                        return;
                    }
                    utils.print("Handler", "Called");
                    checkStatus();
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                        alert = null;
                    }
                } else {
                    showDialog();
                }
                handleCheckStatus.postDelayed(this, 3000);
            }
        }, 3000);
    }

    // Service Flow Layout changes
    void serviceFlow(int strTag) {
        SERVICE_FLOW = strTag;
        utils.hideKeypad(context, getView());

        imgProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowProviderProfile.class);
                intent.putExtra("provider_id", "" + strProviderId);
                startActivity(intent);
            }
        });
        imgProviderRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowProviderProfile.class);
                intent.putExtra("provider_id", "" + strProviderId);
                startActivity(intent);
            }
        });

        if (lnrProviderAccepted.getVisibility() == View.VISIBLE) {
            lnrProviderAccepted.startAnimation(slide_down);
        } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
            lnrInvoice.startAnimation(slide_down);
        } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
            lnrRateProvider.startAnimation(slide_down);
        } else if (lnrScheduleLayout.getVisibility() == View.VISIBLE) {
            lnrScheduleLayout.startAnimation(slide_down);
        }
        lnrScheduleLayout.setVisibility(View.GONE);
//        lnrSearch.setVisibility(View.GONE);
//        crdStatusBar.setVisibility(View.GONE);
        lnrRequestService.setVisibility(View.GONE);
        //lnrWorkStatus.setVisibility(View.GONE);
        lnrProviderAccepted.setVisibility(View.GONE);
        lnrInvoice.setVisibility(View.GONE);
        lnrRateProvider.setVisibility(View.GONE);
//        lnrCallCancelRequest.setVisibility(View.GONE);
        lblTimerText.setVisibility(View.GONE);
        lblStatus.setVisibility(View.GONE);
        lblStatusServiceRequested.setVisibility(View.GONE);
        lnrStatusBar.setVisibility(View.GONE);
        lnrLookingForProviders.setVisibility(View.GONE);
        lnrCancelTrip.setVisibility(View.GONE);
        lnrTimeConsumed.setVisibility(View.GONE);
        imgGotoPhoto.setVisibility(View.GONE);
        frmDestination.setVisibility(View.GONE);
        if (checkwallet == true) {
            checkwallet = false;
            imgchkWallet.setBackground(getResources().getDrawable(R.drawable.check_untick));
        }


        switch (strTag) {
            case FLOW_REQUEST:
                imgBack.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.GONE);
                lblServiceType.setText(SharedHelper.getKey(context, "service_name"));
                //lblHourlyFare.setText(SharedHelper.getKey(context, "currency") + SharedHelper.getKey(context, "hourly_fare"));

                if (SharedHelper.getKey(context, "hourly_fare").equalsIgnoreCase("null"))
                    lblHourlyFare.setText(SharedHelper.getKey(context,"currency")+"" + "0");
                else
                    lblHourlyFare.setText(SharedHelper.getKey(context,"currency")+"" + SharedHelper.getKey(context, "hourly_fare"));


                btnScheduleRequest.setVisibility(View.VISIBLE);
                btnRequestRides.setVisibility(View.VISIBLE);
                btnConfirmRide.setVisibility(View.GONE);
                if (!SharedHelper.getKey(context, "wallet_balance").equalsIgnoreCase("")) {
                    double wallet_balance = Double.parseDouble(SharedHelper.getKey(context, "wallet_balance"));
                    if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                        viewWallet.setVisibility(View.VISIBLE);
                        lnrWallet.setVisibility(View.VISIBLE);
                        lblwalletAmt.setText(SharedHelper.getKey(context,"currency")+""+ wallet_balance);
                    } else {
                        lnrWallet.setVisibility(View.GONE);
                        viewWallet.setVisibility(View.GONE);
                    }
                }
                lnrSearch.setVisibility(View.VISIBLE);
                crdStatusBar.setVisibility(View.VISIBLE);
                frmDestination.setVisibility(View.VISIBLE);
               /* if (lnrProviderAccepted.getVisibility() == View.GONE) {
                    lnrProviderAccepted.startAnimation(slide_up);
                }*/
                lnrRequestService.setVisibility(View.VISIBLE);
                break;
            case FLOW_LOOKING_FOR_PROVIDERS:
                imgBack.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                crdStatusBar.setVisibility(View.GONE);
                frmDestination.setVisibility(View.GONE);
                imgPickAddress.setVisibility(View.GONE);
                lnrLookingForProviders.setVisibility(View.VISIBLE);
                rippleBackground.startRippleAnimation();
//                customDialog.show();
                break;
            case FLOW_PROVIDER_ACCEPTED:
                //Provider accepted your request
                imgBack.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                imgPickAddress.setVisibility(View.GONE);
                rippleBackground.stopRippleAnimation();
//                if (customDialog != null && customDialog.isShowing()){
//                    customDialog.dismiss();
//                }
                if (lnrProviderAccepted.getVisibility() == View.GONE) {
                    lnrProviderAccepted.startAnimation(slide_up);
                }
                lnrCancelTrip.setVisibility(View.VISIBLE);
                lnrProviderAccepted.setVisibility(View.VISIBLE);
                crdStatusBar.setVisibility(View.VISIBLE);
                lnrStatusBar.setVisibility(View.VISIBLE);
                lblStatus.setVisibility(View.VISIBLE);
                lblStatusServiceRequested.setVisibility(View.VISIBLE);
                lblStatusServiceRequested.setText(strStatusServiceRequested);
                lblStatus.setText(getResources().getString(R.string.provider_accepted));
//                lnrCallCancelRequest.setVisibility(View.VISIBLE);
                break;
            case FLOW_PROVIDER_ARRIVED:
                imgBack.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                lnrCancelTrip.setVisibility(View.VISIBLE);
                imgPickAddress.setVisibility(View.GONE);
                if (lnrProviderAccepted.getVisibility() == View.GONE) {
                    lnrProviderAccepted.startAnimation(slide_up);
                }
                lnrProviderAccepted.setVisibility(View.VISIBLE);
//                lnrCallCancelRequest.setVisibility(View.GONE);
                crdStatusBar.setVisibility(View.VISIBLE);
                lnrStatusBar.setVisibility(View.VISIBLE);
                lblStatus.setVisibility(View.VISIBLE);
                lblStatusServiceRequested.setVisibility(View.VISIBLE);
                lblStatusServiceRequested.setText(strStatusServiceRequested);
                lnrCancelTrip.setVisibility(View.VISIBLE);
//                lnrCallCancelRequest.setVisibility(View.GONE);
                lblStatus.setText(getResources().getString(R.string.provider_arrived));
                break;
            case FLOW_PROVIDER_STARTED:
                imgBack.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                imgGotoPhoto.setVisibility(View.VISIBLE);
                lnrTimeConsumed.setVisibility(View.VISIBLE);
                imgPickAddress.setVisibility(View.GONE);
                if (lnrProviderAccepted.getVisibility() == View.GONE) {
                    lnrProviderAccepted.startAnimation(slide_up);
                }
                lnrProviderAccepted.setVisibility(View.VISIBLE);
//                lnrCallCancelRequest.setVisibility(View.GONE);
                crdStatusBar.setVisibility(View.VISIBLE);
                lnrStatusBar.setVisibility(View.VISIBLE);
                lblStatus.setVisibility(View.VISIBLE);
                lblStatusServiceRequested.setVisibility(View.VISIBLE);
                lblStatusServiceRequested.setText(strStatusServiceRequested);
//                lnrCallCancelRequest.setVisibility(View.GONE);
                lblStatus.setText(getResources().getString(R.string.provider_service));
                if (lnrServicePhoto.getVisibility() != View.VISIBLE) {
                    // lnrWorkStatus.setVisibility(View.VISIBLE);
                }
                imgMenu.setVisibility(View.GONE);
                //imgCurrentLocation.setVisibility(View.GONE);
                lblTimerText.setVisibility(View.VISIBLE);
                lblTimerText.start();
                break;
            case FLOW_INVOICE:

                adapterPager = new SampleFragmentPagerAdapter(tabTitles, getFragmentManager(), thisActivity);
                viewPager.setAdapter(adapterPager);

                adapterPager.notifyDataSetChanged();

                crdStatusBar.setVisibility(View.GONE);
                imgBack.setVisibility(View.GONE);
                lnrStatusBar.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                imgPickAddress.setVisibility(View.GONE);
                //lnrWorkStatus.setVisibility(View.GONE);
                imgMenu.setVisibility(View.GONE);
                imgCurrentLocation.setVisibility(View.VISIBLE);
                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);
                lblTimerText.stop();
                imgMenu.setVisibility(View.VISIBLE);

                strFlow = "invoice";
                //viewPager.setAdapter(new SampleFragmentPagerAdapter(tabTitles, getFragmentManager(), thisActivity));


                break;
            case FLOW_RATING:
                txtComments.setText("");
                imgBack.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                imgPickAddress.setVisibility(View.GONE);
                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);
                crdStatusBar.setVisibility(View.GONE);
                //lnrWorkStatus.setVisibility(View.GONE);
                ratingProviderRate.setRating(1.0f);
                feedBackRating = "1";
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(1.0f);
                            feedBackRating = "1";
                        }
                        feedBackRating = String.valueOf((int) rating);
                    }
                });
                break;
            case FLOW_SCHEDULE:
                if (lnrScheduleLayout.getVisibility() == View.GONE) {
                    lnrScheduleLayout.startAnimation(slide_up);
                }
                lnrScheduleLayout.setVisibility(View.VISIBLE);
                lnrRequestService.setVisibility(View.GONE);


                //TIME
                Calendar now = Calendar.getInstance();
                now.add(Calendar.MINUTE, 30);
                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");  //send API
                Calendar c = Calendar.getInstance();

                scheduledTimeNew = df1.format(now.getTime());

                // AM/PM format
                SimpleDateFormat df = new SimpleDateFormat("hh:mm aa"); //Set text

                lblScheduleTime.setText(df.format(now.getTime()));

                scheduledTimeNewTxt = df.format(now.getTime());


                //DATE
                SimpleDateFormat dfdate1 = new SimpleDateFormat("d-M-yyyy"); //send API

                scheduledDateNew = dfdate1.format(c.getTime());

                SimpleDateFormat dfdate = new SimpleDateFormat("dd MMM yyyy"); //set text
                String formattedDate = dfdate.format(c.getTime());

                System.out.println(formattedDate);

                scheduledDateNewTxt = formattedDate;


                lblScheduleDate.setText(formattedDate);
                break;
            default:
                break;
        }
    }

    // Check Status
    private void checkStatus() {
        try {

            utils.print("Handler", "Inside");
            if (helper.isConnectingToInternet()) {

                XuberServicesApplication.getInstance().cancelRequestInQueue("check_status");

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        URLHelper.REQUEST_STATUS_CHECK_API, new JSONObject(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        utils.print("Response", "" + response.toString());

                        if (response.optJSONArray("data") != null && response.optJSONArray("data").length() > 0) {
                            utils.print("response", "not null");
                            try {
                                JSONArray requestStatusCheck = response.optJSONArray("data");
                                JSONObject requestStatusCheckObject = requestStatusCheck.getJSONObject(0);
                                String status = requestStatusCheckObject.optString("status");
                                JSONObject provider_latlng = requestStatusCheckObject.optJSONObject("provider");
                                JSONObject user_details = requestStatusCheckObject.optJSONObject("user");
                                SharedHelper.putKey(context, "currency",user_details.optString("currency"));

                                if (provider_latlng != null) {
                                    strProviderId = provider_latlng.optString("id");
                                }


                                if (!SharedHelper.getKey(context, "current_lat").equalsIgnoreCase("") && !SharedHelper.getKey(context, "current_lng").equalsIgnoreCase("")) {
                                    if (current_marker != null) {
                                        current_marker.remove();
                                    }
                                    userLatLng = new LatLng(Double.parseDouble(SharedHelper.getKey(context, "current_lat")), Double.parseDouble(SharedHelper.getKey(context, "current_lng")));

                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(userLatLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
                                    current_marker = mMap.addMarker(markerOptions);
                                }
                                if (provider_latlng != null) {
                                    if (!provider_latlng.optString("latitude").equalsIgnoreCase("") && !provider_latlng.optString("longitude").equalsIgnoreCase("")) {
                                        if (providerMarker != null) {
                                            providerMarker.remove();
                                        }
                                        provLatLng = new LatLng(Double.parseDouble(provider_latlng.optString("latitude")),
                                                Double.parseDouble(provider_latlng.optString("longitude")));
                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position(provLatLng)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));
                                        providerMarker = mMap.addMarker(markerOptions);

                                        if (!provider_latlng.optString("latitude").equalsIgnoreCase("")) {
                                            provLatitude = Double.valueOf(provider_latlng.optString("latitude"));
                                        }
                                        if (!provider_latlng.optString("longitude").equalsIgnoreCase("")) {
                                            provLongitude = Double.valueOf(provider_latlng.optString("longitude"));
                                        }
                                    }
                                }

                                if (!requestStatusCheckObject.optString("s_latitude").equalsIgnoreCase("")) {
                                    userLatitude = Double.valueOf(requestStatusCheckObject.optString("s_latitude"));
                                }
                                if (!requestStatusCheckObject.optString("s_longitude").equalsIgnoreCase("")) {
                                    userLongitude = Double.valueOf(requestStatusCheckObject.optString("s_longitude"));
                                }

                                if (status.equalsIgnoreCase("STARTED")) {
                                    if (userLatitude != 0 && userLongitude != 0 && provLatitude != 0 && provLongitude != 0) {
                                        mMap.clear();
                                        setRoutePath();
                                    }
                                } else {
                                    mMap.clear();
                                    setUserProvider();
                                }

                                utils.print("PreviousStatus", "" + PreviousStatus);

                                if (!PreviousStatus.equals(status)) {
                                    utils.print("PreviousStatus====>", "" + PreviousStatus);
                                    PreviousStatus = status;
                                    SharedHelper.putKey(context, "request_id", "" + requestStatusCheckObject.optString("id"));
                                    utils.print("ResponseStatus", "SavedCurrentStatus: " + CurrentStatus + " Status: " + status);
                                    switch (status) {
                                        case "SEARCHING":
                                            SharedHelper.putKey(context, "status", "onride");
                                            serviceFlow(FLOW_LOOKING_FOR_PROVIDERS);
                                            break;
                                        case "CANCELLED":
                                            break;
                                        case "ACCEPTED":
                                            SharedHelper.putKey(context, "status", "onride");
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProviderName.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                Picasso.with(context).load(Utilities.getImageURL(provider.optString("avatar"))).memoryPolicy(MemoryPolicy.NO_CACHE)
                                                        .placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                strStatusServiceRequested = service_type.optString("name");
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                serviceFlow(FLOW_PROVIDER_ACCEPTED);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "STARTED":
                                            SharedHelper.putKey(context, "status", "onride");
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                lblProviderName.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                Picasso.with(context).load(Utilities.getImageURL(provider.optString("avatar"))).memoryPolicy(MemoryPolicy.NO_CACHE)
                                                        .placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                strStatusServiceRequested = service_type.optString("name");
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                serviceFlow(FLOW_PROVIDER_ACCEPTED);

                                                userLatLng = new LatLng(userLatitude, userLongitude);
                                                provLatLng = new LatLng(provLatitude, provLongitude);
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(provLatLng).zoom(16).build();
                                                MarkerOptions options = new MarkerOptions();
                                                options.position(provLatLng).isDraggable();
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "ARRIVED":
                                            SharedHelper.putKey(context, "status", "onride");
                                            try {
                                                utils.print("MyTest", "ARRIVED TRY");
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProviderName.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                Picasso.with(context).load(Utilities.getImageURL(provider.optString("avatar")))
                                                        .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                strStatusServiceRequested = service_type.optString("name");
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                serviceFlow(FLOW_PROVIDER_ARRIVED);
                                                lnrBeforeService.setVisibility(View.VISIBLE);
                                                lnrAfterService.setVisibility(View.VISIBLE);

                                                userLatLng = new LatLng(userLatitude, userLongitude);
                                                provLatLng = new LatLng(provLatitude, provLongitude);
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(provLatLng).zoom(16).build();
                                                MarkerOptions options = new MarkerOptions();
                                                options.position(provLatLng).isDraggable();
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            } catch (Exception e) {
                                                utils.print("MyTest", "ARRIVED CATCH");
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "PICKEDUP":
                                            SharedHelper.putKey(context, "status", "onride");
                                            serviceFlow(FLOW_PROVIDER_STARTED);
                                            try {
                                                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                lblProviderName.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                Picasso.with(context).load(Utilities.getImageURL(provider.optString("avatar")))
                                                        .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                lblServiceRequested.setText(service_type.optString("name"));
                                                strStatusServiceRequested = service_type.optString("name");
                                                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                lnrAfterService.setVisibility(View.INVISIBLE);


                                                //image put

                                                if (requestStatusCheckObject.optString("before_image") != null && !requestStatusCheckObject.optString("before_image").equalsIgnoreCase("") && !requestStatusCheckObject.optString("before_image").equalsIgnoreCase("null")) {
                                                    Picasso.with(context).load(Utilities.getImageURL(requestStatusCheckObject.optString("before_image"))).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);
                                                    lnrBeforeService.setVisibility(View.VISIBLE);
                                                    SharedHelper.putKey(context, "before_image", Utilities.getImageURL(requestStatusCheckObject.optString("before_image")));


                                                } else {
                                                    SharedHelper.putKey(context, "before_image", "");
                                                }

                                                // comment put
                                                if (requestStatusCheckObject.optString("before_comment") != null && !requestStatusCheckObject.optString("before_comment").equalsIgnoreCase("") && !requestStatusCheckObject.optString("before_comment").equalsIgnoreCase("null")) {
                                                    imgBeforeComments.setVisibility(View.GONE);
                                                    SharedHelper.putKey(context, "before_comment", requestStatusCheckObject.optString("before_comment"));
                                                } else {
                                                    imgBeforeComments.setVisibility(View.GONE);
                                                    SharedHelper.putKey(context, "before_comment", "");

                                                }

                                                lblBeforeService.setVisibility(View.VISIBLE);
                                                if (SharedHelper.getKey(context, "before_comment").equalsIgnoreCase(""))
                                                    lblBeforeService.setText("");
                                                else
                                                    lblBeforeService.setText("" + SharedHelper.getKey(context, "before_comment"));

//                                                lblTimerText.setBase(getFormatedDateTime(requestStatusCheckObject.optString("started_at"), "", ""));

                                                Long diff = getFormatedDateTime(requestStatusCheckObject.optString("started_at"), "", "");
                                                long diffSeconds = diff / 1000 % 60;
                                                long diffMinutes = diff / (60 * 1000) % 60;
                                                long diffHours = diff / (60 * 60 * 1000);

                                                Log.v("", "Hours:" + diffHours + " ==Minutes:" + diffMinutes + " == Seconds" + diffSeconds);
                                                lblTimerText.setBase(SystemClock.elapsedRealtime() - (diffHours * 3600000 + diffMinutes * 60000 + diffSeconds * 1000));

                                                serviceFlow(FLOW_PROVIDER_STARTED);

                                                userLatLng = new LatLng(userLatitude, userLongitude);
                                                provLatLng = new LatLng(provLatitude, provLongitude);
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(provLatLng).zoom(16).build();
                                                MarkerOptions options = new MarkerOptions();
                                                options.position(provLatLng).isDraggable();
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "DROPPED":
                                            SharedHelper.putKey(context, "status", "onride");
                                            try {
                                                JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                isPaid = requestStatusCheckObject.optString("paid");
                                                paymentMode = requestStatusCheckObject.optString("payment_mode");

                                                if (requestStatusCheckObject.optString("before_comment") != null && !requestStatusCheckObject.optString("before_comment").equalsIgnoreCase("") && !requestStatusCheckObject.optString("before_comment").equalsIgnoreCase("null")) {
                                                    SharedHelper.putKey(context, "before_comment", requestStatusCheckObject.optString("before_comment"));
                                                } else {
                                                    SharedHelper.putKey(context, "before_comment", "");
                                                }


                                                if (requestStatusCheckObject.optString("before_image") != null && !requestStatusCheckObject.optString("before_image").equalsIgnoreCase("null") && !requestStatusCheckObject.optString("before_image").equalsIgnoreCase("")) {
                                                    Picasso.with(context).load(Utilities.getImageURL(requestStatusCheckObject.optString("before_image"))).memoryPolicy(MemoryPolicy.NO_CACHE)
                                                            .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);

                                                    Picasso.with(context).load(Utilities.getImageURL(requestStatusCheckObject.optString("before_image"))).memoryPolicy(MemoryPolicy.NO_CACHE)
                                                            .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeServiceInvoice);

                                                    SharedHelper.putKey(context, "before_image", Utilities.getImageURL(requestStatusCheckObject.optString("before_image")));

                                                    lblBeforeService.setVisibility(View.VISIBLE);


                                                    if (SharedHelper.getKey(context, "before_comment").equalsIgnoreCase(""))
                                                        lblBeforeService.setText("");
                                                    else
                                                        lblBeforeService.setText("" + SharedHelper.getKey(context, "before_comment"));
                                                    // lnrBeforeServiceInvoice.setVisibility(View.VISIBLE);
                                                } else {
                                                    //  lnrBeforeServiceInvoice.setVisibility(View.GONE);
                                                    Picasso.with(context).load(Utilities.getImageURL(requestStatusCheckObject.optString("before_image"))).memoryPolicy(MemoryPolicy.NO_CACHE)
                                                            .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);
                                                    SharedHelper.putKey(context, "before_image", "");
                                                }

                                                if (requestStatusCheckObject.optString("after_image") != null && !requestStatusCheckObject.optString("after_image").equalsIgnoreCase("null") && !requestStatusCheckObject.optString("after_image").equalsIgnoreCase("")) {
                                                    Picasso.with(context).load(Utilities.getImageURL(requestStatusCheckObject.optString("after_image"))).memoryPolicy(MemoryPolicy.NO_CACHE)
                                                            .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgAfterService);

                                                    SharedHelper.putKey(context, "after_image", Utilities.getImageURL(requestStatusCheckObject.optString("after_image")));


                                                    Picasso.with(context).load(Utilities.getImageURL(requestStatusCheckObject.optString("after_image"))).memoryPolicy(MemoryPolicy.NO_CACHE)
                                                            .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgAfterServiceInvoice);


                                                    // lnrAfterServiceInvoice.setVisibility(View.VISIBLE);
                                                } else {
                                                    //  lnrAfterServiceInvoice.setVisibility(View.GONE);
                                                    SharedHelper.putKey(context, "after_image", "");

                                                    Picasso.with(context).load(Utilities.getImageURL(requestStatusCheckObject.optString("after_image"))).memoryPolicy(MemoryPolicy.NO_CACHE)
                                                            .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);
                                                }

                                                if (requestStatusCheckObject.optString("after_comment") != null && !requestStatusCheckObject.optString("after_comment").equalsIgnoreCase("") && !requestStatusCheckObject.optString("after_comment").equalsIgnoreCase("null")) {
                                                    SharedHelper.putKey(context, "after_comment", requestStatusCheckObject.optString("after_comment"));
                                                } else {
                                                    SharedHelper.putKey(context, "after_comment", "");
                                                }


                                                lblAfterService.setVisibility(View.VISIBLE);
                                                if (SharedHelper.getKey(context, "after_comment").equalsIgnoreCase(""))
                                                    lblAfterService.setText("");
                                                else
                                                    lblAfterService.setText("" + SharedHelper.getKey(context, "after_comment"));

//                                                lblBasePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("fixed"));
                                                lblBasePrice.setText("$" + payment.optString("fixed"));
                                                lblTaxFare.setText("$" + payment.optString("tax"));
                                                lblHourlyFareInvoice.setText("$" + payment.optString("time_price"));

                                                if (payment.optString("wallet").equalsIgnoreCase("0"))
                                                    lnrWalletDetection.setVisibility(View.GONE);
                                                else
                                                    lnrWalletDetection.setVisibility(View.VISIBLE);

                                                lblWalletDetection.setText("$" + payment.optString("wallet"));


                                                lblPromotionApplied.setText("$" + payment.optString("discount"));
                                                lblTotal.setText("$" + payment.optString("total"));
                                                lblAmountPaid.setText("$" + payment.optString("total"));
                                                if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    serviceFlow(FLOW_INVOICE);
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    serviceFlow(FLOW_INVOICE);
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("CARD");
                                                } else if (isPaid.equalsIgnoreCase("1")) {
                                                    lblProviderNameRate.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                    Picasso.with(context).load(Utilities.getImageURL(provider.optString("avatar"))).placeholder(R.drawable.ic_dummy_user).memoryPolicy(MemoryPolicy.NO_CACHE).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                    serviceFlow(FLOW_RATING);
                                                }

                                                userLatLng = new LatLng(userLatitude, userLongitude);
                                                provLatLng = new LatLng(provLatitude, provLongitude);
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(provLatLng).zoom(16).build();
                                                MarkerOptions options = new MarkerOptions();
                                                options.position(provLatLng).isDraggable();
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;

                                        case "COMPLETED":
                                            SharedHelper.putKey(context, "status", "onride");
                                            try {
                                                JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                isPaid = requestStatusCheckObject.optString("paid");
                                                paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                lblBasePrice.setText("$" + payment.optString("fixed"));
                                                lblTaxFare.setText("$" + payment.optString("tax"));
                                                lblHourlyFareInvoice.setText("$" + payment.optString("time_price"));

                                                if (payment.optString("wallet").equalsIgnoreCase("0"))
                                                    lnrWalletDetection.setVisibility(View.GONE);
                                                else
                                                    lnrWalletDetection.setVisibility(View.VISIBLE);

                                                lblWalletDetection.setText("$" + payment.optString("wallet"));

                                                lblWalletDetection.setText("$" + payment.optString("wallet"));

                                                lblPromotionApplied.setText("$" + payment.optString("discount"));
                                                lblTotal.setText("$" + payment.optString("total"));
                                                lblAmountPaid.setText("$" + payment.optString("total"));
                                                if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                    btnPayNow.setVisibility(View.GONE);
                                                    serviceFlow(FLOW_INVOICE);
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.money);
                                                    lblPaymentTypeInvoice.setText("CASH");
                                                } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                    btnPayNow.setVisibility(View.VISIBLE);
                                                    serviceFlow(FLOW_INVOICE);
                                                    imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                    lblPaymentTypeInvoice.setText("CARD");
                                                } else if (isPaid.equalsIgnoreCase("1")) {
                                                    lblProviderNameRate.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
                                                    Picasso.with(context).load(Utilities.getImageURL(provider.optString("avatar"))).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgProviderRate);
                                                    serviceFlow(FLOW_RATING);
                                                }

                                                userLatLng = new LatLng(userLatitude, userLongitude);
                                                provLatLng = new LatLng(provLatitude, provLongitude);
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(provLatLng).zoom(16).build();
                                                MarkerOptions options = new MarkerOptions();
                                                options.position(provLatLng).isDraggable();
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                utils.displayMessage(getView(),context, getString(R.string.something_went_wrong));
                            }
                        } else {
                            SharedHelper.putKey(context, "status", "completed");
                            if (PreviousStatus.equals("SEARCHING")) {
                                serviceFlow(FLOW_REQUEST);

                                if (StrFlowStatus.equalsIgnoreCase("")) {
                                    //Toast.makeText(thisActivity, "No Nearby providers Available!", Toast.LENGTH_LONG).show();
                                    StrFlowStatus = "end";
                                    PreviousStatus = "";
                                }
                            }

                            if (PreviousStatus.equals("STARTED")) {
                                if (mMap != null)
                                    mMap.clear();
                                StrFlowStatus = "end";
                                PreviousStatus = "";
                                serviceFlow(FLOW_REQUEST);
                                Toast.makeText(thisActivity, "Provider Cancelled your request!", Toast.LENGTH_LONG).show();
                            }

                            if (SERVICE_FLOW != 1 && SERVICE_FLOW != 8) {
                                if (mMap != null)
                                    mMap.clear();
                                StrFlowStatus = "end";
                                PreviousStatus = "";
                                serviceFlow(FLOW_REQUEST);
                                //     Toast.makeText(thisActivity,"Provider Cancelled your request!",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.print("Error", error.toString());

                        String json = null;
                        String Message;
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            try {
                                JSONObject errorObj = new JSONObject(new String(response.data));
                                if (response.statusCode == 401) {
                                    Toast.makeText(context,getString(R.string.session_timeout),Toast.LENGTH_SHORT).show();
                                    SharedHelper.putKey(context, "current_status", "");
                                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                                    Intent mainIntent = new Intent(context, BeginScreen.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(mainIntent);
                                    activity.finish();
                                }

                            } catch (Exception e) {
                                //utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }

                        } else {
                            //utils.displayMessage(getView(), getString(R.string.please_try_again));
                        }

                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        utils.print("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };
                XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {
                utils.displayMessage(getView(),context, getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Request Providers
    public void sendRequest() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("s_latitude", SharedHelper.getKey(context, "current_lat"));
            object.put("s_longitude", SharedHelper.getKey(context, "current_lng"));
            object.put("s_address", serviceSource.getText().toString());
            object.put("service_type", SharedHelper.getKey(context, "service_type"));
            object.put("schedule_date", scheduledDate);
            object.put("schedule_time", scheduledTime);

            System.out.println("schedule_date==>" + scheduledDate + "schedule_time==>" + scheduledTime);

            if (checkwallet == true) {
                object.put("use_wallet", 1);
            } else {
                object.put("use_wallet", 0);
            }
            if (lblPaymentType.getText().toString().equalsIgnoreCase("CASH")) {
                object.put("payment_mode", "CASH");
            } else {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                object.put("card_id", SharedHelper.getKey(context, "card_id"));
            }
            utils.print("SendRequestInput", "" + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        XuberServicesApplication.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.SEND_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    utils.print("SendRequestResponse", response.toString());
                    customDialog.dismiss();
                    if (response.optString("request_id", "").equals("")) {
                        utils.displayMessageNew(getView(), response.optString("message"), thisActivity);
                    } else {
                        SharedHelper.putKey(context, "current_status", "");
                        SharedHelper.putKey(context, "request_id", "" + response.optString("request_id"));
                        serviceFlow(FLOW_LOOKING_FOR_PROVIDERS);
                        scheduledDate = "";
                        scheduledTime = "";
                        scheduledTimeNew = "";
                        scheduledDateNew = "";
                        scheduledDateNewTxt = "";
                        scheduledTimeNewTxt = "";

                        date_choose = false;
                        time_choose = false;

                        if (date_choose == true && time_choose == true) {
                            lnrServiceDate.setVisibility(View.VISIBLE);
                            viewServiceDate.setVisibility(View.VISIBLE);
                        } else {
                            lnrServiceDate.setVisibility(View.GONE);
                            viewServiceDate.setVisibility(View.GONE);
                        }
                    }
                }
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
                                utils.showAlert(context, errorObj.optString("error"));
                            } catch (Exception e) {
                                utils.showAlert(context, context.getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            Toast.makeText(context,getString(R.string.session_timeout),Toast.LENGTH_SHORT).show();
                            SharedHelper.putKey(context, "current_status", "");
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            Intent mainIntent = new Intent(context, BeginScreen.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            activity.finish();
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.showAlert(context, json);
                            } else {
                                utils.showAlert(context, context.getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.showAlert(context, context.getString(R.string.server_down));
                        } else {
                            utils.showAlert(context, context.getString(R.string.please_try_again));
                        }
                    } catch (Exception e) {
                        utils.showAlert(context, context.getString(R.string.something_went_wrong));
                    }
                } else {
                    utils.showAlert(context, context.getString(R.string.please_try_again));
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

//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Cancelling Request
    public void cancelRequest() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("CancelRequestResponse", response.toString());
                Toast.makeText(context, "You have cancelled the request!", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
                SharedHelper.putKey(context, "request_id", "");
                PreviousStatus = "";
                serviceFlow(FLOW_REQUEST);
                getFragmentManager().popBackStackImmediate();
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
                                utils.displayMessage(getView(),context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(),context, getString(R.string.something_went_wrong));
                            }
                            serviceFlow(FLOW_REQUEST);
                        } else if (response.statusCode == 401) {
                            serviceFlow(FLOW_REQUEST);
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(),context, json);
                            } else {
                                utils.displayMessage(getView(),context, getString(R.string.please_try_again));
                            }
                            serviceFlow(FLOW_REQUEST);
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(),context, getString(R.string.server_down));
                            serviceFlow(FLOW_REQUEST);
                        } else {
                            utils.displayMessage(getView(),context, getString(R.string.please_try_again));
                            serviceFlow(FLOW_REQUEST);
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(),context, getString(R.string.something_went_wrong));
                        serviceFlow(FLOW_REQUEST);
                    }

                } else {
                    utils.displayMessage(getView(),context, getString(R.string.please_try_again));
                    serviceFlow(FLOW_REQUEST);
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

    // Rate Provider
    public void submitReviewCall() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("rating", feedBackRating);
            object.put("comment", "" + txtComments.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RATE_PROVIDER_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("SubmitRequestResponse", response.toString());
                utils.hideKeypad(context, getView());
                customDialog.dismiss();
                serviceSource.setText("");
                //mListener.moveToHomeCategoryFragment();
                GoToMainActivity();
                Toast.makeText(context, "Rated successfully!", Toast.LENGTH_SHORT).show();


                SharedHelper.putKey(context, "payment_mode", "CASH");
                SharedHelper.putKey(context, "before_image", "");
                SharedHelper.putKey(context, "after_image", "");
                SharedHelper.putKey(context, "before_comment", "");
                SharedHelper.putKey(context, "after_comment", "");


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
                                utils.displayMessage(getView(),context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(),context, getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            Toast.makeText(context,getString(R.string.session_timeout),Toast.LENGTH_SHORT).show();
                            SharedHelper.putKey(context, "current_status", "");
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            Intent mainIntent = new Intent(context, BeginScreen.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            activity.finish();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(),context, json);
                            } else {
                                utils.displayMessage(getView(),context, getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(),context, getString(R.string.server_down));
                        } else {
                            utils.displayMessage(getView(), context,getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(),context, getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(),context, getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                        + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Get Card details
    private void getCards() {
        try {
            if (context != null) {
                Ion.with(context)
                        .load(URLHelper.CARD_PAYMENT_LIST)
                        .addHeader("X-Requested-With", "XMLHttpRequest")
                        .addHeader("Authorization", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"))
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> response) {
                                // response contains both the headers and the string result
                                try {
                                    if (response.getHeaders().code() == 200) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(response.getResult());
                                            if (jsonArray.length() > 0) {
                                                CardInfo cardInfo = new CardInfo();
                                                cardInfo.setCardId("CASH");
                                                cardInfo.setCardType("CASH");
                                                cardInfo.setLastFour("CASH");
                                                cardInfoArrayList.add(cardInfo);
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    JSONObject cardObj = jsonArray.getJSONObject(i);
                                                    cardInfo = new CardInfo();
                                                    cardInfo.setCardId(cardObj.optString("card_id"));
                                                    cardInfo.setCardType(cardObj.optString("brand"));
                                                    cardInfo.setLastFour(cardObj.optString("last_four"));
                                                    cardInfoArrayList.add(cardInfo);
                                                }
                                            }

                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                    CardInfo cardInfo = new CardInfo();
                                    cardInfo.setCardId("CASH");
                                    cardInfo.setCardType("CASH");
                                    cardInfo.setLastFour("CASH");
                                    cardInfoArrayList.add(cardInfo);
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // redirect to AddCard
    private void gotoAddCard() {
        Intent mainIntent = new Intent(context, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    // method for choosing cash or card
    private void showChooser() {

        String[] cardsList = new String[cardInfoArrayList.size()];

        for (int i = 0; i < cardInfoArrayList.size(); i++) {
            if (cardInfoArrayList.get(i).getLastFour().equals("CASH")) {
                cardsList[i] = "CASH";
            } else {
                cardsList[i] = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(i).getLastFour();
            }
        }

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle(getString(R.string.choose_payment));
        builderSingle.setSingleChoiceItems(cardsList, selectedPosition, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.custom_tv);
        for (int j = 0; j < cardInfoArrayList.size(); j++) {
            String card;
            if (cardInfoArrayList.get(j).getLastFour().equals("CASH")) {
                card = "CASH";
            } else {
                card = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(j).getLastFour();
            }
            arrayAdapter.add(card);
        }
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                utils.print("Items clicked===>", "" + selectedPosition);
                getCardDetailsForPayment(cardInfoArrayList.get(selectedPosition));
                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
//        builderSingle.setAdapter(
//                arrayAdapter,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        getCardDetailsForPayment(cardInfoArrayList.get(which));
//                        dialog.dismiss();
//                    }
//                });
        final AlertDialog dialog = builderSingle.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        });
        dialog.show();
    }

    // details of the card or cash at invoice
    private void getCardDetailsForPayment(CardInfo cardInfo) {

        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            imgPaymentType.setImageResource(R.drawable.money);
            lblPaymentType.setText("CASH");
        } else {
            SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
            SharedHelper.putKey(context, "payment_mode", "CARD");
            imgPaymentType.setImageResource(R.drawable.visa);
            lblPaymentType.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
        }
    }

    // pay API if the user is selected card payment mode
    public void payNow() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("payment_mode", paymentMode);
            object.put("is_paid", isPaid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_NOW_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("PayNowRequestResponse", response.toString());
                customDialog.dismiss();
                serviceFlow(FLOW_RATING);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = "";
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.displayMessage(getView(), context, errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), context, getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
//                            refreshAccessToken("SEND_REQUEST");
                            Toast.makeText(context,getString(R.string.session_timeout),Toast.LENGTH_SHORT).show();
                            SharedHelper.putKey(context, "current_status", "");
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            Intent mainIntent = new Intent(context, BeginScreen.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            activity.finish();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(),context, json);
                            } else {
                                utils.displayMessage(getView(),context, getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(),context, getString(R.string.server_down));
                        } else {
                            utils.displayMessage(getView(),context, getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        utils.displayMessage(getView(),context, getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(),context, getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    // dialog for network connection
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        if (alert == null) {
            alert = builder.create();
            //alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
            });
            alert.show();
        }
    }


    // set up settings for map
    @SuppressWarnings("MissingPermission")
    private void setupMap() {
        if (mMap != null) {
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context instanceof ServiceFlowFragment.ServiceFlowFgmtListener) {
            mListener = (ServiceFlowFragment.ServiceFlowFgmtListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ServiceFlowFgmtListener");
        }

        if(!SharedHelper.getKey(context, "wallet_balance").equalsIgnoreCase("")){
            double wallet_balance = Double.parseDouble(SharedHelper.getKey(context, "wallet_balance"));
            if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                if (viewWallet != null && lnrWallet != null && lblwalletAmt != null) {
                    viewWallet.setVisibility(View.VISIBLE);
                    lnrWallet.setVisibility(View.VISIBLE);
                    lblwalletAmt.setText(SharedHelper.getKey(context, "currency") + "" + wallet_balance);
                }
            } else {
                if (viewWallet != null && lnrWallet != null) {
                    lnrWallet.setVisibility(View.GONE);
                    viewWallet.setVisibility(View.GONE);
                }
            }
        }

        /* customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_SOURCE) {
            if (resultCode == Activity.RESULT_OK) {
                PlacePredictions placePredictions;
                placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
                if (placePredictions != null) {
                    if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                        source_lat = "" + placePredictions.strSourceLatitude;
                        source_lng = "" + placePredictions.strSourceLongitude;
                        source_address = placePredictions.strSourceAddress;

                        serviceSource.setText(source_address);

                        mMap.clear();

                        LatLng location = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));

                        current_marker = mMap.addMarker(markerOptions);

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        getProvidersList();
                    }

                }
            }
        }

        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards();
                }
            }
        }

        if (requestCode == REQUEST_LOCATION) {

        }

        if (requestCode == ENABLE_LOCATION) {
            if (resultCode == Activity.RESULT_CANCELED) {
                enableLoc();
            }
        }

    }


    public interface ServiceFlowFgmtListener {

        public void moveToHomeCategoryFragment();

        public void onServiceFlowLogout();

        public void handleDrawer();

    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(thisActivity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
//                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            case MY_PERMISSIONS_ACCESS_FINE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                        initMap();
                        MapsInitializer.initialize(getActivity());
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE);
                    }
                }
                break;
            }

            case MY_PERMISSION_PHONE_CALL: {
                if (requestCode == MY_PERMISSION_PHONE_CALL) {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if(SharedHelper.getKey(context, "provider_mobile_no") != null && SharedHelper.getKey(context, "provider_mobile_no") != ""){
                            Intent intentCall = new Intent(Intent.ACTION_CALL);
                            intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                            startActivity(intentCall);
                        }else {
                            Toast.makeText(context,""+getString(R.string.no_mobile),Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
            break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void startAnim(ArrayList<LatLng> routeList) {
        if (mMap != null && routeList.size() > 0) {
            MapAnimator.getInstance().animateRoute(mMap, routeList);
        } else {
            Toast.makeText(context, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }


    private void zoomImageFromThumb(final View thumbView, String strImageURL) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        lnrZoomPhoto.setVisibility(View.VISIBLE);
        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) view.findViewById(R.id.imgZoomService);

        if (!strImageURL.equalsIgnoreCase("")) {
            Picasso.with(context).load(Utilities.getImageURL(strImageURL)).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(expandedImageView);
        }

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        view.findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        view.findViewById(R.id.container).setBackgroundColor(ContextCompat.getColor(context, R.color.black));
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);


        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;


        // Upon clicking the zoomed-in image, it should zoom back_letter down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;

        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back_letter to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        lnrZoomPhoto.setVisibility(View.GONE);
                        thumbView.setAlpha(1f);
                        expandedImageView.setImageResource(R.drawable.placeholder);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        lnrZoomPhoto.setVisibility(View.GONE);
                        thumbView.setAlpha(1f);
//						expandedImageView.setImageResource(android.R.color.transparent);
                        expandedImageView.setImageResource(R.drawable.placeholder);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });

    }

    public static Long getFormatedDateTime(String dateStr, String strReadFormat, String strWriteFormat) {

        long diff = 0;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null, date2 = null;
        try {
            date1 = df.parse(dateStr);

            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat cdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = cdf.format(c.getTime());

            date2 = df.parse(currentDate);

            diff = date2.getTime() - date1.getTime();


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    private void setRoutePath() {
        userLatLng = new LatLng(userLatitude, userLongitude);
        provLatLng = new LatLng(provLatitude, provLongitude);
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(provLatLng).zoom(16).build();
//        MarkerOptions options = new MarkerOptions();
//        options.position(provLatLng).isDraggable();
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        String url = getDirectionsUrl(userLatLng, provLatLng);
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private void setUserProvider() {
        userLatLng = new LatLng(userLatitude, userLongitude);
        provLatLng = new LatLng(provLatitude, provLongitude);
        MarkerOptions markerOptions = new MarkerOptions().title("Source")
                .position(userLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
        mMap.addMarker(markerOptions);
        MarkerOptions markerOptions1 = new MarkerOptions().title("Destination")
                .position(provLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));

        if (userMarker != null) {
            userMarker.remove();
        }
        if (providerMarker != null) {
            providerMarker.remove();
        }
        userMarker = mMap.addMarker(markerOptions);
        providerMarker = mMap.addMarker(markerOptions1);
    }

    private String getDirectionsUrl(LatLng userLatLng, LatLng provLatLng) {

        // Origin of routelng;
        String str_origin = "origin=" + userLatitude + "," + userLongitude;
        String str_dest = "destination=" + provLatitude + "," + provLongitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("url", url.toString());
        return url;

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                Log.e("Entering dowload url", "entrng");
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {

            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.e("Entering dwnload task", "download task");
            } catch (Exception e) {
                utils.print("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Resultmap", result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                utils.print("routes", routes.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if (result != null) {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                        utils.print("abcde", points.toString());
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLACK);
                    mMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions().title("Source")
                            .position(userLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
                    mMap.addMarker(markerOptions);
                    MarkerOptions markerOptions1 = new MarkerOptions().title("Destination")
                            .position(provLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));

                    if (userMarker != null) {
                        userMarker.remove();
                    }
                    if (providerMarker != null) {
                        providerMarker.remove();
                    }
                    userMarker = mMap.addMarker(markerOptions);
                    providerMarker = mMap.addMarker(markerOptions1);
//                    Display display =activity.getWindowManager().getDefaultDisplay();
//                    Point size = new Point();
//                    display.getSize(size);
//                    int width = size.x;
//                    int height = size.y;
//
//                    mMap.setPadding(0, 0, 0, height / 2);

//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                    LatLngBounds bounds;
//                    builder.include(userLatLng);
//                    builder.include(provLatLng);
//                    bounds = builder.build();
//                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 20);
//                    mMap.moveCamera(cu);
                    mMap.getUiSettings().setMapToolbarEnabled(false);
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(bounds.getCenter()).zoom(10).build();
//                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions == null) {
                Toast.makeText(context, "There is no route", Toast.LENGTH_SHORT).show();

            } else {
                mMap.addPolyline(lineOptions);
            }
        }
    }

    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 15);
                }
            }
        });
    }


    public void GoToMainActivity() {
        Intent mainIntent = new Intent(thisActivity, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        thisActivity.finish();
    }

}
