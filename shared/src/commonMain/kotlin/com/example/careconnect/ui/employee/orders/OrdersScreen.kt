package com.example.careconnect.ui.employee.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.platform.LocationTrackerMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.LocationRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.service.TrackingManager
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.EmptyStateView
import com.example.careconnect.ui.components.FilterTabs
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.Info
import org.koin.compose.viewmodel.koinViewModel

// Liste aller Aufträge mit Filter nach Status
@Composable
fun OrdersScreen(
    onOrderClick: (String) -> Unit,
    viewModel: OrdersViewModel = koinViewModel()
) {
    val orders by viewModel.filteredOrders.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val isTrackingActive by viewModel.isTrackingActive.collectAsState()
    var showNewOrderSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(
            title = tr("Aufträge"),
            actions = {
                FilledIconButton(onClick = { showNewOrderSheet = true }) {
                    Icon(Icons.Default.Add, contentDescription = tr("Neuen Auftrag erstellen"))
                }
            }
        )

        FilterTabs(
            options = OrderState.entries,
            selected = selectedFilter,
            label = { it.displayName },
            onSelect = viewModel::onFilterChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (orders.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = tr("Keine Aufträge"),
                message = tr("Erstelle einen neuen Auftrag mit dem + Button oben rechts."),
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    val employeeName = employees.firstOrNull { it.id == order.employeeId }?.name
                        ?: if (order.employeeId.isEmpty()) tr("Noch offen") else tr("Unbekannt")
                    OrderCard(
                        order = order,
                        employeeName = employeeName,
                        modifier = Modifier.clickable { onOrderClick(order.id) }
                    )
                }
            }
        }

        // Live-Tracking Hinweis
        if (isTrackingActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = Info)
                    Text(
                        tr("Live-Tracking aktiv"),
                        color = Info,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }

    if (showNewOrderSheet) {
        NewOrderSheet(
            existingOrder = null,
            onDismiss = { showNewOrderSheet = false }
        )
    }
}

@Preview
@Composable
fun OrdersScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        OrdersScreen(
            onOrderClick = {},
            viewModel = OrdersViewModel(
                OrderRepositoryMock(),
                EmployeeRepositoryMock(),
                TrackingManager(LocationTrackerMock(), LocationRepositoryMock())
            )
        )
    }
}
