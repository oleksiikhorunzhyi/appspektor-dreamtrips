package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.OrientationUtil;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolder;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolderManager;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoTagsBundle;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.EditPhotoTagsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.EditPhotoTagsCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_edit_photo_tags)
public class EditPhotoTagsFragment extends RxBaseFragmentWithArgs<EditPhotoTagsPresenter, EditPhotoTagsBundle> implements EditPhotoTagsPresenter.View {

   @Inject EditPhotoTagsCallback editPhotoTagsCallback;

   @InjectView(R.id.tag_toolbar) Toolbar toolbar;
   @InjectView(R.id.iv_image) SimpleDraweeView ivImage;
   @InjectView(R.id.taggable_holder) PhotoTagHolder taggableImageHolder;

   PhotoTagHolderManager photoTagHolderManager;

   @Override
   protected EditPhotoTagsPresenter createPresenter(Bundle savedInstanceState) {
      return new EditPhotoTagsPresenter(getArgs().getRequestId(), getArgs().getPhotoTags());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      toolbar.inflateMenu(R.menu.menu_photo_tag_screen);
      toolbar.setOnMenuItemClickListener(this::onToolBarMenuItemClicked);
      //
      getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
         int height = 0;

         @Override
         public void onGlobalLayout() {
            height = ViewUtils.getRootViewHeight(getActivity());
            ViewGroup.LayoutParams params = ivImage.getLayoutParams();
            if (height == params.height) {
               ViewUtils.removeSupportGlobalLayoutListener(getView(), this);
               return;
            }
            params.height = height;
            ivImage.setLayoutParams(params);
            ivImage.setController(createTaggableDraweeController());
         }
      });
      //
      photoTagHolderManager = new PhotoTagHolderManager(taggableImageHolder, getPresenter().getAccount(), getPresenter()
            .getAccount());
      photoTagHolderManager.setTagCreatedListener(photoTag -> getPresenter().onTagAdded(photoTag));
      photoTagHolderManager.setTagDeletedListener(photoTag -> getPresenter().onTagDeleted(photoTag));
      photoTagHolderManager.creationTagEnabled(true);
      photoTagHolderManager.setFriendRequestProxy(getPresenter());
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      OrientationUtil.lockOrientation(getActivity());
   }

   protected void addSuggestions() {
      List<PhotoTag> photoTags = Queryable.from(getArgs().getSuggestions())
            .filter(element -> !PhotoTag.isIntersectedWithPhotoTags(getArgs().getPhotoTags(), element))
            .toList();
      photoTagHolderManager.addSuggestionTagView(photoTags, (suggestion) -> photoTagHolderManager.addCreationTagBasedOnSuggestion(suggestion));
   }

   protected boolean onToolBarMenuItemClicked(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_done:
            getPresenter().onDone();
            router.back();
            break;
      }
      return true;
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      SoftInputUtil.hideSoftInputMethod(getActivity());
      OrientationUtil.unlockOrientation(getActivity());
   }

   @Override
   public void notifyAboutTags(long requestId, ArrayList<PhotoTag> addedTags, ArrayList<PhotoTag> deletedTags) {
      editPhotoTagsCallback.onTagsSelected(requestId, addedTags, deletedTags);
   }

   @NonNull
   private PipelineDraweeController createTaggableDraweeController() {
      PipelineDraweeController draweeController = GraphicUtils.provideFrescoResizingController(getArgs().getPhoto()
            .getImageUri(), ivImage.getController());
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
      return draweeController;
   }
}