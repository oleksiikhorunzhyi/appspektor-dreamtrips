package com.worldventures.dreamtrips.wallet.domain.entity;

import org.immutables.value.Value;

import java.io.File;
import java.io.Serializable;

@Value.Immutable
public interface FirmwareDescriptor extends Serializable {

   File firmwareFile();

   String sdkVersion();

   String firmwareVersion();

}
