package com.worldventures.dreamtrips.modules.common.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.command.DeleteCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadCachedEntityCommand;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class CachedEntityDelegate {

   private CachedEntityInteractor cachedEntityInteractor;

   private List<DownloadCachedEntityCommand> downloadFileCommandList = new ArrayList<>();

   public CachedEntityDelegate(CachedEntityInteractor cachedEntityInteractor) {
      this.cachedEntityInteractor = cachedEntityInteractor;
      cachedEntityInteractor.getDownloadCachedEntityPipe()
            .observe()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DownloadCachedEntityCommand>()
               .onStart(startedCommand -> downloadFileCommandList.add(startedCommand))
               .onSuccess(successCommand -> downloadFileCommandList.remove(successCommand))
               .onFail((failedCommand, throwable) -> downloadFileCommandList.remove(failedCommand)));
   }

   public void cancelCaching(CachedEntity cachedEntity, String path) {
      DownloadCachedEntityCommand command = Queryable.from(downloadFileCommandList).firstOrDefault(element
            -> element.getCachedEntity().getUuid().equals(cachedEntity.getUuid()));
      if (command != null) {
         cachedEntityInteractor.getDownloadCachedEntityPipe().cancel(command);
      }
      deleteCache(cachedEntity, path);
   }

   public void deleteCache(CachedEntity cachedEntity, String path) {
      cachedEntityInteractor.getDeleteCachedEntityPipe()
            .send(new DeleteCachedEntityCommand(cachedEntity, new File(path)));
   }

   public void startCaching(CachedEntity cachedEntity, String path) {
      DownloadCachedEntityCommand command = Queryable.from(downloadFileCommandList).firstOrDefault(element
            -> element.getCachedEntity().getUuid().equals(cachedEntity.getUuid()));
      if (command == null) {
         cachedEntityInteractor.getDownloadCachedEntityPipe()
               .send(new DownloadCachedEntityCommand(cachedEntity, new File(path)));
      }
   }
}
