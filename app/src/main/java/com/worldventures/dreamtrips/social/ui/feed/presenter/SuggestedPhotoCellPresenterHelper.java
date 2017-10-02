package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.modules.picker.command.GetPhotosFromGalleryCommand;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

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

   private MediaPickerInteractor mediaPickerInteractor;
   private SessionHolder appSessionHolder;

   @State ArrayList<PhotoPickerModel> suggestionItems;
   @State ArrayList<String> selectedPhotosPaths;
   @State long syncTimestampLast = DEFAULT_START_SYNC_TIMESTAMP;

   private View view;
   private Observable.Transformer<List<PhotoPickerModel>, List<PhotoPickerModel>> stopper;

   public SuggestedPhotoCellPresenterHelper(SessionHolder appSessionHolder,
         MediaPickerInteractor mediaPickerInteractor) {
      this.appSessionHolder = appSessionHolder;
      this.mediaPickerInteractor = mediaPickerInteractor;
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

   public void dropView() {
      view = null;
      suggestionItems = null;
      selectedPhotosPaths = null;
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

   public MediaPickerAttachment getSelectedAttachments() {
      if (selectedPhotosPaths.isEmpty()) return null;

      List<PhotoPickerModel> photoPickerModels = Queryable.from(selectedPhotosPaths)
            .map(path -> {
               PhotoPickerModel model = new PhotoPickerModel(path, 0);
               model.setSource(MediaPickerAttachment.Source.GALLERY);
               return model;
            }).toList();
      return new MediaPickerAttachment(photoPickerModels, -1);
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
      return mediaPickerInteractor.getPhotosFromGalleryPipe()
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