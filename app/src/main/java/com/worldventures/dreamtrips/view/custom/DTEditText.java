package com.worldventures.dreamtrips.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.worldventures.dreamtrips.R;

public class DTEditText extends MaterialEditText {
    public DTEditText(Context context) {
        super(context);
    }

    public DTEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHintTextColor(Color.WHITE);

    }

    public DTEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ColorOptionsView, 0, 0);
        int valueColor = a.getColor(R.styleable.ColorOptionsView_hintColor, android.R.color.holo_blue_light);
        a.recycle();
        setHintTextColor(valueColor);
    }
}
