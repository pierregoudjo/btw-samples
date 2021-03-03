import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

class FactoryState(journal: List<Event>) {
    val journal = journal.toPersistentList()

    val listOfEmployeeNames: List<String> by lazy {
        this.journal
            .filterIsInstance<EmployeeAssignedToFactory>()
            .fold(emptyList(), { acc, event -> acc + event.employeeName })
    }

    val shipmentsWaitingToBeUnpacked: List<List<CarPart>> by lazy {
        this.journal
            .fold(emptyList(), { acc, event ->
                when (event) {
                    is ShipmentTransferredToCargoBay -> acc + listOf(event.carParts)
                    is ShipmentUnpackedInCargoBay -> emptyList()
                    else -> acc
                }
            })
    }

    val inventory: Map<String, Int> by lazy {
        val partsUsed = this.journal
            .filterIsInstance<CarProduced>()
            .flatMap { it.carParts }
            .groupBy { it.name }
            .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

        val partsUnpacked = this.journal
            .filterIsInstance<ShipmentUnpackedInCargoBay>()
            .flatMap { it.carParts }
            .groupBy { it.name }
            .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

        partsUnpacked.mapValues { it.value - partsUsed.getOrDefault(it.key, 0) }
    }

    val employeesWhoHasBuiltCars: List<String> by lazy {
        this.journal
            .filterIsInstance<CarProduced>()
            .map { it.employeeName }
    }

    val employeeWhoHasUnpackedShipmentsInCargoBayToday: List<String> by lazy {
        this.journal
            .filterIsInstance<ShipmentUnpackedInCargoBay>()
            .map { it.employeeName }
    }
}

fun apply(event: List<Event>, state: FactoryState): FactoryState {
    val events = (state.journal + event)
    return FactoryState(events)
}
