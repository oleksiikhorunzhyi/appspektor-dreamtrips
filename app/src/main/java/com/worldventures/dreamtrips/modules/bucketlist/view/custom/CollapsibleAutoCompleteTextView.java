package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.view.CollapsibleActionView;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.techery.spares.utils.ui.SoftInputUtil;

public class CollapsibleAutoCompleteTextView extends AutoCompleteTextView implements CollapsibleActionView {

    public CollapsibleAutoCompleteTextView(Context context) {
        super(context);
    }

    public CollapsibleAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsibleAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onActionViewExpanded() {
        requestFocus();
        SoftInputUtil.showSoftInputMethod(this);
    }

    @Override
    public void onActionViewCollapsed() {
        clearFocus();
        setText("");
        SoftInputUtil.hideSoftInputMethod(this);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!focused) {
            SoftInputUtil.hideSoftInputMethod(this);
        }
    }

}
