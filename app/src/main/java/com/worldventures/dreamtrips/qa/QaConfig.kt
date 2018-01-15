package com.worldventures.dreamtrips.qa

data class QaConfig(val api: QaApiConfig?, val app: QaAppConfig)

data class QaApiConfig(val sessionId: String?,
                       val apiUrl: String?, val uploaderyUrl: String?, val videoUrl: String?, val transactionsUrl: String?)

data class QaAppConfig(val enableAnalytics: Boolean, val enableBlockingInteractions: Boolean)
