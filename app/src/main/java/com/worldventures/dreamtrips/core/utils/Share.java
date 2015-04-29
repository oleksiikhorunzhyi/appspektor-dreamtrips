package com.worldventures.dreamtrips.core.utils;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;

public class Share {

    private Share() {
        //nothing
    }

    public static Intent newEmailIntent(String[] addresses, String subject, String body) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_BCC, addresses);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
        i.setType(MIME_TYPE_EMAIL);

        return i;
    }


    public static Intent newSmsIntent(String[] phoneNumber, String body) {
        final Intent intent;
        if (phoneNumber.length == 0) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + TextUtils.join(",", phoneNumber)));
        }
        intent.putExtra("sms_body", body);
        return intent;
    }

    private static final String MIME_TYPE_EMAIL = "message/rfc822";
}

