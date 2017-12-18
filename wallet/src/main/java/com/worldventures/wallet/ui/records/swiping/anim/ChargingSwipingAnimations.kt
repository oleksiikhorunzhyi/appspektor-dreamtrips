package com.worldventures.wallet.ui.records.swiping.anim

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.view.View
import com.worldventures.core.ui.util.ViewUtils

class ChargingSwipingAnimations {

   private val handler = Handler()

   private var animateSmartCardTask: Runnable? = null
   private var animateRecordTask: Runnable? = null

   private var smartCardAnimation: Animator? = null
   private var recordAnimation: Animator? = null

   fun animateSmartCard(card: View) {
      animateSmartCardTask = Runnable { startSmartCard(card) }
      handler.post(animateSmartCardTask)
   }

   private fun startSmartCard(card: View) {
      val distance = ViewUtils.pxFromDp(card.context, SMARTCARD_TRANSLATION_DISTANCE_DP)
      val translation = ObjectAnimator.ofFloat(card, View.X, distance + card.left * 1f, card
            .left - SMARTCARD_TRANSLATION_DISTANCE_FINISH_DP)
            .setDuration(SMARTCARD_ANIMATION_DURATION)
      val alpha = ObjectAnimator.ofFloat(card, View.ALPHA, 0f, 1f)
            .setDuration((SMARTCARD_ANIMATION_DURATION / 2))
      val way = AnimatorSet()
      way.playTogether(translation, alpha)
      way.start()
      smartCardAnimation = way
   }

   fun animateBankCard(card: View) {
      animateRecordTask = Runnable { startRecordAnimation(card) }
      handler.post(animateRecordTask)
   }

   private fun startRecordAnimation(card: View) {
      val distance = ViewUtils.pxFromDp(card.context, BANKCARD_TRANSLATION_DISTANCE_DP)
      val halfWayDistance = distance / 2
      val halfWayDuration = BANKCARD_ANIMATION_DURATION / 2
      val halfWayAlphaAnimDuration = halfWayDuration / 2

      val halfWay1 = AnimatorSet()
      val translation1 = ObjectAnimator.ofFloat<View>(card, View.Y, card.top - halfWayDistance * 1f, card.top * 1f)
            .setDuration(halfWayDuration)
      val alpha1 = ObjectAnimator.ofFloat(card, View.ALPHA, 0f, 1f).setDuration(halfWayAlphaAnimDuration)
      halfWay1.playTogether(translation1, alpha1)

      val halfWay2 = AnimatorSet()
      val translation2 = ObjectAnimator.ofFloat<View>(card, View.Y, card.top * 1f, card.top * 1f + halfWayDistance)
            .setDuration(halfWayDuration)
      val alpha2 = ObjectAnimator.ofFloat(card, View.ALPHA, 1f, 0f).setDuration(halfWayAlphaAnimDuration)
      alpha2.startDelay = halfWayAlphaAnimDuration
      halfWay2.playTogether(translation2, alpha2)

      val fullWay = AnimatorSet()
      fullWay.playSequentially(halfWay1, halfWay2)
      fullWay.startDelay = BANKCARD_ANIMATION_DELAY
      fullWay.addListener(BankCardAnimListener(card))
      fullWay.start()
      recordAnimation = fullWay
   }

   fun stopAnimations() {
      handler.removeCallbacks(null)

      smartCardAnimation?.removeAllListeners()
      recordAnimation?.removeAllListeners()

      smartCardAnimation?.cancel()
      recordAnimation?.cancel()
   }

   private inner class BankCardAnimListener(private val animatedView: View) : AnimatorListenerAdapter() {

      override fun onAnimationEnd(animator: Animator) {
         animator.removeAllListeners()

         animateRecordTask = Runnable { startRecordAnimation(animatedView) }
         handler.postDelayed(animateRecordTask, BANKCARD_DELAY_BETWEEN_ANIMATIONS)
      }

   }

   companion object {
      private val SMARTCARD_TRANSLATION_DISTANCE_DP = 120f
      private val SMARTCARD_TRANSLATION_DISTANCE_FINISH_DP = 100f
      private val SMARTCARD_ANIMATION_DURATION = 800L

      private val BANKCARD_TRANSLATION_DISTANCE_DP = 200f
      private val BANKCARD_DELAY_BETWEEN_ANIMATIONS = 1200L
      private val BANKCARD_ANIMATION_DURATION = 1600L
      private val BANKCARD_ANIMATION_DELAY = 1000L
   }

}
