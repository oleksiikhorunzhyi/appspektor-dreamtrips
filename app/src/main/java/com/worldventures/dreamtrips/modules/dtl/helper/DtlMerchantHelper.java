package com.worldventures.dreamtrips.modules.dtl.helper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.DayOfWeek;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationHours;
import com.worldventures.dreamtrips.util.ImageTextItem;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DtlMerchantHelper {

    private Context context;

    public static final DateTimeFormatter OPERATION_TIME_FORMATTER = DateTimeFormat.forPattern("hh:mm a");

    public DtlMerchantHelper(Context context) {
        this.context = context;
    }

    public String getCategories(DtlMerchant merchant) {
        List<DtlMerchantAttribute> categories = merchant.getCategories();
        return categories == null ? null : TextUtils.join(", ", categories);
    }

    public List<ImageTextItem> getContactsData(DtlMerchant merchant) {
        ArrayList<ImageTextItem> items = new ArrayList<>();
        addContactIfNotEmpty(items, String.format("%s, %s, %s, %s", merchant.getAddress1(), merchant.getCity(),
                        merchant.getState(), merchant.getZip()),
                R.drawable.address_icon,
                IntentUtils.newMapIntent(merchant.getCoordinates().getLat(), merchant.getCoordinates().getLng()),
                ImageTextItem.Type.ADDRESS);
        addContactIfNotEmpty(items, merchant.getPhone(), R.drawable.phone_icon,
                IntentUtils.newDialerIntent(merchant.getPhone()),
                ImageTextItem.Type.PHONE_NUMBER);
        addContactIfNotEmpty(items, merchant.getWebsite(), R.drawable.website_icon,
                IntentUtils.browserIntent(merchant.getWebsite()),
                ImageTextItem.Type.WEBSITE_URL);
        return items;
    }

    private void addContactIfNotEmpty(List<ImageTextItem> items, String contact, @DrawableRes int icon,
                                      Intent intent, ImageTextItem.Type type) {
        if (TextUtils.isEmpty(contact)) return;
        items.add(new ImageTextItem(contact, ResourcesCompat.getDrawable(context.getResources(), icon, null), intent, type));
    }

    public Spannable getOperationalTime(DtlMerchant DtlMerchant) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean openNow = false;

        DayOfWeek current = DayOfWeek.from(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        OperationDay operationDay = Queryable.from(DtlMerchant.getOperationDays())
                .firstOrDefault(element -> element.getDayOfWeek() == current);

        if (operationDay != null && operationDay.getOperationHours() != null) {
            for (OperationHours hours : operationDay.getOperationHours()) {
                DateTimeZone timeZone = DateTimeZone.forOffsetHours(DtlMerchant.getOffsetHours());
                LocalTime localTimeStart = LocalTime.parse(hours.getFrom());
                LocalTime localTimeEnd = LocalTime.parse(hours.getTo());
                //
                DateTime dateTimeStart = DateTime.now(timeZone).withTime(localTimeStart.getHourOfDay(),
                        localTimeStart.getMinuteOfHour(), 0, 0);
                DateTime dateTimeEnd = DateTime.now(timeZone).withTime(localTimeEnd.getHourOfDay(),
                        localTimeEnd.getMinuteOfHour(), 0, 0);

                if (dateTimeEnd.isBefore(dateTimeStart)) {
                    dateTimeEnd = dateTimeEnd.withFieldAdded(DurationFieldType.days(), 1);
                }

                if (!openNow) {
                    DateTime currentDate = DateTime.now();
                    openNow = currentDate.isAfter(dateTimeStart)
                            && currentDate.isBefore(dateTimeEnd);
                }

                stringBuilder.append(String.format("%s - %s",
                        localTimeStart.toString(OPERATION_TIME_FORMATTER),
                        localTimeEnd.toString(OPERATION_TIME_FORMATTER)));
                stringBuilder.append(", ");
            }
        }

        int length = stringBuilder.length();
        stringBuilder.append(provideOpenClosedStatus(openNow));

        final ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources()
                .getColor(openNow ? R.color.open : R.color.closed));
        Spannable spannable = new SpannableString(stringBuilder);
        spannable.setSpan(fcs, length, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannable;
    }

    private String provideOpenClosedStatus(boolean openNow) {
        return openNow ?
                context.getString(R.string.dtl_open_now) :
                context.getString(R.string.dtl_closed);
    }

}