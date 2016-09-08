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
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MerchantHelper {

   public static final DateTimeFormatter OPERATION_TIME_FORMATTER = DateTimeFormat.forPattern("hh:mm a");

   private MerchantHelper() {
      throw new UnsupportedOperationException("No instance");
   }

   public static String getCategories(DtlMerchant merchant) {
      List<DtlMerchantAttribute> categories = merchant.getCategories();
      return categories == null ? null : TextUtils.join(", ", categories);
   }

   public static String getCategories(Merchant merchant) {
      if (merchant.categories() == null) return null;
      List<String> categories =  Queryable.from(merchant.categories()).map(ThinAttribute::name).toList();
      return TextUtils.join(", ", categories);
   }

   public static List<ImageTextItem> getContactsData(Context context, Merchant merchant) {
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
      return getOperationalTime(context, merchant.getOperationDays(), merchant.getOffsetHours(), true);
   }

   public static Spannable getOperationalTime(Context context, DtlMerchant merchant, boolean includeTime) throws Exception {
      return getOperationalTime(context, merchant.getOperationDays(), merchant.getOffsetHours(), includeTime);
   }

   public static Spannable getOperationalTime(Context context, Merchant merchant) throws Exception {
      return getOperationalTime(context, merchant.operationDays(), merchantTimeOffset(merchant), true);
   }

   public static boolean isOfferExpiringSoon(Offer offerData) {
      if (offerData.endDate() == null) return false;
      DateTime currentDate = DateTime.now();
      DateTime expirationDate = new DateTime(offerData.endDate().getTime());
      return Days.daysBetween(currentDate, expirationDate).isLessThan(Days.SEVEN);
   }

   public static Spannable getOfferExpiringCaption(Context context, Offer offerData, Locale locale) {
      return getOfferExpiringCaption(context.getResources(), offerData, locale);
   }

   public static Spannable getOfferExpiringCaption(Resources resources, Offer offerData, Locale locale) {
      String format = resources.getString(R.string.offer_expiration_format);
      DateTime expiringDate = new DateTime(offerData.endDate()
            .getTime(), ISOChronology.getInstance(DateTimeZone.UTC));
      String caption = expiringDate.toString(DateTimeFormat.forPattern("MMM d"));
      String captionFormatted = String.format(locale, format, caption);
      Spannable spanned = new SpannableString(captionFormatted);
      spanned.setSpan(new StyleSpan(Typeface.BOLD), captionFormatted.length() - caption.length(), captionFormatted.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
      return spanned;
   }

   public static Spannable getOperationalTime(Context context, List<OperationDay> operationDays, int timezone, boolean includeTime) throws Exception {
      StringBuilder stringBuilder = new StringBuilder();
      boolean openNow = false;

      DayOfWeek current = DayOfWeek.from(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

      OperationDay operationDay = Queryable.from(operationDays == null ? Queryable.empty() : operationDays)
            .firstOrDefault(element -> element.dayOfWeek() == current);

      if (operationDay != null && operationDay.operationHours() != null) {
         for (OperationHours hours : operationDay.operationHours()) {
            DateTimeZone timeZone = DateTimeZone.forOffsetHours(timezone);
            LocalTime localTimeStart = LocalTime.parse(hours.from());
            LocalTime localTimeEnd = LocalTime.parse(hours.to());
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
            .map(MerchantHelper::getFormattedHours)
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
         LocalTime localTimeStart = LocalTime.parse(hours.from());
         LocalTime localTimeEnd = LocalTime.parse(hours.to());
         //
         DateTime start = DateTime.now()
               .withTime(localTimeStart.getHourOfDay(), localTimeStart.getMinuteOfHour(), 0, 0);
         DateTime end = DateTime.now().withTime(localTimeEnd.getHourOfDay(), localTimeEnd.getMinuteOfHour(), 0, 0);
         //
         return String.format("%s - %s", start.toString(OPERATION_TIME_FORMATTER), end.toString(OPERATION_TIME_FORMATTER));
      } catch (Exception e) {
         return "";
      }
   }

   public static ShareBundle buildShareBundle(Context context, Merchant merchant, @ShareType String type) {
      ShareBundle shareBundle = new ShareBundle();
      shareBundle.setShareType(type);
      shareBundle.setText(context.getString(merchantHasPoints(merchant) ? R.string.dtl_details_share_title : R.string.dtl_details_share_title_without_points, merchant
            .displayName()));
      shareBundle.setShareUrl(merchant.website());
      // don't attach media if website is attached, this image will go nowhere
      if (TextUtils.isEmpty(merchant.website()) || type.equals(ShareType.TWITTER)) {
         MerchantMedia media = Queryable.from(merchant.images()).firstOrDefault();
         if (media != null) shareBundle.setImageUrl(media.getImagePath());
         // for twitter: sharing image via web (not official app) currently not supported (android sdk v1.9.1)
      }
      return shareBundle;
   }

   public static List<String> buildExpandedOffersIds(String id) {
      return id != null ? Collections.singletonList(id) : null;
   }

   public static boolean merchantHasPoints(Merchant merchant) {
      return merchantOffersCount(merchant, OfferType.POINTS) > 0;
   }

   public static boolean merchantHasPerks(Merchant merchant) {
      return merchantOffersCount(merchant, OfferType.PERK) > 0;
   }

   public static int merchantOffersCount(Merchant merchant, OfferType type) {
      return !merchantHasOffers(merchant) ? 0 : Queryable.from(merchant.offers()).filter(offer -> offer.type() == type).count();
   }

   public static boolean merchantHasOffers(Merchant merchant) {
      return merchant != null && merchant.offers() != null && !merchant.offers().isEmpty();
   }

   public static boolean merchantHasOperationDays(Merchant merchant) {
      return merchant.operationDays() != null && !merchant.operationDays().isEmpty();
   }

   public static Currency merchantDefaultCurrency(Merchant merchant) {
      return Queryable.from(merchant.currencies()).first(Currency::isDefault);
   }

   public static int merchantTimeOffset(Merchant merchant) {
      try {
         return Integer.valueOf(merchant.timeZone());
      } catch (Exception e) {
         return 0;
      }
   }

}
