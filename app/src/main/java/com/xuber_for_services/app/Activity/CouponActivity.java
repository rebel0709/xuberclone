package com.xuber_for_services.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.xuber_for_services.app.Adapter.CouponListAdapter;
import com.xuber_for_services.app.Fragments.PastTrips;
import com.xuber_for_services.app.Helper.CustomDialog;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.Constants.URLHelper;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.Utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CouponActivity extends AppCompatActivity {

    private EditText coupon_et;
    private Button apply_button;
    private String session_token;
    Context context;
    LinearLayout couponListCardView, lnrNoDetails;
    ListView coupon_list_view;
    ArrayList<JSONObject> listItems;
    ListAdapter couponAdapter;
    CustomDialog customDialog;
    ImageView back_Arrow;
    Utilities utils = new Utilities();

    CouponAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setBackgroundDrawableResource(R.drawable.coupon_bg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_coupon);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = CouponActivity.this;
        session_token = SharedHelper.getKey(this, "access_token");
        couponListCardView = (LinearLayout) findViewById(R.id.cardListViewLayout);
        lnrNoDetails = (LinearLayout) findViewById(R.id.lnrNoDetails);
        coupon_list_view = (ListView) findViewById(R.id.coupon_list_view);
        coupon_et = (EditText) findViewById(R.id.coupon_et);
        apply_button = (Button) findViewById(R.id.apply_button);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        back_Arrow = (ImageView) findViewById(R.id.backArrow);
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coupon_et.getText().toString().isEmpty()) {
                    Toast.makeText(CouponActivity.this, "Enter a coupon", Toast.LENGTH_SHORT).show();
                } else {
                    sendToServer();
                }
            }
        });

        back_Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToMainActivity();
            }
        });

        getCoupon();
    }


    public void GoToMainActivity(){
        Intent mainIntent = new Intent(this, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        GoToMainActivity();
    }

    private void sendToServer() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JsonObject json = new JsonObject();
        json.addProperty("promocode", coupon_et.getText().toString());
        Ion.with(this)
                .load(URLHelper.ADD_COUPON_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        try {
                            customDialog.dismiss();
                            // response contains both the headers and the string result
                            if (response.getHeaders().code() == 200) {
                                utils.print("AddCouponRes", "" + response.getResult());
                                coupon_et.setText("");
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getResult());
                                    if (jsonObject.optString("code").equals("promocode_applied")) {
                                        Toast.makeText(CouponActivity.this, getString(R.string.coupon_added), Toast.LENGTH_SHORT).show();
                                        couponListCardView.setVisibility(View.GONE);
                                        getCoupon();
                                    } else if (jsonObject.optString("code").equals("promocode_expired")) {
                                        Toast.makeText(CouponActivity.this, getString(R.string.expired_coupon), Toast.LENGTH_SHORT).show();
                                    } else if (jsonObject.optString("code").equals("promocode_already_in_use")) {
                                        Toast.makeText(CouponActivity.this, getString(R.string.already_in_use_coupon), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                customDialog.dismiss();
                                utils.print("AddCouponErr", "" + response.getResult());
                                Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });

    }


    private void getCoupon() {
        couponListCardView.setVisibility(View.GONE);
        lnrNoDetails.setVisibility(View.VISIBLE);
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        Ion.with(this)
                .load(URLHelper.COUPON_LIST_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        customDialog.dismiss();
                        if (response != null) {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    utils.print("CouponActivity", "" + jsonArray.toString());
                                    if (jsonArray.length() > 0 && jsonArray != null) {
                                        /*listItems = getArrayListFromJSONArray(jsonArray);
                                        couponAdapter = new CouponListAdapter(context,R.layout.coupon_list_item,listItems);
                                        coupon_list_view.setAdapter(couponAdapter);
                                        lnrNoDetails.setVisibility(View.GONE);
                                        couponListCardView.setVisibility(View.VISIBLE);*/

                                        adapter = new CouponAdapter(jsonArray);
                                        //  recyclerView.setHasFixedSize(true);
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                        recyclerView.setLayoutManager(mLayoutManager);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        if (adapter != null && adapter.getItemCount() > 0) {

                                            couponListCardView.setVisibility(View.VISIBLE);
                                            lnrNoDetails.setVisibility(View.GONE);
                                            recyclerView.setAdapter(adapter);
                                        } else {
                                            couponListCardView.setVisibility(View.GONE);
                                            lnrNoDetails.setVisibility(View.VISIBLE);
                                        }


                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                customDialog.dismiss();
                            }
                        } else {
                            customDialog.dismiss();
                        }

                    }
                });
    }


    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {

        ArrayList<JSONObject> aList = new ArrayList<JSONObject>();

        try {
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    aList.add(jsonArray.getJSONObject(i));

                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return aList;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    private class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public CouponAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        public void append(JSONArray array) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    this.jsonArray.put(array.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public CouponAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.coupon_list_item, parent, false);
            return new CouponAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CouponAdapter.MyViewHolder holder, int position) {


            try {
                //discount.setText(SharedHelper.getKey(context, "currency")+""+list.get(position).optJSONObject("promocode").optString("discount")+" "+context.getString(R.string.off));
                holder.discount.setText(SharedHelper.getKey(context,"currency")+""+jsonArray.optJSONObject(position).optJSONObject("promocode").optString("discount"));
                //promo_code.setText(context.getString(R.string.the_applied_coupon)+" "+list.get(position).optJSONObject("promocode").optString("promo_code")+".");
                holder.promo_code.setText(jsonArray.optJSONObject(position).optJSONObject("promocode").optString("promo_code"));
                String date = jsonArray.optJSONObject(position).optJSONObject("promocode").optString("expiration");
                // expires.setText(context.getString(R.string.valid_until)+" "+getDate(date)+" "+getMonth(date)+" "+getYear(date));
                holder.expires.setText(getDate(date) + "/" + getMonth(date) + "/" + getYear(date));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView discount, promo_code, expires;

            public MyViewHolder(View itemView) {
                super(itemView);
                discount = (TextView) itemView.findViewById(R.id.discount);

                promo_code = (TextView) itemView.findViewById(R.id.promo_code);

                expires = (TextView) itemView.findViewById(R.id.expiry);

            }
        }
    }

    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MM").format(cal.getTime());
        //String monthName = new SimpleDateFormat("MMM").format(cal.getTime());    Aug
        return monthName;
    }

    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }
}
