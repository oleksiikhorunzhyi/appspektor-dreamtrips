package com.worldventures.dreamtrips.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.worldventures.dreamtrips.R;

public class DTEditText extends MaterialEditText {

    public static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

    public DTEditText(Context context) {
        this(context, null);
    }

    public DTEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public DTEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DTEditText);
        String valueColor = a.getString(R.styleable.DTEditText_hintColor);
        a.recycle();
        if (valueColor != null && !valueColor.isEmpty()) {
            setHintTextColor(Color.parseColor(valueColor));
        }
        boolean b1 = attrs.getAttributeBooleanValue(NAMESPACE, "focusableInTouchMode", true);
        boolean focusable = attrs.getAttributeBooleanValue(NAMESPACE, "focusable", true);
        boolean clickable = attrs.getAttributeBooleanValue(NAMESPACE, "clickable", true);

        setFocusableInTouchMode(b1);
        setFocusable(focusable);
        setClickable(clickable);
    }
}
