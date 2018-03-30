package com.xuber_for_services.app.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.xuber_for_services.app.Activity.BeginScreen;
import com.xuber_for_services.app.Adapter.ServiceListAdapter;
import com.xuber_for_services.app.Constants.URLHelper;
import com.xuber_for_services.app.Helper.ConnectionHelper;
import com.xuber_for_services.app.Helper.CustomDialog;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.Models.ServiceListModel;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.Utils.ClanProTextView;
import com.xuber_for_services.app.Utils.Utilities;
import com.xuber_for_services.app.View.TBarView;
import com.xuber_for_services.app.XuberServicesApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class HomeCategoryFragment extends Fragment {

    public static final String TAG = "HomeCategoryFragment";
    Context context;
    Activity activity;
    //UI Elements
    private RecyclerView recyclerView;
    SpotsDialog dialog;
    private RelativeLayout errorLayout;
    private ImageView reload_img;
    private ClanProTextView status_txt;
    ArrayList<ServiceListModel> lstServiceModel = new ArrayList<ServiceListModel>() ;
    ServiceListModel serviceListModel;
    ServiceListAdapter serviceListAdapter;
    Utilities utils = new Utilities();
    CustomDialog customDialog;
    private HomeCategoryFgmtListener mListener;
    Handler handleCheckStatus;

    //Internet
    ConnectionHelper helper;
    Boolean isFromError = false;
    int retryCount = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
        dialog = new SpotsDialog(context, R.style.Custom);
    }

    public static HomeCategoryFragment newInstance() {
        HomeCategoryFragment fragment = new HomeCategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findViewById(view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TBarView tBarView = new TBarView(this, toolbar);
        tBarView.setupToolbar(R.drawable.ic_nav_menu, getString(R.string.menu_home), false, false);
        helper = new ConnectionHelper(context);
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        checkStatus();
        return view;
    }

    private void findViewById(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) view.findViewById(R.id.error_layout);
        reload_img = (ImageView) view.findViewById(R.id.reload_img);
        status_txt = (ClanProTextView) view.findViewById(R.id.statusTxt);
        recyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        reload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getServiceList();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeCategoryFgmtListener) {
            mListener = (HomeCategoryFgmtListener) context;
        } else {
            throw new RuntimeException(context.toString()+ " must implement SubCategoryFgmtListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    public interface HomeCategoryFgmtListener {
        void moveToServiceFlowFragment();
    }

    public void getServiceList() {

        if(!customDialog.isShowing()){
            customDialog.show();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_SERVICE_LIST_API,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        utils.print("GetServices", response.toString());
                        customDialog.dismiss();
                        if (response.length() > 0) {
                        lstServiceModel = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            serviceListModel = new ServiceListModel();
                            serviceListModel.setHourlyFare("" + response.optJSONObject(i).optString("fixed"));
                            serviceListModel.setServiceType("" + response.optJSONObject(i).optString("name"));
                            lstServiceModel.add(serviceListModel);
                        }
                            ServiceListAdapter serviceListAdapter = new ServiceListAdapter(mListener, response, lstServiceModel);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(serviceListAdapter);
                            errorLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            retryforGetService();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                isFromError = true;
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 422 || response.statusCode == 503) {
                          retryforGetService();
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SERVICE_LIST");
                        } else {
                           retryforGetService();
                        }

                    } catch (Exception e) {
                        retryforGetService();
                    }

                } else {
                    retryforGetService();
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
                if (tag.equalsIgnoreCase("SERVICE_LIST")) {
                    getServiceList();
                }else if (tag.equalsIgnoreCase("CHECK_STATUS")) {
                    checkStatus();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
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


    private void checkStatus() {
        try {
            utils.print("Handler", "Inside");
            if (helper.isConnectingToInternet()) {
                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        URLHelper.REQUEST_STATUS_CHECK_API, new JSONObject(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        utils.print("Response", "" + response.toString());

                        if (response.optJSONArray("data") != null && response.optJSONArray("data").length() > 0) {
                            customDialog.dismiss();
                            if(mListener != null){
                            mListener.moveToServiceFlowFragment();
                            }else{
                             retryforCheckStatus();
                            }
                        } else{
                            getServiceList();
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
                                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 422 || response.statusCode == 503) {
                                    retryforCheckStatus();
                                } else if (response.statusCode == 401) {
                                    refreshAccessToken("CHECK_STATUS");
                                } else {
                                    retryforCheckStatus();
                                }

                            } catch (Exception e) {
                                retryforCheckStatus();
                            }

                        } else {
                            retryforCheckStatus();
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
                utils.displayMessage(getView(), context, getString(R.string.oops_connect_your_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void retryforGetService(){
      if(isFromError){
          reload_img.setImageResource(R.drawable.ic_reload);
          status_txt.setText(R.string.retry);
          recyclerView.setVisibility(View.GONE);
          errorLayout.setVisibility(View.VISIBLE);

      }else{
          reload_img.setImageResource(R.drawable.ic_reload);
          status_txt.setText(R.string.no_service_retry);
          recyclerView.setVisibility(View.GONE);
          errorLayout.setVisibility(View.VISIBLE);
      }
    }

    public void retryforCheckStatus(){
        if(retryCount != 0){
            retryCount = retryCount -1;
            checkStatus();
        }else {
            getServiceList();
        }
    }


    public void GoToBeginActivity(){
        Toast.makeText(getContext(),getString(R.string.session_timeout),Toast.LENGTH_SHORT).show();
        SharedHelper.putKey(context,"loggedIn",getString(R.string.False));
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

}
