package com.worldventures.dreamtrips.wallet.ui.common.helper;

public interface MessageProvider<T> {

   MessageProvider NULL = action -> null;

   String provide(T action);
}
