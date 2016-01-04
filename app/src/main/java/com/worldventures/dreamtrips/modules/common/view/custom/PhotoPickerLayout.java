package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;

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

    @State
    boolean isShown;

    private WeakHandler handler = new WeakHandler();

    private InputMethodManager inputMethodManager;

    private View draggableView;

    private FragmentManager fragmentManager;

    private boolean multiPickEnabled;
    private int pickLimit;

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
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        if (isShown)
            showPanel();
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
    }

    @Override
    public void setPanelHeight(int val) {
        super.setPanelHeight(val);

        if (getPanelState() == PanelState.EXPANDED) {
            smoothToBottom();
            invalidate();
        }
    }

    public void setup(FragmentManager fragmentManager, boolean multiPickEnabled) {
        this.setup(fragmentManager, multiPickEnabled, true);
    }

    public void setup(FragmentManager fragmentManager, boolean multiPickEnabled, int pickLimit) {
        this.setup(fragmentManager, multiPickEnabled);
        this.pickLimit = pickLimit;
    }

    public void setup(FragmentManager fragmentManager, boolean multiPickEnabled, boolean isVisible) {
        this.fragmentManager = fragmentManager;
        this.multiPickEnabled = multiPickEnabled;
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
        if (fragmentManager.getBackStackEntryCount() < 2)
            cancel.setText(R.string.cancel);
        else
            cancel.setText(R.string.back);
    }

    public void showPanel() {
        isShown = true;
        boolean isKeyboardClosed = inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        //
        if (fragmentManager.getBackStackEntryCount() == 0) openGallery();
        updateCancelButtonState();
        int panelHeight = (int) container.getResources().getDimension(R.dimen.picker_panel_height);
        if (isKeyboardClosed)
            handler.postDelayed(() -> setPanelHeight(panelHeight), 250);
        else
            setPanelHeight(panelHeight);
    }

    public void hidePanel() {
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

    public void setOnDoneClickListener(OnDoneClickListener onDoneClickListener) {
        photoPickerDelegate.setOnDoneClickListener(onDoneClickListener);
    }

    public interface OnDoneClickListener {
        void onDone(List<ChosenImage> chosenImages);
    }

    private void clearAllBackStack() {
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
