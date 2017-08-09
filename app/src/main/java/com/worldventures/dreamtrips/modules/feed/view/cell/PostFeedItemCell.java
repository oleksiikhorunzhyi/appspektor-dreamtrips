package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.service.ConfigurationInteractor;
import com.worldventures.dreamtrips.modules.common.view.custom.HashtagTextView;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.HashtagFeedBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;
import com.worldventures.dreamtrips.modules.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.util.FeedCellListWidthProvider;
import com.worldventures.dreamtrips.modules.feed.view.custom.TranslateView;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageView;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.view.ImageUtils;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesFullscreenArgs;
import com.worldventures.dreamtrips.modules.video.view.custom.VideoView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import rx.Subscription;

@Layout(R.layout.adapter_item_feed_post_event)
public class PostFeedItemCell extends FeedItemDetailsCell<PostFeedItem, BaseFeedCell.FeedCellDelegate<PostFeedItem>>
      implements Focusable {

   @InjectView(R.id.post) HashtagTextView post;
   @InjectView(R.id.card_view_wrapper) View cardViewWrapper;
   @InjectView(R.id.translate_view) TranslateView viewWithTranslation;
   @InjectView(R.id.translate) View translateButton;
   @Optional @InjectView(R.id.collage) CollageView collageView;
   @Optional @InjectView(R.id.tag) ImageView tag;
   @Optional @InjectView(R.id.video_windowed_container) ViewGroup videoWindowedContainer;
   private ViewGroup videoFullscreenContainer;
   @Optional @InjectView(R.id.videoAttachment) VideoView videoView;

   @Inject ActiveFeedRouteInteractor activeFeedRouteInteractor;
   @Inject ConfigurationInteractor configurationInteractor;
   @Inject Activity activity;
   private FeedCellListWidthProvider feedCellListWidthProvider;

   private Subscription configurationSubscription;
   private Route activeCellRoute;
   private boolean mute = false;

   public PostFeedItemCell(View view) {
      super(view);
      feedCellListWidthProvider = new FeedCellListWidthProvider(view.getContext());
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      refreshUi();
      if (configurationSubscription == null || configurationSubscription.isUnsubscribed()) {
         configurationSubscription = configurationInteractor.configurationActionPipe()
               .observeSuccess().subscribe(configurationCommand -> {
                  // happens when there is another feed opened on the top of this one
                  if (getCurrentRoute() != activeCellRoute) return;
                  List<FeedEntityHolder> attachments = getModelObject().getItem().getAttachments();
                  if (attachments != null && attachments.size() > 0) {
                     if (attachments.get(0).getItem() instanceof Photo) {
                        processPhotos();
                     }
                     refreshVideoIfExists();
                  }
               });
      }
   }

   public void setMute(boolean mute) {
      this.mute = mute;
   }

   private void refreshUi() {
      processAttachments(getModelObject().getItem().getAttachments());
      processPostText(getModelObject().getItem());
      processTranslations();
   }

   private void processTranslations() {
      PostFeedItem postFeedItem = getModelObject();
      TextualPost textualPost = postFeedItem.getItem();

      if (!sessionHolder.get().isPresent()) {
         hideTranslationUi();
         return;
      }

      boolean ownPost = textualPost.getOwner().getId() == sessionHolder.get().get().getUser().getId();
      boolean emptyPostText = TextUtils.isEmpty(textualPost.getDescription());
      boolean ownLanguage = LocaleHelper.isOwnLanguage(sessionHolder, textualPost.getLanguage());
      boolean emptyPostLanguage = TextUtils.isEmpty(textualPost.getLanguage());

      if (!ownPost && !emptyPostText && !ownLanguage && !emptyPostLanguage) {
         boolean alreadyTranslated = textualPost.isTranslated();
         if (alreadyTranslated) {
            translateButton.setVisibility(View.GONE);
            viewWithTranslation.showTranslation(textualPost.getTranslation(), textualPost.getLanguage());
         } else {
            translateButton.setVisibility(View.VISIBLE);
            viewWithTranslation.hide();
         }
      } else {
         hideTranslationUi();
      }
   }

   private void hideTranslationUi() {
      translateButton.setVisibility(View.GONE);
      viewWithTranslation.hide();
   }

   private void processPostText(TextualPost textualPost) {
      if (!TextUtils.isEmpty(textualPost.getDescription())) {
         post.setVisibility(View.VISIBLE);
         post.setText(String.format("%s", textualPost.getDescription()));
         post.setHashtagClickListener(this::openHashtagFeeds);

         List<String> clickableHashtags = Queryable.from(textualPost.getHashtags())
               .map(element -> element != null ? element.getName() : null)
               .toList();

         List<String> hightlightedHashtags = getModelObject().getMetaData() != null && getModelObject().getMetaData()
               .getHashtags() != null ? Queryable.from(getModelObject().getMetaData().getHashtags())
               .map(element -> element != null ? element.getName() : null)
               .toList() : new ArrayList<>();

         post.highlightHashtags(clickableHashtags, hightlightedHashtags);
      } else {
         post.setVisibility(View.GONE);
      }
   }

   private void processAttachments(List<FeedEntityHolder> attachments) {
      if (attachments != null && !attachments.isEmpty()) {
         if (attachments.get(0).getItem() instanceof Photo) {
            videoView.hide();
            processPhotos();
         } else if (attachments.get(0).getItem() instanceof Video) {
            clearImages();
            processVideo((Video) attachments.get(0).getItem());
         }
      } else {
         videoView.hide();
         clearImages();
      }
      processTags(attachments);
   }

   private void refreshVideoIfExists() {
      List<FeedEntityHolder> attachments = getModelObject().getItem().getAttachments();
      if (attachments.get(0).getItem() instanceof Video) {
         videoView.resizeView(getCellListWidth());
      }
   }

   protected void clearImages() {
      collageView.clear();
   }

   protected void processPhotos() {
      collageView.setItemClickListener(new CollageView.ItemClickListener() {
         @Override
         public void itemClicked(int position) {
            openFullscreenPhotoList(position);
         }

         @Override
         public void moreClicked() {
            openFeedItemDetails();
         }
      });
      collageView.setItems(attachmentsToCollageItems(getModelObject().getItem().getAttachments()), getCellListWidth());
   }

   private int getCellListWidth() {
      return feedCellListWidthProvider.getFeedCellWidth(activeCellRoute);
   }

   private List<CollageItem> attachmentsToCollageItems(List<FeedEntityHolder> attachments) {
      int thumbSize = itemView.getResources().getDimensionPixelSize(R.dimen.photo_thumb_size);
      return Queryable.from(attachments).map(element -> (Photo) element.getItem()).map(photo -> {
         return new CollageItem(ImageUtils.getParametrizedUrl(photo.getImagePath(), thumbSize, thumbSize),
               ImageUtils.getParametrizedUrl(photo.getImagePath(), itemView.getWidth(), itemView.getHeight()),
               photo.getWidth(), photo.getHeight());
      }).toList();
   }

   private void processTags(List<FeedEntityHolder> attachments) {
      if (tag != null) tag.setVisibility(hasTags(attachments) ? View.VISIBLE : View.GONE);
   }

   private boolean hasTags(List<FeedEntityHolder> attachments) {
      return Queryable.from(attachments)
            .count(attachment -> attachment.getType() == FeedEntityHolder.Type.PHOTO && ((Photo) attachment.getItem()).getPhotoTagsCount() > 0) > 0;
   }

   private void openFullscreenPhotoList(int position) {
      router.moveTo(Route.TRIP_IMAGES_FULLSCREEN, NavigationConfigBuilder.forActivity()
            .data(TripImagesFullscreenArgs.builder()
                  .currentItemPosition(position)
                  .mediaEntityList(Queryable.from(getModelObject().getItem().getAttachments())
                        .filter(element -> element.getType() == FeedEntityHolder.Type.PHOTO)
                        .map(element -> {
                           Photo photo = (Photo) element.getItem();
                           photo.setOwner(getModelObject().getItem().getOwner());
                           return (BaseMediaEntity) new PhotoMediaEntity(photo);
                        })
                        .toList())
                  .build())
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build());
   }

   private void openFeedItemDetails() {
      router.moveTo(Route.FEED_ITEM_DETAILS, NavigationConfigBuilder.forActivity()
            .manualOrientationActivity(true)
            .data(new FeedItemDetailsBundle.Builder().feedItem(getModelObject()).showAdditionalInfo(true).build())
            .build());
   }

   private void openHashtagFeeds(@NotNull String hashtag) {
      router.moveTo(Route.FEED_HASHTAG, NavigationConfigBuilder.forActivity()
            .data(new HashtagFeedBundle(hashtag))
            .toolbarConfig(ToolbarConfig.Builder.create().visible(true).build())
            .build());
   }

   private void processVideo(Video video) {
      videoView.setVideo(video, true);
      videoView.setMute(mute);
      videoView.enableFullscreen(videoFullscreenContainer, videoWindowedContainer);
   }

   @Override
   public void onFocused() {
      videoView.play();
   }

   @Override
   public boolean canFocus() {
      return videoView.getVisibility() == View.VISIBLE;
   }

   @OnClick(R.id.translate)
   public void translate() {
      translateButton.setVisibility(View.GONE);
      viewWithTranslation.showProgress();
      cellDelegate.onTranslateItem(getModelObject().getItem());
   }

   @Override
   protected void onDelete() {
      super.onDelete();
      cellDelegate.onDeleteTextualPost(getModelObject().getItem());
   }

   @Override
   protected void onEdit() {
      cellDelegate.onEditTextualPost(getModelObject().getItem());
   }

   @Override
   protected void onMore() {
      showMoreDialog(R.menu.menu_feed_entity_edit, R.string.post_delete, R.string.post_delete_caption);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      activeCellRoute = getCurrentRoute();
      videoFullscreenContainer = ButterKnife.findById(activity, R.id.container_details_floating);
   }

   private Route getCurrentRoute() {
      return activeFeedRouteInteractor.activeFeedRouteCommandActionPipe()
            .createObservableResult(ActiveFeedRouteCommand.fetch()).toBlocking().single().getResult();
   }

   @Override
   public void clearResources() {
      super.clearResources();
      if (videoView != null) {
         videoView.clear();
      }
      if (configurationSubscription != null && configurationSubscription.isUnsubscribed()) {
         configurationSubscription.unsubscribe();
      }
   }
}
