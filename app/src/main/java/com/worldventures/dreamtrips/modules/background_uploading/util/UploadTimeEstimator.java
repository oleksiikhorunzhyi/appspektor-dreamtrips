package com.worldventures.dreamtrips.modules.background_uploading.util;

import timber.log.Timber;

public class UploadTimeEstimator {

   private static final double SMOOTHING_FACTOR = 0.005;

   private double totalUploadedSize;
   private double totalSize;
   private long currentAttachmentSize;
   private long previousUploadingMeasuredSize;
   private long previousUploadingMeasuredTime;
   private double averageUploadSpeed;

   public void prepare(double totalSize, double totalUploadedSize, long currentAttachmentSize,
         double averageUploadSpeed) {
      this.totalSize = totalSize;
      this.totalUploadedSize = totalUploadedSize;
      this.currentAttachmentSize = currentAttachmentSize;
      this.averageUploadSpeed = averageUploadSpeed;
   }

   public double getAverageUploadSpeed() {
      return averageUploadSpeed;
   }

   public void onUploadingStarted() {
      previousUploadingMeasuredTime = System.currentTimeMillis();
   }

   /**
    * @param progress in percents
    * @return the value of remaining upload time in milliseconds
    */
   public long estimate(int progress) {
      if (progress == 0) return 0;

      long currentUploadedSize = calculateCurrentUploadedSize(progress);
      long currentUploadingTime = System.currentTimeMillis();

      double uploadingSpeed = calculateUploadingSpeed(currentUploadedSize, currentUploadingTime);
      updateAverageSpeed(uploadingSpeed);

      updatePreviousMeasuredValues(currentUploadedSize, currentUploadingTime);

      int remainingTime = calculateRemainingUploadTime(currentUploadedSize);

      Timber.d("Current uploaded size = %d bytes.\nUploading speed = %.2f bytes/sec.\nRemaining time = %d sec",
            currentUploadedSize, uploadingSpeed, remainingTime);

      return remainingTime * 1000;
   }

   /**
    * @param progress in percents
    * @return the value of current uploaded size in bytes
    */
   private long calculateCurrentUploadedSize(int progress) {
      return currentAttachmentSize * progress / 100;
   }

   /**
    * @param currentUploadedSize  of attachment in bytes
    * @param currentUploadingTime in milliseconds
    * @return the value of current uploading speed in bytes/seconds
    */
   private double calculateUploadingSpeed(long currentUploadedSize, long currentUploadingTime) {
      return (currentUploadedSize - previousUploadingMeasuredSize) / calculateTimeDiff(currentUploadingTime);
   }

   /**
    * @param currentUploadingTime in milliseconds
    * @return the difference between previous and current calculated times in seconds
    */
   private double calculateTimeDiff(long currentUploadingTime) {
      return ((double) currentUploadingTime - previousUploadingMeasuredTime) / 1000;
   }

   /**
    * Updates the average value of speed for current post
    *
    * @param uploadingSpeed the value of speed for current part of uploading in bytes/seconds
    */
   private void updateAverageSpeed(double uploadingSpeed) {
      averageUploadSpeed = SMOOTHING_FACTOR * uploadingSpeed + (1 - SMOOTHING_FACTOR) * averageUploadSpeed;
   }

   /**
    * Updates previous values, which used to calculate difference between two uploading parts
    *
    * @param currentUploadedSize  in bytes
    * @param currentUploadingTime in milliseconds
    */
   private void updatePreviousMeasuredValues(long currentUploadedSize, long currentUploadingTime) {
      previousUploadingMeasuredSize = currentUploadedSize;
      previousUploadingMeasuredTime = currentUploadingTime;
   }

   /**
    * @param currentUploadedSize in bytes
    * @return the value of remaining upload time in seconds
    */
   private int calculateRemainingUploadTime(long currentUploadedSize) {
      return (int) Math.round((totalSize - (totalUploadedSize + currentUploadedSize)) / averageUploadSpeed);
   }
}
