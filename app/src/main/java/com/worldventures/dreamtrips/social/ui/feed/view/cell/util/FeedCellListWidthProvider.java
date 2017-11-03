package com.worldventures.dreamtrips.social.ui.feed.view.cell.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;

import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Func0;

public class FeedCellListWidthProvider {

   private final Context context;
   private Resources res;

   private Map<Config, Func0<Integer>> configWidthMap = new HashMap();

   private int navDrawerWidth;
   private float timelineHorizontalMarginShare;

   public FeedCellListWidthProvider(Context context) {
      this.context = context;
      res = context.getResources();

      navDrawerWidth = res.getDimensionPixelSize(R.dimen.navigation_drawer_static_width);
      timelineHorizontalMarginShare = (float) res.getInteger(R.integer.feed_landscape_horizontal_margin) / 100;

      final Func0<Integer> screenWidthFunc = () -> getScreenWidth();
      final Func0<Integer> phoneLandscapeFunc = () -> getScreenWidth() - navDrawerWidth;
      final Func0<Integer> tabletTimelineFunc = () -> (int) (getScreenWidth() * (1 - 2 * timelineHorizontalMarginShare));
      final Func0<Integer> tabletLandscapeFeedFunc = () -> {
         int listWeight = res.getInteger(R.integer.feed_landscape_tablet_weight);
         int additionalInfoWeight = res.getInteger(R.integer.feed_landscape_tablet_additional_info_weight);
         return (getScreenWidth() - navDrawerWidth) * listWeight / (listWeight + additionalInfoWeight);
      };
      final Func0<Integer> tabletFeedItemDetailsFunc = () -> (int) (getScreenWidth()
            * res.getFraction(R.fraction.feed_details_content_tablet_landscape_width, 1, 1));

      putToConfigMap(new Config(FeedType.FEED, Device.PHONE, Orientation.PORTRAIT), screenWidthFunc);
      putToConfigMap(new Config(FeedType.FEED, Device.PHONE, Orientation.LANDSCAPE), phoneLandscapeFunc);
      putToConfigMap(new Config(FeedType.FEED, Device.TABLET, Orientation.PORTRAIT), screenWidthFunc);
      putToConfigMap(new Config(FeedType.FEED, Device.TABLET, Orientation.LANDSCAPE), tabletLandscapeFeedFunc);

      putToConfigMap(new Config(FeedType.TIMELINE, Device.PHONE, Orientation.PORTRAIT), screenWidthFunc);
      putToConfigMap(new Config(FeedType.TIMELINE, Device.PHONE, Orientation.LANDSCAPE), phoneLandscapeFunc);
      putToConfigMap(new Config(FeedType.TIMELINE, Device.TABLET, Orientation.PORTRAIT), tabletTimelineFunc);
      putToConfigMap(new Config(FeedType.TIMELINE, Device.TABLET, Orientation.LANDSCAPE), tabletTimelineFunc);

      putToConfigMap(new Config(FeedType.FEED_DETAILS, Device.PHONE, Orientation.PORTRAIT), screenWidthFunc);
      putToConfigMap(new Config(FeedType.FEED_DETAILS, Device.PHONE, Orientation.LANDSCAPE), phoneLandscapeFunc);
      putToConfigMap(new Config(FeedType.FEED_DETAILS, Device.TABLET, Orientation.PORTRAIT), screenWidthFunc);
      putToConfigMap(new Config(FeedType.FEED_DETAILS, Device.TABLET, Orientation.LANDSCAPE), tabletFeedItemDetailsFunc);
   }

   public int getScreenWidth() {
      WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      Point point = new Point();
      windowManager.getDefaultDisplay().getSize(point);
      return point.x;
   }

   private void putToConfigMap(Config config, Func0<Integer> width) {
      configWidthMap.put(config, width);
   }

   public int getFeedCellWidth(FeedCellListWidthProvider.FeedType route) {
      Config config = makeConfig(route);
      Func0<Integer> widthFunc = configWidthMap.get(config);
      if (widthFunc == null) {
         throw new IllegalStateException("No width func found for route. Supply corresponding config.");
      }
      return widthFunc.call();
   }

   private Config makeConfig(FeedCellListWidthProvider.FeedType feedType) {
      Config config = new Config();
      config.feedType = feedType;
      config.device = ViewUtils.isTablet(context) ? Device.TABLET : Device.PHONE;
      config.orientation = ViewUtils.isLandscapeOrientation(context) ? Orientation.LANDSCAPE : Orientation.PORTRAIT;
      return config;
   }

   public static class Config {
      FeedType feedType;
      Device device;
      Orientation orientation;

      Config() {
      }

      public Config(FeedType feedType, Device device, Orientation orientation) {
         this.feedType = feedType;
         this.device = device;
         this.orientation = orientation;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         }
         if (o == null || getClass() != o.getClass()) {
            return false;
         }

         Config config = (Config) o;

         if (feedType != config.feedType) {
            return false;
         }
         if (device != config.device) {
            return false;
         }
         return orientation == config.orientation;

      }

      @Override
      public int hashCode() {
         int result = feedType.hashCode();
         result = 31 * result + device.hashCode();
         result = 31 * result + orientation.hashCode();
         return result;
      }

      @Override
      public String toString() {
         return "Config{"
               + "feedType=" + feedType
               + ", device=" + device
               + ", orientation=" + orientation
               + '}';
      }
   }

   public enum FeedType {
      FEED,
      TIMELINE,
      FEED_DETAILS
   }

   public enum Device {
      PHONE,
      TABLET
   }

   public enum Orientation {
      PORTRAIT,
      LANDSCAPE
   }
}
