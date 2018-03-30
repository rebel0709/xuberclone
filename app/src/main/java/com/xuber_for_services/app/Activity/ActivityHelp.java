package com.xuber_for_services.app.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.xuber_for_services.app.Constants.URLHelper;
import com.xuber_for_services.app.Helper.CustomDialog;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.XuberServicesApplication;

import org.json.JSONObject;

import java.util.HashMap;

import static com.xuber_for_services.app.XuberServicesApplication.trimMessage;


public class ActivityHelp extends AppCompatActivity implements View.OnClickListener {

    ImageView imgEmail;
    ImageView imgPhone;
    ImageView imgWeb;
    TextView titleTxt,lblContacttextt;

    String phone;
    String email;
    String contact_text="";
    Activity activity;
    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_help);
        findviewById();
        setOnClickListener();

        getHelp();
    }

    private void findviewById() {
        imgEmail = (ImageView) findViewById(R.id.img_mail);
        imgPhone = (ImageView) findViewById(R.id.img_phone);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        imgWeb = (ImageView) findViewById(R.id.img_web);
        titleTxt = (TextView) findViewById(R.id.title_txt);
        lblContacttextt = (TextView) findViewById(R.id.lblContacttext);
//        titleTxt.setText(getString(R.string.app_name) +" "+ getString(R.string.help));
    }

    private void setOnClickListener() {
        imgEmail.setOnClickListener(this);
        imgPhone.setOnClickListener(this);
        imgWeb.setOnClickListener(this);
        backArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgEmail) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name)+"-"+getString(R.string.help));
            intent.putExtra(Intent.EXTRA_TEXT, "Hello team");
            startActivity(Intent.createChooser(intent, "Send Email"));

        }
        if (v == imgPhone) {
            if(phone!=null) {
                if (!phone.equalsIgnoreCase("null") && !phone.equalsIgnoreCase("")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        Intent intentCall = new Intent(Intent.ACTION_CALL);
                        intentCall.setData(Uri.parse("tel:" + phone));
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(intentCall);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.app_name))
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(getString(R.string.sorry_for_inconvinent))
                            .setCancelable(true)
                            .setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                    final AlertDialog alert1 = builder.create();
                    //alert1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    alert1.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg) {
                            alert1.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                            alert1.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                        }
                    });
                    alert1.show();
                }
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(getString(R.string.sorry_for_inconvinent))
                        .setCancelable(true)
                        .setPositiveButton("ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                final AlertDialog alert1 = builder.create();
                //alert1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                alert1.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg) {
                        alert1.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                        alert1.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                    }
                });
                alert1.show();
            }
        }
        if (v == imgWeb) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.HELP_REDIRECT_URL));
            startActivity(browserIntent);
        }
        if(v==backArrow)
        {
            GoToMainActivity();
        }
    }

    @Override
    public void onBackPressed() {
        GoToMainActivity();
    }

    public void GoToMainActivity(){
        Intent mainIntent = new Intent(this, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        this.finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void getHelp() {
        final CustomDialog customDialog = new CustomDialog(this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.GET_HELP_DETAILS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.v("response==>",response.toString());
                phone=response.optString("contact_number");
                email=response.optString("contact_email");
                contact_text=response.optString("contact_text");
                //titleTxt.setText(response.optString("contact_title"));

                if(contact_text.equalsIgnoreCase(""))
                  lblContacttextt.setText("Our team person contact\n you soon!!");
                else
                    lblContacttextt.setText(contact_text);
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
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
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
                        e.printStackTrace();
                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(ActivityHelp.this, "access_token"));
                Log.e("", "Access_Token" + SharedHelper.getKey(ActivityHelp.this, "access_token"));
                return headers;
            }
        };
        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        /*Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();*/
        Toast.makeText(this,toastString,Toast.LENGTH_LONG).show();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(this, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(this, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        this.finish();
    }
}
