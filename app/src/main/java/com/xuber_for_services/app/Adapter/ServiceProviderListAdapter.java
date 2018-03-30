package com.xuber_for_services.app.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.app.Models.ServiceListModel;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.Utils.Utilities;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServiceProviderListAdapter extends RecyclerView.Adapter<ServiceProviderListAdapter.ViewHolder> {

    private ArrayList<ServiceListModel> listModels;
    private Context context;
    String TAG = "ServiceProviderListAdapter";
    boolean[] selectedService;

    public ServiceProviderListAdapter(ArrayList<ServiceListModel> listModel, Context context) {
        this.listModels = listModel;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_service_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView service_list_name, servicePriceTxt;
        ImageView service_image_icon, selectImg;

        public ViewHolder(View itemView) {
            super(itemView);
            service_image_icon = (ImageView) itemView.findViewById(R.id.service_image_icon);
            service_list_name = (TextView) itemView.findViewById(R.id.service_list_name);
            servicePriceTxt = (TextView) itemView.findViewById(R.id.service_price);
            selectImg = (ImageView) itemView.findViewById(R.id.select);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ServiceListModel serviceListModel = listModels.get(position);
        selectedService = new boolean[listModels.size()];
        //Log.v(TAG, "Response Name " + serviceListModel.getAvailable());
        holder.service_list_name.setText(serviceListModel.getName());
        //Log.v(TAG, "onBindViewHolder: " + Utilities.getImageURL(serviceListModel.getImage()));
        Picasso.with(context).load(Utilities.getImageURL(serviceListModel.getImage()))
                .error(R.drawable.no_image).placeholder(R.drawable.no_image)
                .fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(holder.service_image_icon);
        holder.service_image_icon.setTag(position);
        holder.service_list_name.setTag(position);
        holder.service_list_name.setTextColor(ContextCompat.getColor(context, R.color.black));
        holder.selectImg.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return listModels.size();
    }


}
