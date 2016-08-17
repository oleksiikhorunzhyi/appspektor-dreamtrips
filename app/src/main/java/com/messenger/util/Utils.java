package com.messenger.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

   public static void copyToClipboard(Context context, CharSequence sequence) {
      ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
      ClipData clip = ClipData.newPlainText("", sequence);
      clipboard.setPrimaryClip(clip);
   }

   public static boolean isFileUri(Uri uri) {
      return uri != null && uri.getScheme().equals("file");
   }

   public static String unescapeXML(final String xml) {
      Pattern xmlEntityRegex = Pattern.compile("&(#?)([^;]+);");
      //Unfortunately, Matcher requires a StringBuffer instead of a StringBuilder
      StringBuffer unescapedOutput = new StringBuffer(xml.length());

      Matcher m = xmlEntityRegex.matcher(xml);
      Map<String, String> builtinEntities = null;
      String entity;
      String hashmark;
      String ent;
      int code;
      while (m.find()) {
         ent = m.group(2);
         hashmark = m.group(1);
         if ((hashmark != null) && (hashmark.length() > 0)) {
            code = Integer.parseInt(ent);
            entity = Character.toString((char) code);
         } else {
            //must be a non-numerical entity
            if (builtinEntities == null) {
               builtinEntities = buildBuiltinXMLEntityMap();
            }
            entity = builtinEntities.get(ent);
            if (entity == null) {
               //not a known entity - ignore it
               entity = "&" + ent + ';';
            }
         }
         m.appendReplacement(unescapedOutput, entity);
      }
      m.appendTail(unescapedOutput);

      return unescapedOutput.toString();
   }

   private static Map<String, String> buildBuiltinXMLEntityMap() {
      Map<String, String> entities = new HashMap<String, String>(10);
      entities.put("lt", "<");
      entities.put("gt", ">");
      entities.put("amp", "&");
      entities.put("apos", "'");
      entities.put("quot", "\"");
      return entities;
   }
}
