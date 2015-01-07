package com.worldventures.dreamtrips.view.adapter.item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.activity.Injector;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoItem implements ItemWrapper {

    Photo photo;
    @Inject
    UniversalImageLoader universalImageLoader;

    public PhotoItem(Injector injector, Photo photo) {
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
        universalImageLoader.loadImage(photo.getUrl().getMedium(), h.ivBg,null);
    }

    @Override
    public int getItemViewType() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.iv_bg)
        public ImageView ivBg;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }


    public static PhotoItem convert(Injector injector, Photo photo) {
        return new PhotoItem(injector, photo);
    }

    public static List<PhotoItem> convert(Injector injector, List<Photo> photos) {
        List<PhotoItem> result = new ArrayList<>();
        for (Photo p : photos) {
            result.add(convert(injector, p));
        }
        return result;
    }

}
