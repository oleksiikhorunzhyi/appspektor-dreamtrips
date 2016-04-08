package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.CreationPhotoTaggableHolderViewGroup;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoTagsBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.EditPhotoTagsPresenter;

import java.util.ArrayList;

import butterknife.InjectView;


@Layout(R.layout.fragment_edit_photo_tags)
public class EditPhotoTagsFragment extends RxBaseFragmentWithArgs<EditPhotoTagsPresenter, EditPhotoTagsBundle> {

    @InjectView(R.id.tag_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.iv_image)
    SimpleDraweeView ivImage;
    @InjectView(R.id.taggable_holder)
    CreationPhotoTaggableHolderViewGroup taggableImageHolder;

    @Override
    protected EditPhotoTagsPresenter createPresenter(Bundle savedInstanceState) {
        return new EditPhotoTagsPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        toolbar.inflateMenu(R.menu.menu_photo_tag_screen);
        toolbar.setOnMenuItemClickListener(this::onToolBarMenuItemClicked);

        PipelineDraweeController draweeController = GraphicUtils.provideFrescoResizingController(getArgs().getPhoto().getImageUri(), ivImage.getController());
        draweeController.addControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                ivImage.post(() -> taggableImageHolder.show(ivImage));
            }
        });

        ViewGroup.LayoutParams params = ivImage.getLayoutParams();
        params.height = ViewUtils.getRootViewHeight(getActivity());
        ivImage.setLayoutParams(params);

        ivImage.setController(draweeController);
        taggableImageHolder.setup(this, buildPhoto());
    }

    protected boolean onToolBarMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                notifyAboutTags();
                router.back();
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        OrientationUtil.lockOrientation(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        OrientationUtil.unlockOrientation(getActivity());
    }

    private void notifyAboutTags() {
        if (getTargetFragment() instanceof Callback) {
            taggableImageHolder.getLocallyAddedTags().removeAll(getArgs().getPhotoTags());
            taggableImageHolder.getLocallyAddedTags().addAll(getArgs().getPhotoTags());
            taggableImageHolder.getLocallyAddedTags().removeAll(taggableImageHolder.getLocallyDeletedTags());

            ((Callback) getTargetFragment()).onTagSelected(taggableImageHolder.getLocallyAddedTags(), taggableImageHolder.getLocallyDeletedTags());
        }
    }

    @NonNull
    protected Photo buildPhoto() {
        Photo photo = new Photo();
        Image images = new Image();
        images.setUrl(getArgs().getPhoto().getImageUri().toString());
        photo.setImages(images);
        photo.setPhotoTags(getArgs().getPhotoTags());
        return photo;
    }

    public interface Callback {
        void onTagSelected(ArrayList<PhotoTag> addedTags, ArrayList<PhotoTag> removedTags);
    }

}
