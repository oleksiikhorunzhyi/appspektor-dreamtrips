package com.worldventures.wallet.service.command.profile;

import com.worldventures.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
public interface ChangedFields {

   String firstName();

   String middleName();

   String lastName();

   @Nullable
   SmartCardUserPhoto photo();

   @Nullable
   SmartCardUserPhone phone();
}