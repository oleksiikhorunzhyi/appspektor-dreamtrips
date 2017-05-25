package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.techery.janet.body.BytesArrayBody;
import io.techery.janet.body.FileBody;
import rx.Observable;

import static com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils.getBitmap;
import static rx.Observable.fromCallable;

public class MaltyPartImageBodyCreator {

   public static final String PHOTO_MIME_TYPE = "image/*";
   private static final String FILE_SCHEME = "file";

   private final Context context;
   private final SessionHolder<UserSession> userSessionHolder;

   @Inject
   public MaltyPartImageBodyCreator(Context context, SessionHolder<UserSession> userSessionHolder) {
      this.context = context;
      this.userSessionHolder = userSessionHolder;
   }

   private BytesArrayBody toBytesArrayBody(Bitmap source) throws IOException {
      final ByteArrayOutputStream output = new ByteArrayOutputStream(source.getByteCount());
      source.compress(Bitmap.CompressFormat.JPEG, 100, output);
      return new NamedBytesArrayBody(PHOTO_MIME_TYPE, createFakeFileName(), output.toByteArray());
   }

   private boolean isFile(Uri uri) {
      final String uriScheme = uri.getScheme();
      return FILE_SCHEME.equals(uriScheme);
   }

   private FileBody toFileMimeType(Uri uri) throws IOException {
      return new FileBody(PHOTO_MIME_TYPE, new File(uri.getPath()));
   }

   public Observable<BytesArrayBody> createBody(String imageUri) {
      final Uri uri = Uri.parse(imageUri);
      if (isFile(uri)) {
         return fromCallable(() -> toFileMimeType(uri));
      } else {
         return getBitmap(context, uri, 0, 0)
               .flatMap(bitmap -> fromCallable(() -> toBytesArrayBody(bitmap)));
      }
   }

   private String createFakeFileName() {
      return userSessionHolder.get().get().getUsername() + "_social_avatar";
   }

   private static class NamedBytesArrayBody extends BytesArrayBody {

      private final String fileName;

      private NamedBytesArrayBody(String mimeType, String fileName, byte[] bytes) {
         super(mimeType, bytes);
         this.fileName = fileName;
      }

      @Override
      public String fileName() {
         return fileName;
      }
   }

}
