package com.worldventures.dreamtrips.modules.dtl.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.DayOfWeek;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationHours;
import com.worldventures.dreamtrips.util.ImageTextItem;
import com.worldventures.dreamtrips.util.ImageTextItemFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DtlMerchantHelper {

   public static final DateTimeFormatter OPERATION_TIME_FORMATTER = DateTimeFormat.forPattern("hh:mm a");

   private DtlMerchantHelper() {
      throw new UnsupportedOperationException("No instance");
   }

   public static String getCategories(DtlMerchant merchant) {
      List<DtlMerchantAttribute> categories = merchant.getCategories();
      return categories == null ? null : TextUtils.join(", ", categories);
   }

   public static List<ImageTextItem> getContactsData(Context context, DtlMerchant merchant) {
      ArrayList<ImageTextItem> items = new ArrayList<>();
      Queryable.from(ImageTextItem.Type.values()).forEachR(type -> {
         ImageTextItem contact = ImageTextItemFactory.create(context, merchant, type);
         if (contact != null) items.add(contact);
      });
      return items;
   }

   public static boolean contactCanBeResolved(ImageTextItem contact, Activity activity) {
      return contact.intent != null && contact.intent.resolveActivityInfo(activity.getPackageManager(), 0) != null;
   }

   public static Spannable getOperationalTime(Context context, DtlMerchant merchant) throws Exception {
      return getOperationalTime(context, merchant, true);
   }

   public static boolean isOfferExpiringSoon(DtlOffer offerData) {
      if (offerData.getEndDate() == null) return false;
      DateTime currentDate = DateTime.now();
      DateTime expirationDate = new DateTime(offerData.getEndDate().getTime());
      return Days.daysBetween(currentDate, expirationDate).isLessThan(Days.SEVEN);
   }

   public static Spannable getOfferExpiringCaption(Context context, DtlOffer offerData, Locale locale) {
      return getOfferExpiringCaption(context.getResources(), offerData, locale);
   }

   public static Spannable getOfferExpiringCaption(Resources resources, DtlOffer offerData, Locale locale) {
      String format = resources.getString(R.string.offer_expiration_format);
      DateTime expiringDate = new DateTime(offerData.getEndDate()
            .getTime(), ISOChronology.getInstance(DateTimeZone.UTC));
      String caption = expiringDate.toString(DateTimeFormat.forPattern("MMM d"));
      String captionFormatted = String.format(locale, format, caption);
      Spannable spanned = new SpannableString(captionFormatted);
      spanned.setSpan(new StyleSpan(Typeface.BOLD), captionFormatted.length() - caption.length(), captionFormatted.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
      return spanned;
   }

   public static Spannable getOperationalTime(Context context, DtlMerchant dtlMerchant, boolean includeTime) throws Exception {
      StringBuilder stringBuilder = new StringBuilder();
      boolean openNow = false;

      DayOfWeek current = DayOfWeek.from(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

      OperationDay operationDay = Queryable.from(dtlMerchant.getOperationDays())
            .firstOrDefault(element -> element.getDayOfWeek() == current);

      if (operationDay != null && operationDay.getOperationHours() != null) {
         for (OperationHours hours : operationDay.getOperationHours()) {
            DateTimeZone timeZone = DateTimeZone.forOffsetHours(dtlMerchant.getOffsetHours());
            LocalTime localTimeStart = LocalTime.parse(hours.getFrom());
            LocalTime localTimeEnd = LocalTime.parse(hours.getTo());
            //
            DateTime dateTimeStart = DateTime.now(timeZone)
                  .withTime(localTimeStart.getHourOfDay(), localTimeStart.getMinuteOfHour(), 0, 0);
            DateTime dateTimeEnd = DateTime.now(timeZone)
                  .withTime(localTimeEnd.getHourOfDay(), localTimeEnd.getMinuteOfHour(), 0, 0);

            if (dateTimeEnd.isBefore(dateTimeStart)) {
               dateTimeEnd = dateTimeEnd.withFieldAdded(DurationFieldType.days(), 1);
            }

            if (!openNow) {
               DateTime currentDate = DateTime.now();
               openNow = currentDate.isAfter(dateTimeStart) && currentDate.isBefore(dateTimeEnd);
            }

            if (includeTime) {
               stringBuilder.append(String.format("%s - %s", localTimeStart.toString(OPERATION_TIME_FORMATTER), localTimeEnd
                     .toString(OPERATION_TIME_FORMATTER)));
               stringBuilder.append(", ");
            }
         }
      }

      int length = stringBuilder.length();
      stringBuilder.append(provideOpenClosedStatus(context, openNow));

      final ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources()
            .getColor(openNow ? R.color.open : R.color.closed));
      Spannable spannable = new SpannableString(stringBuilder);
      spannable.setSpan(fcs, length, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

      return spannable;
   }

   private static String provideOpenClosedStatus(Context context, boolean openNow) {
      return openNow ? context.getString(R.string.dtl_open_now) : context.getString(R.string.dtl_closed_now);
   }

   public static String formatOperationDayHours(Context context, List<OperationHours> hours) {
      if (hours == null || hours.isEmpty()) return context.getString(R.string.dtl_closed);
      //
      final List<String> workingHours = Queryable.from(hours)
            .map(DtlMerchantHelper::getFormattedHours)
            .filter(format -> !format.isEmpty())
            .toList();
      return TextUtils.join("\n", workingHours);
   }

   /**
    * Formatting merchant operation hours to format %s - %s. Return empty string if formatting failed
    *
    * @param hours merchant operation hours to format
    * @return formatted operation hours.
    */
   private static String getFormattedHours(OperationHours hours) {
      try {
         LocalTime localTimeStart = LocalTime.parse(hours.getFrom());
         LocalTime localTimeEnd = LocalTime.parse(hours.getTo());
         //
         DateTime start = DateTime.now()
               .withTime(localTimeStart.getHourOfDay(), localTimeStart.getMinuteOfHour(), 0, 0);
         DateTime end = DateTime.now().withTime(localTimeEnd.getHourOfDay(), localTimeEnd.getMinuteOfHour(), 0, 0);
         //
         return String.format(Locale.US, "%s - %s", start.toString(OPERATION_TIME_FORMATTER), end.toString(OPERATION_TIME_FORMATTER));
      } catch (Exception e) {
         return "";
      }
   }
}
