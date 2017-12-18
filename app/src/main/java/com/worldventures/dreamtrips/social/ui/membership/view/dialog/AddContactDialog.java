package com.worldventures.dreamtrips.social.ui.membership.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.worldventures.core.utils.ValidationUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;

import butterknife.ButterKnife;
import rx.functions.Action3;

import static com.worldventures.dreamtrips.util.ValidationUtils.isEmailValid;

public class AddContactDialog {

   private View btn;
   private String emailError;
   private MaterialEditText etName;
   private MaterialEditText etPhone;
   private MaterialEditText etEmail;
   private final MaterialDialog materialDialog;
   private Action3<String, String, String> callback;

   public AddContactDialog(Context context) {
      materialDialog = new MaterialDialog.Builder(context).title(R.string.add_contact)
            .customView(R.layout.dialog_add_contact, true)
            .positiveText(R.string.add)
            .onPositive((dialog, which) -> callback.call(etName.getText().toString(), etEmail.getText().toString(),
                  etPhone.getText().toString()))
            .build();
   }

   public void show(@NonNull Action3<String, String, String> callback) {
      this.callback = callback;
      materialDialog.show();
      TextWatcherAdapter watcher = new TextWatcherAdapter() {
         @Override
         public void afterTextChanged(Editable s) {

            ValidationUtils.VResult emailValid = isEmailValid(etEmail.getText().toString());
            boolean emailExist = !TextUtils.isEmpty(etEmail.getText());
            boolean nameExist = !TextUtils.isEmpty(etName.getText());
            boolean phoneExist = !TextUtils.isEmpty(etPhone.getText());

            if (emailExist && !emailValid.isValid()) {
               if (emailError == null) {
                  emailError = etEmail.getResources().getString(emailValid.getMessage());
               }
               etEmail.setError(emailError);
            }

            btn.setEnabled(nameExist && ((emailExist && emailValid.isValid()) || phoneExist && (!emailExist || emailValid
                  .isValid())));
         }
      };

      etName = ButterKnife.findById(materialDialog, R.id.et_name);
      etPhone = ButterKnife.findById(materialDialog, R.id.et_phone);
      etEmail = ButterKnife.findById(materialDialog, R.id.et_email);
      btn = ButterKnife.findById(materialDialog, R.id.buttonDefaultPositive);
      btn.setEnabled(false);

      etName.addTextChangedListener(watcher);
      etPhone.addTextChangedListener(watcher);
      etEmail.addTextChangedListener(watcher);
   }
}
