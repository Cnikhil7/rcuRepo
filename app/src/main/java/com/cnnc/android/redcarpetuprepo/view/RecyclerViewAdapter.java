package com.cnnc.android.redcarpetuprepo.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    private Context mContext;
    private HomeRecyclerInterface mInterface;


    //view holder dimens
    private int itemHeight = 0;
    private int itemWidth = 0;

    interface HomeRecyclerInterface{
        void onHomeRecyclerItemClick(Country country,ImageView v);
    }

    public RecyclerViewAdapter(Context context, CountryResponse response, HomeRecyclerInterface mInterface, int screenWidth) {
        list = (ArrayList<Country>) response.getCountryList();
        mContext = context;
        this.mInterface = mInterface;

        setUpBasicDimensOfViewHolderItem(screenWidth);
    }

    private void setUpBasicDimensOfViewHolderItem(int screenWidth) {
        itemWidth = screenWidth;
        itemHeight = (int)((float)screenWidth/(1.5));//keeping the aspect ratio consistent
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
        Country country = list.get(position);
        holder.countryName.setText(country.getCountryName());
        Glide.with(mContext).load(country.getCountryFlagUrl()).into(holder.countryImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItems(List<Country> response){
        int pos = list.size();
        list.addAll(response);
        notifyItemRangeInserted(pos, list.size());
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView countryName;
        private ImageView countryImage;

        public ViewHolder(View itemView) {
            super(itemView);
            countryName = itemView.findViewById(R.id.country_item_name);
            countryImage = itemView.findViewById(R.id.country_item_image);

            FrameLayout.LayoutParams layoutParams = new FrameLayout
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.width = itemWidth;
            layoutParams.height = itemHeight;
            itemView.setLayoutParams(layoutParams);

            countryImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mInterface.onHomeRecyclerItemClick(list.get(getAdapterPosition()),(ImageView) v);
        }
    }

}
