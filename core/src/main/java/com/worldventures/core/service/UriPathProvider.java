package com.worldventures.core.service;

import android.net.Uri;

import java.net.URISyntaxException;

public interface UriPathProvider {

   String getPath(Uri uri) throws URISyntaxException;
}
