package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.NonNull;

import org.immutables.value.Value;

import java.io.Serializable;

@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
@Value.Immutable
public abstract class SmartCardUserPhoto implements Serializable {

   public static SmartCardUserPhoto of(@NonNull String uri) {
      return ImmutableSmartCardUserPhoto.of(uri);
   }

   @Value.Parameter
   public abstract String uri();
}
