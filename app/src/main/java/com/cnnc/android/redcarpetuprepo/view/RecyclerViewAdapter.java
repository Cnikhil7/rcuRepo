package com.cnnc.android.redcarpetuprepo.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnnc.android.redcarpetuprepo.R;
import com.cnnc.android.redcarpetuprepo.model.pojo.Country;
import com.cnnc.android.redcarpetuprepo.model.pojo.CountryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIKHIL on 3/19/2018.
 */

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Country> list;

    public RecyclerViewAdapter(CountryResponse response) {
        list = (ArrayList<Country>) response.getCountryList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.country_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.countryName.setText(list.get(position).getCountryName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView countryName;

        public ViewHolder(View itemView) {
            super(itemView);
            countryName = itemView.findViewById(R.id.country_item_name);
        }
    }

    public void addItems(List<Country> response){
        int pos = list.size();
        list.addAll(response);
        notifyItemRangeInserted(pos, list.size());
    }
}
