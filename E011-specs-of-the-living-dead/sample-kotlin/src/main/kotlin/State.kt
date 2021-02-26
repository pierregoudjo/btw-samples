import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

class FactoryState(journal: List<Event>) {
    val journal = journal.toPersistentList()

    val listOfEmployeeNames: List<String> by lazy {
        this.journal
            .filterIsInstance<EmployeeAssignedToFactory>()
            .fold(emptyList(), { acc, event -> acc + event.employeeName })
    }


    val shipmentsWaitingToBeUnloaded: List<List<CarPartPack>> by lazy {
        this.journal
            .fold(emptyList(), { acc, event ->
                when (event) {
                    is ShipmentTransferredToCargoBay -> acc + listOf(event.carPartPacks)
                    is ShipmentUnloadedFromCargoBay -> emptyList()
                    else -> acc
                }
            })
    }

    val inventory: Map<String, Int> by lazy {
        val partUsed = this.journal
            .filterIsInstance<CarProduced>()
            .flatMap { it.carPartPacks }
            .groupBy { it.name }
            .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

        val partUnloaded = this.journal
            .filterIsInstance<ShipmentUnloadedFromCargoBay>()
            .flatMap { it.carPartPacks }
            .groupBy { it.name }
            .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

        partUnloaded.mapValues { it.value - partUsed.getOrDefault(it.key, 0)}
    }
}

fun apply(event: List<Event>, state: FactoryState): FactoryState {
    val events = (state.journal + event)
    return FactoryState(events)
}
