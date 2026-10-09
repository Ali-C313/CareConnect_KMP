package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.CompletionReport
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.i18n.tr
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.model.OrderDto
import com.example.careconnect.remote.model.toDto
import com.example.careconnect.remote.model.toOrder
import com.example.careconnect.remote.model.toTimestamp
import com.example.careconnect.remote.org
import com.example.careconnect.session.OrgSession
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.QuerySnapshot
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderRepositoryImpl(
    private val db: FirebaseFirestore,
    private val session: OrgSession
) : OrderRepository {

    private fun ordersRef(orgId: String = session.requireOrgId()) =
        db.org(orgId).collection(CollectionNames.ORDERS)

    override fun getOrders(): Flow<List<Order>> = session.inOrg(emptyList()) { orgId ->
        ordersRef(orgId).snapshots
            .map { it.toOrders() }
            .catch { emit(emptyList()) }
    }

    override fun getOrder(id: String): Flow<Order?> = session.inOrg(null) { orgId ->
        ordersRef(orgId).document(id).snapshots
            .map { doc ->
                if (!doc.exists) null
                else runCatching { doc.data<OrderDto>().toOrder(doc.id) }.getOrNull()
            }
            .catch { emit(null) }
    }

    override fun getOrdersForPatient(patientId: String): Flow<List<Order>> = session.inOrg(emptyList()) { orgId ->
        ordersRef(orgId)
            .where { "patientId" equalTo patientId }
            .snapshots
            .map { it.toOrders() }
            .catch { emit(emptyList()) }
    }

    // true, solange der Mitarbeiter einen angenommenen Auftrag hat
    override fun hasActiveOrder(employeeId: String): Flow<Boolean> = session.inOrg(false) { orgId ->
        ordersRef(orgId)
            .where { "employeeId" equalTo employeeId }
            .snapshots
            .map { snapshot -> snapshot.toOrders().any { it.state == OrderState.AKTUELL } }
            .catch { emit(false) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createOrder(order: Order): Result<Unit> = runCatching {
        val id = order.id.ifEmpty { Uuid.random().toString() }
        ordersRef().document(id).set(OrderDto.serializer(), order.copy(id = id).toDto(), merge = false)
    }

    override suspend fun updateOrder(
        id: String,
        patientId: String,
        patientName: String,
        description: String,
        dueDate: Long
    ): Result<Unit> = runCatching {
        ordersRef().document(id).updateFields {
            "patientId" to patientId
            "patientName" to patientName
            "description" to description
            "dueDate" to dueDate.toTimestamp()
        }
    }

    // Transaktion, damit nicht zwei Mitarbeiter denselben Auftrag annehmen
    override suspend fun acceptOrder(id: String, employeeId: String): Result<Unit> = runCatching {
        val ref = ordersRef().document(id)
        db.runTransaction {
            val snapshot = get(ref)
            if (!snapshot.exists) throw Exception(tr("Der Auftrag existiert nicht mehr."))
            val state = OrderState.fromValue(runCatching { snapshot.get<String?>("state") }.getOrNull())
            if (state != OrderState.OFFEN) throw Exception(tr("Der Auftrag wurde schon von jemand anderem angenommen."))
            updateFields(ref) {
                "state" to OrderState.AKTUELL.value
                "employeeId" to employeeId
            }
        }
        Unit
    }

    // Abschließen mit Leistungsnachweis (Notiz + Unterschrift)
    override suspend fun completeOrder(id: String, report: CompletionReport): Result<Unit> = runCatching {
        ordersRef().document(id).updateFields {
            "state" to OrderState.ERLEDIGT.value
            "report.note" to report.note
            "report.signature" to report.signature
            "report.signedBy" to report.signedBy
            "report.completedAt" to Timestamp.now()
            "report.completedBy" to report.completedBy
            "report.completedByName" to report.completedByName
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createOrders(orders: List<Order>): Result<Unit> = runCatching {
        // Firestore erlaubt max. 500 Schreibvorgänge pro Batch
        orders.chunked(400).forEach { chunk ->
            val batch = db.batch()
            chunk.forEach { order ->
                val id = order.id.ifEmpty { Uuid.random().toString() }
                batch.set(ordersRef().document(id), OrderDto.serializer(), order.copy(id = id).toDto(), merge = false)
            }
            batch.commit()
        }
    }

    override suspend fun deleteOrder(id: String): Result<Unit> = runCatching {
        ordersRef().document(id).delete()
    }

    // Neueste zuerst, kaputte Dokumente überspringen
    private fun QuerySnapshot.toOrders(): List<Order> = documents
        .mapNotNull { doc ->
            runCatching { doc.data<OrderDto>().toOrder(doc.id) }.getOrNull()
        }
        .sortedByDescending { it.createdAt ?: 0L }
}
