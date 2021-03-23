data class CurseWordUttered(val curse: Curse) : Event {
    override fun toString() =
        "'${curse.theWord}' was heard within the walls. It meant: '${curse.meaning}'"
}

data class ShipmentTransferredToCargoBay(val shipment: Shipment) : Event {
    override fun toString() =
        "Shipment '$shipment' transferred to cargo bay: " +
                shipment.carPartPackages.map { "${it.part.name} ${it.quantity} pcs" }
                    .reduce { acc, s -> "$acc, $s" }
}

data class EmployeeAssignedToFactory(val employee: Employee) : Event {
    override fun toString() =
        "new worker joins our forces: '$employee'"
}

data class ShipmentUnpackedInCargoBay(val employee: Employee, val carPartPackages: CarPartPackages) : Event {
    override fun toString() =
        "$employee unpacked " + carPartPackages.map { "${it.part} ${it.quantity} pcs" }.reduce { acc, s -> "$acc, $s" }
}

data class CarProduced(val employee: Employee, val carModel: CarModel, val carPartPackages: CarPartPackages) : Event {
    override fun toString() =
        "Car $carModel built by $employee using " + carPartPackages.map { "${it.part} ${it.quantity} pcs" }
            .reduce { acc, s -> "$acc, $s" }
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
    };

    companion object {
        fun neededParts(model: CarModel) =
            when (model) {
                MODEL_T -> listOf(
                    CarPartPackage(CarPart("wheel"), 2),
                    CarPartPackage(CarPart("engine"), 1),
                    CarPartPackage(CarPart("bits and pieces"), 2)
                )
                MODEL_V -> listOf(
                    CarPartPackage(CarPart("wheel"), 2),
                    CarPartPackage(CarPart("engine"), 1),
                    CarPartPackage(CarPart("bits and pieces"), 2),
                    CarPartPackage(CarPart("chassis"), 1)
                )
            }
    }
}

inline class CarPart(val name: String)
data class CarPartPackage(val part: CarPart, val quantity: Int)
data class Curse(val theWord: String, val meaning: String)

typealias Events = List<Event>
typealias Employees = List<Employee>
typealias CarPartPackages = List<CarPartPackage>
typealias Shipments = List<Shipment>
typealias Inventory = Map<CarPart, Int>

inline class Employee(private val employeeName: String) {
    override fun toString(): String {
        return employeeName
    }
}

data class Shipment(val name: String, val carPartPackages: CarPartPackages) {
    override fun toString(): String {
        return name
    }
}
