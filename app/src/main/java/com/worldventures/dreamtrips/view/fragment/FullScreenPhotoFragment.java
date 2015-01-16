package com.worldventures.dreamtrips.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.FullScreenPhotoFragmentPM;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_fullscreen_photo)
public class FullScreenPhotoFragment extends BaseFragment<FullScreenPhotoFragmentPM> implements FullScreenPhotoFragmentPM.View {

    public static final String EXTRA_PHOTO = "EXTRA_PHOTO";

    @InjectView(R.id.iv_image)
    ImageView ivImage;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.iv_like)
    ImageView ivLike;
    @InjectView(R.id.iv_flag)
    ImageView ivFlag;
    @InjectView(R.id.pb)
    ProgressBar progressBar;

    @Inject
    UniversalImageLoader imageLoader;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        Photo photo = (Photo) getArguments().getSerializable(EXTRA_PHOTO);

        getPresentationModel().setPhoto(photo);

        imageLoader.loadImage(getPresentationModel().getPhoto().getUrl().getOriginal(), ivImage, UniversalImageLoader.OP_FULL_SCREEN, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
                informUser("Error while loading image");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }
        });

        getPresentationModel().onCreate();
    }

    @Override
    protected FullScreenPhotoFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new FullScreenPhotoFragmentPM(this);
    }

    @OnClick(R.id.iv_like)
    public void actionLike() {
        getPresentationModel().onLikeAction();
    }

    @OnClick(R.id.iv_flag)
    public void actionFlag() {
        PopupMenu popup = new PopupMenu(getActivity(), ivFlag);
        popup.getMenuInflater().inflate(R.menu.menu_flag, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            getPresentationModel().flagAction(item.getTitle().toString());
            return true;
        });
        popup.show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void setLiked(boolean isLiked) {
        ivLike.setSelected(isLiked);
    }
}
