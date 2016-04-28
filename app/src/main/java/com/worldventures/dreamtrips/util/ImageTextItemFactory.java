package com.worldventures.dreamtrips.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

public class ImageTextItemFactory {

    private ImageTextItemFactory() {
        throw new IllegalArgumentException("no instance");
    }

    public static ImageTextItem create(Context context, DtlMerchant merchant, ImageTextItem.Type type) {
        switch (type) {
            case ADDRESS:
                return create(context, String.format("%s, %s, %s, %s", merchant.getAddress1(), merchant.getCity(),
                        merchant.getState(), merchant.getZip()), R.drawable.address_icon,
                        IntentUtils.newMapIntent(merchant.getCoordinates().getLat(), merchant.getCoordinates().getLng()),
                        type);
            case PHONE_NUMBER:
                return create(context, merchant.getPhone(), R.drawable.phone_icon,
                        IntentUtils.newDialerIntent(merchant.getPhone()), type);
            case WEBSITE_URL:
                return create(context, merchant.getWebsite(), R.drawable.website_icon,
                        IntentUtils.browserIntent(merchant.getWebsite()),
                        type);
        }
        return null;
    }

    private static ImageTextItem create(Context context, String contact, @DrawableRes int icon, Intent intent, ImageTextItem.Type type) {
        if (TextUtils.isEmpty(contact)) return null;
        return new ImageTextItem(contact, ResourcesCompat.getDrawable(context.getResources(), icon, null), intent, type);
    }
}
