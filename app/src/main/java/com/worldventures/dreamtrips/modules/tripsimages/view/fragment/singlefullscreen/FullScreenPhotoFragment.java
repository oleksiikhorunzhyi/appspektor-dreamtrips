package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen;

import android.graphics.drawable.Animatable;
import android.os.Build;
import android.view.ViewTreeObserver;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.State;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ScaleImageView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;

public abstract class FullScreenPhotoFragment<PRESENTER extends FullScreenPresenter<T, ? extends FullScreenPresenter.View>, T extends IFullScreenObject>
        extends BaseFragmentWithArgs<PRESENTER, FullScreenPhotoBundle> implements FullScreenPresenter.View {

    @InjectView(R.id.iv_image)
    ScaleImageView ivImage;

    @State
    protected IFullScreenObject photo;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

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
                            .setControllerListener(new BaseControllerListener<ImageInfo>() {
                                @Override
                                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                                    super.onFinalImageSet(id, imageInfo, animatable);
                                    onImageGlobalLayout();
                                }
                            })
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

    protected void onImageGlobalLayout() {
    }


    @Override
    public void onDestroyView() {
        if (ivImage != null && ivImage.getController() != null)
            ivImage.getController().onDetach();
        super.onDestroyView();
    }

    @Override
    public void openShare(String imageUrl, String text, @ShareFragment.ShareType String type) {
        ShareBundle data = new ShareBundle();
        data.setImageUrl(imageUrl);
        data.setText(text == null ? "" : text);
        data.setShareType(type);
        router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity()
                .data(data)
                .build());
    }

    @Override
    public void openUser(UserBundle bundle) {
        router.moveTo(routeCreator.createRoute(bundle.getUser().getId()), NavigationConfigBuilder.forActivity()
                .data(bundle)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .build());
    }
}
