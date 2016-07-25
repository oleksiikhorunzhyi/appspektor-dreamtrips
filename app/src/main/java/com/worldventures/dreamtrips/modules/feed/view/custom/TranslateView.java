package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class TranslateView extends FrameLayout {

    @InjectView(R.id.loading_translates) View progressView;
    @InjectView(R.id.translate_holder) View translateHolder;
    @InjectView(R.id.translated_text) TextView translatedText;
    @InjectView(R.id.translated_info) TextView translatedInfo;

    private @IdRes int actionViewId;
    private View actionView;

    public TranslateView(Context context) {
        this(context, null);
    }

    public TranslateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TranslateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(getContext()).inflate(R.layout.layout_translate_view, this, true);
        ButterKnife.inject(this);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.TranslateView, 0, 0);

        try {
            actionViewId = typedArray.getResourceId(R.styleable.TranslateView_action_view, 0);
        } catch (Exception e) {
            Timber.e(e, "Can't parse custom attributes");
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        actionView = getRootView().findViewById(actionViewId);
        if (actionView == null) throw new IllegalArgumentException("Action View can not be null");
    }

    public void showActionView(){
        actionView.setVisibility(VISIBLE);
        progressView.setVisibility(GONE);
        translateHolder.setVisibility(GONE);
    }

    public void showProgress(){
        actionView.setVisibility(GONE);
        progressView.setVisibility(VISIBLE);
        translateHolder.setVisibility(GONE);
    }

    public void showTranslation(String translation, String originalLanguage){
        actionView.setVisibility(GONE);
        progressView.setVisibility(GONE);
        translateHolder.setVisibility(VISIBLE);
        translatedText.setText(translation);
        translatedInfo.setText(getResources().getString(R.string.translated_from, originalLanguage));
    }
}
