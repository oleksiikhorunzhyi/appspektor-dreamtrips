package com.messenger.ui.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.raizlabs.android.dbflow.annotation.NotNull;

import org.apache.commons.lang3.StringUtils;

public class InputUserDialog {

    Context context;

    public interface Listener{

        void onUserInput(String userName);

        void onCancel();
    }

    public InputUserDialog(Context context) {
        this.context = context;
    }

    public void show(@NotNull Listener listener){
        final EditText editText = new EditText(context);

        new AlertDialog.Builder(context)
                .setTitle("Input test user's name")
                .setMessage("The format must look like techery_userN, where N is between 1 and 10.")
                .setView(editText)
                .setPositiveButton("Ok", (dialog, possitiveButton) -> {
                    String userName = editText.getText().toString();
                    if (StringUtils.isEmpty(userName)) return;

                    listener.onUserInput(userName);
                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> {
                    listener.onCancel();
                })
                .show();
    }
}
