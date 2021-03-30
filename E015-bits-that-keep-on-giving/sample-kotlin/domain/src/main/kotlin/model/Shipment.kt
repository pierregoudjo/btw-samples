package model

data class Shipment(val name: String, val carPartPackages: CarPartPackages) {
    override fun toString(): String {
        return name
    }
}
