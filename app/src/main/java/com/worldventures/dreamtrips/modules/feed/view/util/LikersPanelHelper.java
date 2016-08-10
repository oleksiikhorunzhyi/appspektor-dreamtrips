package com.worldventures.dreamtrips.modules.feed.view.util;

import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class LikersPanelHelper {

    private final int INVISIBILITY;

    private LikersPanelListener likersPanelListener;

    public LikersPanelHelper() {
        this(View.GONE);
    }

    public LikersPanelHelper(int invisibility) {
        this.INVISIBILITY = invisibility;
    }

    public void setup(TextView panel, FeedEntity feedEntity) {
        int likesCount = feedEntity.getLikesCount();
        if (likesCount == 0) {
            panel.setVisibility(INVISIBILITY);
            return;
        }

        String appeal;
        @StringRes int stringRes;
        if (feedEntity.isLiked()) {
            stringRes = QuantityHelper.chooseResource(likesCount - 1, R.string.account_who_liked_item_zero,
                    R.string.account_who_liked_item_one, R.string.account_who_liked_item_other);
            appeal = panel.getResources().getString(R.string.you);
        } else {
            stringRes = QuantityHelper.chooseResource(likesCount - 1, R.string.users_who_liked_with_name_zero,
                    R.string.users_who_liked_with_name_one, R.string.users_who_liked_with_name_other);
            appeal = feedEntity.getFirstLikerName();
        }

        if (appeal != null) {
            Spanned text = Html.fromHtml(String.format(panel.getResources().getString(stringRes), appeal, likesCount - 1));
            panel.setText(text);
            panel.setVisibility(View.VISIBLE);
        }

        if (likersPanelListener != null) likersPanelListener.onLikersPanelSetted();
    }

    public void setLikersPanelListener(LikersPanelListener likersPanelListener) {
        this.likersPanelListener = likersPanelListener;
    }

    public interface LikersPanelListener {

        void onLikersPanelSetted();
    }
}
