package com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.items.SettingsRadioModel;

import java.util.ArrayList;

import rx.functions.Action1;

import static android.view.LayoutInflater.from;

public class WalletDelayRadioGroup extends RadioGroup {

   private static final String VIEW_STATE_KEY = "WalletDelayRadioGroup#VIEW_STATE_KEY";
   private static final String CHECKED_ID_KEY = "WalletDelayRadioGroup#CHECKED_ID_KEY";
   private static final String ITEMS_KEY = "WalletDelayRadioGroup#ITEMS_KEY";

   private ArrayList<SettingsRadioModel> items;
   private Action1<SettingsRadioModel> onChosenListener;
   private int checkedId;

   private final OnCheckedChangeListener onCheckedChangeListener = (group, checkedId) -> {
      if (this.checkedId == checkedId) {
         return;
      }
      this.checkedId = checkedId;
      if (onChosenListener != null) {
         onChosenListener.call(items.get(checkedId));
      }
   };

   public WalletDelayRadioGroup(Context context, AttributeSet attrs) {
      super(context, attrs);
      super.setOnCheckedChangeListener(onCheckedChangeListener);
   }

   public void setItems(ArrayList<SettingsRadioModel> items) {
      this.items = items;
      for (int i = 0; i < items.size(); i++) {
         attachItem(i, items.get(i));
      }
   }

   private void attachItem(int position, SettingsRadioModel radioModel) {
      final RadioButton button = (RadioButton) from(getContext()).inflate(R.layout.item_wallet_radio_button, this, false);
      button.setText(radioModel.getText());
      button.setId(position);
      addView(button);
   }

   @Override
   public void check(@IdRes int id) {
      this.checkedId = id;
      super.check(id);
   }

   @Override
   protected void onRestoreInstanceState(Parcelable state) {
      final Bundle bundle = (Bundle) state;
      checkedId = bundle.getInt(CHECKED_ID_KEY, 0);
      items = bundle.getParcelableArrayList(ITEMS_KEY);
      super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE_KEY));
   }

   @Override
   protected Parcelable onSaveInstanceState() {
      Parcelable parcelable = super.onSaveInstanceState();
      final Bundle bundle = new Bundle();
      bundle.putParcelable(VIEW_STATE_KEY, parcelable);
      bundle.putInt(CHECKED_ID_KEY, checkedId);
      bundle.putParcelableArrayList(ITEMS_KEY, items);
      return bundle;
   }

   @Override
   public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
      throw new IllegalStateException("OnCheckedChangeListener cannot be added");
   }

   public void setOnChosenListener(Action1<SettingsRadioModel> onChosenListener) {
      this.onChosenListener = onChosenListener;
   }
}
