package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.techery.janet.body.BytesArrayBody;
import io.techery.janet.body.FileBody;
import io.techery.janet.http.model.MultipartRequestBody.PartBody;
import rx.Observable;

import static com.worldventures.dreamtrips.modules.tripsimages.view.ImageUtils.getBitmap;
import static rx.Observable.fromCallable;

public class MaltyPartImageBodyCreator {

   private static final String PHOTO_MIME_TYPE = "image/*";
   private static final String FILE_SCHEME = "file";

   private final Context context;
   private final WalletSocialInfoProvider socialInfoProvider;

   @Inject
   public MaltyPartImageBodyCreator(Context context, WalletSocialInfoProvider socialInfoProvider) {
      this.context = context;
      this.socialInfoProvider = socialInfoProvider;
   }

   private PartBody toPartBody(Bitmap source) throws IOException {
      final ByteArrayOutputStream output = new ByteArrayOutputStream(source.getByteCount());
      source.compress(Bitmap.CompressFormat.JPEG, 100, output);

      return new PartBody.Builder()
                  .setBody(new BytesArrayBody(PHOTO_MIME_TYPE, output.toByteArray()))
                  .addHeader("filename", createFakeFileName())
                  .build();
   }

   private boolean isFile(Uri uri) {
      final String uriScheme = uri.getScheme();
      return FILE_SCHEME.equals(uriScheme);
   }

   private PartBody toPartBody(Uri fileUri) throws IOException {
      final File file = new File(fileUri.getPath());
      return new PartBody.Builder()
            .setBody(new FileBody(PHOTO_MIME_TYPE, file))
            .addHeader("filename", file.getName())
            .build();
   }

   public Observable<PartBody> createBody(String imageUri) {
      final Uri uri = Uri.parse(imageUri);
      if (isFile(uri)) {
         return fromCallable(() -> toPartBody(uri));
      } else {
         return getBitmap(context, uri, 0, 0)
               .flatMap(bitmap -> fromCallable(() -> toPartBody(bitmap)));
      }
   }

   private String createFakeFileName() {
      return socialInfoProvider.username() + "_social_avatar";
   }
}
