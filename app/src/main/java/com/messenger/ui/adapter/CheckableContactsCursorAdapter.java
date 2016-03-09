package com.messenger.ui.adapter;

import android.content.Context;

import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.cell.CheckableUserCell;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;

import java.util.ArrayList;
import java.util.List;

public class CheckableContactsCursorAdapter extends BaseDelegateAdapter<DataUser> {

    public interface SelectionListener {
        void onSelectionStateChanged(List<DataUser> selectedContacts);
    }

    private List<DataUser> selectedContacts = new ArrayList<>();
    private SelectionListener selectionListener;

    public CheckableContactsCursorAdapter(Context context, Injector injector, List<DataUser> selectedContacts) {
        super(context, injector);
        this.selectedContacts = selectedContacts;
    }

    public void setSelectedContacts(List<DataUser> selectedContacts) {
        this.selectedContacts = selectedContacts;
        notifyDataSetChanged();
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @Override
    public void onBindViewHolder(AbstractCell cell, int position) {
        super.onBindViewHolder(cell, position);
        if (!(cell instanceof CheckableUserCell)) {
            return;
        }
        DataUser user = getItem(position);
        ((CheckableUserCell)cell).getTickImageView().setSelected(selectedContacts.contains(user));
        cell.itemView.setOnClickListener((v) -> {
            if (!selectedContacts.contains(user)) {
                selectedContacts.add(user);
            } else {
                selectedContacts.remove(user);
            }
            if (selectionListener != null) {
                selectionListener.onSelectionStateChanged(selectedContacts);
            }
        });
    }
}
