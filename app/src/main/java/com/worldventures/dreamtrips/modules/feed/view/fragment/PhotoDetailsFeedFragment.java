package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.PhotoBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityEditClickEvent;
import com.worldventures.dreamtrips.modules.feed.presenter.PhotoDetailsFeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;


@Layout(R.layout.adapter_item_feed_photo_child)
public class PhotoDetailsFeedFragment extends BaseFragmentWithArgs<PhotoDetailsFeedPresenter, PhotoBundle> implements PhotoDetailsFeedPresenter.View {


    @InjectView(R.id.photo)
    SimpleDraweeView photoView;
    @InjectView(R.id.title)
    TextView title;

    @Override
    protected PhotoDetailsFeedPresenter createPresenter(Bundle savedInstanceState) {
        return new PhotoDetailsFeedPresenter(getArgs().getPhoto());
    }

    public void showActionPopup(View anchor) {
        anchor.setEnabled(false);
        FeedItemMenuBuilder.create(getActivity(), anchor, R.menu.menu_feed_entity_edit)
                .onDelete(this::showDeleteDialog)
                .onEdit(() -> {
                    if (isVisibleOnScreen()) getPresenter().onEdit();
                })
                .dismissListener(menu -> anchor.setEnabled(true))
                .show();
    }

    public void onEvent(FeedEntityEditClickEvent event) {
        if (isVisibleOnScreen()) {
            showActionPopup(event.getAnchor());
        }
    }

    @Override
    public boolean isVisibleOnScreen() {
        return ViewUtils.isPartVisibleOnScreen(this);
    }

    private void showDeleteDialog() {
        Dialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.photo_delete))
                .setContentText(getResources().getString(R.string.photo_delete_caption))
                .setConfirmText(getResources().getString(R.string.post_delete_confirm))
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    getPresenter().onDelete();
                });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void moveToEdit(Photo photo) {
        router.moveTo(Route.PHOTO_EDIT, NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new EditPhotoBundle(photo))
                .build());
    }

    @Override
    public void setupView(Photo photo) {
        this.title.setText(photo.getTitle());

        Uri imageUrl = Uri.parse(photo.getFSImage().getUrl());
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setImageRequest(ImageRequest.fromUri(imageUrl));
        builder.setOldController(photoView.getController());
        DraweeController draweeController = builder.build();
        photoView.setController(draweeController);

        this.photoView.setOnClickListener(v -> {
            ArrayList<IFullScreenObject> items = new ArrayList<>();
            items.add(photo);
            FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                    .position(0)
                    .userId(photo.getOwner().getId())
                    .route(Route.SOCIAL_IMAGE_FULLSCREEN)
                    .type(TripImagesType.FIXED)
                    .fixedList(items)
                    .build();

            router.moveTo(Route.FULLSCREEN_PHOTO_LIST, NavigationConfigBuilder.forActivity()
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .data(data)
                    .build());
        });
    }
}