package com.worldventures.dreamtrips.view.dialog.facebook.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.adapter.item.ItemWrapper;
import com.worldventures.dreamtrips.view.dialog.facebook.model.FacebookAlbum;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FacebookAlbumItem implements ItemWrapper<FacebookAlbum> {

    FacebookAlbum photo;
    @Inject
    UniversalImageLoader universalImageLoader;

    public FacebookAlbumItem(Injector injector, FacebookAlbum photo) {
        this.photo = photo;
        injector.inject(this);
    }

    public static FacebookAlbumItem convert(Injector injector, FacebookAlbum photo) {
        return new FacebookAlbumItem(injector, photo);
    }

    public static List<FacebookAlbumItem> convert(Injector injector, List<FacebookAlbum> photos) {
        List<FacebookAlbumItem> result = new ArrayList<>();
        if (photos != null) {
            for (FacebookAlbum p : photos) {
                result.add(convert(injector, p));
            }
        }
        return result;
    }

    @Override
    public RecyclerView.ViewHolder getBaseRecycleItem(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_facebook_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder h = ((ViewHolder) holder);
        //https://graph.facebook.com/<?=$album['id']?>/picture?type=album&access_token=<?=$access_token?>
        universalImageLoader.loadImage(photo.getCoverUrl(Session.getActiveSession().getAccessToken()), h.ivBg, null, new SimpleImageLoadingListener());
        h.tvTitle.setText(photo.getName());
        h.tvCount.setText(photo.getCount() + "");
    }

    @Override
    public int getItemViewType() {
        return 0;
    }

    @Override
    public FacebookAlbum getItem() {
        return photo;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.iv_bg)
        public ImageView ivBg;
        @InjectView(R.id.tv_album_title)
        public TextView tvTitle;
        @InjectView(R.id.tv_count)
        public TextView tvCount;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }
}
