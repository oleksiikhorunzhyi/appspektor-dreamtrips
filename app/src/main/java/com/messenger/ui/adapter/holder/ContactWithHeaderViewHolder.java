package com.messenger.ui.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class ContactWithHeaderViewHolder extends BaseViewHolder {

    @InjectView(R.id.section_name_textview)
    TextView sectionNameTextView;

    public ContactWithHeaderViewHolder(View itemView) {
        super(itemView);
    }

    public TextView getSectionNameTextView() {
        return sectionNameTextView;
    }
}
