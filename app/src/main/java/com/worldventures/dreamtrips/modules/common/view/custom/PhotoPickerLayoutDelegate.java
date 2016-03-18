package com.worldventures.dreamtrips.modules.common.view.custom;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import timber.log.Timber;

public class PhotoPickerLayoutDelegate {

    private PhotoPickerLayout photoPickerLayout;

    public PhotoPickerLayoutDelegate() {
    }

    public void setPhotoPickerLayout(PhotoPickerLayout photoPickerLayout) {
        this.photoPickerLayout = photoPickerLayout;
    }

    public void initPicker(FragmentManager fragmentManager) {
        initPicker(fragmentManager, false, true);
    }

    public void initPicker(FragmentManager fragmentManager, boolean multiPickEnabled) {
        initPicker(fragmentManager, multiPickEnabled, true);
    }

    /**
     * Init picker and attach it to provided container
     *
     * @param fragmentManager  FragmentManager to init picker
     * @param multiPickEnabled default value is {false}
     * @param isVisible        default value is {true}
     */
    public void initPicker(FragmentManager fragmentManager, boolean multiPickEnabled, boolean isVisible) {
        photoPickerLayout.setup(fragmentManager, multiPickEnabled, isVisible);
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

    public void showPicker() {
        if (photoPickerLayout != null) {
            photoPickerLayout.showPanel();
            applyWhiteScreenWorkaround();
        }
        else Timber.d("Photo picker was not initialized");
    }

    public void hidePicker() {
        if (photoPickerLayout != null) photoPickerLayout.hidePanel();
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
