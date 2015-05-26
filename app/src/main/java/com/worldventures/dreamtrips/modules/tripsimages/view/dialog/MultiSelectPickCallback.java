package com.worldventures.dreamtrips.modules.tripsimages.view.dialog;

import android.net.Uri;
import android.support.v4.app.Fragment;

import java.util.List;

public interface MultiSelectPickCallback {
    void onResult(Fragment fm, List<Uri> list, String error);
}