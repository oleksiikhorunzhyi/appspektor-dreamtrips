package com.worldventures.dreamtrips.modules.dtl.view.custom.webview.client;

import android.webkit.WebView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;

public class HttpErrorHandlerWebViewClientTest {

   private HttpErrorHandlerWebViewClient webViewClient;
   @Mock WebView webView;


   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      webViewClient = new HttpErrorHandlerWebViewClient() {
         @Override
         protected void onHttpStatusError(String url, int statusCode) {
         }
      };
   }

   @Test
   public void itShouldReturnNullDueToNonHttpSchema() {
      assertNull(webViewClient.shouldInterceptRequest(webView, "file://"));
   }

}