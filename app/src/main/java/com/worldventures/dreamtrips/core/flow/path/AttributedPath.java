package com.worldventures.dreamtrips.core.flow.path;

import android.support.v7.app.AppCompatActivity;

public interface AttributedPath {

   PathAttrs getAttrs();

   public void onPreDispatch(AppCompatActivity activity);
}
