package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.worldventures.dreamtrips.R;

import rx.functions.Action0;

public class WalletPhotoProposalDialog extends BottomSheetDialog {

   private Action0 onChoosePhotoAction;
   private Action0 onDoNotAddPhotoAction;
   private Action0 onCancelAction;

   public WalletPhotoProposalDialog(@NonNull Context context) {
      super(context);
      init(context);
   }

   private void init(Context context) {
      final View view = View.inflate(context, R.layout.dialog_wallet_profile_photo_actions, null);
      view.findViewById(R.id.choose_photo_action).setOnClickListener(v -> onChoosePhotoAction());
      view.findViewById(R.id.do_not_add_photo_action).setOnClickListener(v -> onDoNotAddPhotoAction());
      view.findViewById(R.id.cancel_action).setOnClickListener(v -> onCancelAction());
      setContentView(view);
   }

   private void onChoosePhotoAction() {
      if (onChoosePhotoAction != null) {
         onChoosePhotoAction.call();
      }
   }

   private void onDoNotAddPhotoAction() {
      if (onDoNotAddPhotoAction != null) {
         onDoNotAddPhotoAction.call();
      }
   }

   private void onCancelAction() {
      if (onCancelAction != null) {
         onCancelAction.call();
      }
   }

   public void setOnChoosePhotoAction(Action0 onChoosePhotoAction) {
      this.onChoosePhotoAction = onChoosePhotoAction;
   }

   public void setOnDoNotAddPhotoAction(Action0 onDoNotAddPhotoAction) {
      this.onDoNotAddPhotoAction = onDoNotAddPhotoAction;
   }

   public void setOnCancelAction(Action0 onCancelAction) {
      this.onCancelAction = onCancelAction;
   }
}
