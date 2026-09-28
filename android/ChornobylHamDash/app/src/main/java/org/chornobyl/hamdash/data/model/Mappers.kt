package org.chornobyl.hamdash.data.model

import org.chornobyl.hamdash.database.entity.BandEntity
import org.chornobyl.hamdash.database.entity.DigitalModeEntity
import org.chornobyl.hamdash.database.entity.PropagationEntity
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.database.entity.TalkgroupEntity
import org.chornobyl.hamdash.domain.RepeaterMode

fun RepeaterDto.toEntity(existingFavorite: Boolean = false) = RepeaterEntity(
    id = id,
    name = name,
    callsign = callsign,
    mode = RepeaterMode.fromRaw(mode).name,
    rxFrequencyMhz = rxFrequencyMhz,
    txFrequencyMhz = txFrequencyMhz,
    offsetMhz = offsetMhz,
    toneCtcss = toneCtcss,
    digitalMode = digitalMode,
    colorCode = colorCode,
    timeslot = timeslot,
    talkgroup = talkgroup,
    latitude = latitude,
    longitude = longitude,
    elevationM = elevationM,
    source = source,
    lastUpdated = lastUpdated,
    isFavorite = existingFavorite,
)

fun BandDto.toEntity() = BandEntity(
    id = id,
    name = name,
    freqRangeLow = freqRangeLow,
    freqRangeHigh = freqRangeHigh,
    typicalModes = typicalModes,
    purpose = purpose,
    blurb = blurb,
)

fun DigitalModeDto.toEntity() = DigitalModeEntity(
    id = id,
    name = name,
    description = description,
    howItWorks = howItWorks,
    useCases = useCases,
    equipmentNeeded = equipmentNeeded,
    docUrl = docUrl,
)

fun TalkgroupDto.toEntity() = TalkgroupEntity(
    id = id,
    tgId = tgId,
    name = name,
    description = description,
    source = source,
)

fun PropagationDto.toEntity() = PropagationEntity(
    id = 1,
    solarFluxIndex = solarFluxIndex,
    kIndex = kIndex,
    aIndex = aIndex,
    solarFlares = solarFlares,
    geomagneticState = geomagneticState,
    source = source,
    lastUpdated = lastUpdated,
)
