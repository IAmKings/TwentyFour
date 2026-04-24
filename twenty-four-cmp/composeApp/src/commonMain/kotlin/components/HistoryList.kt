package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.HistoryEntry
import data.HistoryStatus
import data.displayNumbers
import theme.Success
import theme.Surface
import theme.TextMuted
import theme.TextPrimary

@Composable
fun HistoryList(
    history: List<HistoryEntry>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "历史记录",
            style = MaterialTheme.typography.titleMedium,
            color = TextMuted,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            history.forEach { entry ->
                HistoryItem(entry = entry)
            }
        }
    }
}

@Composable
private fun HistoryItem(entry: HistoryEntry) {
    val statusColor = when (entry.status) {
        HistoryStatus.PASS -> Success
        HistoryStatus.SKIP -> TextMuted
        HistoryStatus.FAIL -> androidx.compose.ui.graphics.Color(0xFFE85454)
    }

    val statusSymbol = when (entry.status) {
        HistoryStatus.PASS -> "\u2713"
        HistoryStatus.SKIP -> "\u2014"
        HistoryStatus.FAIL -> "\u2717"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.displayNumbers(),
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
        Text(
            text = "$statusSymbol ${entry.expression}",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = statusColor
            )
        )
    }
}
