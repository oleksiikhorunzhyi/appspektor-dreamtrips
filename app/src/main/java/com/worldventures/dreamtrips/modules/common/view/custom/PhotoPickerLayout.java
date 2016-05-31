package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookAlbumFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

public class PhotoPickerLayout extends SlidingUpPanelLayout {

    @InjectView(R.id.button_cancel)
    TextView cancel;
    @InjectView(R.id.button_done)
    TextView done;
    @InjectView(R.id.selected_count)
    TextView selectedCount;
    @InjectView(R.id.photo_container)
    ViewGroup container;

    @Inject
    Router router;
    @Inject
    PhotoPickerDelegate photoPickerDelegate;

    @State boolean isShown;
    @State boolean multiPickEnabled;
    @State int pickLimit;

    private WeakHandler handler = new WeakHandler();

    private InputMethodManager inputMethodManager;

    private View draggableView;

    private FragmentManager fragmentManager;

    private PhotoPickerListener photoPickerListener;

    private View transparentView;

    public PhotoPickerLayout(Context context) {
        this(context, null);
    }

    public PhotoPickerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoPickerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        draggableView = inflate(getContext(), R.layout.gallery_view, null);

        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && transparentView != null) {
            Rect rect = new Rect();
            transparentView.getLocalVisibleRect(rect);
            rect.bottom = rect.bottom - getPanelHeight();
            boolean isTransparentClicked = getPanelState() != PanelState.EXPANDED
                    && rect.contains((int) ev.getX(), (int) ev.getY());
            if (isTransparentClicked) return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        if (isShown) {
            post(() -> this.showPanel(multiPickEnabled, pickLimit));
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        addView(draggableView, 1);
        ButterKnife.inject(this);

        setDragView(draggableView);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ButterKnife.reset(this);
        photoPickerDelegate.setupPhotoPickerLayout(null);
        setScrollableView(null);
    }

    @Override
    public void setPanelHeight(int val) {
        super.setPanelHeight(val);

        if (getPanelState() == PanelState.EXPANDED) {
            smoothToBottom();
            invalidate();
        }
    }

    public void setup(FragmentManager fragmentManager) {
        this.setup(fragmentManager, true);
    }

    public void setup(FragmentManager fragmentManager, boolean isVisible) {
        this.fragmentManager = fragmentManager;
        if (isVisible) updatePickerDelegate();
    }

    public void updatePickerDelegate() {
        photoPickerDelegate.setupPhotoPickerLayout(this);
    }

    /**
     * post(()->{}) because requestPhotos has to be called after `onAttachedToWindow`
     */
    private void openGallery() {
        post(() -> {
            if (ViewCompat.isAttachedToWindow(PhotoPickerLayout.this)) {
                router.moveTo(Route.GALLERY, NavigationConfigBuilder.forFragment()
                        .fragmentManager(fragmentManager)
                        .backStackEnabled(true)
                        .containerId(container.getId())
                        .build());
            }
        });
    }

    public void openFacebookAlbums() {
        router.moveTo(Route.PICK_FB_ALBUM, NavigationConfigBuilder.forFragment()
                .fragmentManager(fragmentManager)
                .backStackEnabled(true)
                .containerId(container.getId())
                .build());
        cancel.setText(R.string.back);
    }

    public void openFacebookPhoto(Bundle bundle) {
        router.moveTo(Route.PICK_FB_PHOTO, NavigationConfigBuilder.forFragment()
                .fragmentManager(fragmentManager)
                .backStackEnabled(true)
                .containerId(container.getId())
                .data(bundle)
                .build());
    }

    public void updatePickedItemsCount(int pickedCount) {
        if (selectedCount == null) return;
        //
        if (pickedCount == 0) {
            selectedCount.setText(null);
        } else {
            selectedCount.setText(String.format(getResources().getString(R.string.photos_selected),
                    pickedCount));
        }
    }

    public boolean isShowsFacebookAlbumFragment() {
        return fragmentManager.findFragmentById(container.getId()) instanceof FacebookAlbumFragment;
    }

    public boolean isMultiPickEnabled() {
        return multiPickEnabled;
    }

    public int getPickLimit() {
        return pickLimit;
    }

    @OnClick(R.id.button_done)
    void onDone() {
        photoPickerDelegate.onDone();

        hidePanel();
    }

    @OnClick(R.id.button_cancel)
    void onCancel() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
            updatePickedItemsCount(0);
            updateCancelButtonState();
            return;
        }

        hidePanel();
    }

    private void updateCancelButtonState() {
        if (cancel == null) return;
        if (fragmentManager.getBackStackEntryCount() < 2)
            cancel.setText(R.string.action_cancel);
        else
            cancel.setText(R.string.back);
    }

    public void showPanel() {
        showPanel(false, 0);
    }

    public void showPanel(boolean multiPickEnabled) {
        showPanel(multiPickEnabled, Integer.MAX_VALUE);
    }

    public void showPanel(boolean multiPickEnabled, int pickLimit) {
        this.multiPickEnabled = multiPickEnabled;
        this.pickLimit = pickLimit;

        if (photoPickerListener != null) photoPickerListener.onOpened();
        isShown = true;
        boolean isKeyboardClosed = inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        //
        if (fragmentManager.getBackStackEntryCount() == 0) {
            updatePickerDelegate();
            openGallery();
        }
        updateCancelButtonState();
        int panelHeight = (int) getResources().getDimension(R.dimen.picker_panel_height);
        if (isKeyboardClosed)
            handler.postDelayed(() -> setPanelHeight(panelHeight), 250);
        else
            setPanelHeight(panelHeight);
    }

    public void hidePanel() {
        if (photoPickerListener != null) photoPickerListener.onClosed();
        isShown = false;
        updatePickedItemsCount(0);
        setPanelHeight(0);
        setScrollableView(null);
        //
        if (!ViewCompat.isAttachedToWindow(this)) return;
        clearAllBackStack();
    }

    public boolean isPanelVisible() {
        return getPanelHeight() != 0;
    }

    public boolean isShown() {
        return isShown;
    }

    public View getDraggableView() {
        return draggableView;
    }

    public void setOnDoneClickListener(OnDoneClickListener onDoneClickListenerObservable) {
        photoPickerDelegate.setDoneClickListener(onDoneClickListenerObservable);
    }

    public interface OnDoneClickListener {
        void onDone(List<BasePhotoPickerModel> chosenImages, int type);
    }

    private void clearAllBackStack() {
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void setPhotoPickerListener(PhotoPickerListener photoPickerListener) {
        this.photoPickerListener = photoPickerListener;
    }

    public void setTransparentView(View transparentView) {
        this.transparentView = transparentView;
    }

    public interface PhotoPickerListener {

        void onClosed();

        void onOpened();
    }
}
