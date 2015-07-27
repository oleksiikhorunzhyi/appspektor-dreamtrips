package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.TripImageClickedEvent;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.DetailedImagePresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_image_details)
public class TripImagePagerFragment extends BaseFragment<DetailedImagePresenter> implements DetailedImagePresenter.View {

    public static final String EXTRA_PHOTO = "EXTRA_PHOTO";
    public static final String EXTRA_PHOTO_FULLSCREEN = "isFullscreen";

    @InjectView(R.id.imageViewTripImage)
    protected SimpleDraweeView ivImage;

    @InjectView(R.id.progressBarImage)
    protected ProgressBar progressBar;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        Object photo = getArguments().getSerializable(EXTRA_PHOTO);
        boolean isFullScreen = getArguments().getBoolean(EXTRA_PHOTO_FULLSCREEN, false);

        if (isFullScreen) {
            ivImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        } else {
            ivImage.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        }

        getPresenter().setPhoto((TripImage) photo);

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

    @Override
    public void onDestroyView() {
        if (ivImage != null && ivImage.getController() != null)
            ivImage.getController().onDetach();
        super.onDestroyView();
    }

    private void loadImage(int width, int height) {
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                reportError();
            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(getPresenter().getPhoto().getUrl(width, height)))
                .setControllerListener(controllerListener)
                .build();

        ivImage.setController(draweeController);
    }

    private void reportError() {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.imageViewTripImage)
    void onImageClick() {
        getEventBus().post(new TripImageClickedEvent());
    }

    @Override
    protected DetailedImagePresenter createPresenter(Bundle savedInstanceState) {
        return new DetailedImagePresenter();
    }
}