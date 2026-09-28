package org.chornobyl.hamdash.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("ABOUT", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text(
            "Chornobyl HamDash aggregates public, legal amateur-radio information for " +
                "operators working in and around the Chornobyl exclusion zone: repeaters, " +
                "DMR details, band and digital-mode reference, and basic HF propagation.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "This app is not a navigation app and is not intended for military use. It only " +
                "aggregates public, legal amateur-radio data — no interception of closed or " +
                "encrypted systems, no military frequencies, and no covert location tracking.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "Your GPS location is used only to show your own position on the map. It is " +
                "never transmitted anywhere; this app has no backend to send it to.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Text("DATA SOURCES", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Text(
            "Repeaters: the public HearHam.live repeater list (hearham.com), which permits use " +
                "in apps. Only repeaters marked operational within 300 km of Chornobyl are shown, " +
                "and entries whose frequencies fall outside the amateur bands are dropped. The app " +
                "ships with a dated snapshot and refreshes it at most weekly on Wi-Fi, because the " +
                "worldwide list is a ~9.5 MB download. Listings are community-maintained: check " +
                "before transmitting.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "DMR talkgroups: the BrandMeister network's public talkgroup list " +
                "(api.brandmeister.network) — Ukraine's national, emergency and regional " +
                "talkgroups plus the network-wide Local, World-wide and Europe groups. " +
                "BrandMeister publishes names only, so no descriptions are shown.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "Bands and digital modes: a bundled, illustrative mock dataset " +
                "(\"Mock Local Dataset\") seeded from local JSON assets — not a live feed.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "Propagation: live data from the NOAA Space Weather Prediction Center " +
                "(services.swpc.noaa.gov, public domain) — 10.7 cm solar flux, planetary Kp and " +
                "daily Ap, and GOES X-ray flares. Fetched only by auto-sync; no location or " +
                "personal data is sent.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text("Map tiles: OpenStreetMap contributors (via osmdroid).", style = MaterialTheme.typography.bodyMedium)
        Text(
            "Sunrise/sunset: computed locally from device location and date using standard " +
                "solar position equations — no network call.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
