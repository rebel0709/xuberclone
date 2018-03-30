package com.xuber_for_services.app.Adapter;

/**
 * Created by jayakumar on 11/02/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuber_for_services.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PaymentListAdapter extends ArrayAdapter<JSONObject>{

    int vg;

    public  ArrayList<JSONObject> list;

    Context context;

    public PaymentListAdapter(Context context, int vg, ArrayList<JSONObject> list){

        super(context,vg,list);

        this.context=context;

        this.vg=vg;

        this.list=list;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(vg, parent, false);

        ImageView paymentTypeImg =(ImageView) itemView.findViewById(R.id.paymentTypeImg);

        TextView cardNumber =(TextView) itemView.findViewById(R.id.cardNumber);


        try {

           if(list.get(position).optString("brand").equalsIgnoreCase("MASTER")){
               paymentTypeImg.setImageResource(R.drawable.credit_card);
           }else if(list.get(position).optString("brand").equalsIgnoreCase("MASTRO")){
               paymentTypeImg.setImageResource(R.drawable.visa);
           }else if(list.get(position).optString("brand").equalsIgnoreCase("Visa")){
               paymentTypeImg.setImageResource(R.drawable.visa);
           }
           cardNumber.setText("xxxx - xxxx - xxxx - "+list.get(position).getString("last_four"));



        } catch (JSONException e) {

            e.printStackTrace();

        }



        return itemView;

    }

}

