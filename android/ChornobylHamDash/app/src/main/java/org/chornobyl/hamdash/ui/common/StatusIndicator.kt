package org.chornobyl.hamdash.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.chornobyl.hamdash.ui.theme.HamGreen
import org.chornobyl.hamdash.ui.theme.HamRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConnectivityBadge(isOnline: Boolean, modifier: Modifier = Modifier) {
    val color = if (isOnline) HamGreen else HamRed
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .padding(end = 6.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = if (isOnline) "ONLINE" else "OFFLINE",
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
    }
}

fun formatLastSync(epochMillis: Long?): String {
    if (epochMillis == null) return "Last sync: never"
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    return "Last sync: ${formatter.format(Date(epochMillis))}"
}
