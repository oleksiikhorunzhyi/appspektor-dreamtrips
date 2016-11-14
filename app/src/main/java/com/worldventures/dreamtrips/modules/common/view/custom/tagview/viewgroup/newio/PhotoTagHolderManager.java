package com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio;

import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.TagSuggestionActionListener;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.List;

import rx.functions.Action1;

public class PhotoTagHolderManager {

   PhotoTagHolder photoTagHolder;
   User account;
   private User photoOwner;

   private boolean creationEnabled;
   private GestureDetector gestureDetector;
   private FriendRequestProxy friendRequestProxy;

   private Action1<PhotoTag> tagCreatedListener;
   private Action1<PhotoTag> tagDeletedListener;

   public PhotoTagHolderManager(PhotoTagHolder photoTagHolder, User account, User photoOwner) {
      this.photoTagHolder = photoTagHolder;
      this.account = account;
      this.photoOwner = photoOwner;

      gestureDetector = createGestureDetector(photoTagHolder);
   }

   public void show(SimpleDraweeView imageView) {
      photoTagHolder.show(this, imageView);
   }

   public void hide() {
      photoTagHolder.hide();
   }

   public void addExistsTagViews(List<PhotoTag> photoTags) {
      for (PhotoTag photoTag : photoTags) {
         boolean isAccountOnPhoto = photoTag.getUser().getId() == account.getId();
         boolean isAccountOwner = creationEnabled || photoOwner.getId() == account.getId();
         photoTagHolder.addExistsTagView(photoTag, isAccountOnPhoto || isAccountOwner);
      }
   }

   public void addSuggestionTagViews(List<PhotoTag> photoTags, TagSuggestionActionListener listener) {
      Queryable.from(photoTags).forEachR(arg -> photoTagHolder.addSuggestionTagView(arg, listener));
   }

   public void addCreationTagBasedOnSuggestion(PhotoTag suggestion) {
      photoTagHolder.addCreationTagViewBasedOnSuggestion(suggestion);
   }

   public void creationTagEnabled(boolean creationEnabled) {
      this.creationEnabled = creationEnabled;
      if (creationEnabled) {
         photoTagHolder.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
         });
      } else {
         photoTagHolder.setOnTouchListener(null);
      }
   }

   public void setFriendRequestProxy(FriendRequestProxy friendRequestProxy) {
      this.friendRequestProxy = friendRequestProxy;
   }

   public void setTagCreatedListener(Action1<PhotoTag> tagCreatedListener) {
      this.tagCreatedListener = tagCreatedListener;
   }

   public void setTagDeletedListener(Action1<PhotoTag> tagDeletedListener) {
      this.tagDeletedListener = tagDeletedListener;
   }

   void requestFriends(String query, int page) {
      if (friendRequestProxy != null) {
         friendRequestProxy.requestFriends(query, page, users -> {
            if (photoTagHolder.getCreationTagView() != null) photoTagHolder.getCreationTagView().setUserFriends(users);
         });
      }
   }

   void notifyTagCreated(PhotoTag photoTag) {
      tagCreatedListener.call(photoTag);
   }

   void notifyTagDeleted(PhotoTag photoTag) {
      tagDeletedListener.call(photoTag);
   }

   @NonNull
   private GestureDetector createGestureDetector(final PhotoTagHolder photoTagHolder) {
      return new GestureDetector(photoTagHolder.getContext(), new GestureDetector.SimpleOnGestureListener() {
         @Override
         public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isEnableToTag(e)) photoTagHolder.addCreationTagView(e.getX(), e.getY());
            return isEnableToTag(e);
         }
      });
   }

   private boolean isEnableToTag(MotionEvent e) {
      return creationEnabled && photoTagHolder.getImageBounds().contains(e.getX(), e.getY());
   }

   public interface FriendRequestProxy {

      void requestFriends(String query, int page, Action1<List<User>> friends);
   }

}