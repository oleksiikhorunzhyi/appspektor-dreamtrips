package com.worldventures.wallet.domain;

import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

public class WalletConstants {

   public static final String WALLET_COMPONENT = "Wallet";

   public static final int MAX_CARD_LIMIT = 20;
   public static final int AUTO_SYNC_PERIOD_MINUTES = 10;
   public static final int SMART_CARD_DEFAULT_DISPLAY_TYPE = SetHomeDisplayTypeAction.DISPLAY_NAME_ONLY;

   public static final int WALLET_FEEDBACK_MAX_PHOTOS_ATTACHMENT = 5;
}
