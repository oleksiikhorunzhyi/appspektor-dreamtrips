package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.State;

public class DtlFilterButton extends FrameLayout {

    private final int enabledBackgroundColor;
    private final int disabledBackgroundColor;
    private final int enabledCaptionColor;
    private final int disabledCaptionColor;

    @InjectView(R.id.dtlfb_rootView)
    ViewGroup rootView;
    @InjectView(R.id.dtlfb_caption)
    AppCompatTextView caption;
    @InjectView(R.id.dtlfb_separator)
    View separator;

    @State
    protected boolean filterEnabled = false;

    public DtlFilterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        enabledBackgroundColor =
                ContextCompat.getColor(context, R.color.dtlfb_enabled_background_color);
        disabledBackgroundColor =
                ContextCompat.getColor(context, R.color.dtlfb_disabled_background_color);
        enabledCaptionColor = ContextCompat.getColor(context, R.color.dtlfb_enabled_caption_color);
        disabledCaptionColor =
                ContextCompat.getColor(context, R.color.dtlfb_disabled_caption_color);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        redrawState();
    }

    protected void redrawState() {
        if (filterEnabled) {
            rootView.setBackgroundColor(enabledBackgroundColor);
            caption.setTextColor(enabledCaptionColor);
            caption.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_dtl_filter_button_enabled, 0, 0, 0);
            separator.setVisibility(INVISIBLE);
        } else {
            rootView.setBackgroundColor(disabledBackgroundColor);
            caption.setTextColor(disabledCaptionColor);
            caption.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_dtl_filter_button_disabled, 0, 0, 0);
            separator.setVisibility(VISIBLE);
        }
    }

    public void setFilterEnabled(final boolean enabled) {
        filterEnabled = enabled;
        redrawState();
    }

    ///////////////////////////////////////////////////////////////////////////
    // State saving
    ///////////////////////////////////////////////////////////////////////////

    @Override public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }
}
