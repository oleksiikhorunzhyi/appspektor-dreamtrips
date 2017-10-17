package com.worldventures.core.modules.video.service.storage;


import com.worldventures.core.model.CachedEntity;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.VideoLanguage;
import com.worldventures.core.modules.video.model.VideoLocale;

import java.util.List;

public interface MediaModelStorage {

   void saveDownloadMediaModel(CachedModel e);

   List<CachedModel> getDownloadMediaModels();

   @Deprecated
   List<CachedEntity> getDownloadMediaEntities();

   @Deprecated
   void deleteAllMediaEntities();

   CachedModel getDownloadMediaModel(String id);

   void saveLastSelectedVideoLocale(VideoLocale videoLocale);

   VideoLocale getLastSelectedVideoLocale();

   void saveLastSelectedVideoLanguage(VideoLanguage videoLocale);

   VideoLanguage getLastSelectedVideoLanguage();
}
