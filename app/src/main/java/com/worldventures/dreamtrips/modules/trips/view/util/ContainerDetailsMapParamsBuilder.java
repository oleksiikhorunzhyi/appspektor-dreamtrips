package com.worldventures.dreamtrips.modules.trips.view.util;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Pair;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.map.TripMapDetailsAnchor;

import static com.worldventures.dreamtrips.modules.trips.view.util.ContainerDetailsMapParamsBuilder.PointPosition.BOTTOM_LEFT;
import static com.worldventures.dreamtrips.modules.trips.view.util.ContainerDetailsMapParamsBuilder.PointPosition.BOTTOM_RIGHT;
import static com.worldventures.dreamtrips.modules.trips.view.util.ContainerDetailsMapParamsBuilder.PointPosition.TOP_LEFT;
import static com.worldventures.dreamtrips.modules.trips.view.util.ContainerDetailsMapParamsBuilder.PointPosition.TOP_RIGHT;

@SuppressWarnings("PMD.GodClass") //TODO: Resolve this PMD error
public class ContainerDetailsMapParamsBuilder {

   public static final int DEFAULT_MARGIN = 20;

   public static final int TRIP_HEIGHT_DP = 100;
   public static final int TRIP_WIDTH_DP = 320;

   public static final int HORIZONTAL_OFFSET = 12;
   public static final int VERTICAL_OFFSET = 12;

   public static final int PIN_HEIGHT_DP = 26;
   public static final int PIN_WIDTH_DP = 24;
   public static final int CLUSTER_HEIGHT_DP = 48;
   public static final int CLUSTER_WIDTH_DP = 44;
   public static final int TRIANGLE_WIDTH_DP = 34;
   public static final int TRIANGLE_HEIGHT_DP = 17;

   private Context context;
   private Point point;
   private Rect rect;
   private int tripsCount;
   private boolean isTabletLandscape;

   public ContainerDetailsMapParamsBuilder context(Context context) {
      this.context = context;
      return this;
   }

   public ContainerDetailsMapParamsBuilder markerPoint(Point point) {
      this.point = point;
      return this;
   }

   public ContainerDetailsMapParamsBuilder mapRect(Rect rect) {
      this.rect = rect;
      return this;
   }

   public ContainerDetailsMapParamsBuilder tripsCount(int tripsCount) {
      this.tripsCount = tripsCount;
      return this;
   }

   public ContainerDetailsMapParamsBuilder tabletLandscape(boolean isTabletLandscape) {
      this.isTabletLandscape = isTabletLandscape;
      return this;
   }

   public Pair<FrameLayout.LayoutParams, TripMapDetailsAnchor> build() {
      return generateContainerLayoutParams();
   }

   private Pair<FrameLayout.LayoutParams, TripMapDetailsAnchor> generateContainerLayoutParams() {
      FrameLayout.LayoutParams params;
      TripMapDetailsAnchor tripMapDetailsAnchor = null;
      //
      int tripHeight = (int) ViewUtils.pxFromDp(context, TRIP_HEIGHT_DP);
      int tripsHeight = tripHeight * tripsCount;
      //
      if (isTabletLandscape) {
         params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
         //
         int tripWidth = (int) ViewUtils.pxFromDp(context, TRIP_WIDTH_DP);
         int markerHeight = (int) ViewUtils.pxFromDp(context, (tripsCount == 1 ? PIN_HEIGHT_DP : CLUSTER_HEIGHT_DP));
         int markerWidth = (int) ViewUtils.pxFromDp(context, (tripsCount == 1 ? PIN_WIDTH_DP : CLUSTER_WIDTH_DP));
         int triangleHeight = (int) ViewUtils.pxFromDp(context, TRIANGLE_HEIGHT_DP);
         int triangleWidth = (int) ViewUtils.pxFromDp(context, TRIANGLE_WIDTH_DP);
         //
         PointPosition pointPosition = getPointPosition();
         params.gravity = getContainerGravity(pointPosition);
         int horizontalMargin;
         int anchorMargin;
         if (canBeOnTop(tripHeight, tripWidth)) {
            int offset = tripWidth / 2;
            horizontalMargin = getContainerHorizontalMargin(pointPosition) - offset;
            anchorMargin = 0;
            //
            if (isOnTop(pointPosition)) {
               params.topMargin = point.y - tripHeight - triangleHeight - markerHeight - VERTICAL_OFFSET;
            } else {
               params.bottomMargin = rect.bottom - point.y + markerHeight + VERTICAL_OFFSET;
            }
         } else {
            horizontalMargin = getContainerHorizontalMargin(pointPosition) + markerWidth / 2 + HORIZONTAL_OFFSET;
            //
            if (isOnTop(pointPosition) && tripsHeight < point.y) {
               params.topMargin = point.y - tripsHeight / 2;
               anchorMargin = point.y - params.topMargin - markerHeight / 2 - triangleWidth / 2;
            } else if (isOnBottom(pointPosition) && (tripsHeight / 2) < rect.bottom - point.y) {
               params.bottomMargin = rect.bottom - point.y - tripsHeight / 2;
               int topMargin = rect.bottom - params.bottomMargin - tripsHeight;
               anchorMargin = point.y - topMargin - markerHeight / 2 - triangleWidth / 2;
            } else {
               params.topMargin = DEFAULT_MARGIN;
               params.bottomMargin = DEFAULT_MARGIN;
               int calculatedTopMargin = rect.bottom - params.bottomMargin - tripsHeight;
               anchorMargin = point.y - (isOnBottom(pointPosition) && params.topMargin + params.bottomMargin + tripsHeight < rect.bottom ? calculatedTopMargin : params.topMargin)
                     - markerHeight / 2 - triangleWidth / 2;
            }
         }
         if (isOnLeft(pointPosition)) {
            params.leftMargin = horizontalMargin;
         } else {
            params.rightMargin = horizontalMargin;
         }
         //
         tripMapDetailsAnchor = generateTripMapDetailsAnchor(pointPosition, tripHeight, tripWidth);
         tripMapDetailsAnchor.setMargin(anchorMargin);
         //
         params.height = rect.bottom - 2 * DEFAULT_MARGIN < tripsHeight ? rect.bottom - 2 * DEFAULT_MARGIN : tripsHeight;
         if (tripMapDetailsAnchor.getPointerPosition() == TripMapDetailsAnchor.Position.BOTTOM) {
            params.height += triangleHeight;
         }
         params.width = tripWidth;
         if (tripMapDetailsAnchor.getPointerPosition() == TripMapDetailsAnchor.Position.LEFT || tripMapDetailsAnchor.getPointerPosition() == TripMapDetailsAnchor.Position.RIGHT) {
            params.width += triangleHeight;
         }
      } else {
         params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
         //
         int tripDetailSpacing = (int) context.getResources().getDimension(R.dimen.map_trip_detail_spacing);
         //
         params.height = tripsHeight > rect.bottom - tripDetailSpacing ? rect.bottom - tripDetailSpacing : tripsHeight;
         params.gravity = Gravity.BOTTOM;
         params.topMargin = tripDetailSpacing;
      }
      return new Pair<>(params, tripMapDetailsAnchor);
   }

   private TripMapDetailsAnchor generateTripMapDetailsAnchor(PointPosition pointPosition, int tripHeight, int tripWidth) {
      TripMapDetailsAnchor.Position position = null;
      if (canBeOnTop(tripHeight, tripWidth)) {
         position = TripMapDetailsAnchor.Position.BOTTOM;
      } else if (isOnLeft(pointPosition)) {
         position = TripMapDetailsAnchor.Position.LEFT;
      } else if (isOnRight(pointPosition)) {
         position = TripMapDetailsAnchor.Position.RIGHT;
      }

      return new TripMapDetailsAnchor(position);
   }

   private boolean canBeOnTop(int tripHeight, int tripWidth) {
      return point.y - (tripsCount + 0.8) * tripHeight > 0 && point.x + tripWidth / 2 < rect.right && point.x - tripWidth / 2 > rect.left;
   }

   private int getContainerGravity(PointPosition pointPosition) {
      int gravity = 0;
      switch (pointPosition) {
         case TOP_LEFT:
            gravity = Gravity.TOP | Gravity.LEFT;
            break;
         case TOP_RIGHT:
            gravity = Gravity.TOP | Gravity.RIGHT;
            break;
         case BOTTOM_LEFT:
            gravity = Gravity.BOTTOM | Gravity.LEFT;
            break;
         case BOTTOM_RIGHT:
            gravity = Gravity.BOTTOM | Gravity.RIGHT;
            break;
         default:
            break;
      }

      return gravity;
   }

   private int getContainerHorizontalMargin(PointPosition pointPosition) {
      return isOnRight(pointPosition) ? (rect.right - point.x) : (rect.left + point.x);
   }

   private PointPosition getPointPosition() {
      boolean left, top;
      left = rect.centerX() > point.x;
      top = rect.centerY() > point.y;
      return top ? (left ? TOP_LEFT : TOP_RIGHT) : (left ? BOTTOM_LEFT : BOTTOM_RIGHT);
   }

   private boolean isOnTop(PointPosition pointPosition) {
      return pointPosition == TOP_LEFT || pointPosition == TOP_RIGHT;
   }

   private boolean isOnBottom(PointPosition pointPosition) {
      return pointPosition == BOTTOM_LEFT || pointPosition == BOTTOM_RIGHT;
   }

   private boolean isOnLeft(PointPosition pointPosition) {
      return pointPosition == TOP_LEFT || pointPosition == BOTTOM_LEFT;
   }

   private boolean isOnRight(PointPosition pointPosition) {
      return pointPosition == TOP_RIGHT || pointPosition == BOTTOM_RIGHT;
   }

   enum PointPosition {
      TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
   }
}
