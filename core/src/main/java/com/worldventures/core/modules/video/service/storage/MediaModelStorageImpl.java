package com.worldventures.core.modules.video.service.storage;

import android.content.Context;
import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.core.model.CachedEntity;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.VideoLanguage;
import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.core.repository.BaseSnappyRepository;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MediaModelStorageImpl extends BaseSnappyRepository implements MediaModelStorage {

   final static String MEDIA_UPLOAD_ENTITY = "VIDEO_UPLOAD_ENTITY"; // "VIDEO_" left as is for existing user stores
   final static String MEDIA_UPLOAD_MODEL = "MEDIA_UPLOAD_MODEL";
   final static String LAST_SELECTED_VIDEO_LOCALE = "LAST_SELECTED_VIDEO_LOCALE";
   final static String LAST_SELECTED_VIDEO_LANGUAGE = "LAST_SELECTED_VIDEO_LANGUAGE ";

   private final DefaultSnappyOpenHelper defaultSnappyOpenHelper;

   public MediaModelStorageImpl(Context context, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      super(context, defaultSnappyOpenHelper.provideExecutorService());
      this.defaultSnappyOpenHelper = defaultSnappyOpenHelper;
   }

   @Override
   public void saveDownloadMediaModel(CachedModel e) {
      act(db -> db.put(MEDIA_UPLOAD_MODEL + e.getUuid(), e));
   }

   @Override
   public List<CachedModel> getDownloadMediaModels() {
      return actWithResult(db -> {
         List<CachedModel> entities = new ArrayList<>();
         String[] keys = db.findKeys(MEDIA_UPLOAD_MODEL);
         for (String key : keys) {
            entities.add(db.get(key, CachedModel.class));
         }
         return entities;
      }).or(Collections.emptyList());
   }

   @Override
   public List<CachedEntity> getDownloadMediaEntities() {
      return actWithResult(db -> {
         List<CachedEntity> entities = new ArrayList<>();
         String[] keys = db.findKeys(MEDIA_UPLOAD_ENTITY);
         for (String key : keys) {
            entities.add(db.get(key, CachedEntity.class));
         }
         return entities;
      }).or(Collections.emptyList());
   }

   @Override
   public void deleteAllMediaEntities() {
      act(db -> {
         String[] keys = db.findKeys(MEDIA_UPLOAD_ENTITY);
         for (String key : keys) {
            db.del(key);
         }
      });
   }

   @Override
   public CachedModel getDownloadMediaModel(String id) {
      return actWithResult(db -> db.get(MEDIA_UPLOAD_MODEL + id, CachedModel.class)).orNull();
   }

   @Override
   public void saveLastSelectedVideoLocale(VideoLocale videoLocale) {
      act(db -> db.put(LAST_SELECTED_VIDEO_LOCALE, videoLocale));
   }

   @Override
   public VideoLocale getLastSelectedVideoLocale() {
      return actWithResult(db -> db.get(LAST_SELECTED_VIDEO_LOCALE, VideoLocale.class)).orNull();
   }

   @Override
   public void saveLastSelectedVideoLanguage(VideoLanguage videoLocale) {
      act(db -> db.put(LAST_SELECTED_VIDEO_LANGUAGE, videoLocale));
   }

   @Override
   public VideoLanguage getLastSelectedVideoLanguage() {
      return actWithResult(db -> db.get(LAST_SELECTED_VIDEO_LANGUAGE, VideoLanguage.class)).orNull();
   }

   @Nullable
   @Override
   protected DB openDbInstance(Context context) throws SnappydbException {
      return defaultSnappyOpenHelper.openDbInstance(context);
   }
}
