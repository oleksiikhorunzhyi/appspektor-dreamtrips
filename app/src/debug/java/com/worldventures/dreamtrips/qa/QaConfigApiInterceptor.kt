package com.worldventures.dreamtrips.qa

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class QaConfigApiInterceptor(
      private val qaConfig: QaApiConfig,
      private val apiHosts: ConfigurableApiHosts
) : Interceptor {

   @Throws(IOException::class)
   override fun intercept(chain: Interceptor.Chain): Response {
      val request = chain.request()
      val url = request.url().newBuilder()
      val headers = request.headers().newBuilder()

      val newHost = HttpUrl.parse(when (request.url().host()) {
         apiHosts.monolith.host() -> qaConfig.apiUrl
         apiHosts.uploadery.host() -> qaConfig.uploaderyUrl
         apiHosts.video.host() -> qaConfig.videoUrl
         apiHosts.transactions.host() -> qaConfig.transactionsUrl
         else -> null
      })
      newHost?.let {
         url.host(it.host())
         url.port(it.port())
      }

      qaConfig.sessionId?.let { headers.add("X-Session-id", it) }

      return chain.proceed(request.newBuilder()
            .url(url.build())
            .headers(headers.build())
            .build())
   }

   internal class ConfigurableApiHosts(monolith: String, uploadery: String, video: String, transactions: String) {
      val monolith: HttpUrl by lazy { HttpUrl.parse(monolith) }
      val uploadery: HttpUrl by lazy { HttpUrl.parse(uploadery) }
      val video: HttpUrl by lazy { HttpUrl.parse(video) }
      val transactions: HttpUrl by lazy { HttpUrl.parse(transactions) }
   }
}
