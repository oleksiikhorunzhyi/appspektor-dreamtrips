package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.Serializable;

@Gson.TypeAdapters
@Value.Immutable
public interface FirmwareInfo extends Serializable {

   String id();

   String firmwareName();

   String firmwareVersion();

   String sdkVersion();

   int fileSize(); // bytes

   String releaseNotes();

   boolean isCompatible();

   String url();
}
