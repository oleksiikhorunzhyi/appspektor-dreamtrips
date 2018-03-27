package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.utils.ImageUtils;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.ViewFeedEntityAction;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.TranslateView;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesFullscreenArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesFullscreenFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_feed_photo_event)
public class PhotoFeedItemDetailsCell extends FeedItemDetailsCell<PhotoFeedItem, BaseFeedCell.FeedCellDelegate<PhotoFeedItem>> {

   @InjectView(R.id.photo) SimpleDraweeView photoImageView;
   @InjectView(R.id.title) TextView title;
   @InjectView(R.id.tag) ImageView tag;
   @InjectView(R.id.translate_view) TranslateView viewWithTranslation;
   @InjectView(R.id.translate) View translateButton;

   @Inject SessionHolder appSessionHolder;

   public PhotoFeedItemDetailsCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      PhotoFeedItem photoItem = getModelObject();
      if (photoItem != null) {
         Photo photo = photoItem.getItem();
         photoImageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.0f));
         loadPhoto(photo);
         updateTitle(photo);
         updateTranslations(photo);
         tag.setVisibility(photo.getPhotoTagsCount() > 0 || !photo.getPhotoTags()
               .isEmpty() ? View.VISIBLE : View.GONE);
      }

      photoImageView.setOnClickListener(v -> {
         openFullscreen();
      });
   }

   private void updateTitle(Photo photo) {
      if (!TextUtils.isEmpty(photo.getTitle())) {
         title.setVisibility(View.VISIBLE);
         title.setText(photo.getTitle());
      } else {
         title.setVisibility(View.GONE);
      }
   }

   private void updateTranslations(Photo photo) {
      // TODO Extract UI translation manipulating logic to common place
      if (!appSessionHolder.get().isPresent()) {
         hideTranslations();
         return;
      }
      boolean own = photo.getOwner().getId() == appSessionHolder.get().get().user().getId();
      boolean emptyText = TextUtils.isEmpty(photo.getTitle());
      boolean ownLanguage = LocaleHelper.isOwnLanguage(appSessionHolder, photo.getLanguage());
      boolean emptyLanguage = TextUtils.isEmpty(photo.getLanguage());

      if (own || emptyText || ownLanguage || emptyLanguage) {
         hideTranslations();
      } else {
         boolean alreadyTranslated = photo.isTranslated();
         if (alreadyTranslated) {
            translateButton.setVisibility(View.GONE);
            viewWithTranslation.showTranslation(photo.getTranslation(), photo.getLanguage());
         } else {
            translateButton.setVisibility(View.VISIBLE);
            viewWithTranslation.hide();
         }
      }
   }

   private void hideTranslations() {
      translateButton.setVisibility(View.GONE);
      viewWithTranslation.hide();
   }

   @OnClick(R.id.translate)
   public void translate() {
      translateButton.setVisibility(View.GONE);
      viewWithTranslation.showProgress();
      cellDelegate.onTranslateItem(getModelObject().getItem());
   }

   private void loadPhoto(Photo photoObj) {
      int thumbSize = itemView.getResources().getDimensionPixelSize(R.dimen.photo_thumb_size);
      photoImageView.setController(GraphicUtils.provideFrescoResizingController(
            Uri.parse(ImageUtils.getParametrizedUrl(photoObj.getImagePath(), thumbSize, thumbSize)),
            photoImageView.getController()));
   }

   @OnClick(R.id.tag)
   void onTagClick() {
      openFullscreen();
   }

   private void openFullscreen() {
      List<BaseMediaEntity> items = new ArrayList<>();
      items.add(new PhotoMediaEntity(getModelObject().getItem()));

      NavigationConfig config = NavigationConfigBuilder.forActivity()
            .manualOrientationActivity(true)
            .data(TripImagesFullscreenArgs.builder()
                  .mediaEntityList(items)
                  .build())
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build();
      router.moveTo(TripImagesFullscreenFragment.class, config);
      analyticsInteractor.analyticsActionPipe().send(ViewFeedEntityAction.view(getModelObject().getType(),
            getModelObject().getItem().getUid()));
   }

   @Override
   protected void onMore() {
      showMoreDialog(R.menu.menu_feed_entity_edit, R.string.photo_delete, R.string.photo_delete_caption);
   }

   @Override
   protected void onDelete() {
      super.onDelete();
      cellDelegate.onDeletePhoto(getModelObject().getItem());
   }

   @Override
   protected void onEdit() {
      cellDelegate.onEditPhoto(getModelObject().getItem());
   }
}
