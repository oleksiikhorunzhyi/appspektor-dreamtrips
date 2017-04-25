package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateSubscriber;

@CommandAction
public class DownloadCachedModelCommand extends CachedEntityCommand implements InjectableAction {

   // TODO We must send min progress > 0 as view level determines is entity is NOT in progress
   // if entity is not failed and progress is 0.
   public static final int PROGRESS_START_MIN = 1;

   private File file;

   @Inject DownloadFileInteractor downloadFileInteractor;
   @Inject SnappyRepository db;

   private DownloadFileCommand downloadFileCommand;
   private int lastProgress = PROGRESS_START_MIN;

   public DownloadCachedModelCommand(CachedModel cachedModel, File file) {
      super(cachedModel);
      this.file = file;
   }

   @Override
   protected void run(CommandCallback<CachedModel> callback) throws Throwable {
      downloadFileInteractor.
            getDownloadFileCommandPipe().createObservable(downloadFileCommand =
               new DownloadFileCommand(file, cachedModel.getUrl()))
            .onBackpressureLatest()
            .subscribe(new ActionStateSubscriber<DownloadFileCommand>()
               .onStart(this::onStart)
               .onSuccess(successCommand -> onSuccess(callback))
               .onFail((failedCommand, throwable) -> onFail(callback, throwable))
               .onProgress((commandInProgress, progress) -> onProgress(callback, progress)));
   }

   private void onStart(DownloadFileCommand command) {
      cachedModel.setIsFailed(false);
      cachedModel.setProgress(PROGRESS_START_MIN);
      db.saveDownloadMediaModel(cachedModel);
   }

   private void onSuccess(CommandCallback<CachedModel> callback) {
      cachedModel.setProgress(100);
      db.saveDownloadMediaModel(cachedModel);
      callback.onSuccess(cachedModel);
   }

   private void onFail(CommandCallback<CachedModel> callback, Throwable throwable) {
      cachedModel.setProgress(0);
      cachedModel.setIsFailed(true);
      db.saveDownloadMediaModel(cachedModel);
      callback.onFail(throwable);
   }

   private void onProgress(CommandCallback<CachedModel> callback, int progress) {
      if (progress == 0) progress = PROGRESS_START_MIN;
      if (progress <= lastProgress) return;
      lastProgress = progress;
      cachedModel.setProgress(progress);
      db.saveDownloadMediaModel(cachedModel);
      callback.onProgress(progress);
   }

   public CachedModel getCachedModel() {
      return cachedModel;
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
      cachedModel.setProgress(0);
      db.saveDownloadMediaModel(cachedModel);
   }

   @Override
   public boolean isCanceled() {
      return downloadFileCommand != null && downloadFileCommand.isCanceled();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DownloadCachedModelCommand that = (DownloadCachedModelCommand) o;

      if (cachedModel != null ? !cachedModel.equals(that.cachedModel) : that.cachedModel != null) return false;
      return file != null ? file.equals(that.file) : that.file == null;
   }

   @Override
   public int hashCode() {
      int result = cachedModel != null ? cachedModel.hashCode() : 0;
      result = 31 * result + (file != null ? file.hashCode() : 0);
      return result;
   }
}
