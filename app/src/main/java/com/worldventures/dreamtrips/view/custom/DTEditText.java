package com.worldventures.dreamtrips.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.fragment.ProfileFragment;

public class DTEditText extends MaterialEditText {
    public DTEditText(Context context) {
        this(context, null);
    }

    public DTEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public DTEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ColorOptionsView, 0, 0);
        int valueColor = a.getColor(R.styleable.ColorOptionsView_hintColor, 0);
        a.recycle();
        if (valueColor > 0) {
            setHintTextColor(valueColor);
        }
    }


}
