package com.worldventures.dreamtrips.modules.dtl.validator;

import com.rengwuxian.materialedittext.validation.RegexpValidator;

public class AmountValidator extends RegexpValidator {

   private static final String DOUBLE_PATTERN = "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
         "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
         "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
         "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";

   public AmountValidator(String errorMessage) {
      super(errorMessage, DOUBLE_PATTERN);
   }
}
