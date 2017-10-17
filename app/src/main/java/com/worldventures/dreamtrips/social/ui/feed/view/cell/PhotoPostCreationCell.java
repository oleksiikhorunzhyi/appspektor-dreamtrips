package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.PhotoPostCreationDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.PhotoTagHolder;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.PhotoTagHolderManager;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_photo_post)
public class PhotoPostCreationCell extends BaseAbstractDelegateCell<PhotoCreationItem, PhotoPostCreationDelegate> implements ResizeableCell {

   private static final int DEGREE_PER_ONE_ROTATION = 90;

   @Inject SessionHolder userSessionHolder;

   @InjectView(R.id.photo_container) View photoContainer;
   @InjectView(R.id.attached_photo) SimpleDraweeView attachedPhoto;
   @InjectView(R.id.tag_btn) TextView tagButton;
   @InjectView(R.id.photo_title) EditText photoTitle;
   @InjectView(R.id.photo_post_taggable_holder) PhotoTagHolder photoTagHolder;
   @InjectView(R.id.remove) View remove;

   private volatile int previousWidth;
   private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

   public PhotoPostCreationCell(View view) {
      super(view);

      globalLayoutListener = () -> {
         if (itemView.getWidth() != previousWidth) {
            resizePhotoComponents();
         }
      };
   }

   @Override
   public void onAttachedToWindow(View v) {
      super.onAttachedToWindow(v);
      photoTitle.addTextChangedListener(textWatcher);

      photoTitle.setOnFocusChangeListener((view, hasFocus) -> {
         if (!hasFocus) photoContainer.requestFocus();
         cellDelegate.onPhotoTitleFocusChanged(hasFocus);
      });
      itemView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
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
   public void syncUIStateWithModel() {
      if (itemView.getWidth() > 0) {
         updateUi();
      } else {
         itemView.setVisibility(View.INVISIBLE);
         ViewUtils.runTaskAfterMeasure(itemView, this::updateUi);
      }
   }

   private void updateUi() {
      resizePhotoComponents();
      photoContainer.post(() -> {
         itemView.setVisibility(View.VISIBLE);
         photoTagHolder.removeAllViews();
         if (getModelObject().isCanEdit()) {
            showTagViewGroup();
         }
         invalidateAddTagBtn();
      });

      photoTitle.setText(getModelObject().getTitle());
      boolean titleChangesEnabled = getModelObject().isCanEdit();
      photoTitle.setVisibility(titleChangesEnabled || !TextUtils.isEmpty(getModelObject().getTitle()) ? View.VISIBLE : View.GONE);
      photoTitle.setEnabled(titleChangesEnabled);
      invalidateAddTagBtn();
      invalidateDeleteBtn();
   }

   private void resizePhotoComponents() {
      int width = itemView.getWidth();
      int height = calculateHeight();

      photoContainer.getLayoutParams().width = width;
      photoContainer.getLayoutParams().height = height;
      photoContainer.requestLayout();
      previousWidth = width;

      attachedPhoto.setController(GraphicUtils.provideFrescoResizingController(getPhotoUri(),
            attachedPhoto.getController(), width, height));
   }

   private Uri getPhotoUri() {
      return Uri.parse(getModelObject().getFileUri() == null
            ? getModelObject().getOriginUrl()
            : getModelObject().getFileUri());
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

      boolean normalRotationPosition = (getModelObject().getRotation() / DEGREE_PER_ONE_ROTATION) % 2 == 0;
      double aspectRatio = normalRotationPosition ? (double) height / width : (double) width / height;
      return (int) (cellWidth * aspectRatio);
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
      itemView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
   }

   @Override
   public void checkSize() {
      if (itemView.getWidth() != previousWidth) {
         resizePhotoComponents();
      }
   }

   private void invalidateTags() {
      invalidateAddTagBtn();
      cellDelegate.onTagsChanged();
   }

   private void invalidateAddTagBtn() {
      tagButton.setVisibility(getModelObject().isCanEdit() ? View.VISIBLE : View.GONE);
      //
      if (getModelObject().getCombinedTags().isEmpty()) {
         tagButton.setText(R.string.tag_people);
         tagButton.setSelected(false);
      } else {
         tagButton.setText(R.string.empty);
         tagButton.setSelected(true);
      }
   }

   private void invalidateDeleteBtn() {
      remove.setVisibility(getModelObject().isCanDelete() ? View.VISIBLE : View.GONE);
   }
}
