package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.utils.ImageUtils;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.bundle.HashtagFeedBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedCellListWidthProvider;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.HashtagTextView;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.TranslateView;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.collage.CollageItem;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.collage.CollageView;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedItemDetailsFragment;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.HashtagFeedFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesFullscreenArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.TripImagesFullscreenFragment;
import com.worldventures.dreamtrips.social.ui.video.view.custom.DTVideoConfig;
import com.worldventures.dreamtrips.social.ui.video.view.custom.DTVideoViewImpl;
import com.worldventures.dreamtrips.social.ui.video.view.custom.VideoPlayerHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.adapter_item_feed_post_event)
public class PostFeedItemCell extends FeedItemDetailsCell<PostFeedItem, BaseFeedCell.FeedCellDelegate<PostFeedItem>>
      implements Focusable {

   @InjectView(R.id.post) HashtagTextView post;
   @InjectView(R.id.card_view_wrapper) View cardViewWrapper;
   @InjectView(R.id.translate_view) TranslateView viewWithTranslation;
   @InjectView(R.id.translate) View translateButton;
   @Optional @InjectView(R.id.collage) CollageView collageView;
   @Optional @InjectView(R.id.tag) ImageView tag;
   @Optional @InjectView(R.id.videoAttachment) DTVideoViewImpl dtVideoView;

   @Inject ActiveFeedRouteInteractor activeFeedRouteInteractor;
   @Inject VideoPlayerHolder videoPlayerHolder;

   private FeedCellListWidthProvider feedCellListWidthProvider;

   private FeedCellListWidthProvider.FeedType activeFeedType;
   private boolean displayingInList;

   public PostFeedItemCell(View view) {
      super(view);
      feedCellListWidthProvider = new FeedCellListWidthProvider(view.getContext());
   }

   public void setDisplayingInList(boolean displayingInList) {
      this.displayingInList = displayingInList;
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      refreshUi();
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

      boolean ownPost = textualPost.getOwner().getId() == sessionHolder.get().get().user().getId();
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
            dtVideoView.setVisibility(View.GONE);
            processPhotos();
         } else if (attachments.get(0).getItem() instanceof Video) {
            dtVideoView.setVisibility(View.VISIBLE);
            dtVideoView.setThumbnailAction(this::playVideoIfNeeded);
            clearImages();
            Video video = (Video) attachments.get(0).getItem();
            updateVideoHeight(video);
            dtVideoView.setThumbnail(video.getThumbnail());
         }
      } else {
         dtVideoView.setVisibility(View.GONE);
         clearImages();
      }
      processTags(attachments);
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
      return feedCellListWidthProvider.getFeedCellWidth(activeFeedType);
   }

   private List<CollageItem> attachmentsToCollageItems(List<FeedEntityHolder> attachments) {
      int thumbSize = itemView.getResources().getDimensionPixelSize(R.dimen.photo_thumb_size);
      return Queryable.from(attachments).map(element -> (Photo) element.getItem())
            .map(photo -> new CollageItem(ImageUtils.getParametrizedUrl(photo.getImagePath(), thumbSize, thumbSize),
                  ImageUtils.getParametrizedUrl(photo.getImagePath(), itemView.getWidth(), itemView.getHeight()),
                  photo.getWidth(), photo.getHeight())).toList();
   }

   private void processTags(List<FeedEntityHolder> attachments) {
      if (tag != null) {
         tag.setVisibility(hasTags(attachments) ? View.VISIBLE : View.GONE);
      }
   }

   private boolean hasTags(List<FeedEntityHolder> attachments) {
      return Queryable.from(attachments)
            .count(attachment -> attachment.getType() == FeedEntityHolder.Type.PHOTO && ((Photo) attachment.getItem()).getPhotoTagsCount() > 0) > 0;
   }

   private void openFullscreenPhotoList(int position) {
      router.moveTo(TripImagesFullscreenFragment.class, NavigationConfigBuilder.forActivity()
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
      router.moveTo(FeedItemDetailsFragment.class, NavigationConfigBuilder.forActivity()
            .data(new FeedItemDetailsBundle.Builder().feedItem(getModelObject()).showAdditionalInfo(true).build())
            .build());
   }

   private void openHashtagFeeds(@NotNull String hashtag) {
      router.moveTo(HashtagFeedFragment.class, NavigationConfigBuilder.forActivity()
            .data(new HashtagFeedBundle(hashtag))
            .toolbarConfig(ToolbarConfig.Builder.create().visible(true).build())
            .build());
   }

   @Override
   public boolean canFocus() {
      return dtVideoView.getVisibility() == View.VISIBLE;
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
      activeFeedType = getCurrentRoute();
   }

   private FeedCellListWidthProvider.FeedType getCurrentRoute() {
      return activeFeedRouteInteractor.activeFeedRouteCommandActionPipe()
            .createObservableResult(ActiveFeedRouteCommand.fetch()).toBlocking().single().getResult();
   }


   private void updateVideoHeight(Video video) {
      int height = (int) (getCellListWidth() / video.getAspectRatio());
      ViewGroup.LayoutParams params = dtVideoView.getLayoutParams();
      params.height = height;
      dtVideoView.setLayoutParams(params);
   }

   @Override
   public void onFocused() {
      playVideoIfNeeded();
   }

   void playVideoIfNeeded() {
      List<FeedEntityHolder> attachments = getModelObject().getItem().getAttachments();
      if (attachments.get(0).getItem() instanceof Video) {
         Video video = (Video) getModelObject().getItem().getAttachments().get(0).getItem();
         if (videoPlayerHolder.getCurrentVideoConfig() != null
               && video.getUid().equals(videoPlayerHolder.getCurrentVideoConfig().getUid())) {
            if (videoPlayerHolder.inFullscreen()) {
               videoPlayerHolder.switchFromFullscreen(dtVideoView, displayingInList
                     || videoPlayerHolder.getCurrentVideoConfig().getMute());
            } else {
               videoPlayerHolder.reattachVideoView(dtVideoView, displayingInList);
            }
         } else {
            dtVideoView.playVideo(new DTVideoConfig(video.getUid(), displayingInList, video.getQualities(), 0));
         }
      }
   }

   @Override
   public void onUnfocused() {
      List<FeedEntityHolder> attachments = getModelObject().getItem().getAttachments();
      if (attachments.size() > 0 && attachments.get(0).getItem() instanceof Video) {
         dtVideoView.pauseVideo();
         dtVideoView.detachPlayer();
         dtVideoView.showThumbnail();
      }
   }

   @Override
   public void clearResources() {
      super.clearResources();
      if (dtVideoView != null) {
         dtVideoView.detachPlayer();
         dtVideoView.showThumbnail();
      }
   }
}
