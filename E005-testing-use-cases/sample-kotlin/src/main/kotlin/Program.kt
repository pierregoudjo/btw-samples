fun main() {
    println("A new day at the factory starts")
    val factory =  FactoryAggregate(FactoryState(emptyList()))
    factory.transferShipmentToCargoBay("chassis", listOf(CarPart("chassis", 4)))

    factory.assignEmployeeToFactory("Yoda")
    factory.assignEmployeeToFactory("Luke")

    // Hmm, a duplicate employee name, wonder if that will work?
    factory.assignEmployeeToFactory("Yoda")

    // An employee named "bender", why is that ringing a bell?
    factory.assignEmployeeToFactory("Bender")

    // Order an unknown employee to unload the shipment. Will it work
    factory.unloadShipmentFromCargoBay("Lea")

    factory.unloadShipmentFromCargoBay("Yoda")

    factory.transferShipmentToCargoBay("model T spare parts", listOf(
        CarPart("wheels", 20),
        CarPart("engine", 7),
        CarPart("bits and pieces", 2),
    ))

    factory.unloadShipmentFromCargoBay("Yoda")


    factory.transferShipmentToCargoBay("model T spare parts", listOf(
        CarPart("wheels", 20),
        CarPart("engine", 7),
        CarPart("bits and pieces", 2),
    ))
    factory.transferShipmentToCargoBay("model T spare parts", listOf(
        CarPart("wheels", 20),
        CarPart("engine", 7),
        CarPart("bits and pieces", 2),
    ))
    factory.transferShipmentToCargoBay("model T spare parts", listOf(
        CarPart("wheels", 20),
        CarPart("engine", 7),
        CarPart("bits and pieces", 2),
    ))
    factory.unloadShipmentFromCargoBay("Yoda")

    factory.produceCar("Yoda", CarModel.MODEL_T)
    factory.produceCar("Luke", CarModel.MODEL_V)

    println("""
        It's the end of the day. Let's read our journal of events once more:
        We should only see events below that were actually allowed to be recorded.
    """.trimIndent())

    factory.state.journal.forEach { println("!> $it") }

    println("It seems, this was an interesting day!  Two Yoda's there should be not!")

}