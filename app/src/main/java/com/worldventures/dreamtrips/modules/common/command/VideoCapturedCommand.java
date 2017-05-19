package com.worldventures.dreamtrips.modules.common.command;


import android.net.Uri;

import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class VideoCapturedCommand extends ValueCommandAction<String> {

   public VideoCapturedCommand(String value) {
      super(value);
   }

   public Uri getUri() {
      return Uri.parse("file://" + getResult());
   }
}
