/*
 * Copyright (C) 2013-2015 Juha Kuitunen
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.worldventures.dreamtrips.core.utils;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;

import org.xml.sax.XMLReader;

import java.util.ArrayDeque;
import java.util.Deque;

import timber.log.Timber;

/**
 * Implements support for ordered and unordered lists in to Android TextView.
 * <p>
 * Some code taken from inner class android.text.Html.HtmlToSpannedConverter. If you find this code useful,
 * please vote my answer at <a href="http://stackoverflow.com/a/17365740/262462">StackOverflow</a> up.
 */
public class HtmlTagHandler implements Html.TagHandler {

   public static final String UL = "ul";
   public static final String OL = "ol";
   public static final String LI = "li";

   /**
    * List indentation in pixels. Nested lists use multiple of this.
    */
   private static final int INDENT = 10;
   private static final int LIST_ITEM_INDENT = INDENT * 2;
   private static final BulletSpan BULLET_SPAN = new BulletSpan(INDENT);
   /**
    * Keeps track of lists (ol, ul). On bottom of Stack is the outermost list
    * and on top of Stack is the most nested list
    */
   protected Deque<String> lists = new ArrayDeque<>();
   /**
    * Tracks indexes of ordered lists so that after a nested list ends
    * we can continue with correct index of outer list
    */
   protected Deque<Integer> olNextIndex = new ArrayDeque<>();

   /**
    * @see android.text.Html
    */
   private static void start(Editable text, Object mark) {
      int len = text.length();
      text.setSpan(mark, len, len, Spanned.SPAN_MARK_MARK);
   }

   /**
    * Modified from {@link android.text.Html}
    */
   private static void end(Editable text, Class<?> kind, Object... replaces) {
      int len = text.length();
      Object obj = getLast(text, kind);
      int where = text.getSpanStart(obj);
      text.removeSpan(obj);
      if (where != len) {
         for (Object replace : replaces) {
            text.setSpan(replace, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         }
      }
      return;
   }

   /**
    * @see android.text.Html
    */
   private static Object getLast(Spanned text, Class<?> kind) {
        /*
         * This knows that the last returned object from getSpans()
		 * will be the most recently added.
		 */
      Object[] objs = text.getSpans(0, text.length(), kind);
      if (objs.length == 0) {
         return null;
      }
      return objs[objs.length - 1];
   }

   @Override
   public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
      if (UL.equalsIgnoreCase(tag)) {
         if (opening) {
            lists.push(tag);
         } else {
            lists.pop();
         }
      } else if (OL.equalsIgnoreCase(tag)) {
         if (opening) {
            lists.push(tag);
         } else {
            lists.pop();
         }
      } else if (LI.equalsIgnoreCase(tag)) {
         if (opening) {
            if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
               output.append("\n");
            }
            String parentList = lists.peek();
            if (OL.equalsIgnoreCase(parentList)) {
               start(output, new Ol());
               output.append(olNextIndex.peek().toString()).append(". ");
               olNextIndex.push(olNextIndex.pop() + 1);
            } else if (UL.equalsIgnoreCase(parentList)) {
               start(output, new Ul());
            }
         } else {
            if (UL.equalsIgnoreCase(lists.peek())) {
               if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                  output.append("\n");
               }
               // Nested BulletSpans increases distance between bullet and text, so we must prevent it.
               int bulletMargin = INDENT;
               if (lists.size() > 1) {
                  bulletMargin = INDENT - BULLET_SPAN.getLeadingMargin(true);
                  if (lists.size() > 2) {
                     // This get's more complicated when we add a LeadingMarginSpan into the same line:
                     // we have also counter it's effect to BulletSpan
                     bulletMargin -= (lists.size() - 2) * LIST_ITEM_INDENT;
                  }
               }
               BulletSpan newBullet = new BulletSpan(bulletMargin);
               end(output, Ul.class, new LeadingMarginSpan.Standard(LIST_ITEM_INDENT * (lists.size() - 1)), newBullet);
            } else if (OL.equalsIgnoreCase(lists.peek())) {
               if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                  output.append("\n");
               }
               int numberMargin = LIST_ITEM_INDENT * (lists.size() - 1);
               if (lists.size() > 2) {
                  // Same as in ordered lists: counter the effect of nested Spans
                  numberMargin -= (lists.size() - 2) * LIST_ITEM_INDENT;
               }
               end(output, Ol.class, new LeadingMarginSpan.Standard(numberMargin));
            }
         }
      } else {
         if (opening) {
            Timber.d("Found an unsupported tag", tag);
         }
      }
   }

   private static class Ul {
      private Ul() {
      }
   }

   private static class Ol {
      private Ol() {
      }
   }

}