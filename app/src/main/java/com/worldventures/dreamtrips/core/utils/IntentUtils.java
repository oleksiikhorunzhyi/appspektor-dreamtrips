package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.text.Html;
import android.text.TextUtils;

import java.util.Locale;

public class IntentUtils {

   private IntentUtils() {
      //nothing
   }

   public static Intent newEmailIntent(String subject, String body, String... addresses) {
      Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));

      if (addresses != null && addresses.length == 1) {
         intent.putExtra(Intent.EXTRA_EMAIL, addresses);
      } else {
         intent.putExtra(Intent.EXTRA_BCC, addresses);
      }
      intent.putExtra(Intent.EXTRA_SUBJECT, subject);
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
         intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body,Html.FROM_HTML_MODE_LEGACY));
      } else {
         intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
      }
      return intent;
   }

   public static Intent newDialerIntent(String phone) {
      if (TextUtils.isEmpty(phone)) return null;
      Intent intent = new Intent(Intent.ACTION_DIAL);
      intent.setData(Uri.parse("tel:" + phone));
      return intent;
   }

   public static Intent newMapIntent(double latitude, double longitude) {
      String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
      return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
   }


   public static Intent newSmsIntent(Context context, String body, String... phoneNumber) {
      Uri smsUri;
      if (phoneNumber == null) {
         smsUri = Uri.parse("smsto:");
      } else {
         smsUri = Uri.parse("smsto:" + Uri.encode(TextUtils.join(",", phoneNumber)));
      }
      Intent intent;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
         intent = new Intent(Intent.ACTION_SENDTO, smsUri);
         intent.setPackage(Telephony.Sms.getDefaultSmsPackage(context));
      } else {
         intent = new Intent(Intent.ACTION_VIEW, smsUri);
      }
      intent.putExtra("sms_body", body);
      return intent;
   }

   public static Intent callIntnet(String phone) {
      return new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
   }

   public static Intent browserIntent(String url) {
      return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
   }
}

