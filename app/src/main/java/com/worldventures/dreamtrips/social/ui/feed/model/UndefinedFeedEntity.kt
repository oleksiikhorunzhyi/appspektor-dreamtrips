package com.worldventures.dreamtrips.social.ui.feed.model

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment

class UndefinedFeedEntity : FeedEntity {
   override fun getUid() = notImplemented()

   override fun getOriginalText() = notImplemented()

   override fun getTranslation() = notImplemented()

   override fun setTranslation(text: String?) = notImplemented()

   override fun isTranslated() = notImplemented()

   override fun setTranslated(translated: Boolean) = notImplemented()

   override fun getOwner() = notImplemented()

   override fun place() = notImplemented()

   override fun setOwner(user: User?) = notImplemented()

   override fun getCommentsCount() = notImplemented()

   override fun setCommentsCount(count: Int) = notImplemented()

   override fun getComments() = notImplemented()

   override fun setComments(comments: MutableList<Comment>?) = notImplemented()

   override fun setLikesCount(count: Int) = notImplemented()

   override fun getLikesCount() = notImplemented()

   override fun isLiked() = notImplemented()

   override fun setLiked(isLiked: Boolean) = notImplemented()

   override fun getFirstLikerName() = notImplemented()

   override fun setFirstLikerName(fullName: String?) = notImplemented()

   override fun syncLikeState(feedEntity: FeedEntity?) = notImplemented()

   override fun getLanguage(): String? = notImplemented()

   override fun contentSame(feedEntity: FeedEntity?) = notImplemented()

   override fun getCreatedAt() = notImplemented()

   private fun notImplemented(): Nothing = throw RuntimeException("Not implemented")
}
