package com.worldventures.dreamtrips.social.background_uploading

import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.social.ui.background_uploading.util.UploadTimeEstimator
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class UploadTimeEstimatorTest : BaseSpec({
   describe("Test upload time estimation") {
      val totalSize = 10000000.0
      val currentAttachmentSize = 500000L

      val uploadingTimeStart = 1000000L
      val uploadingTimeEndForFirstIteration = 1001000L
      val uploadingTimeEndForSecondIteration = 1002000L
      val uploadingTimeEndForThirdIteration = 1003000L

      it("should return correct speed and time for first iteration") {
         val uploadTimeEstimator = UploadTimeEstimator()
         uploadTimeEstimator.prepare(totalSize, 0.0, currentAttachmentSize, 0.0)
         uploadTimeEstimator.onUploadingStarted(uploadingTimeStart)

         val remainingTime = uploadTimeEstimator.estimate(6, uploadingTimeEndForFirstIteration)

         assert(remainingTime == 332000L)
      }

      it("should return correct speed and time for second iteration") {
         val uploadTimeEstimator = UploadTimeEstimator()
         uploadTimeEstimator.prepare(totalSize, 0.0, currentAttachmentSize, 0.0)
         uploadTimeEstimator.onUploadingStarted(uploadingTimeStart)

         uploadTimeEstimator.estimate(6, uploadingTimeEndForFirstIteration)
         val remainingTime = uploadTimeEstimator.estimate(12, uploadingTimeEndForSecondIteration)

         assert(remainingTime == 331000L)
      }

      it("should return correct speed and time for third iteration") {
         val uploadTimeEstimator = UploadTimeEstimator()
         uploadTimeEstimator.prepare(totalSize, 0.0, currentAttachmentSize, 0.0)
         uploadTimeEstimator.onUploadingStarted(uploadingTimeStart)

         uploadTimeEstimator.estimate(6, uploadingTimeEndForFirstIteration)
         uploadTimeEstimator.estimate(12, uploadingTimeEndForSecondIteration)
         val remainingTime = uploadTimeEstimator.estimate(18, uploadingTimeEndForThirdIteration)

         assert(remainingTime == 330000L)
      }
   }
})