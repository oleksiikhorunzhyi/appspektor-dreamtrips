package com.techery.spares.utils.params;

import android.content.Intent;

public class ParamsBuilderCreator {
   public ParamsBuilder create(Intent intent) {
      return new ParamsBuilder(intent);
   }
}
