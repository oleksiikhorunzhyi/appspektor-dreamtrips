package com.messenger.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;

public class Utils {

    public static void copyToClipboard(Context context, CharSequence sequence) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", sequence);
        clipboard.setPrimaryClip(clip);
    }

    public static boolean isFileUri(Uri uri) {
        return uri != null && uri.getScheme().equals("file");
    }
}
