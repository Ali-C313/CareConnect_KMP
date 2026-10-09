package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.toAge
import com.example.careconnect.util.toDateString

@Composable
fun HeaderCard(
    order: Order,
    patient: Patient?
) {
    SectionCard {
        Text(
            text = tr("Patientenauftrag"),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = order.patientName.ifEmpty { tr("Unbekannter Patient") },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (order.description.isNotEmpty()) {
            Text(
                text = order.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        patient?.birthDate?.let { birthDate ->
            Text(
                text = tr("Geboren: {0} • {1} Jahre", birthDate.toDateString(), birthDate.toAge()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (order.notes.isNotEmpty()) {
            InfoRow(Icons.Default.EditNote, tr("Bemerkungen"), order.notes.joinToString(", "))
        }
    }
}

@Preview
@Composable
fun HeaderCardPreview() {
    CareConnectTheme(darkTheme = false) {
        HeaderCard(order = MockData.orders.first(), patient = MockData.patient)
    }
}
