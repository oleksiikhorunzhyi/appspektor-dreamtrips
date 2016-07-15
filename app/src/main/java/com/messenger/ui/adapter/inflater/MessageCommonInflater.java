package com.messenger.ui.adapter.inflater;

import android.content.res.Resources;
import android.view.View;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;

public class MessageCommonInflater {
    private final int messageVerticalPadding;

    protected final View itemView;

    public MessageCommonInflater(View itemView) {
        this.itemView = itemView;
        ButterKnife.inject(this, itemView);
        //
        Resources res = itemView.getResources();
        messageVerticalPadding = res.getDimensionPixelSize(R.dimen.chat_list_item_row_vertical_padding);
    }

    public void onCellBind(boolean previousMessageIsTheSameType) {
        setPaddings(previousMessageIsTheSameType);
    }

    protected void setPaddings(boolean previousMessageIsTheSameType) {
        itemView.setPadding(itemView.getPaddingLeft(),
                previousMessageIsTheSameType ? 0 : messageVerticalPadding,
                itemView.getPaddingRight(),
                itemView.getPaddingBottom());
    }
}
