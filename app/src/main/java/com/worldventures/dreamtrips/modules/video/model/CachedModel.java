package com.worldventures.dreamtrips.modules.video.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class CachedModel implements Serializable {

   protected static final long serialVersionUID = 12332;
   protected String url;
   protected int progress;
   protected String uuid;
   protected int downloadId;
   protected String name;
   protected Class entityClass;
   protected @Status.CacheStatus int cacheStatus;

   public CachedModel(String url, String id, String name) {
      this.url = url;
      uuid = id;
      this.name = name;
   }

   public CachedModel() {
   }

   @Status.CacheStatus
   public int getCacheStatus() {
      return cacheStatus;
   }

   public void setCacheStatus(@Status.CacheStatus int cacheStatus) {
      this.cacheStatus = cacheStatus;
   }

   public String getName() {
      return name;
   }

   public int getProgress() {
      return progress;
   }

   public void setProgress(int progress) {
      this.progress = progress;
   }

   public String getUrl() {
      return url;
   }

   public String getUuid() {
      return uuid;
   }

   public Class getEntityClass() {
      return entityClass == null? Object.class : entityClass;
   }

   public void setEntityClass(Class entityClass) {
      this.entityClass = entityClass;
   }

   @Override
   public String toString() {
      return "CachedEntity{" +
            "url='" + url + '\'' +
            ", failed=" + (cacheStatus == Status.FAILED) +
            ", create=" + progress +
            ", uuid='" + uuid + '\'' +
            ", downloadId=" + downloadId +
            '}';
   }

}
