package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.worldventures.dreamtrips.R;

import rx.functions.Action1;

public class WalletSuffixSelectingDialog extends BottomSheetDialog {

   private Action1<String> actionSelect;

   public WalletSuffixSelectingDialog(@NonNull Context context) {
      super(context);
      init(context);
   }

   private void init(Context context) {
      final View view = View.inflate(context, R.layout.wallet_dialog_profile_suffix_actions, null);
      final ListView lvSuffixes = (ListView) view.findViewById(R.id.lv_suffixes);
      final String[] suffixes = context.getResources().getStringArray(R.array.wallet_edit_profile_suffixes);

      final ArrayAdapter<String> adapter = new ArrayAdapter<>(
            context, R.layout.wallet_item_profile_suffix, R.id.tv_suffix, suffixes);

      lvSuffixes.setAdapter(adapter);

      lvSuffixes.setOnItemClickListener((adapterView, view1, i, l) -> {
         if (actionSelect != null) actionSelect.call(i == 0 ? "" : suffixes[i]);
         dismiss();
      });
      setContentView(view);
   }

   public void setOnSelectedAction(Action1<String> actionSelect) {
      this.actionSelect = actionSelect;
   }
}
