package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolder;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolderManager;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoTagsBundle;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.EditPhotoTagsPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import icepick.State;


@Layout(R.layout.fragment_edit_photo_tags)
public class EditPhotoTagsFragment extends RxBaseFragmentWithArgs<EditPhotoTagsPresenter, EditPhotoTagsBundle> {


    @State
    ArrayList<PhotoTag> locallyAddedTags = new ArrayList<>();
    @State
    ArrayList<PhotoTag> locallyDeletedTags = new ArrayList<>();

    @InjectView(R.id.tag_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.iv_image)
    SimpleDraweeView ivImage;
    @InjectView(R.id.taggable_holder)
    PhotoTagHolder taggableImageHolder;

    PhotoTagHolderManager photoTagHolderManager;

    @Override
    protected EditPhotoTagsPresenter createPresenter(Bundle savedInstanceState) {
        return new EditPhotoTagsPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        toolbar.inflateMenu(R.menu.menu_photo_tag_screen);
        toolbar.setOnMenuItemClickListener(this::onToolBarMenuItemClicked);

        photoTagHolderManager = new PhotoTagHolderManager(taggableImageHolder, getPresenter().getAccount(), getPresenter().getAccount());

        PipelineDraweeController draweeController = GraphicUtils.provideFrescoResizingController(getArgs().getPhoto().getImageUri(), ivImage.getController());
        draweeController.addControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                ivImage.post(() -> {
                    photoTagHolderManager.show(ivImage);
                    addSuggestions();
                    photoTagHolderManager.addExistsTagViews(getArgs().getPhotoTags());
                    if (getArgs().getActiveSuggestion() != null) {
                        photoTagHolderManager.addCreationTagBasedOnSuggestion(getArgs().getActiveSuggestion());
                    }
                });

            }
        });

        ViewGroup.LayoutParams params = ivImage.getLayoutParams();
        params.height = ViewUtils.getRootViewHeight(getActivity());
        ivImage.setLayoutParams(params);
        ivImage.setController(draweeController);

        photoTagHolderManager.setTagCreatedListener(photoTag -> {
            locallyAddedTags.add(photoTag);
            locallyDeletedTags.remove(photoTag);
        });

        photoTagHolderManager.setTagDeletedListener(photoTag -> {
            locallyDeletedTags.add(photoTag);
            locallyAddedTags.remove(photoTag);
        });

        photoTagHolderManager.creationTagEnabled(true);
        photoTagHolderManager.setFriendRequestProxy(getPresenter());
    }

    protected void addSuggestions() {
        List<PhotoTag> photoTags = Queryable.from(getArgs().getSuggestions())
                .filter(element -> !PhotoTag.isIntersectedWithPhotoTags(getArgs().getPhotoTags(), element)).toList();
        photoTagHolderManager.addSuggestionTagView(photoTags,
                (suggestion) -> {
                    photoTagHolderManager.addCreationTagBasedOnSuggestion(suggestion);
                });

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
            locallyAddedTags.removeAll(getArgs().getPhotoTags());
            locallyAddedTags.addAll(getArgs().getPhotoTags());
            locallyAddedTags.removeAll(locallyDeletedTags);

            ((Callback) getTargetFragment()).onTagSelected(getArgs().getRequestId(), locallyAddedTags, locallyDeletedTags);
        }
    }

    public interface Callback {
        void onTagSelected(long requestId, ArrayList<PhotoTag> addedTags, ArrayList<PhotoTag> removedTags);
    }

}
