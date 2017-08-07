package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder;

import android.view.View;

import com.worldventures.dreamtrips.databinding.ListItemDisableDefaultCardBinding;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseHolder;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.SelectableCallback;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

public class ListItemDisableHolder extends BaseHolder<SettingsRadioModel> {

   private final ListItemDisableDefaultCardBinding binding;
   private final Callback callback;

   public ListItemDisableHolder(ListItemDisableDefaultCardBinding binding, Callback callback) {
      super(binding.getRoot());
      this.binding = binding;
      this.callback = callback;
   }

   @Override
   public void setData(SettingsRadioModel data) {
      binding.checkbox.setText(itemView.getContext().getString(data.getTextResId()));
      binding.checkbox.setChecked(callback.isSelected(getAdapterPosition()));
      binding.divider.setVisibility(callback.isLast(getAdapterPosition()) ? View.GONE : View.VISIBLE);

      binding.checkbox.setOnClickListener(view -> {
         callback.toggleSelection(getAdapterPosition());
         callback.onClick(data);
      });
   }

   public interface Callback extends SelectableCallback {

      boolean isLast(int position);

      void onClick(SettingsRadioModel model);
   }
}
