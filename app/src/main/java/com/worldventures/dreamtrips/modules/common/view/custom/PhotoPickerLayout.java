package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.PhotoPickerPresenter;
import com.worldventures.dreamtrips.modules.feed.model.AttachPhotoModel;
import com.worldventures.dreamtrips.modules.feed.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.AttachPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoGalleryCell;
import com.worldventures.dreamtrips.modules.feed.view.util.GridAutofitLayoutManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PhotoPickerLayout extends SlidingUpPanelLayout implements PhotoPickerPresenter.View {

    @InjectView(R.id.button_cancel)
    TextView cancel;
    @InjectView(R.id.button_done)
    TextView done;
    @InjectView(R.id.selected_count)
    TextView selectedCount;
    @InjectView(R.id.picker)
    RecyclerView photoPicker;

    OnDoneClickListener onDoneClickListener;

    private InputMethodManager inputMethodManager;

    private PhotoPickerPresenter presenter;

    private BaseArrayListAdapter adapter;

    private View draggableView;

    private Injector injector;

    private boolean multiPickEnabled;

    public PhotoPickerLayout(Context context) {
        this(context, null);
    }

    public PhotoPickerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoPickerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        draggableView = inflate(getContext(), R.layout.gallery_view, null);

        presenter = new PhotoPickerPresenter();

        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        addView(draggableView, 1);
        ButterKnife.inject(this);

        setDragView(draggableView);
        setScrollableView(draggableView.findViewById(R.id.picker));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.onStop();
        presenter.dropView();
        ButterKnife.reset(this);

    }

    @Override
    public void setPanelHeight(int val) {
        super.setPanelHeight(val);

        if (getPanelState() == PanelState.EXPANDED) {
            smoothToBottom();
            invalidate();
        }
    }

    public void setup(Injector injector, boolean multiPickEnabled) {
        this.injector = injector;
        this.multiPickEnabled = multiPickEnabled;
        hidePanel();
        injector.inject(presenter);
        presenter.takeView(PhotoPickerLayout.this);
        presenter.onStart();
        requestPhotos();
    }

    /**
     * post(()->{}) because requestPhotos has to be called after `onAttachedToWindow`
     */
    private void requestPhotos() {
        post(() -> {
            if (ViewCompat.isAttachedToWindow(PhotoPickerLayout.this)) {
                presenter.loadGallery();
            }
        });
    }

    public void setup(Injector injector, boolean multiPickEnabled, int pickLimit) {
        this.setup(injector, multiPickEnabled);
        presenter.setLimit(pickLimit);
    }

    @Override
    public void updatePickedItemsCount(int pickedCount) {
        if (pickedCount == 0) {
            selectedCount.setText(null);
        } else {
            selectedCount.setText(String.format(getResources().getString(R.string.photos_selected),
                    pickedCount));
        }
    }

    @Override
    public void updateItem(PhotoGalleryModel item) {
        adapter.updateItem(item);
    }

    @Override
    public void initPhotos(List<PhotoGalleryModel> photos) {
        adapter = new BaseArrayListAdapter<>(photoPicker.getContext(), injector);
        adapter.registerCell(PhotoGalleryModel.class, PhotoGalleryCell.class);
        adapter.registerCell(AttachPhotoModel.class, AttachPhotoCell.class);
        adapter.addItem(new AttachPhotoModel(AttachPhotoModel.CAMERA, R.drawable.ic_picker_camera, R.string.camera, R.color.share_camera_color));
        adapter.addItem(new AttachPhotoModel(AttachPhotoModel.FACEBOOK, R.drawable.fb_logo, R.string.add_from_facebook, R.color.facebook_color));
        adapter.addItems(photos);

        photoPicker.setLayoutManager(new GridAutofitLayoutManager(photoPicker.getContext(),
                photoPicker.getContext().getResources().getDimension(R.dimen.photo_picker_size)));
        photoPicker.setAdapter(adapter);

        cancel.setOnClickListener(view -> hidePanel());
    }

    @Override
    public boolean isMultiPickEnabled() {
        return multiPickEnabled;
    }

    @OnClick(R.id.button_done)
    void onDone() {
        if (onDoneClickListener != null) {
            onDoneClickListener.onDone(presenter.getSelectedPhotos());
        }

        hidePanel();
    }

    public void showPanel() {
        inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        //
        setPanelHeight((int) photoPicker.getContext().getResources().getDimension(R.dimen.picker_panel_height));
    }

    public void hidePanel() {
        presenter.cancelAllSelections();
        if (photoPicker != null) photoPicker.scrollToPosition(0);
        setPanelHeight(0);
    }

    public boolean isPanelVisible() {
        return getPanelHeight() != 0;
    }

    public void setOnDoneClickListener(OnDoneClickListener onDoneClickListener) {
        this.onDoneClickListener = onDoneClickListener;
    }

    @Override
    public void informUser(int stringId) {

    }

    @Override
    public void informUser(String string) {
        Snackbar.make(this, string, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void alert(String s) {

    }

    @Override
    public boolean isVisibleOnScreen() {
        return false;
    }

    @Override
    public boolean isTabletLandscape() {
        return false;
    }

    public interface OnDoneClickListener {
        void onDone(List<ChosenImage> chosenImages);
    }
}
