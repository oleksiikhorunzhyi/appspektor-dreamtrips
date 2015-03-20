package com.worldventures.dreamtrips.view.adapter.item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoItem implements ItemWrapper<Photo> {

    Photo photo;

    @Inject
    UniversalImageLoader universalImageLoader;

    public PhotoItem(Photo photo) {
        this.photo = photo;
    }

    public static PhotoItem convert(Photo photo) {
        return new PhotoItem(photo);
    }

    public static List<PhotoItem> convert(List<Photo> photos) {
        List<PhotoItem> result = new ArrayList<>();
        if (photos != null) {
            for (Photo p : photos) {
                result.add(convert(p));
            }
        }
        return result;
    }

    @Override
    public RecyclerView.ViewHolder getBaseRecycleItem(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder h = ((ViewHolder) holder);
        universalImageLoader.loadImage(photo.getImages().getThumb().getUrl(), h.ivBg, null, new SimpleImageLoadingListener());
    }

    @Override
    public int getItemViewType() {
        return 0;
    }

    @Override
    public Photo getItem() {
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

}
