package com.xuber_for_services.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.vision.text.Line;
import com.xuber_for_services.app.Helper.ConnectionHelper;
import com.xuber_for_services.app.Helper.CustomDialog;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.Constants.URLHelper;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.XuberServicesApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    String TAG = "ChangePasswordActivity";
    public Context context = ChangePassword.this;
    public Activity activity = ChangePassword.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Button changePasswordBtn;
    ImageView backArrow;
    TextView lblTitle;
    EditText current_password, new_password, confirm_new_password;
    LinearLayout lnrCurrentPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_change_password);
        findViewByIdandInitialization();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SharedHelper.getKey(context, "otp").equalsIgnoreCase("")) {
//                    GoToBeginActivity();
                    onBackPressed();
                }else{
                    onBackPressed();
                }
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current_password_value = current_password.getText().toString();
                String new_password_value = new_password.getText().toString();
                String confirm_password_value = confirm_new_password.getText().toString();

                if (changePasswordBtn.getText().toString().equalsIgnoreCase("Change Password")){
                    if(current_password_value == null || current_password_value.equalsIgnoreCase("")){
                        displayMessage(getString(R.string.please_enter_current_pass));
                    }else if(new_password_value == null || new_password_value.equalsIgnoreCase("")){
                        displayMessage(getString(R.string.please_enter_new_pass));
                    }else if(confirm_password_value == null || confirm_password_value.equalsIgnoreCase("")){
                        displayMessage(getString(R.string.please_enter_confirm_pass));
                    }else if(!new_password_value.equals(confirm_password_value)){
                        displayMessage(getString(R.string.different_passwords));
                    }else{
                        changePassword(current_password_value, new_password_value, confirm_password_value);
                    }
                }else{
                    if(new_password_value == null || new_password_value.equalsIgnoreCase("")){
                        displayMessage(getString(R.string.please_enter_new_pass));
                    }else if(confirm_password_value == null || confirm_password_value.equalsIgnoreCase("")){
                        displayMessage(getString(R.string.please_enter_confirm_pass));
                    }else if(!new_password_value.equals(confirm_password_value)){
                        displayMessage(getString(R.string.different_passwords));
                    }else{
                        resetPassword(new_password_value, confirm_password_value);
                    }
                }
            }
        });

    }

    public void findViewByIdandInitialization(){
        current_password = (EditText)findViewById(R.id.current_password);
        new_password = (EditText)findViewById(R.id.new_password);
        confirm_new_password = (EditText) findViewById(R.id.confirm_password);
        changePasswordBtn = (Button) findViewById(R.id.changePasswordBtn);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lnrCurrentPassword = (LinearLayout) findViewById(R.id.lnrCurrentPassword);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        if (!SharedHelper.getKey(context, "otp").equalsIgnoreCase("")){
            lblTitle.setText("Reset Password");
            lnrCurrentPassword.setVisibility(View.GONE);
            changePasswordBtn.setText("Reset Password");
        }else{
            lblTitle.setText("Change Password");
            lnrCurrentPassword.setVisibility(View.VISIBLE);
            changePasswordBtn.setText("Change Password");
        }
    }



    private void changePassword(String current_pass, String new_pass, String confirm_new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("password", new_pass);
            object.put("password_confirmation", confirm_new_pass);
            object.put("old_password", current_pass);
            Log.e("ChangePasswordAPI",""+object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHANGE_PASSWORD_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
                displayMessage(response.optString("message"));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log.e("MyTest",""+error);
                Log.e("MyTestError",""+error.networkResponse);
                Log.e("MyTestError1",""+response.statusCode);
                if(response != null && response.data != null){
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI",""+errorObj.toString());

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.optString("error"));
                            }catch (Exception e){
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        }else if(response.statusCode == 401){
                                GoToBeginActivity();
                        }else if(response.statusCode == 422){
                            json = XuberServicesApplication.trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(getString(R.string.please_try_again));
                            }
                        }else{
                            displayMessage(getString(R.string.please_try_again));
                        }

                    }catch (Exception e){
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void resetPassword(String new_pass, String confirm_new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("password", new_pass);
            object.put("password_confirmation", confirm_new_pass);
            object.put("id", SharedHelper.getKey(context, "reset_id"));
            Log.e("ChangePasswordAPI",""+object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RESET_PASSWORD, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
                displayMessage(response.optString("message"));
                GoToBeginActivity();
                SharedHelper.putKey(context, "otp", "");
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI",""+errorObj.toString());

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.optString("error"));
                            }catch (Exception e){
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        }else if(response.statusCode == 401){
                            GoToBeginActivity();
                        }else if(response.statusCode == 422){
                            json = XuberServicesApplication.trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(getString(R.string.please_try_again));
                            }
                        }else{
                            displayMessage(getString(R.string.please_try_again));
                        }

                    }catch (Exception e){
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }



    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(ChangePassword.this, OTPActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        ChangePassword.this.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public void GoToBeginActivity(){
        SharedHelper.putKey(activity,"loggedIn",getString(R.string.False));
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public void displayMessage(String toastString){
        Log.e("displayMessage",""+toastString);
        /*Snackbar.make(getCurrentFocus(),toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
*/
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(this.getResources().getColor(R.color.user_name));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(this.getResources().getColor(R.color.white));
        snackbar.show();
    }
}
