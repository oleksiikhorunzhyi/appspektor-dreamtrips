package com.messenger.ui.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class DateViewHolder extends ViewHolder {
    @InjectView(R.id.chat_date)
    public TextView dateTextView;

    public DateViewHolder(View itemView) {
        super(itemView);
    }
}