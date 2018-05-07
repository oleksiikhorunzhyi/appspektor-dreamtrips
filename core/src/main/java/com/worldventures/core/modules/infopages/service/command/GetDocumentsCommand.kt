package com.worldventures.core.modules.infopages.service.command

import com.facebook.common.internal.ImmutableList
import com.worldventures.core.R
import com.worldventures.janet.cache.CacheBundleImpl
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.cache.storage.KeyValueStorage
import com.worldventures.janet.cache.storage.PaginatedStorage
import com.worldventures.core.modules.infopages.model.Document
import com.worldventures.core.service.command.api_action.MappableApiActionCommand
import com.worldventures.dreamtrips.api.documents.GetDocumentsHttpAction
import io.techery.janet.ActionHolder
import io.techery.janet.command.annotations.CommandAction

private const val PER_PAGE = 10

@CommandAction
class GetDocumentsCommand(private val documentType: DocumentType, val isRefresh: Boolean = false) :
      MappableApiActionCommand<GetDocumentsHttpAction, List<Document>, Document>(), CachedAction<List<Document>> {

   constructor(documentType: DocumentType) : this(documentType, false)

   private var cachedDocuments: List<Document> = emptyList()

   override fun mapHttpActionResult(httpAction: GetDocumentsHttpAction): Any = httpAction.response()

   override fun getHttpAction(): GetDocumentsHttpAction {
      val type = mapperyContext.convert(documentType,
            com.worldventures.dreamtrips.api.documents.model.DocumentType::class.java)
      return GetDocumentsHttpAction(type, getPage(), PER_PAGE)
   }

   override fun getHttpActionClass() = GetDocumentsHttpAction::class.java

   override fun getMappingTargetClass() = Document::class.java

   override fun getFallbackErrorMessage() = R.string.error_fail_to_load_documents

   override fun onRestore(holder: ActionHolder<*>?, cache: List<Document>?) {
      cachedDocuments = ImmutableList.copyOf(cache)
   }

   override fun onSuccess(callback: CommandCallback<List<Document>>?, documents: List<Document>?) {
      clearCacheIfNeeded()
      super.onSuccess(callback, documents)
   }

   override fun getCacheData() = ArrayList(result)

   override fun getCacheOptions(): CacheOptions {
      val cacheBundle = CacheBundleImpl()
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, isRefresh)
      cacheBundle.put(KeyValueStorage.BUNDLE_KEY_VALUE, documentType.toString())
      return CacheOptions(params = cacheBundle)
   }

   fun items(): List<Document> {
      val documents = ArrayList<Document>()
      if (!cachedDocuments.isEmpty()) documents.addAll(cachedDocuments)
      if (result != null) documents.addAll(result)
      return documents
   }

   fun isNoMoreElements() = result.size < PER_PAGE

   private fun clearCacheIfNeeded() {
      if (isRefresh) cachedDocuments = emptyList()
   }

   private fun getPage() = if (isRefresh || cachedDocuments.isEmpty()) 1 else cachedDocuments.size / PER_PAGE + 1

   enum class DocumentType {
      HELP,
      LEGAL,
      SMARTCARD,
   }
}
