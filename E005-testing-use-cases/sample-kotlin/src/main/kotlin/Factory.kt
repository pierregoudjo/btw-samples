import java.lang.IllegalStateException

class FactoryAggregate(val state: FactoryState) {

    fun assignEmployeeToFactory(employeeName: String) {
        echoCommand("assign employee $employeeName to the factory")

        // Hey look, a business rule implementation
        if (state.listOfEmployeeNames.contains(employeeName)) {
            fail("the name of $employeeName only one can have")
            return
        }
        // another check that needs to happen when assigning employees to the factory
        // multiple options to prove this critical business rule:
        // John Bender: http://en.wikipedia.org/wiki/John_Bender_(character)#Main_characters
        // Bender Bending Rodríguez: http://en.wikipedia.org/wiki/Bender_(Futurama)

        if (employeeName == "Bender") {
            fail("Guys with the name 'bender' are trouble")
            return
        }

        doPaperWork("Assign employee to the factory")
        recordThat(EmployeeAssignedToFactory(employeeName))
    }

    fun transferShipmentToCargoBay(shipmentName: String, partPacks: List<CarPartPack>) {
        echoCommand("transfer shipment to cargo")

        if (state.listOfEmployeeNames.isEmpty()) {
            fail("there has to be somebody at the factory in order to accept the shipment")
            return
        }

        if (partPacks.isEmpty()) {
            fail("Empty shipments are not accepted!")
            return
        }

        if (state.shipmentsWaitingToBeUnloaded.size >= 2) {
            fail("More than two shipments can't fit into this cargo bay")
            return
        }
        doRealWork("opening cargo bay doors")
        recordThat(ShipmentTransferredToCargoBay(shipmentName = shipmentName, carPartPacks = partPacks))

        val totalCountOfParts = partPacks.sumOf { it.quantity }

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
        echoCommand("Order $employeeName to unload shipment from cargo bay")

        if (!state.listOfEmployeeNames.contains(employeeName)) {
            fail("$employeeName must be assigned to the factory to unload the cargo bay")
            return
        }

        if (state.shipmentsWaitingToBeUnloaded.isEmpty()) {
            fail("There should be a shipment to unload")
            return
        }

        doRealWork("passing supplies")

        recordThat(
            CargoBayUnloaded(
                employeeName,
                state.shipmentsWaitingToBeUnloaded.flatten()
            )
        )
    }

    fun produceCar(employeeName: String, carModel: CarModel) {
        echoCommand("Order $employeeName to build a $carModel car")
        // CheckIfWeHaveEnoughSpareParts
        val neededParts = when(carModel) {
            CarModel.MODEL_T -> listOf(
                CarPartPack("wheels", 2),
                CarPartPack("engine", 1),
                CarPartPack("bits and pieces", 2)
            )
            CarModel.MODEL_V -> listOf(
                CarPartPack("wheels", 2),
                CarPartPack("engine", 1),
                CarPartPack("bits and pieces", 2),
                CarPartPack("chassis", 1)
            )
        }

        val enoughPart = neededParts.fold( true,
            {acc, curr -> acc && state.stock.getOrDefault(curr.name, 0) >= curr.quantity}
        )

        if (!enoughPart) {
            println("There is not enough part to build $carModel car")
            return
        }

        if (!state.listOfEmployeeNames.contains(employeeName)) {
            println(":> $employeeName must be assigned to the factory to build a car")
            return
        }

        doRealWork("Building the car...")

        doPaperWork("Writing car specification documents")

        recordThat(CarBuilt(employeeName, carModel, neededParts))
    }

    private fun recordThat(event: Event) {
        state.mutate(event)
        announceInsideFactory(event)
    }

    private fun announceInsideFactory(event: Event) {
        println("!> Event $event".green())
    }

    private fun doPaperWork(workName: String) {
        println(" > Work: Papers... $workName ...")
        Thread.sleep(1000)
    }

    private fun doRealWork(workName: String) {
        println(" > Work: heavy stuff... $workName ...")
        Thread.sleep(1000)
    }
}

fun echoCommand(message: String) {
    println("?> Command: $message".yellow())
}

fun fail(message: String) {
    println(":> $message".red())
    throw IllegalStateException(message)
}

fun String.red() = "\u001B[41m$this\u001B[0m"
fun String.green() = "\u001B[32m$this\u001B[0m"
fun String.yellow() = "\u001B[33m$this\u001B[0m"