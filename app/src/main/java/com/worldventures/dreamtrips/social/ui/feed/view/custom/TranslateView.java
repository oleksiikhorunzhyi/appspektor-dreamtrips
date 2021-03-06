package com.worldventures.dreamtrips.social.ui.feed.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TranslateView extends FrameLayout {

   @InjectView(R.id.loading_translates) View progressView;
   @InjectView(R.id.translate_holder) View translateHolder;
   @InjectView(R.id.translated_text) TextView translatedText;
   @InjectView(R.id.translated_info) TextView translatedInfo;

   @ColorRes
   private int textColor;

   public TranslateView(Context context) {
      this(context, null);
   }

   public TranslateView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public TranslateView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TranslateView, 0, 0);
      textColor = a.getResourceId(R.styleable.TranslateView_translatedTextColor, R.color.black);
      a.recycle();

      LayoutInflater.from(getContext()).inflate(R.layout.layout_translate_view, this, true);
      ButterKnife.inject(this);

      translatedText.setTextColor(context.getResources().getColor(textColor));
   }

   public void hide() {
      progressView.setVisibility(GONE);
      translateHolder.setVisibility(GONE);
   }

   public void showProgress() {
      progressView.setVisibility(VISIBLE);
      translateHolder.setVisibility(GONE);
   }

   public void showTranslation(String translation, String language) {
      progressView.setVisibility(GONE);
      translateHolder.setVisibility(VISIBLE);
      translatedText.setText(translation);
      translatedInfo.setText(getResources().getString(R.string.translated_from, new Locale(language).getDisplayLanguage()));
   }
}
