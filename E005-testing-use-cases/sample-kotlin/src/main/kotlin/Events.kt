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