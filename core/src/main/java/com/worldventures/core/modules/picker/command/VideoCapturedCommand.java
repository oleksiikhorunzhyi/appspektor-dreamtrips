package com.worldventures.core.modules.picker.command;


import android.net.Uri;

import com.worldventures.core.janet.ValueCommandAction;

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
