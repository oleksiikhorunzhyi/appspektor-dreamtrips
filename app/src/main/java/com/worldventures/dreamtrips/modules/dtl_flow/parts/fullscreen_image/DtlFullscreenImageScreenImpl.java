package com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class DtlFullscreenImageScreenImpl extends DtlLayout<DtlFullscreenImageScreen,
        DtlFullscreenImagePresenter, DtlFullscreenImagePath>
        implements DtlFullscreenImageScreen {

    @InjectView(R.id.imageView) SimpleDraweeView imageView;
    @InjectView(R.id.progressBar) View progressBar;

    @Override
    public void showImage(String url) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(Uri.parse(url))
                .build();
        imageView.setController(controller);
//        imageView.setImageUrl(url); // TODO :: 4/27/16 no image resizing here
    }

    @OnClick(R.id.back)
    protected void onBackClick() {
        getActivity().onBackPressed();
    }

    private ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo,
                                    @Nullable Animatable anim) {
            progressBar.setVisibility(GONE);
        }

        @Override
        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            informUser("Could not load image");
            Timber.e(throwable, "Error loading fullscreen image for offer");
            Crashlytics.logException(throwable);
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    // Boilerplate stuff
    ///////////////////////////////////////////////////////////////////////////

    public DtlFullscreenImageScreenImpl(Context context) {
        super(context);
    }

    public DtlFullscreenImageScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public DtlFullscreenImagePresenter createPresenter() {
        return new DtlFullscreenImagePresenterImpl(getContext(), getPath().getUrl());
    }
}
