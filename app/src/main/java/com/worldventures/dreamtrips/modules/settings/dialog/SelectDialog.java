package com.worldventures.dreamtrips.modules.settings.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragment;

public class SelectDialog extends BaseDialogFragment {

    private static final String TITLE_ID = "titleId";
    private static final String ITEMS = "items";
    private static final String SELECTED_POSITION = "selectedPosition";

    private int titleId;
    private String[] items;
    private int selectedPosition;
    private SelectionListener selectionListener;

    public static SelectDialog newInstance(int titleId, String[] items, int selectedPosition) {
        SelectDialog dialog = new SelectDialog();
        Bundle args = new Bundle();
        args.putInt(TITLE_ID, titleId);
        args.putStringArray(ITEMS, items);
        args.putInt(SELECTED_POSITION, selectedPosition);
        dialog.setArguments(args);
        return dialog;
    }

    public SelectDialog() {
        super();
        injectCustomLayout = false;
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        titleId = args.getInt(TITLE_ID);
        items = args.getStringArray(ITEMS);
        selectedPosition = args.getInt(SELECTED_POSITION);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity())
                .title(titleId)
                .items(items)
                .itemsCallbackSingleChoice(selectedPosition, (dialog1, view, which, text) -> {
                    if (selectionListener != null) {
                        selectionListener.onItemSelected(which, items[which]);
                    }
                    return true;
                });
        return dialog.show();
    }

    public interface SelectionListener {
        void onItemSelected(int position, String value);
    }
}
