package org.chornobyl.hamdash.data

import org.chornobyl.hamdash.data.model.TalkgroupDto

/**
 * Picks the talkgroups relevant around Chornobyl from BrandMeister's worldwide ID → name
 * list: Ukraine's (country code 255 and its national, emergency and regional groups) plus
 * the network-wide Local, World-wide and Europe groups. BrandMeister publishes names only,
 * so descriptions are left empty rather than written by us.
 */
object BrandmeisterTalkgroupMapper {
    const val SOURCE = "BrandMeister talkgroup list (api.brandmeister.network)"

    private const val UKRAINE_PREFIX = "255"
    private const val MAX_UKRAINE_ID_LENGTH = 5
    private val NETWORK_WIDE = setOf(9, 91, 92)

    fun map(talkgroups: Map<String, String>): List<TalkgroupDto> =
        talkgroups.mapNotNull { (key, rawName) ->
            val tgId = key.toIntOrNull() ?: return@mapNotNull null
            val name = rawName.trim().takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            val isUkrainian = key.startsWith(UKRAINE_PREFIX) && key.length <= MAX_UKRAINE_ID_LENGTH
            if (!isUkrainian && tgId !in NETWORK_WIDE) return@mapNotNull null
            TalkgroupDto(id = "bm-$tgId", tgId = tgId, name = name, description = "", source = SOURCE)
        }.sortedBy { it.tgId }
}
