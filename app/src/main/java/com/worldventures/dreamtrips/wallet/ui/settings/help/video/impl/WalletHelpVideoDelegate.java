package com.worldventures.dreamtrips.wallet.ui.settings.help.video.impl;

import android.content.Context;
import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.social.ui.video.model.CachedModel;
import com.worldventures.dreamtrips.social.ui.video.model.VideoLanguage;
import com.worldventures.dreamtrips.social.ui.video.model.VideoLocale;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.WalletHelpVideoScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.model.WalletVideoModel;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class WalletHelpVideoDelegate {

   private List<VideoLocale> videoLocales = null;
   private VideoLocale lastVideoLocale = null;

   private final Context context;

   public WalletHelpVideoDelegate(Context context) {
      this.context = context;
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

   private String getDefaultLocaleName() {
      return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry().toLowerCase();
   }

   public String getPathForCache(final CachedModel entity) {
      return getFilePath(entity.getUrl());
   }

   public String obtainVideoLanguage(final WalletVideoModel video) {
      return ProjectTextUtils.defaultIfEmpty(video.getVideo().getLanguage(), "null");
   }

   public Uri playVideo(final WalletVideoModel video) {
      CachedModel videoEntity = video.getVideo().getCacheEntity();
      Uri parse = Uri.parse(video.getVideo().getVideoUrl());
      if (isCached(videoEntity)) {
         parse = Uri.parse(getFilePath(videoEntity.getUrl()));
      }
      return parse;
   }

   public boolean isCurrentSelectedVideoLocale(final VideoLocale videoLocale) {
      return lastVideoLocale == null || videoLocale.equals(lastVideoLocale);
   }

   public void processCachingState(final CachedModel cachedEntity, final WalletHelpVideoScreen view) {
      Queryable.from(view.getCurrentItems())
            .notNulls()
            .map(WalletVideoModel::getVideo)
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

   private boolean isCached(CachedModel cachedModel) {
      return new File(getFilePath(cachedModel.getUrl())).exists() && cachedModel.getProgress() == 100;
   }

   private String getFilePath(String url) {
      return context.getFilesDir().getPath() + File.separator + getFileName(url);
   }

   private String getFileName(String url) {
      return url.substring(url.lastIndexOf("/") + 1);
   }
}
