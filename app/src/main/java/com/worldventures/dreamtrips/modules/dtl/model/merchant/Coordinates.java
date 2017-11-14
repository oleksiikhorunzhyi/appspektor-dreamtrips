package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.Serializable;

@Gson.TypeAdapters
@Value.Immutable
public interface Coordinates extends Serializable {

   Double lat();

   Double lng();
}
