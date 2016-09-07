package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import org.immutables.value.Value;

@Value.Immutable
public interface Coordinates {

   Double lat();

   Double lng();
}
