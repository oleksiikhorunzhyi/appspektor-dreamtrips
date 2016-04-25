package com.worldventures.dreamtrips.modules.feed.presenter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class SuggestedPhotoCellPresenter {
    public static final int MAX_SELECTION_SIZE = 15;

    private static final int SUGGESTION_ITEM_CHUNK = 20;

    private View view;

    private List<PhotoGalleryModel> selectedPhotos;

    @Inject
    SnappyRepository db;

    @Inject
    @ForApplication
    Context context;

    @Inject
    Router router;

    @Inject
    SessionHolder<UserSession> appSessionHolder;

    public void takeView(View view) {
        checkView(view);
        this.view = view;

        selectedPhotos = new ArrayList<>(MAX_SELECTION_SIZE);
    }

    public void preloadSuggestedPhotos(@Nullable PhotoGalleryModel lastVisibleModel) {
        view.bind(getSuggestionObservable(getStartDateFromOrDefault(lastVisibleModel))
                .map(cursor -> {
                    List<PhotoGalleryModel> photos = new ArrayList<>(cursor.getCount());

                    int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(dataColumn);
                        long dateTaken = cursor.getLong(dateColumn);

                        photos.add(new PhotoGalleryModel(path, dateTaken));
                    }

                    return photos;
                })
                .compose(new IoToMainComposer<>()))
                .subscribe(photoGalleryModels -> {
                    view.appendPhotoSuggestions(photoGalleryModels);
                }, throwable -> {
                    Timber.e(throwable, "Cannot prefetch suggestions");
                });
    }

    @NonNull
    private Observable<Cursor> getSuggestionObservable(long lastTimestamp) {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                Cursor cursor = null;
                String[] projectionPhotos = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
                //
                try {
                    cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projectionPhotos,
                            MediaStore.Images.Media.DATE_TAKEN + "<?",
                            new String[]{String.valueOf(lastTimestamp)},
                            MediaStore.Images.Media.DATE_TAKEN + " DESC LIMIT " + SUGGESTION_ITEM_CHUNK);

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(cursor);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    Timber.e(e, "Cannot fetch suggestions");

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });
    }

    public void fetchUser() {
        view.setUser(appSessionHolder.get().get().getUser());
    }

    public void removeSuggestedPhotos() {
        selectedPhotos.clear();
        db.saveLastSuggestedPhotosSyncTime(System.currentTimeMillis());
    }

    public void selectPhoto(PhotoGalleryModel model) {
        int selectedSize = selectedPhotos.size();
        boolean isChecked = !model.isChecked();

        if (isChecked) {
            if (selectedSize == MAX_SELECTION_SIZE) {
                view.showMaxSelectionMessage();
                return;
            }
            selectedPhotos.add(model);
        } else {
            selectedPhotos.remove(model);
        }
        model.setChecked(isChecked);

        view.setSuggestionTitle(selectedPhotos.size());
    }

    public boolean hasSelectedPhotos() {
        return !selectedPhotos.isEmpty();
    }

    public void openProfile() {
        router.moveTo(Route.ACCOUNT_PROFILE, NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new UserBundle(appSessionHolder.get().get().getUser()))
                .build());
    }

    @NonNull
    public List<PhotoGalleryModel> selectedPhotos() {
        return selectedPhotos;
    }

    private void checkView(View view) {
        if (this.view != null) {
            if (this.view != view) {
                throw new AssertionError("Cannot take another view");
            }
        }
    }

    private long getStartDateFromOrDefault(@Nullable PhotoGalleryModel lastVisibleModel) {
        return lastVisibleModel == null ? Long.MAX_VALUE : lastVisibleModel.getDateTaken();
    }

    public interface View {
        void appendPhotoSuggestions(List<PhotoGalleryModel> items);

        void setUser(User user);

        void setSuggestionTitle(int sizeOfSelectedPhotos);

        void showMaxSelectionMessage();

        <T> Observable<T> bind(Observable<T> observable);
    }
}