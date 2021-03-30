import model.CarModel
import model.Curse
import model.Employee
import model.Shipment

internal fun assignEmployeeToFactory(employee: Employee, state: Factory): List<FactoryDomainEvent> {
    echoCommand("assign employee $employee to the factory")

    // Hey look, a business rule implementation
    if (state.listOfEmployeeNames.contains(employee)) {
        fail("the name of $employee only one can have")
    }
    // another check that needs to happen when assigning employees to the factory
    // multiple options to prove this critical business rule:
    // John Bender: http://en.wikipedia.org/wiki/John_Bender_(character)#Main_characters
    // Bender Bending Rodríguez: http://en.wikipedia.org/wiki/Bender_(Futurama)

    if (employee == Employee("Bender")) {
        fail("Guys with the name 'bender' are trouble")
    }

    doPaperWork("Assign employee to the factory")
    return listOf(EmployeeAssignedToFactory(employee))
}

internal fun produceCar(employee: Employee, carModel: CarModel, state: Factory): List<FactoryDomainEvent> {

    echoCommand("Order $employee to build a $carModel car")

    if (!state.listOfEmployeeNames.contains(employee)) {
        fail("$employee must be assigned to the factory to build a car")
    }

    if (state.employeesWhoHasBuiltCars.contains(employee)) {
        fail("$employee may only produce a car once a day")
    }

    // CheckIfWeHaveEnoughSpareParts
    val neededPartsToBuildTheCar = CarModel.neededParts(carModel)

    val isThereEnoughPartToBuildTheCar = neededPartsToBuildTheCar.fold(true,
        { acc, curr -> acc && state.inventory.getOrDefault(curr.part, 0) >= curr.quantity }
    )

    if (!isThereEnoughPartToBuildTheCar) {
        fail("There is not enough part to build $carModel car")
    }

    doRealWork("Building the car...")

    doPaperWork("Writing car specification documents")

    return listOf(CarProduced(employee, carModel, neededPartsToBuildTheCar))
}

internal fun unpackAndInventoryShipmentInCargoBay(employee: Employee, state: Factory): List<FactoryDomainEvent> {

    echoCommand("Order $employee to unpack shipments from cargo bay")

    if (!state.listOfEmployeeNames.contains(employee)) {
        fail("$employee must be assigned to the factory to unpack the cargo bay")
    }

    if (state.employeeWhoHasUnpackedShipmentsInCargoBayToday.contains(employee)) {
        fail("$employee may only unpack and inventory all Shipments in the CargoBay once a day")
    }

    if (state.shipmentsWaitingToBeUnpacked.isEmpty()) {
        fail("There should be a shipment to unpack")
    }

    doRealWork("passing supplies")

    return listOf(
        ShipmentUnpackedInCargoBay(
            employee,
            state.shipmentsWaitingToBeUnpacked.map { it.carPartPackages }.flatten()
        )
    )
}

internal const val NUMBER_OF_PARTS_TOO_MUCH_TO_HANDLE = 10

internal fun transferShipmentToCargoBay(shipment: Shipment, state: Factory): List<FactoryDomainEvent> {
    echoCommand("transfer shipment to cargo")

    if (state.listOfEmployeeNames.isEmpty()) {
        fail("there has to be somebody at the factory in order to accept the shipment")
    }

    if (shipment.carPartPackages.isEmpty()) {
        fail("Empty shipments are not accepted!")
    }

    if (state.shipmentsWaitingToBeUnpacked.size >= 2) {
        fail("More than two shipments can't fit into this cargo bay")
    }
    doRealWork("opening cargo bay doors")
    val transferredEvent = listOf(ShipmentTransferredToCargoBay(shipment = shipment))

    val totalCountOfParts = shipment.carPartPackages.sumOf { it.quantity }

    val curseWordEvent = when {
        (totalCountOfParts > NUMBER_OF_PARTS_TOO_MUCH_TO_HANDLE) -> listOf(
            CurseWordUttered(
                Curse(
                    theWord = "Boltov tebe v korobky peredach",
                    meaning = "awe in the face of the amount of parts delivered"
                )
            )
        )
        else -> emptyList()
    }

    return transferredEvent + curseWordEvent
}

fun fold(events: List<FactoryDomainEvent>, command: FactoryCommand): List<FactoryDomainEvent> {
    val factory = events.fold(Factory.empty) { acc, curr -> evolve(curr, acc) }
    return decide(command, factory)
}

operator fun List<FactoryDomainEvent>.times(command: FactoryCommand): List<FactoryDomainEvent> {
    return fold(this, command)
}
