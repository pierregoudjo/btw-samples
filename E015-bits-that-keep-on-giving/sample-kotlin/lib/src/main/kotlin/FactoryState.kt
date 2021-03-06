import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

class FactoryState(journal: Events) {
    val journal = journal.toPersistentList()

    val listOfEmployeeNames: Employees by lazy {
        this.journal
            .filterIsInstance<EmployeeAssignedToFactory>()
            .fold(emptyList(), { acc, event -> acc + event.employee })
    }

    val shipmentsWaitingToBeUnpacked: Shipments by lazy {
        this.journal
            .fold(emptyList(), { acc, event ->
                when (event) {
                    is ShipmentTransferredToCargoBay -> acc + listOf(event.shipment)
                    is ShipmentUnpackedInCargoBay -> emptyList()
                    else -> acc
                }
            })
    }

    val inventory: Inventory by lazy {
        val partsUsed = this.journal
            .filterIsInstance<CarProduced>()
            .flatMap { it.carPartPackages }
            .groupBy { it.part }
            .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

        val partsUnpacked = this.journal
            .filterIsInstance<ShipmentUnpackedInCargoBay>()
            .flatMap { it.carPartPackages }
            .groupBy { it.part }
            .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

        partsUnpacked.mapValues { it.value - partsUsed.getOrDefault(it.key, 0) }
    }

    val employeesWhoHasBuiltCars: Employees by lazy {
        this.journal
            .filterIsInstance<CarProduced>()
            .map { it.employee }
    }

    val employeeWhoHasUnpackedShipmentsInCargoBayToday: Employees by lazy {
        this.journal
            .filterIsInstance<ShipmentUnpackedInCargoBay>()
            .map { it.employee }
    }
}

fun apply(event: Events, state: FactoryState): FactoryState {
    val events = (state.journal + event)
    return FactoryState(events)
}
