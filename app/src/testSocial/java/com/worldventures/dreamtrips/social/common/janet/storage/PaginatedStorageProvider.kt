package com.worldventures.dreamtrips.social.common.janet.storage

import com.worldventures.core.janet.cache.CacheBundle
import com.worldventures.core.janet.cache.storage.PaginatedStorage

interface PaginatedStorageProvider {

   fun provideStorage(): PaginatedStorage<List<Any>>

   fun getDefaultParams(): CacheBundle

   fun getParamsForRefresh(): CacheBundle
}