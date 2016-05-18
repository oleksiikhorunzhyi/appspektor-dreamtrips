package com.messenger.ui.dialog;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.worldventures.dreamtrips.R;

import rx.functions.Action0;
import rx.functions.Action1;

public class ChangeSubjectDialog {
    
    private final AlertDialog dialog;
    private EditText etSubject;
    
    private Action1<String> positiveListener;
    private Action0 cancelListener;

    public ChangeSubjectDialog(Context context, String currentSubject) {
        dialog = new AlertDialog.Builder(context)
                .setView(obtainDialogUi(context, currentSubject))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    if (cancelListener != null)
                        cancelListener.call();
                })
                .setPositiveButton(android.R.string.ok, (dialog, which1) -> {
                    if (positiveListener != null)
                        positiveListener.call(etSubject.getText().toString());
                })
                .setTitle(R.string.change_subject)
                .create();
    }
    
    private View obtainDialogUi (Context context, String currentSubject) {
        final View dialogView = View.inflate(context, R.layout.dialog_messenger_input, null);
        etSubject = (EditText) dialogView.findViewById(R.id.et_input);
        etSubject.setText(currentSubject);
        etSubject.setHint(R.string.subject);
        if (currentSubject != null) {
            etSubject.setSelection(currentSubject.length());
        }
        
        return dialogView;
    }

    public ChangeSubjectDialog setPositiveListener(Action1<String> positiveListener) {
        this.positiveListener = positiveListener;
        return this;
    }

    public ChangeSubjectDialog setCancelListener(Action0 cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }

    public void show(){
        dialog.show();
    }
}
