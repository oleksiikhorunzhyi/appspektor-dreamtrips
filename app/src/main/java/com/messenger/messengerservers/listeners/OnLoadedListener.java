package com.messenger.messengerservers.listeners;

import java.util.List;

public interface OnLoadedListener<T> {

   void onLoaded(List<T> entities);

   void onError(Exception e);
}
