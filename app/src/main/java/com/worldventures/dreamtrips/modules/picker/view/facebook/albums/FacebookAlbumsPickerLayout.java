package com.worldventures.dreamtrips.modules.picker.view.facebook.albums;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.facebook.exception.FacebookAccessTokenException;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.dreamtrips.modules.picker.model.FacebookAlbumPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.presenter.facebook.albums.FacebookAlbumsPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.util.MediaPickerStep;
import com.worldventures.dreamtrips.modules.picker.view.facebook.FacebookMediaPickerLayout;
import com.worldventures.dreamtrips.modules.picker.view.facebook.photos.FacebookPhotosPickerLayout;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class FacebookAlbumsPickerLayout extends FacebookMediaPickerLayout<FacebookAlbumsPickerPresenter, FacebookAlbumPickerViewModel> implements FacebookAlbumPickerView {

   @Inject FacebookAlbumsPickerPresenter presenter;

   public FacebookAlbumsPickerLayout(@NonNull Context context) {
      super(context);
   }

   public FacebookAlbumsPickerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public void handleItemClick(int position) {
      if (getAdapter().getItemViewType(position) == R.layout.picker_adapter_item_album_facebook) {
         final FacebookAlbumPickerViewModel model = getAdapter().getItem(position);
         if (getOnNextClickListener() != null) {
            final Bundle args = new Bundle();
            args.putString(FacebookPhotosPickerLayout.FB_ALBUM_ID, model.getId());
            getOnNextClickListener().onNextClick(args);
         }
      }
   }

   @Override
   public void clearItems() {
      getAdapter().clear();
   }

   @Override
   public FacebookAlbumsPickerPresenter getPresenter() {
      return presenter;
   }

   @Override
   public MediaPickerStep getStep() {
      return MediaPickerStep.FB_ALBUMS;
   }

   @Override
   public String getFailedActionText() {
      return getContext().getString(R.string.media_picker_fb_albums_actions);
   }

   @Override
   public Observable<List<FacebookAlbumPickerViewModel>> attachedItems() {
      throw new UnsupportedOperationException("FacebookAlbumsPickerLayout doesn't support attachments");
   }

   @Override
   public void showError(Object o, Throwable throwable) {
      final Throwable causeThrowable = throwable.getCause();
      if(!(causeThrowable instanceof FacebookAccessTokenException)) {
         super.showError(o, throwable);
      }
   }

   @Override
   public OperationView<GetAlbumsCommand> provideOperationGetAlbums() {
      return new ComposableOperationView<>(this, this);
   }

   @Override
   public void goBack() {
      if (getOnBackClickListener() != null) {
         getOnBackClickListener().onBackClick();
      }
   }
}
