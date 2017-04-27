package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;

import java.util.List;
import java.util.Locale;

public class WalletHelpVideoDelegate {

   private final Context context;

   private List<VideoLocale> videoLocales = null;
   private VideoLocale lastVideoLocale = null;

   public WalletHelpVideoDelegate(Context context) {
      this.context = context;
   }

   VideoLanguage getDefaultLanguage(final List<VideoLocale> videoLocales) {
      VideoLocale videoLocale = null;
      if (videoLocales != null && !videoLocales.isEmpty()) {
         videoLocale = Queryable.from(videoLocales).firstOrDefault(element -> element.getCountry()
               .equalsIgnoreCase(Locale.getDefault().getCountry()));
      }

      //for retry when HttpError
      if (videoLocale == null && lastVideoLocale != null) videoLocale = lastVideoLocale;

      return getDefaultLanguage(videoLocale);
   }

   private VideoLanguage getDefaultLanguage(VideoLocale videoLocale) {
      if (videoLocale != null) {
         final VideoLanguage videoLanguage = Queryable.from(videoLocale.getLanguages())
               .firstOrDefault(language -> language.getLocaleName()
                     .equalsIgnoreCase(Locale.getDefault().getLanguage()));
         if (videoLanguage != null) return videoLanguage;
      }
      return new VideoLanguage(
            Locale.getDefault().getDisplayLanguage(),
            getDefaultLocaleName());
   }

   String getDefaultLocaleName() {
      return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry().toLowerCase();
   }

   String getPathForCache(final CachedEntity entity) {
      return CachedEntity.getFilePath(context, entity.getUrl());
   }

   String obtainVideoLanguage(final Video video) {
      return ProjectTextUtils.defaultIfEmpty(video.getLanguage(), "null");
   }

   void playVideo(final Video video) {
      CachedEntity videoEntity = video.getCacheEntity();
      Uri parse = Uri.parse(video.getVideoUrl());
      if (videoEntity.isCached(context)) {
         parse = Uri.parse(CachedEntity.getFilePath(context, videoEntity.getUrl()));
      }

      Intent intent = new Intent(context, PlayerActivity.class).setData(parse)
            .putExtra(PlayerActivity.EXTRA_VIDEO_NAME, video.getVideoName())
            .putExtra(PlayerActivity.EXTRA_LAUNCH_COMPONENT, getClass())
            .putExtra(PlayerActivity.EXTRA_LANGUAGE, obtainVideoLanguage(video));
      context.startActivity(intent);
   }

   boolean isCurrentSelectedVideoLocale(final VideoLocale videoLocale) {
      return lastVideoLocale == null || videoLocale.equals(lastVideoLocale);
   }

   void processCachingState(final CachedEntity cachedEntity, final HelpScreen view) {
      Queryable.from(view.getCurrentItems())
            .notNulls()
            .filter(video -> video.getCacheEntity().getUuid().equals(cachedEntity.getUuid()))
            .forEachR(video -> {
               video.setCacheEntity(cachedEntity);
               view.notifyItemChanged(cachedEntity);
            });
   }

   int getDefaultLocaleIndex(List<VideoLocale> videoLocales) {
      VideoLocale videoLocale = Queryable.from(videoLocales).firstOrDefault(element -> element.getCountry()
            .equalsIgnoreCase(Locale.getDefault().getCountry()));
      return videoLocale == null ? 0 : videoLocales.indexOf(videoLocale);
   }

   int getLastSelectedLocaleIndex() {
      return videoLocales == null ? 0 : videoLocales.indexOf(lastVideoLocale);
   }

   void setVideoLocales(List<VideoLocale> videoLocales) {
      this.videoLocales = videoLocales;
   }

   void setCurrentSelectedVideoLocale(VideoLocale videoLocale) {
      this.lastVideoLocale = videoLocale;
   }

   public VideoLanguage getDefaultLanguageFromLastLocales() {
      return getDefaultLanguage(lastVideoLocale);
   }
}
