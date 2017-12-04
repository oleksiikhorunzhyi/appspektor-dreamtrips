package com.worldventures.core.janet.cache

/**
 * Compatibility-class to use as CacheOptions implementations in kotlin classes
 */
data class CacheOptionsImpl(val restoreFromCache: Boolean = true,
                            val saveToCache: Boolean = true,
                            val sendAfterRestore: Boolean = true,
                            val params: CacheBundle? = null) : CacheOptions() {

   override fun restoreFromCache(): Boolean {
      return restoreFromCache
   }

   override fun saveToCache(): Boolean {
      return saveToCache
   }

   override fun sendAfterRestore(): Boolean {
      return sendAfterRestore
   }

   override fun params(): CacheBundle? {
      return params
   }
}
