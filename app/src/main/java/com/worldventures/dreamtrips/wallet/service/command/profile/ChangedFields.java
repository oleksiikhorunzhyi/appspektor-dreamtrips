package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
public interface ChangedFields {

      String firstName();

      String middleName();

      String lastName();

      @Nullable
      SmartCardUserPhoto photo();
   }