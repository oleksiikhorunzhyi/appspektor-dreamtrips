package com.worldventures.dreamtrips.messenger.delegate.chat.typing;

import com.messenger.delegate.chat.typing.MemoryTypingStore;
import com.messenger.delegate.chat.typing.TypingStore;
import com.worldventures.dreamtrips.BaseTest;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class StorageTests extends BaseTest {
   private TypingStore typingStore = new MemoryTypingStore();

   @Test
   public void testAdding() {
      typingStore.add("conversation1", "user1");
      typingStore.add("conversation2", "user1");
      typingStore.add("conversation1", "user2");
      typingStore.add("conversation1", "user3");

      Assert.assertEquals(getTypingUsers("conversation1").size(), 3);
      Assert.assertEquals(getTypingUsers("conversation2").size(), 1);

      typingStore.deleteAll();
   }

   @Test
   public void testDeleting() {
      typingStore.add("conversation1", "user1");
      typingStore.add("conversation1", "user2");
      typingStore.add("conversation1", "user3");
      typingStore.add("conversation2", "user1");
      typingStore.add("conversation3", "user4");
      typingStore.add("conversation3", "user5");

      typingStore.delete("conversation1", "user2");

      Assert.assertEquals(getTypingUsers("conversation1").size(), 2);
      Assert.assertEquals(getTypingUsers("conversation2").size(), 1);
      Assert.assertEquals(getTypingUsers("conversation3").size(), 2);

      typingStore.delete("conversation1", "user5");

      Assert.assertEquals(getTypingUsers("conversation1").size(), 2);
      Assert.assertEquals(getTypingUsers("conversation2").size(), 1);
      Assert.assertEquals(getTypingUsers("conversation3").size(), 2);

      typingStore.deleteAll();

      Assert.assertEquals(getTypingUsers("conversation2").size(), 0);
      Assert.assertEquals(getTypingUsers("conversation1").size(), 0);
      Assert.assertEquals(getTypingUsers("conversation3").size(), 0);
   }

   @Test
   public void testDeletingUserById() {
      typingStore.add("conversation1", "user1");
      typingStore.add("conversation1", "user2");
      typingStore.add("conversation1", "user3");
      typingStore.add("conversation2", "user1");
      typingStore.add("conversation3", "user4");
      typingStore.add("conversation3", "user5");

      typingStore.deleteByUserId("user6");

      Assert.assertEquals(getTypingUsers("conversation1").size(), 3);
      Assert.assertEquals(getTypingUsers("conversation2").size(), 1);
      Assert.assertEquals(getTypingUsers("conversation3").size(), 2);

      typingStore.deleteByUserId("user1");

      Assert.assertEquals(getTypingUsers("conversation1").size(), 2);
      Assert.assertEquals(getTypingUsers("conversation2").size(), 0);
      Assert.assertEquals(getTypingUsers("conversation3").size(), 2);

      typingStore.deleteAll();
   }

   private List<String> getTypingUsers(String conversationId) {
      return typingStore.getTypingUsers(conversationId).toBlocking().first();
   }
}
