package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FlagView extends FrameLayout {
    @InjectView(R.id.iv_flag)
    ImageView ivFlag;

    @InjectView(R.id.progress_flag)
    ProgressBar progressBar;

    public FlagView(Context context) {
        this(context, null);
    }

    public FlagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_flag_item, this, true);
        ButterKnife.inject(this);
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    public void showFlagsPopup(List<Flag> flags, Context context, DialogConfirmationCallback dialogConfirmationCallback) {
        PopupMenu popup = new PopupMenu(context, this);
        for (int i = 0; i < flags.size(); i++) {
            Flag flagContent = flags.get(i);
            popup.getMenu().add(0, i, i, flagContent.getName());
        }
        popup.setOnMenuItemClickListener(item -> {
            Flag flag = flags.get(item.getOrder());
            if (flag.isRequireDescription()) {
                showFlagDescription(context, flag.getName(), dialogConfirmationCallback);
            } else {
                showFlagConfirmDialog(context, flag.getName(), null, dialogConfirmationCallback);
            }

            return true;
        });
        popup.show();
    }

    private void showFlagConfirmDialog(Context context, String reason, String desc, DialogConfirmationCallback dialogConfirmationCallback) {
        String content = getResources().getString(R.string.flag_photo_first) + " " + reason.toLowerCase() + " " + getResources().getString(R.string.flag_photo_second);
        new MaterialDialog.Builder(context)
                .title(R.string.flag_photo_title)
                .content(content)
                .positiveText(R.string.flag_photo_positive)
                .negativeText(R.string.flag_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialogConfirmationCallback.onFlagConfirmed(reason, desc);
                    }
                })
                .show();
    }

    private void showFlagDescription(Context context, String reason, DialogConfirmationCallback dialogConfirmationCallback) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.flag_description_title)
                .customView(R.layout.dialog_flag_description, true)
                .positiveText(R.string.flag_description_positive)
                .negativeText(R.string.flag_description_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        EditText et = ButterKnife.findById(dialog, R.id.tv_description);
                        String desc = et.getText().toString();
                        showFlagConfirmDialog(context, reason, desc, dialogConfirmationCallback);
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
        void onFlagConfirmed(String reason, String desc);
    }
}
