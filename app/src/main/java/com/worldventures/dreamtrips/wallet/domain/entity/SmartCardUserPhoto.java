package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

import java.io.File;

@Value.Immutable
public interface SmartCardUserPhoto {

   @Value.Parameter
   @Nullable
   File original();

   @Nullable
   File monochrome();//pending value

   @Nullable
   String photoUrl();
}
