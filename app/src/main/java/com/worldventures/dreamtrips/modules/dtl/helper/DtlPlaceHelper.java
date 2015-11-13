package com.worldventures.dreamtrips.modules.dtl.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.modules.dtl.model.DayOfWeek;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.OperationDay;
import com.worldventures.dreamtrips.util.ImageTextItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DtlPlaceHelper {

    private Context context;

    public DtlPlaceHelper(Context context) {
        this.context = context;
    }

    public String getCategories(DtlPlace place) {
        List<DtlPlaceAttribute> attributes = place.getAttributes();
        if (attributes == null) return null;
        DtlPlaceAttribute category = Queryable.from(attributes).firstOrDefault(element ->
                element.getName().equals("categories"));
        return category == null ? null : TextUtils.join(", ", category.getAttributes());
    }

    public List<ImageTextItem> getContactsData(DtlPlace place) {
        ArrayList<ImageTextItem> items = new ArrayList<>();
        addContactIfNotEmpty(items, place.getAddress1(), R.drawable.address_icon, null);
        addContactIfNotEmpty(items, place.getPhone(), R.drawable.phone_icon, IntentUtils.newDialerIntent(place.getPhone()));
        addContactIfNotEmpty(items, place.getWebsite(), R.drawable.website_icon, null);
        return items;
    }

    private void addContactIfNotEmpty(List<ImageTextItem> items, String contact, @DrawableRes int icon, Intent intent) {
        if (TextUtils.isEmpty(contact)) return;
        items.add(new ImageTextItem(contact, ResourcesCompat.getDrawable(context.getResources(), icon, null), intent));
    }

    public String getOperationalTime(DtlPlace dtlPlace) {
        Calendar today = Calendar.getInstance();

        DayOfWeek current = DayOfWeek.from(today.get(Calendar.DAY_OF_WEEK));

        OperationDay operationDay = Queryable.from(dtlPlace.getOperationDays())
                .firstOrDefault(element -> element.getDayOfWeek() == current);

        boolean openNow = false;

        StringBuilder stringBuilder = new StringBuilder();

        if (operationDay != null && operationDay.getOperationHours() != null) {
            openNow = operationDay.openNow();

            Queryable.from(operationDay.getOperationHours()).forEachR(operationHour -> {
                stringBuilder.append(String.format("%s - %s",
                        operationHour.getStartTime(),
                        operationHour.getEndTime()));
                stringBuilder.append(",");
            });
        }

        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(83, 181, 254));
        Spannable openNowCaption = new SpannableString(openNow ?
                context.getString(R.string.dtl_open_now) :
                context.getString(R.string.dtl_closed));
        openNowCaption.setSpan(fcs, 0, openNowCaption.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        stringBuilder.append(openNowCaption);

        return stringBuilder.toString();
    }
}
