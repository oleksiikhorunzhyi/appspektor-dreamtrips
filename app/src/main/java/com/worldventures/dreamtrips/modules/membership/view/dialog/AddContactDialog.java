package com.worldventures.dreamtrips.modules.membership.view.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import butterknife.ButterKnife;

import static com.worldventures.dreamtrips.core.utils.ValidationUtils.VResult;
import static com.worldventures.dreamtrips.core.utils.ValidationUtils.isEmailValid;

public class AddContactDialog {

    private MaterialEditText etName;
    private MaterialEditText etPhone;
    private MaterialEditText etEmail;
    private Button btn;
    private final MaterialDialog md;
    private Callback callback;
    private String emailError;

    public AddContactDialog(Context context) {
        md = new MaterialDialog.Builder(context)
                .title(R.string.add_contact)
                .customView(R.layout.dialog_add_contact)
                .positiveText(R.string.add)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (callback != null) {
                            Member member = new Member();
                            member.setId(String.valueOf(System.currentTimeMillis()));
                            member.setName(etName.getText().toString());
                            member.setEmail(etEmail.getText().toString());
                            member.setPhone(etPhone.getText().toString());
                            callback.add(member);
                        }
                    }
                })
                .build();


    }

    public void show(Callback callback) {
        this.callback = callback;
        md.show();

        TextWatcherAdapter watcher = new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {

                VResult emailValid = isEmailValid(etEmail.getText().toString());
                boolean emailExist = !TextUtils.isEmpty(etEmail.getText());
                boolean nameExist = !TextUtils.isEmpty(etName.getText());
                boolean phoneExist = !TextUtils.isEmpty(etPhone.getText());


                if (emailExist && !emailValid.isValid()) {
                    if (emailError == null) {
                        emailError = etEmail.getResources().getString(emailValid.getMessage());
                    }
                    etEmail.setError(emailError);
                }
                btn.setEnabled(nameExist && (emailExist || phoneExist));
            }
        };

        etName = ButterKnife.findById(md, R.id.et_name);
        etPhone = ButterKnife.findById(md, R.id.et_phone);
        etEmail = ButterKnife.findById(md, R.id.et_email);
        btn = ButterKnife.findById(md, R.id.buttonDefaultPositive);
        btn.setEnabled(false);

        etName.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
    }

    public interface Callback {
        void add(Member member);
    }

}
