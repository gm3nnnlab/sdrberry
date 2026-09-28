package org.chornobyl.hamdash.data

import org.junit.Assert.assertEquals
import org.junit.Test

class BrandmeisterTalkgroupMapperTest {

    @Test
    fun `keeps Ukrainian and network-wide talkgroups in ID order`() {
        val result = BrandmeisterTalkgroupMapper.map(
            mapOf(
                "25510" to "Kyivs`ka obl",
                "91" to "World-wide",
                "255" to "Ukraine",
                "2559" to "Emergency Ukraine",
                "9" to "Local",
                "92" to "Europe",
            ),
        )
        assertEquals(listOf(9, 91, 92, 255, 2559, 25510), result.map { it.tgId })
    }

    @Test
    fun `drops other countries and long private-looking IDs`() {
        val result = BrandmeisterTalkgroupMapper.map(
            mapOf("202" to "Greece", "3100" to "USA Bridge", "2551234" to "Someone", "255" to "Ukraine"),
        )
        assertEquals(listOf(255), result.map { it.tgId })
    }

    @Test
    fun `skips non-numeric IDs and blank names`() {
        val result = BrandmeisterTalkgroupMapper.map(mapOf("abc" to "Bad", "25501" to "  ", "25514" to " Lvivs`ka obl "))
        assertEquals(listOf(25514), result.map { it.tgId })
        assertEquals("Lvivs`ka obl", result.single().name)
    }

    @Test
    fun `records the source and invents no description`() {
        val tg = BrandmeisterTalkgroupMapper.map(mapOf("255" to "Ukraine")).single()
        assertEquals("bm-255", tg.id)
        assertEquals("", tg.description)
        assertEquals(BrandmeisterTalkgroupMapper.SOURCE, tg.source)
    }
}
