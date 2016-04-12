package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.view.View;
import android.widget.ViewSwitcher;

import com.messenger.messengerservers.constant.MessageStatus;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import butterknife.OnClick;

public class OwnTextMessageViewHolder extends TextMessageViewHolder {

    @InjectView(R.id.view_switcher)
    ViewSwitcher retrySwitcher;

    public OwnTextMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        super.bindCursor(cursor);
        updateMessageStatusUi();
    }

    @Override
    protected int provideBackgroundForFollowing() {
        return selected ? R.drawable.dark_blue_bubble : R.drawable.blue_bubble;
    }

    @Override
    protected int provideBackgroundForInitial() {
        return selected ? R.drawable.dark_blue_bubble_comics : R.drawable.blue_bubble_comics;
    }

    @OnClick(R.id.retry)
    void onRetry() {
        cellDelegate.onRetryClicked(dataMessage);
    }

    public void updateMessageStatusUi() {
        boolean isError = dataMessage.getStatus() == MessageStatus.ERROR;
        int viewVisible = isError ? View.VISIBLE : View.GONE;
        if (viewVisible != retrySwitcher.getVisibility()) {
            retrySwitcher.setVisibility(viewVisible);
        }
        if (isError && retrySwitcher.getCurrentView().getId() == R.id.progress_bar) {
            retrySwitcher.showPrevious();
        }
    }
}