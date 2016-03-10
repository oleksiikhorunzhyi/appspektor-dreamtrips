package com.messenger.storage;

import android.content.ContentResolver;
import android.net.Uri;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.BuildConfig;

@ContentProvider(authority = MessengerDatabase.AUTHORITY,
        databaseName = MessengerDatabase.NAME,
        baseContentUri = ContentResolver.SCHEME_CONTENT)
@Database(name = MessengerDatabase.NAME, version = MessengerDatabase.VERSION)
public class MessengerDatabase {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final String NAME = "MessengerDatabase";
    public static final int VERSION = 3;

    public static Uri buildUri(String... paths) {
        return ContentUtils.buildUri(AUTHORITY, paths);
    }
}