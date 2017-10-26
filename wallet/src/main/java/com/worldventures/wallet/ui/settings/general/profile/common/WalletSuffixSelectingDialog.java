package com.worldventures.wallet.ui.settings.general.profile.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.worldventures.wallet.R;

import rx.functions.Action1;

import static android.view.View.inflate;

public class WalletSuffixSelectingDialog extends BottomSheetDialog {

   private Action1<String> actionSelect;

   public WalletSuffixSelectingDialog(@NonNull Context context) {
      super(context);
      init(context);
   }

   private void init(Context context) {
      final ListView lvSuffixes = (ListView) inflate(context, R.layout.dialog_wallet_profile_suffix_actions, null);
      final String[] suffixes = context.getResources().getStringArray(R.array.wallet_edit_profile_suffixes);

      final ArrayAdapter<String> adapter = new ArrayAdapter<>(
            context, R.layout.item_wallet_profile_suffix, R.id.tv_suffix, suffixes);

      lvSuffixes.setAdapter(adapter);

      lvSuffixes.setOnItemClickListener((adapterView, view, i, l) -> {
         if (actionSelect != null) actionSelect.call(i == 0 ? "" : suffixes[i]);
         dismiss();
      });
      setContentView(lvSuffixes);
   }

   public void setOnSelectedAction(Action1<String> actionSelect) {
      this.actionSelect = actionSelect;
   }
}
