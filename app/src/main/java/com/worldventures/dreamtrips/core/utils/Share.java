package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.text.Html;
import android.text.TextUtils;

public class Share {

    private Share() {
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
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intent.putExtra(Intent.EXTRA_HTML_TEXT, Html.fromHtml(body));
        }
        return intent;
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
}

