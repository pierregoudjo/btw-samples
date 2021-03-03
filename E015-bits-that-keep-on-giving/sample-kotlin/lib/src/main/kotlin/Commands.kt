fun produceCar(employeeName: String, carModel: CarModel, state: FactoryState): FactoryState {
    echoCommand("Order $employeeName to build a $carModel car")

    if (!state.listOfEmployeeNames.contains(employeeName)) {
        fail("$employeeName must be assigned to the factory to build a car")
    }

    if (state.employeesWhoHasBuiltCars.contains(employeeName)) {
        fail("$employeeName may only produce a car once a day")
    }

    // CheckIfWeHaveEnoughSpareParts
    val neededPartsToBuildTheCar = CarModel.neededParts(carModel)

    val isThereEnoughPartToBuildTheCar = neededPartsToBuildTheCar.fold(true,
        { acc, curr -> acc && state.inventory.getOrDefault(curr.name, 0) >= curr.quantity }
    )

    if (!isThereEnoughPartToBuildTheCar) {
        fail("There is not enough part to build $carModel car")
    }

    doRealWork("Building the car...")

    doPaperWork("Writing car specification documents")

    return recordThat(listOf(CarProduced(employeeName, carModel, neededPartsToBuildTheCar)), state)
}

fun unpackAndInventoryShipmentInCargoBay(employeeName: String, state: FactoryState): FactoryState {
    echoCommand("Order $employeeName to unpack shipments from cargo bay")

    if (!state.listOfEmployeeNames.contains(employeeName)) {
        fail("$employeeName must be assigned to the factory to unpack the cargo bay")
    }

    if (state.employeeWhoHasUnpackedShipmentsInCargoBayToday.contains(employeeName)) {
        fail("$employeeName may only unpack and inventory all Shipments in the CargoBay once a day")
    }

    if (state.shipmentsWaitingToBeUnpacked.isEmpty()) {
        fail("There should be a shipment to unpack")
    }

    doRealWork("passing supplies")

    return recordThat(
        listOf(
            ShipmentUnpackedInCargoBay(
                employeeName,
                state.shipmentsWaitingToBeUnpacked.flatten()
            )
        ),
        state
    )
}

fun assignEmployeeToFactory(employeeName: String, state: FactoryState): FactoryState {
    echoCommand("assign employee $employeeName to the factory")

    // Hey look, a business rule implementation
    if (state.listOfEmployeeNames.contains(employeeName)) {
        fail("the name of $employeeName only one can have")
    }
    // another check that needs to happen when assigning employees to the factory
    // multiple options to prove this critical business rule:
    // John Bender: http://en.wikipedia.org/wiki/John_Bender_(character)#Main_characters
    // Bender Bending Rodríguez: http://en.wikipedia.org/wiki/Bender_(Futurama)

    if (employeeName == "Bender") {
        fail("Guys with the name 'bender' are trouble")
    }

    doPaperWork("Assign employee to the factory")
    return recordThat(listOf(EmployeeAssignedToFactory(employeeName)), state)
}

private const val NUMBER_OF_PARTS_TOO_MUCH_TO_HANDLE = 10

fun transferShipmentToCargoBay(shipmentName: String, parts: List<CarPart>, state: FactoryState): FactoryState {
    echoCommand("transfer shipment to cargo")

    if (state.listOfEmployeeNames.isEmpty()) {
        fail("there has to be somebody at the factory in order to accept the shipment")
    }

    if (parts.isEmpty()) {
        fail("Empty shipments are not accepted!")
    }

    if (state.shipmentsWaitingToBeUnpacked.size >= 2) {
        fail("More than two shipments can't fit into this cargo bay")
    }
    doRealWork("opening cargo bay doors")
    val transferredEvent = listOf(ShipmentTransferredToCargoBay(shipmentName = shipmentName, carParts = parts))

    val totalCountOfParts = parts.sumOf { it.quantity }

    val curseWordEvent = when {
        (totalCountOfParts > NUMBER_OF_PARTS_TOO_MUCH_TO_HANDLE) -> listOf(
            CurseWordUttered(
                theWord = "Boltov tebe v korobky peredach",
                meaning = "awe in the face of the amount of parts delivered"
            )
        )
        else -> emptyList()
    }

    return recordThat(transferredEvent + curseWordEvent, state)
}

private fun recordThat(events: List<Event>, state: FactoryState): FactoryState {
    return apply(events, state).also {
        events.forEach {
            announceInsideFactory(it)
        }
    }
}

private fun announceInsideFactory(event: Event) {
    println("!> Event $event".green())
}
