package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import butterknife.ButterKnife;
import rx.functions.Action1;

public class FlagPopupMenu extends PopupMenu {
    private Context context;
    private DialogConfirmationCallback dialogConfirmationCallback;

    public FlagPopupMenu(Context context, View anchor) {
        this(context, anchor, Gravity.NO_GRAVITY);
    }

    public FlagPopupMenu(Context context, View anchor, int gravity) {
        super(context, anchor, gravity);
        this.context = context;
    }

    public void show(List<Flag> flags, Action1<Flag> onSelected) {
        for (int i = 0; i < flags.size(); i++) {
            Flag flagContent = flags.get(i);
            getMenu().add(0, i, i, flagContent.getName());
        }
        setOnMenuItemClickListener(item -> {
            Flag flag = flags.get(item.getOrder());
            onSelected.call(flag);
            return true;
        });
        show();
    }

    @Deprecated
    public void show(List<Flag> flags, DialogConfirmationCallback dialogConfirmationCallback) {
        this.dialogConfirmationCallback = dialogConfirmationCallback;
        for (int i = 0; i < flags.size(); i++) {
            Flag flagContent = flags.get(i);
            getMenu().add(0, i, i, flagContent.getName());
        }
        setOnMenuItemClickListener(item -> {
            Flag flag = flags.get(item.getOrder());
            if (flag.isRequireDescription()) {
                showFlagDescription(flag);
            } else {
                showFlagConfirmDialog(flag, flag.getName());
            }

            return true;
        });
        show();
    }

    private void showFlagConfirmDialog(Flag flag, String reason) {
        String content = context.getResources().getString(R.string.flag_photo_first) + " "
                + flag.getName().toLowerCase()
                + " "
                + context.getResources().getString(R.string.flag_photo_second);
        new MaterialDialog.Builder(context)
                .title(R.string.flag_photo_title)
                .content(content)
                .positiveText(R.string.flag_photo_positive)
                .negativeText(R.string.flag_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (dialogConfirmationCallback != null) {
                            dialogConfirmationCallback.onFlagConfirmed(flag.getId(), reason);
                        }
                    }
                })
                .show();
    }

    private void showFlagDescription(Flag flag) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.flag_description_title)
                .customView(R.layout.dialog_flag_description, true)
                .positiveText(R.string.flag_description_positive)
                .negativeText(R.string.flag_description_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        EditText et = ButterKnife.findById(dialog, R.id.tv_description);
                        String reason = et.getText().toString();
                        showFlagConfirmDialog(flag, reason);
                    }
                }).build();
        dialog.show();
        View positiveButton = dialog.getActionButton(DialogAction.POSITIVE);
        positiveButton.setEnabled(false);
        EditText etDesc = (EditText) dialog.getCustomView().findViewById(R.id.tv_description);
        etDesc.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveButton.setEnabled(s.toString().trim().length() > 0);
            }
        });
    }

    public interface DialogConfirmationCallback {
        void onFlagConfirmed(int flagReasonId, String reason);
    }
}
