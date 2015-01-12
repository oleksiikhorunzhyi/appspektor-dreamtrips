package com.worldventures.dreamtrips.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.activity.FullScreenPhotoActivity;
import com.worldventures.dreamtrips.view.presentation.FullScreenPhotoFragmentPM;

import org.robobinding.ViewBinder;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FullScreenPhotoFragment extends BaseFragment<FullScreenPhotoActivity> {

    public static final String EXTRA_PHOTO = "EXTRA_PHOTO";
    Photo photo;

    @InjectView(R.id.iv_image)
    ImageView ivImage;
    @InjectView(R.id.pb)
    ProgressBar pb;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @Inject
    UniversalImageLoader imageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FullScreenPhotoFragmentPM presentationModel = new FullScreenPhotoFragmentPM(this, getAbsActivity());
        ViewBinder viewBinder = getAbsActivity().createViewBinder();
        View view = viewBinder.inflateAndBindWithoutAttachingToRoot(R.layout.fragment_fullscreen_photo, presentationModel, container);
        ButterKnife.inject(this, view);
        getAbsActivity().inject(this);
        photo = (Photo) getArguments().getSerializable(EXTRA_PHOTO);
        imageLoader.loadImage(photo.getUrl().getOriginal(), ivImage, UniversalImageLoader.OP_FULL_SCREEN, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                pb.setVisibility(View.GONE);
                informUser("Error while loading image");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                pb.setVisibility(View.GONE);
            }
        });
        tvTitle.setText(photo.getTitle());
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
