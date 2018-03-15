package com.worldventures.dreamtrips.modules.dtl.helper.howtopayvideo

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.modules.dtl.domain.storage.snappy.DtlSnappyRepository
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HowToPayHintDelegateSpec : BaseSpec({

   describe("HowToPayHintDelegate") {

      beforeEachTest {
         repo = mock()
      }

      it("should show hint on clean repo") {
         whenever(repo.shouldShowHowToVideoHint()).thenReturn(SHOW_HINT_DEFAULT_VALUE)
         setup(repo)

         assertTrue(delegate.shouldShowHowToPayHint())
      }

      it("should not show hint when it was shown already or it was dismissed completely") {
         whenever(repo.shouldShowHowToVideoHint()).thenReturn(false)
         setup(repo)

         assertFalse(delegate.shouldShowHowToPayHint())
      }

      it("should show hint when remind later chosen and more than 1 day passed") {
         whenever(repo.shouldShowHowToVideoHint()).thenReturn(true)
         val now = System.currentTimeMillis()
         val moreThanOneDayAgo = now - 36 * 3600 * 1000
         whenever(repo.lastRemindMeLaterTimestamp()).thenReturn(moreThanOneDayAgo)
         setup(repo)

         assertTrue(delegate.shouldShowHowToPayHint())
      }

      it("should not show hint when remind later chosen and less than 1 day passed") {
         whenever(repo.shouldShowHowToVideoHint()).thenReturn(true)
         val now = System.currentTimeMillis()
         val lessThanOneDayAgo = now - 16 * 3600 * 1000
         whenever(repo.lastRemindMeLaterTimestamp()).thenReturn(lessThanOneDayAgo)
         setup(repo)

         assertFalse(delegate.shouldShowHowToPayHint())
      }

      it("should show hint when remind later chosen, more than 1 day passed, but hint was completely dismissed") {
         whenever(repo.shouldShowHowToVideoHint()).thenReturn(false)
         val now = System.currentTimeMillis()
         val moreThanOneDayAgo = now - 36 * 3600 * 1000
         whenever(repo.lastRemindMeLaterTimestamp()).thenReturn(moreThanOneDayAgo)
         setup(repo)

         assertFalse(delegate.shouldShowHowToPayHint())
      }

      it("should use cached values properly") {
         whenever(repo.shouldShowHowToVideoHint()).thenReturn(true)
         val now = System.currentTimeMillis()
         val lessThanOneDayAgo = now - 16 * 3600 * 1000
         whenever(repo.lastRemindMeLaterTimestamp()).thenReturn(lessThanOneDayAgo)
         setup(repo)
         delegate.shouldShowHowToPayHint()

         verify(repo, times(1)).shouldShowHowToVideoHint() // gets called once
         verify(repo, times(1)).lastRemindMeLaterTimestamp() // gets called once

         delegate.shouldShowHowToPayHint()

         verify(repo, times(1)).shouldShowHowToVideoHint() // doesn't get called anymore
         verify(repo, times(1)).lastRemindMeLaterTimestamp() // doesn't get called anymore
      }
   }

}) {

   companion object {
      const val SHOW_HINT_DEFAULT_VALUE = true

      lateinit var delegate: HowToPayHintDelegate
      lateinit var repo: DtlSnappyRepository

      fun setup(repository: DtlSnappyRepository) {
         delegate = HowToPayHintDelegate(repository)
      }
   }
}
