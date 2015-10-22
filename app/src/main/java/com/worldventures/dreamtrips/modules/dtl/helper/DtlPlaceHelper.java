package com.worldventures.dreamtrips.modules.dtl.helper;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceAttribute;
import com.worldventures.dreamtrips.util.ImageTextItem;

import java.util.ArrayList;
import java.util.List;

public class DtlPlaceHelper {

    private Context context;

    public DtlPlaceHelper(Context context) {
        this.context = context;
    }

    public String getFirstCategoryName(DtlPlace place) {
        List<DtlPlaceAttribute> categories = place.getAttributes();
        if (categories == null) return null;
        DtlPlaceAttribute category = Queryable.from(categories).firstOrDefault(element ->
                element.getName().equals("categories"));
        return category == null ? null : Queryable.from(category.getAttributes()).firstOrDefault();
    }

    public List<ImageTextItem> getContactsData(DtlPlace place) {
        ArrayList<ImageTextItem> items = new ArrayList<>();
        addContactIfNotEmpty(items, place.getAddress1(), R.drawable.address_icon);
        addContactIfNotEmpty(items, place.getPhone(), R.drawable.phone_icon);
        addContactIfNotEmpty(items, place.getWebsite(), R.drawable.website_icon);
        return items;
    }

    private void addContactIfNotEmpty(List<ImageTextItem> items, String contact, @DrawableRes int icon) {
        if (TextUtils.isEmpty(contact)) return;
        items.add(new ImageTextItem(contact, ResourcesCompat.getDrawable(context.getResources(), icon, null)));
    }
}
