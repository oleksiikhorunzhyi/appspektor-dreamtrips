package com.worldventures.core.service;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.service.command.DeleteCachedModelCommand;
import com.worldventures.core.service.command.DownloadCachedModelCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class CachedEntityDelegate {

   private CachedEntityInteractor cachedEntityInteractor;

   private List<DownloadCachedModelCommand> downloadFileCommandList = new ArrayList<>();

   public CachedEntityDelegate(CachedEntityInteractor cachedEntityInteractor) {
      this.cachedEntityInteractor = cachedEntityInteractor;
      cachedEntityInteractor.getDownloadCachedModelPipe()
            .observe()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DownloadCachedModelCommand>()
               .onStart(startedCommand -> downloadFileCommandList.add(startedCommand))
               .onSuccess(successCommand -> downloadFileCommandList.remove(successCommand))
               .onFail((failedCommand, throwable) -> downloadFileCommandList.remove(failedCommand)));
   }

   public void cancelCaching(CachedModel cachedModel, String path) {
      DownloadCachedModelCommand command = Queryable.from(downloadFileCommandList).firstOrDefault(element
            -> element.getCachedModel().getUuid().equals(cachedModel.getUuid()));
      if (command != null) {
         cachedEntityInteractor.getDownloadCachedModelPipe().cancel(command);
         downloadFileCommandList.remove(command);
      }
      deleteCache(cachedModel, path);
   }

   public void deleteCache(CachedModel cachedModel, String path) {
      cachedEntityInteractor.getDeleteCachedModelPipe()
            .send(new DeleteCachedModelCommand(cachedModel, new File(path)));
   }

   public void startCaching(CachedModel cachedModel, String path) {
      DownloadCachedModelCommand command = Queryable.from(downloadFileCommandList).firstOrDefault(element
            -> element.getCachedModel().getUuid().equals(cachedModel.getUuid()));
      if (command == null) {
         cachedEntityInteractor.getDownloadCachedModelPipe()
               .send(new DownloadCachedModelCommand(cachedModel, new File(path)));
      }
   }
}
