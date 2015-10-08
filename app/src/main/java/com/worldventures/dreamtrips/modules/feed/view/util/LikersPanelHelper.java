package com.worldventures.dreamtrips.modules.feed.view.util;

import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.seppius.i18n.plurals.PluralResources;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

import timber.log.Timber;

public class LikersPanelHelper {

    public void setup(TextView panel, FeedEntity feedEntity) {
        String firstLiker = feedEntity.getFirstUserLikedItem();
        int likesCount = feedEntity.getLikesCount();
        if (likesCount == 0) {
            panel.setVisibility(View.GONE);
            return;
        }
        //
        String appeal;
        int stringRes;
        if (feedEntity.isLiked()) {
            stringRes = R.plurals.account_who_liked_item;
            appeal = panel.getResources().getString(R.string.you);
        } else {
            stringRes = R.plurals.users_who_liked_with_name;
            appeal = firstLiker;
        }
        Spanned text = null;
        try {
            text = Html.fromHtml(new PluralResources(panel.getResources()).getQuantityString(stringRes, likesCount - 1, appeal, likesCount - 1));
        } catch (NoSuchMethodException e) {
            Timber.e(e, "Problem with plural for likers panel");
        }
        panel.setText(text);
        panel.setVisibility(View.VISIBLE);
    }

}
