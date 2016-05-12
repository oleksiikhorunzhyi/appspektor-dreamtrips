package com.worldventures.dreamtrips.modules.common.view.custom;

import android.Manifest;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;

import timber.log.Timber;

public class PhotoPickerLayoutDelegate {

    private BackStackDelegate backStackDelegate;

    private PhotoPickerLayout photoPickerLayout;

    public PhotoPickerLayoutDelegate(BackStackDelegate backStackDelegate) {
        this.backStackDelegate = backStackDelegate;
    }

    public void setPhotoPickerLayout(PhotoPickerLayout photoPickerLayout) {
        this.photoPickerLayout = photoPickerLayout;
    }

    public void initPicker(FragmentManager fragmentManager) {
        initPicker(fragmentManager, true);
    }

    /**
     * Init picker and attach it to provided container
     *
     * @param fragmentManager  FragmentManager to init picker
     * @param isVisible        default value is {true}
     */
    public void initPicker(FragmentManager fragmentManager, boolean isVisible) {
        photoPickerLayout.setup(fragmentManager, isVisible);
    }

    public void setPhotoPickerListener(PhotoPickerLayout.PhotoPickerListener listener) {
        if (photoPickerLayout != null) photoPickerLayout.setPhotoPickerListener(listener);
        else Timber.d("Photo picker was not initialized");
    }

    public boolean isPanelVisible() {
        return photoPickerLayout != null && photoPickerLayout.isPanelVisible();
    }

    public void setOnDoneClickListener(PhotoPickerLayout.OnDoneClickListener onDoneClickListener) {
        if (photoPickerLayout != null)  photoPickerLayout.setOnDoneClickListener(onDoneClickListener);
        else Timber.d("Photo picker was not initialized");
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE})
    public void showPicker() {
        //noinspection all
        showPicker(false, Integer.MAX_VALUE);
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE})
    public void showPicker(boolean multiPickEnabled) {
        //noinspection all
        showPicker(multiPickEnabled, Integer.MAX_VALUE);
    }

    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE})
    public void showPicker(boolean multiPickEnabled, int pickerLimit) {
        if (photoPickerLayout != null) {
            photoPickerLayout.showPanel(multiPickEnabled, pickerLimit);
            backStackDelegate.setListener(() -> {
                if (photoPickerLayout.isPanelVisible()) {
                    photoPickerLayout.hidePanel();
                    return true;
                }
                return false;
            });
            applyWhiteScreenWorkaround();
        }
        else Timber.d("Photo picker was not initialized");
    }

    public void hidePicker() {
        if (photoPickerLayout != null) {
            photoPickerLayout.hidePanel();
            backStackDelegate.setListener(null);
        }
        else Timber.d("Photo picker was not initialized");
    }

    /**
     * Sometimes a bug happens in parent view (SlidingUpPanelLayout) that leads
     * to photo picker shown with white screen. The reason for this is that SlidingUpPanelLayout
     * for some reason randomly sets visibility of draggable view to INVISIBLE (cause white screen)
     * and its panel state to HIDDEN (ignores all touch events). To avoid this we need
     * to revert those and requestLayout so that SlidingUpPanelLayout can update it's state in onMeasure
     */
    private void applyWhiteScreenWorkaround() {
        View draggableView = photoPickerLayout.getDraggableView();
        if (draggableView.getVisibility() == View.INVISIBLE) {
            draggableView.setVisibility(View.VISIBLE);
            photoPickerLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            photoPickerLayout.requestLayout();
        }
    }

    public void disableEditTextUntilPickerIsShown(EditText editText) {
        if (photoPickerLayout == null) return;

        photoPickerLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (!editText.hasFocus()) editText.setEnabled(false);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                editText.setEnabled(true);
            }

            @Override
            public void onPanelExpanded(View panel) {
                editText.setEnabled(true);
            }

            @Override
            public void onPanelAnchored(View panel) {
                editText.setEnabled(true);
            }

            @Override
            public void onPanelHidden(View panel) {
                editText.setEnabled(true);
            }
        });
    }
}
