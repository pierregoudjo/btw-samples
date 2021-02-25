class FactoryAggregate(val state: FactoryState) {
}

fun produceCar(employeeName: String, carModel: CarModel, state: FactoryState) {
    echoCommand("Order $employeeName to build a $carModel car")

    if (!state.listOfEmployeeNames.contains(employeeName)) {
        return fail("$employeeName must be assigned to the factory to build a car")
    }

    // CheckIfWeHaveEnoughSpareParts
    val neededPartsToBuildTheCar = CarModel.neededParts(carModel)

    val isThereEnoughPartToBuildTheCar = neededPartsToBuildTheCar.fold(true,
        { acc, curr -> acc && state.stock.getOrDefault(curr.name, 0) >= curr.quantity }
    )

    if (!isThereEnoughPartToBuildTheCar) {
        return fail("There is not enough part to build $carModel car")
    }

    doRealWork("Building the car...")

    doPaperWork("Writing car specification documents")

    recordThat(CarProduced(employeeName, carModel, neededPartsToBuildTheCar), state)
}

fun unloadShipmentFromCargoBay(employeeName: String, state: FactoryState) {
    echoCommand("Order $employeeName to unload shipment from cargo bay")

    if (!state.listOfEmployeeNames.contains(employeeName)) {
        return fail("$employeeName must be assigned to the factory to unload the cargo bay")
    }

    if (state.shipmentsWaitingToBeUnloaded.isEmpty()) {
        return fail("There should be a shipment to unload")
    }

    doRealWork("passing supplies")

    recordThat(
        ShipmentUnloadedFromCargoBay(
            employeeName,
            state.shipmentsWaitingToBeUnloaded.flatten()
        ),
        state
    )
}

fun assignEmployeeToFactory(employeeName: String, state: FactoryState) {
    echoCommand("assign employee $employeeName to the factory")

    // Hey look, a business rule implementation
    if (state.listOfEmployeeNames.contains(employeeName)) {
        return fail("the name of $employeeName only one can have")
    }
    // another check that needs to happen when assigning employees to the factory
    // multiple options to prove this critical business rule:
    // John Bender: http://en.wikipedia.org/wiki/John_Bender_(character)#Main_characters
    // Bender Bending Rodríguez: http://en.wikipedia.org/wiki/Bender_(Futurama)

    if (employeeName == "Bender") {
        return fail("Guys with the name 'bender' are trouble")
    }

    doPaperWork("Assign employee to the factory")
    recordThat(EmployeeAssignedToFactory(employeeName), state)
}

fun transferShipmentToCargoBay(shipmentName: String, partPacks: List<CarPartPack>, state: FactoryState) {
    echoCommand("transfer shipment to cargo")

    if (state.listOfEmployeeNames.isEmpty()) {
        return fail("there has to be somebody at the factory in order to accept the shipment")
    }

    if (partPacks.isEmpty()) {
        return fail("Empty shipments are not accepted!")
    }

    if (state.shipmentsWaitingToBeUnloaded.size >= 2) {
        return fail("More than two shipments can't fit into this cargo bay")
    }
    doRealWork("opening cargo bay doors")
    recordThat(ShipmentTransferredToCargoBay(shipmentName = shipmentName, carPartPacks = partPacks), state)

    val totalCountOfParts = partPacks.sumOf { it.quantity }

    if (totalCountOfParts > 10) {
        recordThat(
            CurseWordUttered(
                theWord = "Boltov tebe v korobky peredach",
                meaning = "awe in the face of the amount of parts delivered"
            ),
            state
        )
    }
}

private fun recordThat(event: Event, state: FactoryState) {
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