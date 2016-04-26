package com.worldventures.dreamtrips.modules.feed.presenter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
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

    @Inject
    SnappyRepository db;

    @Inject
    @ForApplication
    Context context;

    @Inject
    Router router;

    @Inject
    SessionHolder<UserSession> appSessionHolder;

    private View view;

    private Observable<Void> notificationObservable;

    private List<PhotoGalleryModel> selectedPhotos;

    private long syncTimestampLast = Long.MAX_VALUE;

    public void takeView(View view) {
        checkView(view);
        this.view = view;

        notificationObservable = view.notificationObservable();
        selectedPhotos = new ArrayList<>(MAX_SELECTION_SIZE);
    }

    public void preloadSuggestedPhotos(@Nullable PhotoGalleryModel model) {
        syncTimestampLast = getLastSyncOrDefault(model);

        view.bind(getSuggestionObservable(syncTimestampLast, true))
                .subscribe(photoGalleryModels -> {
                    view.appendPhotoSuggestions(photoGalleryModels);
                }, throwable -> {
                    Timber.e(throwable, "Cannot prefetch suggestions");
                });
    }

    public void subscribeNewPhotoNotifications() {
        view.bind(notificationObservable
                .concatMap(aVoid -> {
                    long startTimestamp = getStartTimestampOrDefault(view.firstElement());
                    return getSuggestionObservable(startTimestamp, false);
                }))
                .subscribe(photoGalleryModels -> {
                    Timber.d("New photos got");
                    view.pushForward(photoGalleryModels);
                }, throwable -> {
                    Timber.e(throwable, "Cannot fetch new suggestion items");
                });
    }

    public void sync() {
        view.setUser(appSessionHolder.get().get().getUser());
        setSuggestionTitle();

        subscribeNewPhotoNotifications();
    }

    public long lastSyncTime() {
        return syncTimestampLast;
    }

    public void removeSuggestedPhotos() {
        resetListState();

        db.saveLastSuggestedPhotosSyncTime(System.currentTimeMillis());
        resetSyncTimestamp();
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

        setSuggestionTitle();
    }

    public void openProfile() {
        router.moveTo(Route.ACCOUNT_PROFILE, NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new UserBundle(appSessionHolder.get().get().getUser()))
                .build());
    }

    @NonNull
    public List<PhotoGalleryModel> selectedPhotos() {
        return new ArrayList<>(selectedPhotos);
    }

    @NonNull
    private Observable<List<PhotoGalleryModel>> getSuggestionObservable(long toTimestamp, boolean reverse) {
        return Observable.create(new Observable.OnSubscribe<PhotoGalleryModel>() {
            @Override
            public void call(Subscriber<? super PhotoGalleryModel> subscriber) {
                Cursor cursor = null;
                String[] projectionPhotos = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};
                //
                try {
                    cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projectionPhotos,
                            selection(reverse),
                            new String[]{String.valueOf(toTimestamp)},
                            MediaStore.Images.Media.DATE_TAKEN + " DESC LIMIT " + SUGGESTION_ITEM_CHUNK);

                    int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(dataColumn);
                        long dateTaken = cursor.getLong(dateColumn);

                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(new PhotoGalleryModel(path, dateTaken));
                        }
                    }

                    if (!subscriber.isUnsubscribed()) {
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
        }).toList().compose(new IoToMainComposer<>());
    }

    @NonNull
    private String selection(boolean reverse) {
        return MediaStore.Images.Media.DATE_TAKEN +
                (reverse ? " < " : " > ") +
                " ?";
    }

    private void checkView(View view) {
        if (this.view != null) {
            if (this.view != view) {
                throw new AssertionError("Cannot take another view");
            }
        }
    }

    private void setSuggestionTitle() {
        view.setSuggestionTitle(selectedPhotos.size());
    }

    private long getLastSyncOrDefault(@Nullable PhotoGalleryModel model) {
        return model == null ? Long.MAX_VALUE : model.getDateTaken();
    }

    private long getStartTimestampOrDefault(@Nullable PhotoGalleryModel model) {
        return model == null ? Long.MIN_VALUE : model.getDateTaken();
    }

    private void resetListState() {
        Queryable.from(selectedPhotos).forEachR(model -> model.setChecked(false));
        selectedPhotos.clear();

        view.clearListState();
    }

    private void resetSyncTimestamp() {
        syncTimestampLast = Long.MAX_VALUE;
    }

    public interface View {
        void appendPhotoSuggestions(List<PhotoGalleryModel> items);

        void pushForward(List<PhotoGalleryModel> items);

        void clearListState();

        void setUser(User user);

        void setSuggestionTitle(int sizeOfSelectedPhotos);

        void showMaxSelectionMessage();

        @Nullable
        PhotoGalleryModel firstElement();

        Observable<Void> notificationObservable();

        <T> Observable<T> bind(Observable<T> observable);
    }
}