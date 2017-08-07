package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import android.content.Context;
import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletVideo;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class WalletHelpVideoDelegate {

   private List<VideoLocale> videoLocales = null;
   private VideoLocale lastVideoLocale = null;

   public WalletHelpVideoDelegate() {
   }

   public VideoLanguage getDefaultLanguage(final List<VideoLocale> videoLocales) {
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

   public String getPathForCache(Context context, final CachedModel entity) {
      return getFilePath(context, entity.getUrl());
   }

   public String obtainVideoLanguage(final WalletVideo video) {
      return ProjectTextUtils.defaultIfEmpty(video.getLanguage(), "null");
   }

   public Uri playVideo(Context context, final WalletVideo video) {
      CachedModel videoEntity = video.getCacheEntity();
      Uri parse = Uri.parse(video.getVideoUrl());
      if (isCached(context, videoEntity)) {
         parse = Uri.parse(getFilePath(context, videoEntity.getUrl()));
      }
      return parse;
   }

   public boolean isCurrentSelectedVideoLocale(final VideoLocale videoLocale) {
      return lastVideoLocale == null || videoLocale.equals(lastVideoLocale);
   }

   public void processCachingState(final CachedModel cachedEntity, final HelpScreen view) {
      Queryable.from(view.getCurrentItems())
            .notNulls()
            .filter(video -> video.getCacheEntity().getUuid().equals(cachedEntity.getUuid()))
            .forEachR(video -> {
               video.setCacheEntity(cachedEntity);
               view.notifyItemChanged(cachedEntity);
            });
   }

   public int getDefaultLocaleIndex(List<VideoLocale> videoLocales) {
      VideoLocale videoLocale = Queryable.from(videoLocales).firstOrDefault(element -> element.getCountry()
            .equalsIgnoreCase(Locale.getDefault().getCountry()));
      return videoLocale == null ? 0 : videoLocales.indexOf(videoLocale);
   }

   public int getLastSelectedLocaleIndex() {
      return videoLocales == null ? 0 : videoLocales.indexOf(lastVideoLocale);
   }

   public void setVideoLocales(List<VideoLocale> videoLocales) {
      this.videoLocales = videoLocales;
   }

   public void setCurrentSelectedVideoLocale(VideoLocale videoLocale) {
      this.lastVideoLocale = videoLocale;
   }

   public VideoLanguage getDefaultLanguageFromLastLocales() {
      return getDefaultLanguage(lastVideoLocale);
   }

   private boolean isCached(Context context, CachedModel cachedModel) {
      return new File(getFilePath(context, cachedModel.getUrl())).exists() && cachedModel.getProgress() == 100;
   }

   private String getFilePath(Context context, String url) {
      return context.getFilesDir().getPath() + File.separator + getFileName(url);
   }

   private String getFileName(String url) {
      return url.substring(url.lastIndexOf("/") + 1);
   }
}
