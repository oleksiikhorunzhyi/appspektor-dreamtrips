package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.helpers;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolder;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolderManager;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.PhotoPostCreationDelegate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.adapter_item_photo_post)
public class PhotoReviewPostCreationCell extends AbstractDelegateCell<PhotoCreationItem, PhotoPostCreationDelegate> {

   @Inject @ForActivity Injector injector;
   @Inject SessionHolder<UserSession> userSessionHolder;

   @InjectView(R.id.shadow) View shadow;
   @InjectView(R.id.photo_container) View photoContainer;
   @InjectView(R.id.fab_progress) FabButton fabProgress;
   @InjectView(R.id.attached_photo) SimpleDraweeView attachedPhoto;
   @InjectView(R.id.fabbutton_circle) CircleImageView circleView;
   @InjectView(R.id.tag_btn) TextView tagButton;
   @InjectView(R.id.photo_title) EditText photoTitle;
   @InjectView(R.id.photo_post_taggable_holder) PhotoTagHolder photoTagHolder;
   @InjectView(R.id.remove) View remove;

   private void hideInfo(){
      photoTitle.setVisibility(View.GONE);
      tagButton.setVisibility(View.GONE);
   }

   public PhotoReviewPostCreationCell(View view) {
      super(view);
      view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
         @Override
         public void onViewAttachedToWindow(View v) {
            photoTitle.addTextChangedListener(textWatcher);
            photoTitle.setOnFocusChangeListener((view, hasFocus) -> {
               if (!hasFocus) photoContainer.requestFocus();
               cellDelegate.onPhotoTitleFocusChanged(hasFocus);
            });
         }

         @Override
         public void onViewDetachedFromWindow(View v) {
         }
      });
   }

   private TextWatcherAdapter textWatcher = new TextWatcherAdapter() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
         super.onTextChanged(s, start, before, count);
         getModelObject().setTitle(s.toString().trim());
         cellDelegate.onPhotoTitleChanged(s.toString().trim());
      }
   };

   @Override
   protected void syncUIStateWithModel() {
      if (itemView.getWidth() > 0) {
         updateUi();
      } else {
         itemView.setVisibility(View.INVISIBLE);
         ViewUtils.runTaskAfterMeasure(itemView, () -> {
            updateUi();
         });
      }
   }

   private void updateUi() {
      photoContainer.getLayoutParams().width = itemView.getWidth();
      photoContainer.getLayoutParams().height = calculateHeight();
      photoContainer.requestLayout();
      photoContainer.post(() -> {
         itemView.setVisibility(View.VISIBLE);
         photoTagHolder.removeAllViews();
         if (getModelObject().isCanEdit()) {
            showTagViewGroup();
         }
         invalidateAddTagBtn();
      });

      PipelineDraweeController draweeController = GraphicUtils.provideFrescoResizingController(Uri.parse(getModelObject()
            .getFileUri() == null ? getModelObject().getOriginUrl() : getModelObject().getFileUri()), attachedPhoto
            .getController());

      attachedPhoto.setController(draweeController);
      photoTitle.setText(getModelObject().getTitle());
      boolean titleChangesEnabled = getModelObject().isCanEdit();
      photoTitle.setVisibility(titleChangesEnabled || !TextUtils.isEmpty(getModelObject().getTitle()) ? View.VISIBLE : View.GONE);
      photoTitle.setEnabled(titleChangesEnabled);
      invalidateAddTagBtn();
      invalidateDeleteBtn();
      hideInfo();
   }

   private int calculateHeight() {
      int width = getModelObject().getWidth();
      int height = getModelObject().getHeight();
      int cellWidth = itemView.getWidth();
      //in case of server response width = 0, height = 0;
      if (width == 0 || height == 0) {
         width = cellWidth;
         height = cellWidth;
      }
      int calculated = (int) (cellWidth / (float) width * height);
      return calculated;
   }

   private void showTagViewGroup() {
      User user = userSessionHolder.get().get().getUser();
      PhotoTagHolderManager photoTagHolderManager = new PhotoTagHolderManager(photoTagHolder, user, user);
      photoTagHolderManager.setTagCreatedListener(photoTag -> {
         getModelObject().getCachedRemovedPhotoTags().remove(photoTag);
         getModelObject().getCachedAddedPhotoTags().add(photoTag);
         invalidateTags();
      });

      photoTagHolderManager.setTagDeletedListener(photoTag -> {
         boolean removed = getModelObject().getCachedAddedPhotoTags().remove(photoTag);
         if (!removed) getModelObject().getCachedRemovedPhotoTags().add(photoTag);
         addTagSuggestions(photoTagHolderManager);
         invalidateTags();
      });
      photoTagHolderManager.show(attachedPhoto);

      addTagSuggestions(photoTagHolderManager);
      photoTagHolderManager.addExistsTagViews(getModelObject().getCombinedTags());
   }

   private void addTagSuggestions(PhotoTagHolderManager photoTagHolderManager) {
      Set<PhotoTag> currentTags = new HashSet<>();
      currentTags.addAll(getModelObject().getCombinedTags());
      currentTags.addAll(getModelObject().getCachedAddedPhotoTags());
      currentTags.removeAll(getModelObject().getCachedRemovedPhotoTags());
      List<PhotoTag> notIntersectingSuggestions =
            PhotoTag.findSuggestionsNotIntersectingWithTags(getModelObject().getSuggestions(),
                  new ArrayList<>(currentTags));
      photoTagHolderManager.addSuggestionTagViews(notIntersectingSuggestions,
            tag -> cellDelegate.onSuggestionClicked(getModelObject(), tag));
   }

   @OnClick(R.id.tag_btn)
   void onTag() {
      cellDelegate.onTagIconClicked(getModelObject());
   }

   @OnClick(R.id.remove)
   void onDelete() {
      cellDelegate.onRemoveClicked(getModelObject());
   }

   @Override
   public void clearResources() {
      super.clearResources();
      photoTitle.removeTextChangedListener(textWatcher);
      photoTitle.setOnFocusChangeListener(null);
   }

   private void invalidateTags() {
      invalidateAddTagBtn();
      cellDelegate.onTagsChanged();
   }

   private void invalidateAddTagBtn() {
      /*tagButton.setVisibility(getModelObject().isCanEdit() ? View.VISIBLE : View.GONE);
      //
      if (getModelObject().getCombinedTags().isEmpty()) {
         tagButton.setText(R.string.tag_people);
         tagButton.setSelected(false);
      } else {
         tagButton.setText(R.string.empty);
         tagButton.setSelected(true);
      }*/
   }

   private void invalidateDeleteBtn() {
      remove.setVisibility(getModelObject().isCanDelete() ? View.VISIBLE : View.GONE);
   }
}
