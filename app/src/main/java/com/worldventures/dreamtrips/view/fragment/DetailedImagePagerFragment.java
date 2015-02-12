package com.worldventures.dreamtrips.view.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.TripImage;
import com.worldventures.dreamtrips.presentation.DetailedImagePagerFragmentPresentation;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.busevents.TripImageClickedEvent;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 1 on 23.01.15.
 */
@Layout(R.layout.fragment_image_detailed)
public class DetailedImagePagerFragment extends BaseFragment<DetailedImagePagerFragmentPresentation> implements DetailedImagePagerFragmentPresentation.View {

    public static final String EXTRA_PHOTO = "EXTRA_PHOTO";

    @InjectView(R.id.imageViewTripImage)
    ImageView ivImage;
    @InjectView(R.id.progressBarImage)
    ProgressBar progressBar;

    @Inject
    UniversalImageLoader imageLoader;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        Object photo = getArguments().getSerializable(EXTRA_PHOTO);

        if (photo instanceof TripImage) {
            getPresentationModel().setPhoto((TripImage) photo);
        }


        ViewTreeObserver viewTreeObserver = ivImage.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = ivImage.getWidth();
                int height = ivImage.getHeight();
                loadImage(width, height);
                ViewTreeObserver viewTreeObserver = ivImage.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void loadImage(int width, int height) {
        imageLoader.loadImage(getPresentationModel().getPhoto().getUrl(width, height), ivImage, UniversalImageLoader.OP_FULL_SCREEN, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d("TAG", "loading started = " + Calendar.getInstance().getTimeInMillis());
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
                informUser("Error while loading image");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
                Log.d("TAG", "loading finished = " + Calendar.getInstance().getTimeInMillis());
            }
        });
    }

    @OnClick(R.id.imageViewTripImage)
    void onImageClick() {
        getEventBus().post(new TripImageClickedEvent());
    }

    @Override
    protected DetailedImagePagerFragmentPresentation createPresentationModel(Bundle savedInstanceState) {
        return new DetailedImagePagerFragmentPresentation(this);
    }
}