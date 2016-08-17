package com.worldventures.dreamtrips.core.api.error;

import java.util.Map;

public class FieldError {

   public final String field;
   public final String[] errors;

   FieldError(String field, String[] errors) {
      this.field = field;
      this.errors = errors;
   }

   public String getFirstMessage() {
      return errors[0];
   }

   static FieldError from(Map.Entry<String, String[]> entry) {
      return new FieldError(entry.getKey(), entry.getValue());
   }
}
