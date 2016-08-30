package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateSubscriber;

@CommandAction
public class DownloadCachedEntityCommand extends CachedEntityCommand implements InjectableAction {

   private File file;

   @Inject DownloadFileInteractor downloadFileInteractor;
   @Inject SnappyRepository db;

   private DownloadFileCommand downloadFileCommand;
   private int lastProgress;

   public DownloadCachedEntityCommand(CachedEntity cachedEntity, File file) {
      super(cachedEntity);
      this.file = file;
   }

   @Override
   protected void run(CommandCallback<CachedEntity> callback) throws Throwable {
      downloadFileInteractor.
            getDownloadFileCommandPipe().createObservable(downloadFileCommand =
               new DownloadFileCommand(file, cachedEntity.getUrl()))
            .onBackpressureLatest()
            .subscribe(new ActionStateSubscriber<DownloadFileCommand>()
               .onStart(this::onStart)
               .onSuccess(successCommand -> onSuccess(callback))
               .onFail((failedCommand, throwable) -> onFail(callback, throwable))
               .onProgress((commandInProgress, progress) -> onProgress(callback, progress)));
   }

   private void onStart(DownloadFileCommand command) {
      cachedEntity.setIsFailed(false);
      cachedEntity.setProgress(0);
      db.saveDownloadMediaEntity(cachedEntity);
   }

   private void onSuccess(CommandCallback<CachedEntity> callback) {
      cachedEntity.setProgress(100);
      db.saveDownloadMediaEntity(cachedEntity);
      callback.onSuccess(cachedEntity);
   }

   private void onFail(CommandCallback<CachedEntity> callback, Throwable throwable) {
      cachedEntity.setProgress(0);
      cachedEntity.setIsFailed(true);
      db.saveDownloadMediaEntity(cachedEntity);
      callback.onFail(throwable);
   }

   private void onProgress(CommandCallback<CachedEntity> callback, int progress) {
      if (progress <= lastProgress) return;
      lastProgress = progress;
      cachedEntity.setProgress(progress);
      db.saveDownloadMediaEntity(cachedEntity);
      callback.onProgress(progress);
   }

   public CachedEntity getCachedEntity() {
      return cachedEntity;
   }

   public File getFile() {
      return file;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.fail;
   }

   @Override
   protected void cancel() {
      if (downloadFileCommand != null) downloadFileCommand.cancel();
      cachedEntity.setProgress(0);
      db.saveDownloadMediaEntity(cachedEntity);
   }

   @Override
   public boolean isCanceled() {
      return downloadFileCommand != null && downloadFileCommand.isCanceled();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DownloadCachedEntityCommand that = (DownloadCachedEntityCommand) o;

      if (cachedEntity != null ? !cachedEntity.equals(that.cachedEntity) : that.cachedEntity != null) return false;
      return file != null ? file.equals(that.file) : that.file == null;
   }

   @Override
   public int hashCode() {
      int result = cachedEntity != null ? cachedEntity.hashCode() : 0;
      result = 31 * result + (file != null ? file.hashCode() : 0);
      return result;
   }
}
