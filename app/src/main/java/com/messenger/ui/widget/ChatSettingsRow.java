package com.messenger.ui.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatSettingsRow extends LinearLayout {

   @InjectView(R.id.chat_settings_row_icon) ImageView iconImageView;
   @InjectView(R.id.chat_settings_row_title) TextView titleTextView;
   @InjectView(R.id.chat_settings_row_switch) SwitchCompat settingsSwitch;

   public ChatSettingsRow(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public ChatSettingsRow(Context context) {
      super(context);
      init();
   }

   private void init() {
      setOrientation(LinearLayout.VERTICAL);
      ButterKnife.inject(this, LayoutInflater.from(getContext())
            .inflate(R.layout.widget_chat_settings_row, this, true));
   }

   public void setIcon(@DrawableRes int icon) {
      iconImageView.setImageResource(icon);
   }

   public void setTitle(@StringRes int title) {
      titleTextView.setText(title);
   }

   public void setTitle(String title) {
      titleTextView.setText(title);
   }

   public void enableSwitch(CompoundButton.OnCheckedChangeListener listener) {
      settingsSwitch.setVisibility(View.VISIBLE);
      settingsSwitch.setOnCheckedChangeListener(listener);
   }

   public void setSwitchChecked(boolean checked) {
      settingsSwitch.setChecked(checked);
   }
}
