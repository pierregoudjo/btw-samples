import kotlin.reflect.KProperty

class CarPartStockDelegate {
    operator fun getValue(thisRef: FactoryState, property: KProperty<*>): Map<String, Int> {

        val partUsed = thisRef.journal
            .filterIsInstance<CarBuilt>()
            .flatMap { it.carPartPacks }
            .groupBy { it.name }
            .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

        val partUnloaded = thisRef.journal
            .filterIsInstance<CargoBayUnloaded>()
            .flatMap { it.carPartPacks }
            .groupBy { it.name }
            .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

        return partUnloaded.mapValues { it.value - partUsed.getOrDefault(it.key, 0) }
    }
}

class ShipmentWaitingToBeUnloadedDelegate {
    operator fun getValue(thisRef: FactoryState, property: KProperty<*>): List<List<CarPartPack>> {
        return thisRef.journal
            .fold(emptyList(), { acc, event ->
                when (event) {
                    is ShipmentTransferredToCargoBay -> acc + listOf(event.carPartPacks)
                    is CargoBayUnloaded -> emptyList()
                    else -> acc
                }
            })

    }
}

class EmployeeDelegate {
    operator fun getValue(thisRef: FactoryState, property: KProperty<*>): List<String> {
        return thisRef.journal
            .filterIsInstance<EmployeeAssignedToFactory>()
            .fold(emptyList(), { acc, event -> acc + event.employeeName })
    }
}