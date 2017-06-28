package com.worldventures.dreamtrips.modules.common.delegate.system;

import android.net.Uri;

import java.net.URISyntaxException;

public interface UriPathProvider {

   String getPath(Uri uri) throws URISyntaxException;
}
