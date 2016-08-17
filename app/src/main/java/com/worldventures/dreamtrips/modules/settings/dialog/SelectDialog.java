package com.worldventures.dreamtrips.modules.settings.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragment;

import java.util.ArrayList;

public class SelectDialog extends BaseDialogFragment {

   private static final String MODEL = "model";
   private static final String SELECTED_POSITION = "selectedPosition";

   private int titleId;
   private ArrayList<String> items;
   private int selectedPosition;
   private ButtonListener buttonListener;

   public static SelectDialog newInstance(SelectDialogModel model) {
      SelectDialog dialog = new SelectDialog();
      Bundle args = new Bundle();
      args.putParcelable(MODEL, model);
      dialog.setArguments(args);
      return dialog;
   }

   public SelectDialog() {
      super();
      injectCustomLayout = false;
   }

   public void setButtonListener(ButtonListener buttonListener) {
      this.buttonListener = buttonListener;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (savedInstanceState != null) dismiss(); //temp while dialog opens from cell
      Bundle args = getArguments();
      SelectDialogModel model = args.getParcelable(MODEL);
      titleId = model.getTitleId();
      items = model.getItems();
      selectedPosition = savedInstanceState != null ? savedInstanceState.getInt(SELECTED_POSITION) : model.getSelectedPosition();
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      outState.putInt(SELECTED_POSITION, selectedPosition);
      super.onSaveInstanceState(outState);
   }

   @NonNull
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      ListView listView = new ListView(getContext());
      listView.setDivider(null);
      listView.setDividerHeight(0);
      listView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.adapter_item_single_select, android.R.id.text1, items));
      listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      listView.setItemChecked(selectedPosition, true);
      listView.setOnItemClickListener((parent, view, position, id) -> selectedPosition = position);
      //
      MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity()).title(titleId)
            .customView(listView, false)
            .positiveText(R.string.ok)
            .negativeText(R.string.action_cancel)
            .onPositive((dialog1, which) -> {
               if (buttonListener != null) {
                  buttonListener.onPositiveClick(listView.getCheckedItemPosition());
               }
            });
      return dialog.show();
   }

   public interface ButtonListener {
      void onPositiveClick(int checkedPosition);
   }
}
