package com.worldventures.dreamtrips.view.dialog.facebook.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.adapter.item.ItemWrapper;
import com.worldventures.dreamtrips.view.dialog.facebook.model.FacebookPhoto;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FacebookPhotoItem implements ItemWrapper<FacebookPhoto> {

    FacebookPhoto photo;
    @Inject
    UniversalImageLoader universalImageLoader;

    public FacebookPhotoItem(Injector injector, FacebookPhoto photo) {
        this.photo = photo;
        injector.inject(this);

    }

    @Override
    public RecyclerView.ViewHolder getBaseRecycleItem(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder h = ((ViewHolder) holder);
        universalImageLoader.loadImage(photo.getPicture(), h.ivBg, null, new SimpleImageLoadingListener());
    }

    @Override
    public int getItemViewType() {
        return 0;
    }

    @Override
    public FacebookPhoto getItem() {
        return photo;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.iv_bg)
        public ImageView ivBg;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }


    public static FacebookPhotoItem convert(Injector injector, FacebookPhoto photo) {
        return new FacebookPhotoItem(injector, photo);
    }

    public static List<FacebookPhotoItem> convert(Injector injector, List<FacebookPhoto> photos) {
        List<FacebookPhotoItem> result = new ArrayList<>();
        if (photos != null) {
            for (FacebookPhoto p : photos) {
                result.add(convert(injector, p));
            }
        }
        return result;
    }

}
