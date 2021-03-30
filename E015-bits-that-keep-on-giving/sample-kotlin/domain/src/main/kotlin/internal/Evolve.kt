package internal

import Factory
import model.CarPartPackages
import model.Employee
import model.Shipment

internal fun unpackShipment(cartPackages: CarPartPackages, employee: Employee, factory: Factory): Factory {
    val elementsToAddToInventory = cartPackages
        .groupBy { it.part }
        .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

    val newInventory =
        (factory.inventory.asSequence() + elementsToAddToInventory.asSequence())
            .groupBy({ it.key }, { it.value })
            .mapValues { (_, values) -> values.sum() }

    return factory.copy(
        shipmentsWaitingToBeUnpacked = emptyList(),
        inventory = newInventory,
        employeeWhoHasUnpackedShipmentsInCargoBayToday = factory.employeeWhoHasUnpackedShipmentsInCargoBayToday
                + employee
    )
}

internal fun addShipment(shipment: Shipment, factory: Factory) =
    factory.copy(shipmentsWaitingToBeUnpacked = factory.shipmentsWaitingToBeUnpacked + shipment)

internal fun updateListofEmployeeWhoBuiltCars(employee: Employee, factory: Factory) =
    factory.copy(employeesWhoHasBuiltCars = factory.employeesWhoHasBuiltCars + employee)

internal fun registerEmployee(employee: Employee, factory: Factory) =
    factory.copy(listOfEmployeeNames = factory.listOfEmployeeNames + employee)

internal fun updateInventory(packages: CarPartPackages, factory: Factory): Factory {

    val partUsed = packages.groupBy { it.part }
        .mapValues { it.value.sumOf { carPart -> carPart.quantity } }

    val inventor = factory.inventory.mapValues { (key, value) ->
        value - partUsed.getOrDefault(key,0)
    }
    return factory.copy(
        inventory = inventor
    )
}
