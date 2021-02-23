import kotlinx.collections.immutable.*
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

    class EmployeeDelegate {
        operator fun getValue(thisRef: FactoryImplementation3, property: KProperty<*>): List<String> {
            return thisRef.journal
                .filterIsInstance<EmployeeAssignedToFactory>()
                .fold(emptyList(), { acc, event -> acc + event.employeeName })
        }
    }

    class ShipmentWaitingToBeUnloadedDelegate {
        operator fun getValue(thisRef: FactoryImplementation3, property: KProperty<*>): List<List<CarPart>> {
            return thisRef.journal
                .fold(emptyList(), { acc, event -> when(event) {
                    is ShipmentTransferredToCargoBay -> acc + listOf(event.carParts)
                    is CargoBayUnloaded -> emptyList()
                    else -> acc
                } })

        }
    }

    class CarPartStockDelegate {
        operator fun getValue(thisRef: FactoryImplementation3, property: KProperty<*>): Map<String, Int> {

            val partUsed = thisRef.journal
                .filterIsInstance<CarBuilt>()
                .flatMap { it.carParts }
                .groupBy { it.name }
                .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

            val partUnloaded = thisRef.journal
                .filterIsInstance<CargoBayUnloaded>()
                .flatMap { it.carParts }
                .groupBy { it.name }
                .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

            return partUnloaded.mapValues { it.value - partUsed.getOrDefault(it.key, 0) }
        }
    }

    // THE Factory Journal!
    // Where all things that happen inside of the factory are recorded
    private var journalFactoryEvents = persistentListOf<Event>()

    val journal: List<Event> by this::journalFactoryEvents

    // internal "state" variables
    // these are the things that hold the data that represents
    // our current understanding of the state of the factory
    // they get their data from the methods that use them while the methods react to events
//    private val _ourListOfEmployeeNames = ArrayList<String>()

    private val _ourListOfEmployeeNames: List<String> by EmployeeDelegate()


    private val _shipmentsWaitingToBeUnloaded: List<List<CarPart>> by ShipmentWaitingToBeUnloadedDelegate()

    private val _stock: Map<String, Int> by CarPartStockDelegate()


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
        if (_ourListOfEmployeeNames.isEmpty()) {
            println(":> there has to be somebody at the factory in order to accept the shipment")
            return
        }

        if (_shipmentsWaitingToBeUnloaded.size >= 2) {
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
        println("?> Command: Order $employeeName to unload shipment from cargo bay")
        if (!_ourListOfEmployeeNames.contains(employeeName)) {
            println(":> $employeeName must be assigned to the factory to unload the cargo bay")
            return
        }

        if (_shipmentsWaitingToBeUnloaded.isEmpty()) {
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

    fun produceCar(employeeName: String, carModel: CarModel) {
        // CheckIfWeHaveEnoughSpareParts
        val neededParts = when(carModel) {
            CarModel.MODEL_T -> listOf(
                CarPart("wheels", 2),
                CarPart("engine", 1),
                CarPart("bits and pieces", 2))
            CarModel.MODEL_V -> listOf(
                CarPart("wheels", 2),
                CarPart("engine", 1),
                CarPart("bits and pieces", 2),
                CarPart("chassis", 1))
        }

        val enoughPart = neededParts.fold( true,
            {acc, curr -> acc && _stock.getOrDefault(curr.name, 0) >= curr.quantity}
        )

        if (!enoughPart) {
            println("There is not enough part to build $carModel car")
            return
        }

        if (!_ourListOfEmployeeNames.contains(employeeName)) {
            println(":> $employeeName must be assigned to the factory to build a car")
            return
        }

        doRealWork("Building the car...")

        doPaperWork("Writing car specification documents")

        recordThat(CarBuilt(employeeName, carModel, neededParts))
    }

    private fun doRealWork(workName: String) {
        println(" > Work: heavy stuff... $workName ...")
        Thread.sleep(1000)
    }

    private fun recordThat(event: Event) {
        journalFactoryEvents = (journalFactoryEvents + event)
        announceInsideFactory(event)

    }

    private fun announceInsideFactory(event: Event) {
        println("!> Event $event")
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

data class CarBuilt(val employeeName: String, val carModel: CarModel, val carParts: List<CarPart>): Event {
    override fun toString() =
        "Car $carModel built by $employeeName using " + carParts.map{ "${it.name} ${it.quantity} pcs"}.reduce { acc, s -> "$acc, $s" }
}

interface Event

enum class CarModel {
    MODEL_T {
        override fun toString(): String {
            return "Model T"
        }
    },
    MODEL_V {
        override fun toString(): String {
            return "Model V"
        }
    }
}

data class CarPart(val name: String, val quantity: Int)

fun main() {
    println("A new day at the factory starts")
    val factory =  FactoryImplementation3()
    factory.transferShipmentToCargoBay("chassis", listOf(CarPart("chassis", 4)))

    factory.assignEmployeeToFactory("Yoda")
    factory.assignEmployeeToFactory("Luke")

    // Hmm, a duplicate employee name, wonder if that will work?
    factory.assignEmployeeToFactory("Yoda")

    // An employee named "bender", why is that ringing a bell?
    factory.assignEmployeeToFactory("Bender")

    // Order an unknown employee to unload the shipment. Will it work
    factory.unloadShipmentFromCargoBay("Lea")

    factory.unloadShipmentFromCargoBay("Yoda")

    factory.transferShipmentToCargoBay("model T spare parts", listOf(
        CarPart("wheels", 20),
        CarPart("engine", 7),
        CarPart("bits and pieces", 2),
    ))

    factory.unloadShipmentFromCargoBay("Yoda")


    factory.transferShipmentToCargoBay("model T spare parts", listOf(
        CarPart("wheels", 20),
        CarPart("engine", 7),
        CarPart("bits and pieces", 2),
    ))
    factory.transferShipmentToCargoBay("model T spare parts", listOf(
        CarPart("wheels", 20),
        CarPart("engine", 7),
        CarPart("bits and pieces", 2),
    ))
    factory.transferShipmentToCargoBay("model T spare parts", listOf(
        CarPart("wheels", 20),
        CarPart("engine", 7),
        CarPart("bits and pieces", 2),
    ))
    factory.unloadShipmentFromCargoBay("Yoda")

    factory.produceCar("Yoda", CarModel.MODEL_T)
    factory.produceCar("Luke", CarModel.MODEL_V)

    println("""
        It's the end of the day. Let's read our journal of events once more:
        We should only see events below that were actually allowed to be recorded.
    """.trimIndent())

    factory.journal.forEach { println("!> $it") }

    println("It seems, this was an interesting day!  Two Yoda's there should be not!")

}