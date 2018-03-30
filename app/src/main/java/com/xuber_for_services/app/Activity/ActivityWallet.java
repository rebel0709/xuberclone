package com.xuber_for_services.app.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.xuber_for_services.app.Helper.CustomDialog;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.Constants.URLHelper;
import com.xuber_for_services.app.Models.CardInfo;
import com.xuber_for_services.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityWallet extends AppCompatActivity implements View.OnClickListener {

    private final int ADD_CARD_CODE = 435;

    private Button add_fund_button;
    private ProgressDialog loadingDialog;
    private CardView add_money_card;

    private Button add_money_button;
    private EditText money_et;
    private TextView balance_tv;
    private String session_token;
    private Button one, two, three;
    private double update_amount = 0;
    private ArrayList<CardInfo> cardInfoArrayList;
    private String currency = "";
    private CustomDialog customDialog;
    private Context context;
    private TextView currencySymbol;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_wallet);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardInfoArrayList = new ArrayList<>();
        add_fund_button = (Button) findViewById(R.id.add_fund_button);
        add_money_card = (CardView) findViewById(R.id.add_money_card);
        balance_tv = (TextView) findViewById(R.id.balance_tv);
        currencySymbol = (TextView) findViewById(R.id.currencySymbol);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        context = this;

        currencySymbol.setText(SharedHelper.getKey(context,"currency"));
        money_et = (EditText) findViewById(R.id.money_et);
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        /*one.setText(SharedHelper.getKey(context,"currency")+"199");
        two.setText(SharedHelper.getKey(context,"currency")+"599");
        three.setText(SharedHelper.getKey(context,"currency")+"1099");*/

        one.setText("$199");
        two.setText("$599");
        three.setText("$1099");

        float alpha = 0.45f;
        AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
        alphaUp.setFillAfter(true);
        add_fund_button.startAnimation(alphaUp);


        money_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().length() == 0) {
                    //add_fund_button.setBackgroundColor(Color.TRANSPARENT);
                    float alpha = 0.45f;
                    AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
                    alphaUp.setFillAfter(true);
                    add_fund_button.startAnimation(alphaUp);

                }
                else {

                    float alpha = 1f;
                    AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
                    alphaUp.setFillAfter(true);
                    add_fund_button.startAnimation(alphaUp);

                }
                if (count == 1 || count == 0) {
                    one.setBackground(getResources().getDrawable(R.drawable.border_stroke_new));
                    two.setBackground(getResources().getDrawable(R.drawable.border_stroke_new));
                    three.setBackground(getResources().getDrawable(R.drawable.border_stroke_new));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        add_fund_button.setOnClickListener(this);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Please wait...");

        session_token = SharedHelper.getKey(this, "access_token");

        add_money_card.setVisibility(View.VISIBLE);

        getBalance();
        getCards(false);

    }

    private void getBalance() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        Ion.with(this)
                .load(URLHelper.GET_USER_PROFILE)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if (response != null){
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getResult());
                                    currency = jsonObject.optString("currency");
                                    balance_tv.setText(SharedHelper.getKey(context,"currency") + jsonObject.optString("wallet_balance"));
                                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                customDialog.dismiss();
                            }else{
                                customDialog.dismiss();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void getCards(final boolean showLoading) {
        if (showLoading){
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            customDialog.show();
        }
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.getResult());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject cardObj = jsonArray.getJSONObject(i);
                                    CardInfo cardInfo = new CardInfo();
                                    cardInfo.setCardId(cardObj.optString("card_id"));
                                    cardInfo.setCardType(cardObj.optString("brand"));
                                    cardInfo.setLastFour(cardObj.optString("last_four"));
                                    cardInfoArrayList.add(cardInfo);
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                        if (showLoading) {
                            customDialog.dismiss();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fund_button:
                if (money_et.getText().toString().isEmpty() || money_et.getText().toString().equalsIgnoreCase("0")) {
                    update_amount = 0;
                    Toast.makeText(this, "Enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                    float alpha = 0.45f;
                    AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
                    alphaUp.setFillAfter(true);
                    add_fund_button.startAnimation(alphaUp);
                } else {
                    update_amount = Double.parseDouble(money_et.getText().toString());
                    //  payByPayPal(update_amount);
                    if(cardInfoArrayList.size() > 0){
                        showChooser();
                    }else{
                        gotoAddCard();
                    }
                }
                break;

            case R.id.backArrow:
                GoToMainActivity();
                break;

            case R.id.one:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke_new));
                two.setBackground(getResources().getDrawable(R.drawable.review_bg_money));
                three.setBackground(getResources().getDrawable(R.drawable.review_bg_money));
                money_et.setText("199");
                break;
            case R.id.two:
                one.setBackground(getResources().getDrawable(R.drawable.review_bg_money));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke_new));
                three.setBackground(getResources().getDrawable(R.drawable.review_bg_money));
                money_et.setText("599");
                break;
            case R.id.three:
                one.setBackground(getResources().getDrawable(R.drawable.review_bg_money));
                two.setBackground(getResources().getDrawable(R.drawable.review_bg_money));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke_new));
                money_et.setText("1099");
                break;
        }
    }

    public void GoToMainActivity(){
        Intent mainIntent = new Intent(this, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        this.finish();
    }

    private void gotoAddCard() {
        Intent mainIntent = new Intent(this, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards(true);
                }
            }
        }
    }

    private void showChooser() {

        String[] cardsList = new String[cardInfoArrayList.size()];
        
        for(int i = 0; i < cardInfoArrayList.size(); i++){
            cardsList[i] = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(i).getLastFour();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builderSingle.setTitle("Add money using");
        builderSingle.setSingleChoiceItems(cardsList, 0, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_tv);

        for (int j = 0; j < cardInfoArrayList.size(); j++) {
            String card = "";
            card =  "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(j).getLastFour();
            arrayAdapter.add(card);
        }
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                Log.e("Items clicked===>",""+selectedPosition);
                addMoney(cardInfoArrayList.get(selectedPosition));
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
//                        addMoney(cardInfoArrayList.get(which));
//                    }
//                });
        builderSingle.show();
    }

    private void addMoney(CardInfo cardInfo) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("card_id", cardInfo.getCardId());
        json.addProperty("amount", money_et.getText().toString());

        Ion.with(this)
                .load(URLHelper.ADD_CARD)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.getResult());
                                Toast.makeText(ActivityWallet.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                JSONObject userObj = jsonObject.getJSONObject("user");
                                balance_tv.setText(currency + userObj.optString("wallet_balance"));
                                //balance_tv.setText("$"+ userObj.optString("wallet_balance"));
                                SharedHelper.putKey(context, "wallet_balance", userObj.optString("wallet_balance"));
                                money_et.setText("");
                                customDialog.dismiss();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }else{
                            customDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        GoToMainActivity();
    }
}
