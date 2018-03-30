package com.xuber_for_services.app.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.splunk.mint.Mint;
import com.xuber_for_services.app.Constants.URLHelper;
import com.xuber_for_services.app.Helper.ConnectionHelper;
import com.xuber_for_services.app.Helper.CustomDialog;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.Utils.Utilities;
import com.xuber_for_services.app.XuberServicesApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xuber_for_services.app.XuberServicesApplication.trimMessage;

/**
 * Created by jayakumar on 22/06/17.
 */

public class SignIn extends AppCompatActivity {

    EditText txtemail,txtpassword;
    TextView lblforgotpassword;
    Button btnSignIn;
    Activity thisActivity;
    Boolean isInternet;
    ConnectionHelper helper;
    CustomDialog customDialog;
    LinearLayout lnrRegister;
    String TAG = "SignIn";

    String device_token, device_UDID;
    Utilities utils =new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(this.getApplication(), "06fb8b21");

        setContentView(R.layout.activity_begin_signin);


        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        helper = new ConnectionHelper(thisActivity);
        isInternet = helper.isConnectingToInternet();

        txtemail = (EditText)findViewById(R.id.txtemail);
        lnrRegister = (LinearLayout) findViewById(R.id.lnrRegister);
        lblforgotpassword = (TextView) findViewById(R.id.lblforgotpassword);
        txtpassword = (EditText)findViewById(R.id.txtpassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        GetToken();

        lblforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedHelper.putKey(thisActivity, "password", "");
                Intent mainIntent = new Intent(thisActivity, ForgetPassword.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        lnrRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(getApplicationContext(), "from", "email");
                SharedHelper.putKey(getApplicationContext(),"email", ""+txtemail.getText().toString());
                Intent mainIntent = new Intent(getApplicationContext(), Register.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtemail.getText().toString().equals("") || txtemail.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))){

                    displayMessage(getString(R.string.email_validation));

                }
                else if((!isValidEmail(txtemail.getText().toString()))){

                    displayMessage(getString(R.string.not_valid_email));

                }
                else if(txtpassword.getText().toString().equals("") || txtpassword.getText().toString().equalsIgnoreCase (getString(R.string.password_txt))){
                    displayMessage(getString(R.string.password_validation));
                } else if (txtpassword.getText().toString().length() < 6) {
                    displayMessage(getString(R.string.passwd_length));
                }
                else{

                        SharedHelper.putKey(thisActivity,"email",txtemail.getText().toString());
                        SharedHelper.putKey(thisActivity,"password",txtpassword.getText().toString());
                        signIn();

                }
            }
        });


    }

    private void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(thisActivity);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "password");
                object.put("client_id", URLHelper.CLIENT_ID);
                object.put("client_secret", URLHelper.CLIENT_SECRET_KEY);
                object.put("username", SharedHelper.getKey(thisActivity, "email"));
                object.put("password", SharedHelper.getKey(thisActivity, "password"));
                object.put("scope", "");
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                utils.print("InputToLoginAPI", "" + object);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(thisActivity, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(thisActivity, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(thisActivity, "token_type", response.optString("token_type"));
                    getProfile();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);

                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            }else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }


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

        }else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }



    public void getProfile() {

        if (isInternet) {

            customDialog = new CustomDialog(thisActivity);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.GET_USER_PROFILE, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    utils.print("GetProfile", response.toString());
                    SharedHelper.putKey(thisActivity, "id", response.optString("id"));
                    SharedHelper.putKey(thisActivity, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(thisActivity, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(thisActivity, "email", response.optString("email"));
                    SharedHelper.putKey(thisActivity, "picture", Utilities.getImageURL(response.optString("picture")));
                    SharedHelper.putKey(thisActivity, "gender", response.optString("gender"));
                    SharedHelper.putKey(thisActivity, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(thisActivity, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(thisActivity, "payment_mode", response.optString("payment_mode"));
                    SharedHelper.putKey(thisActivity, "currency",response.optString("currency"));
                    SharedHelper.putKey(thisActivity, "loggedIn", getString(R.string.True));
                    GoToMainActivity();

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
                                refreshAccessToken();
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            }else if(response.statusCode == 503){
                                displayMessage(getString(R.string.server_down));
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(thisActivity, "token_type") + " "
                            + SharedHelper.getKey(thisActivity, "access_token"));
                    utils.print("authoization",""+SharedHelper.getKey(thisActivity, "token_type") + " "
                            + SharedHelper.getKey(thisActivity, "access_token"));
                    return headers;
                }
            };

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        }else{
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void GoToMainActivity(){
        Intent mainIntent = new Intent(thisActivity, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        thisActivity.finish();
    }

    private void refreshAccessToken() {
        if (isInternet) {
            customDialog = new CustomDialog(thisActivity);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "refresh_token");
                object.put("client_id", URLHelper.CLIENT_ID);
                object.put("client_secret", URLHelper.CLIENT_SECRET_KEY);
                object.put("refresh_token", SharedHelper.getKey(thisActivity, "refresh_token"));
                object.put("scope", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(thisActivity, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(thisActivity, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(thisActivity, "token_type", response.optString("token_type"));
                    getProfile();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);

                    if (response != null && response.data != null) {
                        SharedHelper.putKey(thisActivity,"loggedIn",getString(R.string.False));
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

        }else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void GoToBeginActivity(){
        Intent mainIntent = new Intent(thisActivity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        thisActivity.finish();
    }


    public void displayMessage(String toastString) {
        try {
            if (getCurrentFocus() != null) {
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(thisActivity, "device_token").equals("") && SharedHelper.getKey(thisActivity, "device_token") != null) {
                device_token = SharedHelper.getKey(thisActivity, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }


}