package com.worldventures.dreamtrips.wallet.service.command.settings.general.display.exception;

public class MissingUserPhotoException extends NullPointerException {
   public MissingUserPhotoException() {
      super("User must have a photo in order to use chosen display option");
   }
}
