import internal.addShipment
import internal.registerEmployee
import internal.unpackShipment
import internal.updateInventory
import internal.updateListofEmployeeWhoBuiltCars
import model.Employee
import model.Inventory
import model.Shipment

data class Factory(
    val listOfEmployeeNames: List<Employee>,
    val shipmentsWaitingToBeUnpacked: List<Shipment>,
    val inventory: Inventory,
    val employeesWhoHasBuiltCars: List<Employee>,
    val employeeWhoHasUnpackedShipmentsInCargoBayToday: List<Employee>,
) {
    companion object {
        val empty = Factory(
            emptyList(),
            emptyList(),
            emptyMap(),
            emptyList(),
            emptyList(),
        )
    }
}

fun evolve(event: FactoryDomainEvent, factory: Factory): Factory = when (event) {
    is EmployeeAssignedToFactory -> registerEmployee(event.employee, factory)
    is CarProduced -> updateInventory(event.carPartPackages, updateListofEmployeeWhoBuiltCars(event.employee, factory))
    is CurseWordUttered -> factory
    is ShipmentTransferredToCargoBay -> addShipment(event.shipment, factory)
    is ShipmentUnpackedInCargoBay -> unpackShipment(event.carPartPackages, event.employee, factory)
}

fun decide(command: FactoryCommand, factory: Factory): List<FactoryDomainEvent> = when (command) {
    is AssignEmployeeToFactory -> assignEmployeeToFactory(command.employee, factory)
    is ProduceCar -> produceCar(command.employee, command.carModel, factory)
    is TransferShipmentToCargoBay -> transferShipmentToCargoBay(command.shipment, factory)
    is UnpackAndInventoryShipmentInCargoBay -> unpackAndInventoryShipmentInCargoBay(command.employee, factory)
}
