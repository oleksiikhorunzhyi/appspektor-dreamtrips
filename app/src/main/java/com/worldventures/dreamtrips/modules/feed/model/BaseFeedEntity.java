package com.worldventures.dreamtrips.modules.feed.model;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public abstract class BaseFeedEntity implements FeedEntity {

   protected String uid;
   protected User owner;
   protected int commentsCount;
   protected List<Comment> comments;
   protected boolean liked;
   protected int likesCount;
   protected String language;

   ///////////////////////////////////////////////////////////////////////////
   // Getters & Setters
   ///////////////////////////////////////////////////////////////////////////

   public void setUid(String uid) {
      this.uid = uid;
   }

   public void setLanguage(String language) {
      this.language = language;
   }

   @Override
   public String getUid() {
      return uid;
   }

   @Override
   public User getOwner() {
      return owner;
   }

   @Override
   public void setOwner(User owner) {
      this.owner = owner;
   }

   @Override
   public int getCommentsCount() {
      return commentsCount;
   }

   @Override
   public void setCommentsCount(int count) {
      commentsCount = count;
   }

   @Override
   @NotNull
   public List<Comment> getComments() {
      if (comments == null) {
         comments = new ArrayList<>();
      }
      return comments;
   }

   @Override
   public void setComments(List<Comment> comments) {
      this.comments = comments;
   }

   @Override
   public boolean isLiked() {
      return liked;
   }

   @Override
   public void setLiked(boolean liked) {
      this.liked = liked;
   }

   @Override
   public void setLikesCount(int likesCount) {
      this.likesCount = likesCount;
   }

   @Override
   public int getLikesCount() {
      return likesCount;
   }

   @Override
   public String getLanguage() {
      return language;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Translate staff
   ///////////////////////////////////////////////////////////////////////////

   private transient String translation;
   private transient boolean translated;

   @Override
   public String getOriginalText() {
      return null;
   }

   @Override
   public void setTranslation(String translation) {
      this.translation = translation;
   }

   @Nullable
   @Override
   public String getTranslation() {
      return translation;
   }

   @Override
   public boolean isTranslated() {
      return translated;
   }

   @Override
   public void setTranslated(boolean translated) {
      this.translated = translated;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Helpers
   ///////////////////////////////////////////////////////////////////////////

   private String firstLikerName;

   @Override
   public String getFirstLikerName() {
      return firstLikerName;
   }

   @Override
   public void setFirstLikerName(String firstLikerName) {
      this.firstLikerName = firstLikerName;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Misc
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void syncLikeState(FeedEntity feedEntity) {
      this.setLiked(feedEntity.isLiked());
      this.setLikesCount(feedEntity.getLikesCount());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      BaseFeedEntity that = (BaseFeedEntity) o;

      return uid != null && that.uid != null && uid.equals(that.uid);
   }

   @Override
   public int hashCode() {
      return uid.hashCode();
   }

   @Override
   public String toString() {
      return "BaseFeedEntity{" +
            "likesCount=" + likesCount +
            ", liked=" + liked +
            ", commentsCount=" + commentsCount +
            ", comments=" + comments +
            ", owner=" + owner +
            ", uid='" + uid + '\'' +
            ", language='" + language + '\'' +
            '}';
   }
}
