package com.example.careconnect.ui.patient.orders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.toDateTimeString

// Erstellt -> Angenommen -> Erledigt
@Composable
fun StatusTimeline(order: Order) {
    SectionCard(title = tr("Status"), icon = Icons.Default.Timeline) {
        TimelineStep(tr("Erstellt"), order.createdAt?.toDateTimeString(), done = true)
        TimelineStep(tr("Angenommen"), null, done = order.state != OrderState.OFFEN)
        TimelineStep(
            tr("Erledigt"),
            order.report?.completedAt?.toDateTimeString(),
            done = order.state == OrderState.ERLEDIGT
        )
    }
}

@Preview
@Composable
fun StatusTimelinePreview() {
    CareConnectTheme(darkTheme = false) {
        StatusTimeline(order = MockData.orders.last())
    }
}
