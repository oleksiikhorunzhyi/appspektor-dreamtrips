package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import org.immutables.value.Value;

import java.io.Serializable;

@Value.Immutable
public interface Coordinates extends Serializable {

   Double lat();

   Double lng();
}
