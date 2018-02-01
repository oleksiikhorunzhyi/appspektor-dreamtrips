package com.worldventures.janet.cache

data class CacheOptions(val restoreFromCache: Boolean = true,
                        val saveToCache: Boolean = true,
                        val sendAfterRestore: Boolean = true,
                        val params: CacheBundle? = null)

