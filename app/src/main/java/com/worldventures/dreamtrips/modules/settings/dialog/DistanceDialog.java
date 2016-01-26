package com.worldventures.dreamtrips.modules.settings.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragment;

import icepick.Icepick;
//String[] items = {getResources().getString(R.string.settings_kilometers), getResources().getString(R.string.settings_miles)};
//DistanceDialog dialog = DistanceDialog.create(items, 1);
//dialog.show(getFragmentManager());
public class DistanceDialog extends BaseDialogFragment{

    /**
     * IcePick does not want to save String[]
     */
    private static final String KEY_ITEMS = "KEY_ITEMS_" + DistanceDialog.class.getSimpleName();

    String[] items;
    int selectedPosition;

    public static DistanceDialog create (String[] items, int selectedPosition){
        DistanceDialog dialog = new DistanceDialog();
        dialog.items = items;
        dialog.selectedPosition = selectedPosition;
        return dialog;
    }

    public DistanceDialog() {
        super();
        injectCustomLayout = false;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (savedInstanceState != null){
            items = savedInstanceState.getStringArray(KEY_ITEMS);
        }

        AlertDialog.Builder dialog =  new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.show_distance_in);
        dialog.setSingleChoiceItems(items, selectedPosition, (dialogInterface, which) -> {
            //todo
            dialogInterface.cancel();
        });
        return dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(KEY_ITEMS, items);
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
