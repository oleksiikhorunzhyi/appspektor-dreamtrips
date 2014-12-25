package com.worldventures.dreamtrips.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.worldventures.dreamtrips.R;

public class DTEditText extends MaterialEditText {
    public DTEditText(Context context) {
        this(context, null);
    }

    public DTEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public DTEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DTEditText);
        int valueColor = a.getColor(R.styleable.DTEditText_hintColor, 0);
        a.recycle();
        if (valueColor > 0) {
            setHintTextColor(valueColor);
        }
        boolean b1 = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "focusableInTouchMode", true);
        boolean focusable = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "focusable", true);
        boolean clickable = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "clickable", true);

        setFocusableInTouchMode(b1);
        setFocusable(focusable);
        setClickable(clickable);
    }
}
