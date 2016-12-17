package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

import java.io.File;
import java.io.Serializable;

@Value.Immutable
public interface SmartCardUserPhoto extends Serializable {

   @Value.Parameter
   @Nullable
   File original();

   @Nullable
   File monochrome();//pending value

   @Nullable
   String photoUrl();
}
