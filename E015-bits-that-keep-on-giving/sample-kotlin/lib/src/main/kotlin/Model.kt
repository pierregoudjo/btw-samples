data class CurseWordUttered(val theWord: String, val meaning: String) : Event {
    override fun toString() =
        "'$theWord' was heard within the walls. It meant: '$meaning'"
}

data class ShipmentTransferredToCargoBay(val shipmentName: String, val carParts: List<CarPart>) : Event {
    override fun toString() =
        "Shipment '$shipmentName' transferred to cargo bay: " + carParts.map { "${it.name} ${it.quantity} pcs" }
            .reduce { acc, s -> "$acc, $s" }
}

data class EmployeeAssignedToFactory(val employeeName: String) : Event {
    override fun toString() =
        "new worker joins our forces: '$employeeName'"
}

data class ShipmentUnpackedInCargoBay(val employeeName: String, val carParts: List<CarPart>) : Event {
    override fun toString() =
        "$employeeName unpacked " + carParts.map { "${it.name} ${it.quantity} pcs" }.reduce { acc, s -> "$acc, $s" }
}

data class CarProduced(val employeeName: String, val carModel: CarModel, val carParts: List<CarPart>) : Event {
    override fun toString() =
        "Car $carModel built by $employeeName using " + carParts.map { "${it.name} ${it.quantity} pcs" }
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
                    CarPart("wheel", 2),
                    CarPart("engine", 1),
                    CarPart("bits and pieces", 2)
                )
                MODEL_V -> listOf(
                    CarPart("wheel", 2),
                    CarPart("engine", 1),
                    CarPart("bits and pieces", 2),
                    CarPart("chassis", 1)
                )
            }
    }
}

data class CarPart(val name: String, val quantity: Int)
