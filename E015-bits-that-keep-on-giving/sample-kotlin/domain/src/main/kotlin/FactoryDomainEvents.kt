import model.Curse
import model.Shipment
import model.Employee
import model.CarPartPackages
import model.CarModel


sealed class FactoryDomainEvent

data class CurseWordUttered(val curse: Curse) : FactoryDomainEvent() {
    override fun toString() =
        "'${curse.theWord}' was heard within the walls. It meant: '${curse.meaning}'"
}

data class ShipmentTransferredToCargoBay(val shipment: Shipment) : FactoryDomainEvent() {
    override fun toString() =
        "Shipment '$shipment' transferred to cargo bay: " +
                shipment.carPartPackages.map { "${it.part.name} ${it.quantity} pcs" }
                    .reduce { acc, s -> "$acc, $s" }
}

data class ShipmentUnpackedInCargoBay(
    val employee: Employee,
    val carPartPackages: CarPartPackages
) : FactoryDomainEvent() {
    override fun toString() = "$employee unpacked " + carPartPackages
        .map { "${it.part} ${it.quantity} pcs" }
        .reduce { acc, s -> "$acc, $s" }
}

data class EmployeeAssignedToFactory(val employee: Employee) : FactoryDomainEvent() {
    override fun toString() =
        "new worker joins our forces: '$employee'"
}

data class CarProduced(val employee: Employee, val carModel: CarModel, val carPartPackages: CarPartPackages) :
    FactoryDomainEvent() {
    override fun toString() =
        "Car $carModel built by $employee using " + carPartPackages.map { "${it.part} ${it.quantity} pcs" }
            .reduce { acc, s -> "$acc, $s" }
}
