package com.worldventures.dreamtrips.modules.common.view.util;

import android.graphics.RectF;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.Position;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.TagPosition;

public class CoordinatesTransformer {

   public static Position convertToProportional(Position position, RectF imageBounds) {
      float propX = (position.getX() - imageBounds.left) / (imageBounds.right - imageBounds.left);
      float propY = (position.getY() - imageBounds.top) / (imageBounds.bottom - imageBounds.top);

      return new Position(propX, propY);
   }

   public static Position convertToAbsolute(Position position, RectF imageBounds) {
      float absX = ((imageBounds.right - imageBounds.left) * position.getX()) + imageBounds.left;
      float absY = ((imageBounds.bottom - imageBounds.top) * position.getY()) + imageBounds.top;

      return new Position(absX, absY);
   }

   public static TagPosition convertToAbsolute(TagPosition position, RectF imageBounds) {
      return new TagPosition(convertToAbsolute(position.getTopLeft(), imageBounds), convertToAbsolute(position.getBottomRight(), imageBounds));
   }

   public static TagPosition convertToProportional(TagPosition position, RectF imageBounds) {
      return new TagPosition(convertToProportional(position.getTopLeft(), imageBounds), convertToProportional(position.getBottomRight(), imageBounds));
   }
}
