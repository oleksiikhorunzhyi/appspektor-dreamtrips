package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.feed.presenter.SuggestedPhotoCellPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.SuggestedPhotosDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.SuggestedPhotosListDecorator;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.subjects.PublishSubject;

@Layout(R.layout.adapter_item_suggested_photos)
public class SuggestedPhotosCell extends AbstractDelegateCell<MediaAttachment, SuggestedPhotosDelegate>
        implements CellDelegate<PhotoGalleryModel>, SuggestedPhotoCellPresenter.View {
    private static final int OFFSET = 5;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.suggestion_avatar)
    SmartAvatarView avatar;
    @InjectView(R.id.suggested_photos_user)
    TextView userName;
    @InjectView(R.id.suggested_photos_description)
    TextView description;
    @InjectView(R.id.suggested_photos)
    RecyclerView suggestedList;
    @InjectView(R.id.btn_attach)
    Button btnAttach;
    @InjectView(R.id.card_view_wrapper)
    CardView cardViewWrapper;

    @Inject
    SuggestedPhotoCellPresenter presenter;

    private BaseDelegateAdapter suggestionAdapter;
    private PublishSubject<Void> notificationSubject = PublishSubject.create();

    private ContentObserver contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if (!selfChange) {
                notificationSubject.onNext(null);
            }
        }
    };

    public SuggestedPhotosCell(View view) {
        super(view);
    }

    @Override
    public void afterInject() {
        super.afterInject();
        presenter.takeView(this);
    }

    @Override
    public void setCellDelegate(SuggestedPhotosDelegate cellDelegate) {
        super.setCellDelegate(cellDelegate);
        cellDelegate.onRegisterObserver(contentObserver);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (suggestionAdapter == null) {
            final LinearLayoutManager layoutManager =
                    new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            layoutManager.setAutoMeasureEnabled(true);

            RecyclerView.OnScrollListener preLoadScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int count = layoutManager.getItemCount();
                    int position = layoutManager.findLastVisibleItemPosition();

                    if (position >= count - OFFSET) {
                        if (count == 0) {
                            presenter.preloadSuggestedPhotos(null);
                            return;
                        }

                        PhotoGalleryModel model = (PhotoGalleryModel) suggestionAdapter.getItem(layoutManager.getItemCount() - 1);
                        if (model.getDateTaken() < presenter.lastSyncTime()) {
                            presenter.preloadSuggestedPhotos(model);
                        }
                    }
                }
            };

            suggestionAdapter = new BaseDelegateAdapter(itemView.getContext(), injectorProvider.get());
            suggestionAdapter.registerCell(PhotoGalleryModel.class, SuggestionPhotoCell.class);
            suggestionAdapter.registerDelegate(PhotoGalleryModel.class, this);

            suggestedList.setLayoutManager(layoutManager);
            suggestedList.setAdapter(suggestionAdapter);
            suggestedList.addItemDecoration(new SuggestedPhotosListDecorator());
            suggestedList.addOnScrollListener(preLoadScrollListener);

            presenter.preloadSuggestedPhotos(null);
        }

        //
        if (ViewUtils.isTablet(itemView.getContext())) {
            cardViewWrapper.setCardElevation(4);
            int m = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_small);
            ((ViewGroup.MarginLayoutParams) cardViewWrapper.getLayoutParams()).setMargins(m, m, m, m);
        } else {
            cardViewWrapper.setCardElevation(0);
        }

        presenter.sync();
    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.suggestion_cancel)
    void onCancel() {
        presenter.removeSuggestedPhotos();
        cellDelegate.onCancelClicked();
    }

    @OnClick(R.id.btn_attach)
    void onAttach() {
        List<PhotoGalleryModel> selectedPhotos = presenter.selectedPhotos();
        presenter.removeSuggestedPhotos();

        cellDelegate.onAttachClicked(selectedPhotos);
    }

    @OnClick(R.id.suggestion_avatar)
    void onAvatarClicked() {
        presenter.openProfile();
    }

    @Override
    public void onCellClicked(PhotoGalleryModel model) {
        presenter.selectPhoto(model);
        suggestionAdapter.notifyDataSetChanged();
        //
    }

    @Override
    public void appendPhotoSuggestions(List<PhotoGalleryModel> items) {
        suggestionAdapter.addItems(items);
    }

    @Override
    public void pushForward(List<PhotoGalleryModel> items) {
        suggestionAdapter.addItems(0, items);
        suggestedList.scrollToPosition(0);
    }

    @Override
    public void setUser(User user) {
        avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        avatar.setup(user, injectorProvider.get());
        avatar.invalidate();
        //
        userName.setText(user.getFullName());
    }

    @Override
    public void setSuggestionTitle(int sizeOfSelectedPhotos) {
        boolean hasPhotos = sizeOfSelectedPhotos > 0;
        if (hasPhotos) {
            int resource = QuantityHelper.chooseResource(sizeOfSelectedPhotos,
                    R.string.suggested_photo_selected_one, R.string.suggested_photo_selected_multiple);

            description.setText(String.format(itemView.getContext().getResources().getString(resource), sizeOfSelectedPhotos));
        } else {
            description.setText(R.string.suggested_photo);
        }

        btnAttach.setVisibility(hasPhotos ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showMaxSelectionMessage() {
        Snackbar.make(itemView, itemView.getContext().getString(R.string.photo_limitation_message,
                SuggestedPhotoCellPresenter.MAX_SELECTION_SIZE), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void clearListState() {
        suggestionAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public PhotoGalleryModel firstElement() {
        return suggestionAdapter.getItemCount() == 0 ? null : (PhotoGalleryModel) suggestionAdapter.getItem(0);
    }

    @Override
    public Observable<Void> notificationObservable() {
        return notificationSubject
                .asObservable()
                .throttleLast(1, TimeUnit.SECONDS)
                .replay(1)
                .autoConnect();
    }

    @Override
    public <T> Observable<T> bind(Observable<T> observable) {
        return observable.compose(RxLifecycle.bindView(itemView));
    }
}