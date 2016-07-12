package com.messenger.ui.widget;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;

/**
 * Class for applying workaround for white screen problem in PhotoPicker.
 * The problem that is fixed here is that parent class of PhotoPickerLayout (SlidingUpPanelLayout)
 * assigns to the draggable view visibility INVISIBLE after some time when it was shown.
 */
public class MessengerPhotoPickerLayout extends PhotoPickerLayout {
    private boolean isInstanceStateSaved;

    public MessengerPhotoPickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessengerPhotoPickerLayout(Context context) {
        super(context);
    }

    @Override
    public void setPanelHeight(int val) {
        super.setPanelHeight(val);

        if (getPanelState() != PanelState.EXPANDED && getHeight() > 0) {
            smoothToBottom();
            invalidate();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == getDraggableView() && visibility == View.INVISIBLE) {
            getDraggableView().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidePanel() {
        if (!isInstanceStateSaved || isShown()) {
            super.hidePanel();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        isInstanceStateSaved = true;
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        isInstanceStateSaved = false;
        super.onRestoreInstanceState(state);
    }
}
