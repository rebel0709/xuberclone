package com.xuber_for_services.app.Activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.app.Constants.URLHelper;
import com.xuber_for_services.app.Helper.AppHelper;
import com.xuber_for_services.app.Helper.ConnectionHelper;
import com.xuber_for_services.app.Helper.CustomDialog;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.Helper.VolleyMultipartRequest;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.Utils.Utilities;
import com.xuber_for_services.app.XuberServicesApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    public Context context = EditProfile.this;
    public Activity activity = EditProfile.this;
    String TAG = "EditActivity";
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    ImageView backArrow;
    TextView changePasswordTxt, lblSave;
    EditText email, first_name, last_name, mobile_no;
    View first_name_view, last_name_view, mobile_no_view;
    ImageView profile_Image;
    Boolean isImageChanged = false;
    Activity thisActivity;
    ChangePasswodPopup pwdpopup;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        thisActivity = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_edit_profile_new);
        findViewByIdandInitialization();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToMainActivity();
            }
        });

        lblSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lblSave.getText().toString().equalsIgnoreCase("Edit")) {
                    lblSave.setText("Save");
                    first_name_view.setVisibility(View.VISIBLE);
                    last_name_view.setVisibility(View.VISIBLE);
                    mobile_no_view.setVisibility(View.VISIBLE);
                    first_name.setEnabled(true);
                    last_name.setEnabled(true);
                    mobile_no.setEnabled(true);

                } else {
                    lblSave.setText("Edit");
                    first_name_view.setVisibility(View.GONE);
                    last_name_view.setVisibility(View.GONE);
                    mobile_no_view.setVisibility(View.GONE);
                    first_name.setEnabled(false);
                    last_name.setEnabled(false);
                    mobile_no.setEnabled(false);

                    Pattern ps = Pattern.compile(".*[0-9].*");
                    Matcher firstName = ps.matcher(first_name.getText().toString());
                    Matcher lastName = ps.matcher(last_name.getText().toString());


                    if (email.getText().toString().equals("") || email.getText().toString().length() == 0) {
                        displayMessage(getString(R.string.email_validation));
                    } else if (mobile_no.getText().toString().equals("") || mobile_no.getText().toString().length() == 0) {
                            displayMessage(getString(R.string.mobile_number_empty));
                    } else if (first_name.getText().toString().equals("") || first_name.getText().toString().length() == 0) {
                        displayMessage(getString(R.string.first_name_empty));
                    } else if (last_name.getText().toString().equals("") || last_name.getText().toString().length() == 0) {
                        displayMessage(getString(R.string.last_name_empty));
                    } else if (firstName.matches()) {
                        displayMessage(getString(R.string.first_name_no_number));
                    } else if (lastName.matches()) {
                        displayMessage(getString(R.string.last_name_no_number));
                    }else if(mobile_no.getText().toString().length() < 10 || mobile_no.getText().toString().length() > 20){
                        displayMessage(getString(R.string.mobile_no_length));
                    }else {
                        if (isInternet) {
                            updateProfile();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }
                    }


                }


            }
        });


        changePasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*SharedHelper.putKey(context, "otp", "");
                startActivity(new Intent(activity, ChangePassword.class));*/

                pwdpopup = new ChangePasswodPopup(context);
                pwdpopup.show();

            }
        });


        profile_Image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                if (lblSave.getText().toString().equalsIgnoreCase("Edit")) {

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(checkStoragePermission()) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                        }else{
                            goToImageIntent();
                        }
                    }else{
                        goToImageIntent();
                    }
                }
            }
        });


    }


    public void goToImageIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
            for (int grantResult : grantResults)
                if (grantResult == PackageManager.PERMISSION_GRANTED)
                    goToImageIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                //Uri uri = data.getData();
                //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap bitmap = null;
                if(data.getData()==null){
                    bitmap = (Bitmap)data.getExtras().get("data");
                }else{
                    bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());
                }
                profile_Image.setImageBitmap(bitmap);
                isImageChanged = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void updateProfile() {
        if (isImageChanged) {
            updateProfileWithImage();
        } else {
            updateProfileWithoutImage();
        }
    }

    public void updateProfileWithImage(){
        isImageChanged = false;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.USER_PROFILE_UPDATE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                    SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        SharedHelper.putKey(context, "picture", Utilities.getImageURL(jsonObject.optString("picture")));
                    }

                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    displayMessage(getString(R.string.update_success));

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(getString(R.string.something_went_wrong));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                Log.e(TAG, "" + error);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                    updateProfileWithoutImage();
                    }else{
                    displayMessage(getString(R.string.something_went_wrong));
                    }
                }else{
                    displayMessage(getString(R.string.something_went_wrong));
                }

            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                params.put("picture", new DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(profile_Image.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        XuberServicesApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
    }

    public void updateProfileWithoutImage(){
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.USER_PROFILE_UPDATE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                customDialog.dismiss();

                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                    SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                    if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                        SharedHelper.putKey(context, "picture", "");
                    } else {
                        SharedHelper.putKey(context, "picture", Utilities.getImageURL(jsonObject.optString("picture")));
                    }

                    SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));

                    displayMessage(getString(R.string.update_success));

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(getString(R.string.something_went_wrong));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                Log.e(TAG, "" + error);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        updateProfileWithoutImage();
                    }else{
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                }else{
                    displayMessage(getString(R.string.something_went_wrong));
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", last_name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                params.put("picture", "");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        XuberServicesApplication.getInstance().addToRequestQueue(volleyMultipartRequest);
    }


    public void findViewByIdandInitialization(){
        email = (EditText) findViewById(R.id.email);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile_no = (EditText) findViewById(R.id.mobile_no);

        first_name_view = (View) findViewById(R.id.first_name_view);
        last_name_view = (View) findViewById(R.id.last_name_view);
        mobile_no_view = (View) findViewById(R.id.mobile_no_view);

        lblSave = (TextView) findViewById(R.id.lblSave);
        changePasswordTxt = (TextView) findViewById(R.id.changePasswordTxt);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        profile_Image = (ImageView) findViewById(R.id.img_profile);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        //Assign current profile values to the edittext
        //Glide.with(activity).load(SharedHelper.getKey(context, "picture")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(profile_Image);

        System.out.println("Prfile url" + SharedHelper.getKey(context, "picture"));
        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")) {
            Picasso.with(context).load(SharedHelper.getKey(context, "picture")).networkPolicy(NetworkPolicy.NO_CACHE).
                    memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(profile_Image);
        }
        email.setText(SharedHelper.getKey(context, "email"));
        first_name.setText(SharedHelper.getKey(context, "first_name"));
        last_name.setText(SharedHelper.getKey(context, "last_name"));
        if (SharedHelper.getKey(context, "mobile") != null
                && !SharedHelper.getKey(context, "mobile").equals("null")) {
            mobile_no.setText(SharedHelper.getKey(context, "mobile"));
        }
    }


    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        /*Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();*/
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(this.getResources().getColor(R.color.black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(this.getResources().getColor(R.color.white));
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        GoToMainActivity();
    }


    class ChangePasswodPopup extends Dialog {

        EditText current_password, new_password, confirm_password;
        Button changePasswordBtn, btnCancel;

        public ChangePasswodPopup(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setCancelable(false);
            setContentView(R.layout.activity_change_password_new);

            current_password = (EditText) findViewById(R.id.current_password);
            new_password = (EditText) findViewById(R.id.new_password);
            confirm_password = (EditText) findViewById(R.id.confirm_password);
            changePasswordBtn = (Button) findViewById(R.id.changePasswordBtn);
            btnCancel = (Button) findViewById(R.id.btnCancel);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            changePasswordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String current_password_value = current_password.getText().toString();
                    String new_password_value = new_password.getText().toString();
                    String confirm_password_value = confirm_password.getText().toString();

                    if (changePasswordBtn.getText().toString().equalsIgnoreCase("Change Password")) {
                        if (current_password_value == null || current_password_value.equalsIgnoreCase("")) {
                            displayMessage(getString(R.string.please_enter_current_pass));
                        } else if (new_password_value == null || new_password_value.equalsIgnoreCase("")) {
                            displayMessage(getString(R.string.please_enter_new_pass));
                        } else if (confirm_password_value == null || confirm_password_value.equalsIgnoreCase("")) {
                            displayMessage(getString(R.string.please_enter_confirm_pass));
                        } else if (!new_password_value.equals(confirm_password_value)) {
                            displayMessage(getString(R.string.different_passwords));
                        } else {
                            changePassword(current_password_value, new_password_value, confirm_password_value);
                        }
                    }

                }
            });


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
            Log.e("ChangePasswordAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHANGE_PASSWORD_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
                displayMessage(response.optString("message"));
                if (pwdpopup != null)
                    pwdpopup.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log.e("MyTest", "" + error);
                Log.e("MyTestError", "" + error.networkResponse);
                Log.e("MyTestError1", "" + response.statusCode);
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI", "" + errorObj.toString());

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("error"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {
                            json = XuberServicesApplication.trimMessage(new String(response.data));
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
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }


}
