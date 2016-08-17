package com.worldventures.dreamtrips.modules.common.view.util;

/**
 * Immutable class for describing width and height dimensions in pixels.
 *
 * @see android.util.Size
 * Original class is for API >= 21. This fork for API >= 1
 */
public final class Size {

   private final int width;
   private final int height;

   /**
    * Create a new immutable Size instance.
    *
    * @param width  The width of the size, in pixels
    * @param height The height of the size, in pixels
    */
   public Size(int width, int height) {
      this.width = width;
      this.height = height;
   }

   /**
    * Get the width of the size (in pixels).
    *
    * @return width
    */
   public int getWidth() {
      return width;
   }

   /**
    * Get the height of the size (in pixels).
    *
    * @return height
    */
   public int getHeight() {
      return height;
   }

   /**
    * Check if this size is equal to another size.
    * <p>
    * Two sizes are equal if and only if both their widths and heights are
    * equal.
    * </p>
    * <p>
    * A size object is never equal to any other type of object.
    * </p>
    *
    * @return {@code true} if the objects were equal, {@code false} otherwise
    */
   @Override
   public boolean equals(final Object obj) {
      if (obj == null) {
         return false;
      }
      if (this == obj) {
         return true;
      }
      if (obj instanceof Size) {
         Size other = (Size) obj;
         return width == other.width && height == other.height;
      }
      return false;
   }

   /**
    * Return the size represented as a string with the format {@code "WxH"}
    *
    * @return string representation of the size
    */
   @Override
   public String toString() {
      return width + "x" + height;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      // assuming most sizes are <2^16, doing a rotate will give us perfect hashing
      return height ^ ((width << (Integer.SIZE / 2)) | (width >>> (Integer.SIZE / 2)));
   }
}