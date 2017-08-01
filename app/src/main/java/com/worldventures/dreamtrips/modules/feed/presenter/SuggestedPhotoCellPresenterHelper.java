package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetPhotosFromGalleryCommand;
import com.worldventures.dreamtrips.modules.media_picker.util.CapturedRowMediaHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import icepick.Icepick;
import icepick.State;
import io.techery.janet.Command;
import rx.Observable;
import timber.log.Timber;

public class SuggestedPhotoCellPresenterHelper {

   public static final int MAX_SELECTION_SIZE = 15;
   private static final int SUGGESTION_ITEM_CHUNK = 20;
   private static final long DEFAULT_START_SYNC_TIMESTAMP = Long.MAX_VALUE;

   private MediaInteractor mediaInteractor;
   private CapturedRowMediaHelper capturedRowMediaHelper;
   private SessionHolder<UserSession> appSessionHolder;

   @State ArrayList<PhotoPickerModel> suggestionItems;
   @State ArrayList<String> selectedPhotosPaths;
   @State long syncTimestampLast = DEFAULT_START_SYNC_TIMESTAMP;

   private View view;
   private Observable.Transformer<List<PhotoPickerModel>, List<PhotoPickerModel>> stopper;

   public SuggestedPhotoCellPresenterHelper(SessionHolder<UserSession> appSessionHolder,
         MediaInteractor mediaInteractor, CapturedRowMediaHelper capturedRowMediaHelper) {
      this.appSessionHolder = appSessionHolder;
      this.mediaInteractor = mediaInteractor;
      this.capturedRowMediaHelper = capturedRowMediaHelper;
   }

   public void takeView(View view, Observable.Transformer<List<PhotoPickerModel>, List<PhotoPickerModel>> stopper, Bundle bundle) {
      checkView(view);
      this.view = view;
      this.stopper = stopper;

      restoreInstanceState(bundle);

      if (suggestionItems == null) {
         suggestionItems = new ArrayList<>(SUGGESTION_ITEM_CHUNK);
      }
      if (selectedPhotosPaths == null) {
         selectedPhotosPaths = new ArrayList<>(MAX_SELECTION_SIZE);
      }

      if (suggestionItems.isEmpty()) {
         preloadSuggestionPhotos(null);
      } else {
         view.appendPhotoSuggestions(suggestionItems);
      }
   }

   public void preloadSuggestionPhotos(@Nullable PhotoPickerModel model) {
      syncTimestampLast = getLastSyncOrDefault(model);

      getSuggestionObservable(syncTimestampLast)
            .compose(stopper)
            .subscribe(photoGalleryModels -> {
               suggestionItems.addAll(photoGalleryModels);
               view.appendPhotoSuggestions(photoGalleryModels);
            }, throwable -> Timber.e(throwable, "Cannot prefetch suggestions"));
   }

   public void subscribeNewPhotoNotifications(Observable<Void> notificationObservable) {
      notificationObservable.concatMap(aVoid -> getSuggestionObservable(DEFAULT_START_SYNC_TIMESTAMP))
            .compose(stopper)
            .subscribe(photoGalleryModels -> {
               clearCache();
               resetSyncTimestamp();
               sync();
               suggestionItems.addAll(photoGalleryModels);
               view.replacePhotoSuggestions(photoGalleryModels);
            }, throwable -> Timber.e(throwable, "Cannot fetch new suggestion items"));
   }

   public void sync() {
      Optional<UserSession> userSessionOptional = appSessionHolder.get();
      if (userSessionOptional.isPresent()) {
         view.setUser(userSessionOptional.get().getUser());
      }
      setSuggestionTitle();
   }

   public long lastSyncTime() {
      return syncTimestampLast;
   }

   public void reset() {
      clearCacheAndUpdate();
      resetSyncTimestamp();
   }

   public void selectPhoto(PhotoPickerModel model) {
      int selectedSize = selectedPhotosPaths.size();
      boolean isChecked = !model.isChecked();

      if (isChecked) {
         if (selectedSize == MAX_SELECTION_SIZE) {
            view.showMaxSelectionMessage();
            return;
         }
         selectedPhotosPaths.add(model.getAbsolutePath());
      } else {
         selectedPhotosPaths.remove(model.getAbsolutePath());
      }
      model.setChecked(isChecked);

      setSuggestionTitle();
   }

   public Observable<MediaAttachment> mediaAttachmentObservable() {
      return Observable.from(selectedPhotosPaths)
            .map(element -> capturedRowMediaHelper.processPhotoModel(element))
            .map(photoGalleryModel -> new MediaAttachment(photoGalleryModel, MediaAttachment.Source.GALLERY));
   }

   void saveInstanceState(Bundle bundle) {
      Icepick.saveInstanceState(this, bundle);
      // can happen if it hasn't taken view yet
      if (view != null) view.saveInstanceState(bundle);
   }

   private void restoreInstanceState(Bundle bundle) {
      Icepick.restoreInstanceState(this, bundle);
      view.restoreInstanceState(bundle);
   }

   @NonNull
   private Observable<List<PhotoPickerModel>> getSuggestionObservable(long toTimestamp) {
      return mediaInteractor.getPhotosFromGalleryPipe()
            .createObservableResult(new GetPhotosFromGalleryCommand(SUGGESTION_ITEM_CHUNK, new Date(toTimestamp)))
            .map(Command::getResult)
            .compose(new IoToMainComposer<>());
   }

   private void checkView(View view) {
      if (this.view != null) {
         if (this.view != view) {
            throw new AssertionError("Cannot take another view");
         }
      }
   }

   private void setSuggestionTitle() {
      view.setSuggestionTitle(selectedPhotosPaths.size());
   }

   private long getLastSyncOrDefault(@Nullable PhotoPickerModel model) {
      return model == null ? DEFAULT_START_SYNC_TIMESTAMP : model.getDateTaken();
   }

   private void clearCacheAndUpdate() {
      clearCache();
      view.notifyListChange();
   }

   private void clearCache() {
      selectedPhotosPaths.clear();
      suggestionItems.clear();
   }

   private void resetSyncTimestamp() {
      syncTimestampLast = Long.MAX_VALUE;
   }

   public interface View {

      void appendPhotoSuggestions(List<PhotoPickerModel> items);

      void replacePhotoSuggestions(List<PhotoPickerModel> items);

      void notifyListChange();

      void setUser(User user);

      void setSuggestionTitle(int sizeOfSelectedPhotos);

      void showMaxSelectionMessage();

      void saveInstanceState(@Nullable Bundle bundle);

      void restoreInstanceState(@Nullable Bundle bundle);
   }
}