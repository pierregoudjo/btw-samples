import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

class FactoryState(journal: List<Event>) {
    val journal = journal.toPersistentList()

    val listOfEmployeeNames: List<String> by EmployeeDelegate()


    val shipmentsWaitingToBeUnloaded: List<List<CarPartPack>> by ShipmentWaitingToBeUnloadedDelegate()

    val stock: Map<String, Int> by CarPartStockDelegate()
}

fun apply(event: List<Event>, state: FactoryState): FactoryState {
    val events = (state.journal + event)
    return FactoryState(events)
}