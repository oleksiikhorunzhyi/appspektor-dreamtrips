package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.view.CollapsibleActionView;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

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
        showSoftKeyboard(this);
    }

    @Override
    public void onActionViewCollapsed() {
        clearFocus();
        setText("");
        hideSoftKeyboard(this);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!focused) {
            hideSoftKeyboard(this);
        }
    }

    public void hideSoftKeyboard(View v) {
        Object systemService = getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        InputMethodManager inputMethodManager = (InputMethodManager) systemService;
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    public void showSoftKeyboard(View v) {
        Object systemService = getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        InputMethodManager inputMethodManager = (InputMethodManager) systemService;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
