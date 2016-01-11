package com.worldventures.dreamtrips.modules.common.view.util;

import android.graphics.RectF;

import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

public class CoordinatesTransformer {

    public static PhotoTag.Position convertToProportional(PhotoTag.Position position, RectF imageBounds) {
        float propX = (position.getX() - imageBounds.left) / (imageBounds.right - imageBounds.left);
        float propY = (position.getY() - imageBounds.top) / (imageBounds.bottom - imageBounds.top);

        return new PhotoTag.Position(propX, propY);
    }

    public static PhotoTag.Position convertToAbsolute(PhotoTag.Position position, RectF imageBounds) {
        float absX = ((imageBounds.right - imageBounds.left) * position.getX()) + imageBounds.left;
        float absY = ((imageBounds.bottom - imageBounds.top) * position.getY()) + imageBounds.top;

        return new PhotoTag.Position(absX, absY);
    }

    public static PhotoTag.TagPosition convertToAbsolute(PhotoTag.TagPosition position, RectF imageBounds) {
        return new PhotoTag.TagPosition(convertToAbsolute(position.getTopLeft(), imageBounds),
                convertToAbsolute(position.getBottomRight(), imageBounds));
    }

    public static PhotoTag.TagPosition convertToProportional(PhotoTag.TagPosition position, RectF imageBounds) {
        return new PhotoTag.TagPosition(convertToProportional(position.getTopLeft(), imageBounds),
                convertToProportional(position.getBottomRight(), imageBounds));
    }
}
