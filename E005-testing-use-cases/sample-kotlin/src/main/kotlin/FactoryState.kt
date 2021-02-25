import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

class FactoryState (journal: List<Event>){
    private var events = journal.toPersistentList()

    val journal: List<Event> by this::events

    val listOfEmployeeNames: List<String> by EmployeeDelegate()


    val shipmentsWaitingToBeUnloaded: List<List<CarPartPack>> by ShipmentWaitingToBeUnloadedDelegate()

    val stock: Map<String, Int> by CarPartStockDelegate()

    fun mutate(event: Event) {
        events = (events + event)
    }

}