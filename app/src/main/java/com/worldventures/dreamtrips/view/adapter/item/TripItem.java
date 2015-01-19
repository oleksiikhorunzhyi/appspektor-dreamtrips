package com.worldventures.dreamtrips.view.adapter.item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TripItem implements ItemWrapper<Trip> {

    Trip trip;

    @Inject
    UniversalImageLoader universalImageLoader;

    public TripItem(Injector injector, Trip trip) {
        this.trip = trip;
        injector.inject(this);
    }


    @Override
    public RecyclerView.ViewHolder getBaseRecycleItem(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_trip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder h = ((ViewHolder) holder);
        h.textViewName.setText(getItem().getName());
        h.textViewPlace.setText(getItem().getLocation());
        h.textViewPrice.setText(getItem().getPrice());
    }

    @Override
    public int getItemViewType() {
        return 0;
    }

    @Override
    public Trip getItem() {
        return trip;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.imageViewTripImage)
        ImageView imageViewTripImage;
        @InjectView(R.id.imageViewLike)
        ImageView imageViewLike;
        @InjectView(R.id.textViewName)
        TextView textViewName;
        @InjectView(R.id.textViewPlace)
        TextView textViewPlace;
        @InjectView(R.id.textViewPrice)
        TextView textViewPrice;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public static TripItem convert(Injector injector, Trip photo) {
        return new TripItem(injector, photo);
    }

    public static List<TripItem> convert(Injector injector, List<Trip> trips) {
        List<TripItem> result = new ArrayList<>();
        if (trips != null) {
            for (Trip p : trips) {
                result.add(convert(injector, p));
            }
        }
        return result;
    }


}
