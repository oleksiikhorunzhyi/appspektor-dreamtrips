import com.worldventures.dreamtrips.core.test.BaseTest
import com.worldventures.dreamtrips.modules.feed.view.util.HashtagSuggestionUtil
import org.junit.Test
import kotlin.test.assertEquals

class HashtagSuggestionUtilUnitTest : BaseTest() {

    @Test
    fun generateTextTest() {
        assertEquals("#hello ", HashtagSuggestionUtil.generateText("hel", "#hello", "hel".length))

        assertEquals("#welcome ", HashtagSuggestionUtil.generateText("#wel", "welcome", "#wel".length))

        assertEquals("hello #hello ", HashtagSuggestionUtil.generateText("hello #hel", "hello", "hello #hel".length))
        assertEquals("hello #hello ", HashtagSuggestionUtil.generateText("hello #hel", "#hello", "hello #hel".length))

        assertEquals("#tagCorrect #tag2", HashtagSuggestionUtil.generateText("#tag1 #tag2", "tagCorrect", "#tag1".length))
        assertEquals("#tagCorrect #tag3", HashtagSuggestionUtil.generateText("#tag1 #tag3", "#tagCorrect", "#tag1".length))

        assertEquals("#tagCorrect 1 #tag4", HashtagSuggestionUtil.generateText("#tag1 #tag4", "tagCorrect", "#tag".length))

        assertEquals("#TAG #tag #tag", HashtagSuggestionUtil.generateText("#tag #tag #tag", "TAG", "#tag".length))
        assertEquals("#tag #TAG #tag", HashtagSuggestionUtil.generateText("#tag #tag #tag", "TAG", "#tag #tag".length))
        assertEquals("#tag #tag #TAG ", HashtagSuggestionUtil.generateText("#tag #tag #tag", "TAG", "#tag #tag #tag".length))

        assertEquals("#hello #helt ", HashtagSuggestionUtil.generateText("#hello hel", "helt", "#hello hel".length))
        assertEquals("hello #helt ", HashtagSuggestionUtil.generateText("hello hel", "helt", "hello hel".length))
        assertEquals("#helt ", HashtagSuggestionUtil.generateText("hel", "helt", "hel".length))
        assertEquals("hel #helt hel", HashtagSuggestionUtil.generateText("hel hel hel", "helt", "hel hel".length))
        assertEquals("hel #helt hel", HashtagSuggestionUtil.generateText("hel hel hel", "#helt", "hel hel".length))
    }


    @Test
    fun calcStartPosBeforeReplaceTest() {
        assertEquals(0, HashtagSuggestionUtil.calcStartPosBeforeReplace("#welcome", "#welcome".length))
        assertEquals(3, HashtagSuggestionUtil.calcStartPosBeforeReplace("he #welcome", "he #welcome".length))
        assertEquals(0, HashtagSuggestionUtil.calcStartPosBeforeReplace("welcome", "welcome".length))
        assertEquals(8, HashtagSuggestionUtil.calcStartPosBeforeReplace("welcome #welcome", "welcome #wel".length))
    }
}