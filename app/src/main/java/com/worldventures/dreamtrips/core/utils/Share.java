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

    private static final String MIME_TYPE_EMAIL = "message/rfc822";

    public static Intent newEmailIntent(String[] addresses, String subject, String body) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_BCC, addresses);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
        i.setType(MIME_TYPE_EMAIL);

        return i;
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

