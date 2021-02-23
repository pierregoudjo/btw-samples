import kotlin.properties.Delegates
import kotlin.reflect.KProperty

//// let's define our list of commands that the factory can carry out.
//sealed class FactoryImplementation1 {
//    // the methods below are linguistically equivalent to a command message
//    // that could be sent to this factory. A command such as:
//    // public class AssignEmployeeToFactory
//    // {
//    //    public string EmployeeName { get; set; }
//    // }
//
//    // in this sample we will not create command messages to represent
//    // and call these methods, we will just use the methods themselves to be our
//    // "commands" for convenience.
//
//    fun assignEmployeeToFactory(employeeName: String) {}
//    fun transferShipmentToCargoBay(shipmentName: String, parts: Array<CarPart>) {}
//    fun unloadShipmentFromCargoBay(employeeName: String) {}
//    fun produceCar(employeeName: String, carModel: String) {}
//}
//
//// these factory methods could contain the following elements (which can be
//// really complex or can be optional):
//// * Checks (aka "guards") to see if an operation is allowed
//// * some work that might involve calculations, thinking, access to some tooling
//// * Events that we write to the journal to mark the work as being done.
//// These elements are noted as comments inside of the methods below for now
//sealed class FactoryImplementation2 {
//    fun AssignEmployeeToFactory(employeeName: String) {
//        // CheckIfEmployeeCanBeAssignedToFactory(employeeName);
//        // DoPaperWork();
//        // RecordThatEmployeeAssignedToFactory(employeeName);
//    }
//
//    fun TransferShipmentToCargoBay(shipmentName: String, parts: List<CarPart>) {
//        // CheckIfCargoBayHasFreeSpace(parts);
//        // DoRealWork("unloading supplies...");
//        // DoPaperWork("Signing the shipment acceptance form");
//        // RecordThatSuppliesAreAvailableInCargoBay()
//    }
//
//    fun UnloadShipmentFromCargoBay(employeeName: String) {
//        // DoRealWork("passing supplies");
//        // RecordThatSuppliesWereUnloadedFromCargoBay()
//    }
//
//    fun ProduceCar(employeeName: String, carModel: String) {
//        // CheckIfWeHaveEnoughSpareParts
//        // CheckIfEmployeeIsAvailable
//        // DoRealWork
//        // RecordThatCarWasProduced
//    }
//}




// Now let's "unwrap" AssignEmployeeToFactory
// we'll start by adding a list of employees

class FactoryImplementation3 {

    // THE Factory Journal!
    // Where all things that happen inside of the factory are recorded
    private val journalFactoryEvents = ArrayList<Event>()

    val journal: List<Event> by this::journalFactoryEvents

    // internal "state" variables
    // these are the things that hold the data that represents
    // our current understanding of the state of the factory
    // they get their data from the methods that use them while the methods react to events
    private val _ourListOfEmployeeNames = ArrayList<String>()
    private val _shipmentsWaitingToBeUnloaded = ArrayList<List<CarPart>>()


//    val _stockJournal: String by this::journalFactoryEvents.getValue().size


    fun assignEmployeeToFactory(employeeName: String) {
        println("<? Command assign employee $employeeName to the factory")

        // Hey look, a business rule implementation
        if (_ourListOfEmployeeNames.contains(employeeName)) {
            println(":> the name of $employeeName only one can have")
            return
        }
        // another check that needs to happen when assigning employees to the factory
        // multiple options to prove this critical business rule:
        // John Bender: http://en.wikipedia.org/wiki/John_Bender_(character)#Main_characters
        // Bender Bending Rodríguez: http://en.wikipedia.org/wiki/Bender_(Futurama)

        if (employeeName == "Bender") {
            println(":> Guys with the name 'bender' are trouble")
            return
        }

        doPaperWork("Assign employee to the factory")
        recordThat(EmployeeAssignedToFactory(employeeName))
    }

    fun transferShipmentToCargoBay(shipmentName: String, parts: List<CarPart>) {
        println("?> Command: transfer shipment to cargo")
        if (_ourListOfEmployeeNames.size == 0) {
            println(":> there has to be somebody at the factory in order to accept the shipment")
            return
        }

        if (_shipmentsWaitingToBeUnloaded.size > 2) {
            println(":> More than two shipments can't fit into this cargo bay")
            return
        }
        doRealWork("opening cargo bay doors")
        recordThat(ShipmentTransferredToCargoBay(shipmentName = shipmentName, carParts = parts))

        val totalCountOfParts = parts.sumOf { it.quantity }

        if (totalCountOfParts > 10) {
            recordThat(
                CurseWordUttered(
                    theWord = "Boltov tebe v korobky peredach",
                    meaning = "awe in the face of the amount of parts delivered"
                )
            )
        }
    }

    fun unloadShipmentFromCargoBay(employeeName: String) {
        if (!_ourListOfEmployeeNames.contains(employeeName)) {
            println(":> $employeeName must be assigned to the factory to unload the cargo bay")
            return
        }

        if (_shipmentsWaitingToBeUnloaded.size == 0) {
            println(":> There should be a shipment to unload")
            return
        }

         doRealWork("passing supplies")
        // RecordThatSuppliesWereUnloadedFromCargoBay()
        recordThat(
            CargoBayUnloaded(
                employeeName,
                _shipmentsWaitingToBeUnloaded.flatten()
            )
        )
    }

    fun produceCar(employeeName: String, carModel: String) {
        // CheckIfWeHaveEnoughSpareParts
        // CheckIfEmployeeIsAvailable
        // DoRealWork
        // RecordThatCarWasProduced
    }

    private fun doRealWork(workName: String) {
        println(" > Work: heavy stuff... $workName ...")
        Thread.sleep(1000)
    }

    private fun recordThat(event: Event) {
        journalFactoryEvents.add(event)
        announceInsideFactory(event)
        println("!> Event $event")
    }

    private fun announceInsideFactory(event: Event) {
        when (event) {
            is EmployeeAssignedToFactory -> announceInsideFactory(event)
            is ShipmentTransferredToCargoBay -> announceInsideFactory(event)
            is CurseWordUttered -> announceInsideFactory(event)
            is CargoBayUnloaded -> announceInsideFactory(event)
        }
    }

    private fun announceInsideFactory(event: EmployeeAssignedToFactory) {
        _ourListOfEmployeeNames.add(event.employeeName)
    }

    private fun announceInsideFactory(event: ShipmentTransferredToCargoBay) {
        _shipmentsWaitingToBeUnloaded.add(event.carParts)
    }

    private fun announceInsideFactory(event: CurseWordUttered) {

    }

    private fun announceInsideFactory(event: CargoBayUnloaded) {

    }

    private fun doPaperWork(workName: String) {
        println(" > Work: Papers... $workName ...")
        Thread.sleep(1000)
    }
}

data class CurseWordUttered(val theWord: String, val meaning: String): Event {
    override fun toString() =
        "'$theWord' was heard within the walls. It meant: '$meaning'"

}

data class ShipmentTransferredToCargoBay(val shipmentName: String, val carParts: List<CarPart>): Event {
    override fun toString() =
        "Shipment '$shipmentName' transferred to cargo bay: "+ carParts.map{ "${it.name} ${it.quantity} pcs"}.reduce { acc, s -> "$acc, $s" }

}

data class EmployeeAssignedToFactory(val employeeName: String): Event {
    override fun toString() =
        "new worker joins our forces: '$employeeName'"
}

data class CargoBayUnloaded(val employeeName: String, val carParts: List<CarPart>): Event {
    override fun toString() =
        "$employeeName unloaded "+ carParts.map{ "${it.name} ${it.quantity} pcs"}.reduce { acc, s -> "$acc, $s" }
}

interface Event

data class CarPart(val name: String, val quantity: Int)

fun main() {
    println("A new day at the factory starts")
    val factory =  FactoryImplementation3()
    factory.transferShipmentToCargoBay("chassis", listOf(CarPart("chassis", 4)))
    factory.assignEmployeeToFactory("Yoda")
    factory.assignEmployeeToFactory("Luke")
    // Hmm, a duplicate employee name, wonder if that will work?
    factory.assignEmployeeToFactory("Yoda");
    factory.assignEmployeeToFactory("Bender");

    factory.transferShipmentToCargoBay("model T spare parts", listOf(
        CarPart("wheels", 20),
        CarPart("engine", 7),
        CarPart("bits and pieces", 2),
    ))

    println("""
        It's the end of the day. Let's read our journal of events once more:
        We should only see events below that were actually allowed to be recorded.
    """.trimIndent())

    factory.journal.forEach { println("!> $it") }

    println("It seems, this was an interesting day!  Two Yoda's there should be not!")

}