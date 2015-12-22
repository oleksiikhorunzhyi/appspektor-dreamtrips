package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen;

import android.os.Build;
import android.view.ViewTreeObserver;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.State;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ScaleImageView;

import butterknife.InjectView;

public abstract class FullScreenPhotoFragment<PRESENTER extends FullScreenPresenter<T, ? extends FullScreenPresenter.View>, T extends IFullScreenObject>
        extends BaseFragmentWithArgs<PRESENTER, FullScreenPhotoBundle> implements FullScreenPresenter.View {

    @InjectView(R.id.iv_image)
    ScaleImageView ivImage;

    @State
    protected IFullScreenObject photo;


    @Override
    public void setContent(IFullScreenObject photo) {
        loadImage(photo.getFSImage());
    }

    private void loadImage(Image image) {
        String lowUrl = image.getThumbUrl(getResources());
        ivImage.requestLayout();
        ViewTreeObserver viewTreeObserver = ivImage.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (ivImage != null) {
                    int size = Math.max(ivImage.getWidth(), ivImage.getHeight());
                    DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                            .setLowResImageRequest(ImageRequest.fromUri(lowUrl))
                            .setImageRequest(ImageRequest.fromUri(image.getUrl(size, size)))
                            .build();
                    ivImage.setController(draweeController);

                    ViewTreeObserver viewTreeObserver = ivImage.getViewTreeObserver();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this);
                    } else {
                        viewTreeObserver.removeGlobalOnLayoutListener(this);
                    }
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

}
