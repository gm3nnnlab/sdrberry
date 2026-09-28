package org.chornobyl.hamdash.domain

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class SunTimes(
    val sunriseUtc: ZonedDateTime?,
    val sunsetUtc: ZonedDateTime?,
    val dayLengthMinutes: Long?,
)

/**
 * Standard NOAA-style sunrise/sunset approximation. Returns null times for polar
 * day/night where the sun does not rise or set. Deterministic, no network required.
 */
object SunCalculator {
    private fun deg2rad(deg: Double) = deg * PI / 180.0
    private fun rad2deg(rad: Double) = rad * 180.0 / PI

    fun calculate(date: LocalDate, latitude: Double, longitude: Double): SunTimes {
        val zenith = 90.833 // official sunrise/sunset zenith, includes atmospheric refraction

        fun compute(isSunrise: Boolean): ZonedDateTime? {
            val dayOfYear = date.dayOfYear
            val lngHour = longitude / 15.0
            val t = if (isSunrise) dayOfYear + ((6 - lngHour) / 24.0) else dayOfYear + ((18 - lngHour) / 24.0)

            val m = (0.9856 * t) - 3.289
            var l = m + (1.916 * sin(deg2rad(m))) + (0.020 * sin(deg2rad(2 * m))) + 282.634
            l = normalizeDegrees(l)

            var ra = rad2deg(atan2(0.91764 * tan(deg2rad(l)), 1.0))
            ra = normalizeDegrees(ra)
            val lQuadrant = floor(l / 90.0) * 90.0
            val raQuadrant = floor(ra / 90.0) * 90.0
            ra += (lQuadrant - raQuadrant)
            ra /= 15.0

            val sinDec = 0.39782 * sin(deg2rad(l))
            val cosDec = cos(asin(sinDec))

            val cosH = (cos(deg2rad(zenith)) - (sinDec * sin(deg2rad(latitude)))) /
                (cosDec * cos(deg2rad(latitude)))

            if (cosH > 1.0 || cosH < -1.0) return null // sun never rises/sets on this date

            val h = if (isSunrise) {
                360.0 - rad2deg(acos(cosH))
            } else {
                rad2deg(acos(cosH))
            } / 15.0

            val localMeanTime = h + ra - (0.06571 * t) - 6.622
            var utcTime = localMeanTime - lngHour
            utcTime = ((utcTime % 24.0) + 24.0) % 24.0

            val hour = floor(utcTime).toInt()
            val minute = floor((utcTime - hour) * 60.0).toInt()
            return ZonedDateTime.of(date, LocalTime.of(hour, minute), ZoneOffset.UTC)
        }

        val sunrise = compute(isSunrise = true)
        val sunset = compute(isSunrise = false)
        val dayLength = if (sunrise != null && sunset != null) {
            var minutes = java.time.Duration.between(sunrise, sunset).toMinutes()
            if (minutes < 0) minutes += 24 * 60
            minutes
        } else {
            null
        }
        return SunTimes(sunrise, sunset, dayLength)
    }

    private fun normalizeDegrees(value: Double): Double {
        var v = value
        if (v < 0) v += 360.0
        if (v >= 360.0) v -= 360.0
        return v
    }
}

fun ZonedDateTime.toLocalZone(zone: ZoneId): ZonedDateTime = this.withZoneSameInstant(zone)
