package model

typealias Shipments = List<Shipment>

data class Shipment(val name: String, val carPartPackages: CarPartPackages) {
    override fun toString(): String {
        return name
    }
}
