package com.worldventures.wallet.service.command.settings.general.display.exception;

public class MissingUserPhoneException extends NullPointerException {
   public MissingUserPhoneException() {
      super("User must have a phone number in order to use chosen display option");
   }
}
