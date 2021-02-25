import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertTrue

object MyTest : Spek({
    lateinit var factory: FactoryAggregate
    lateinit var exceptions: MutableList<Throwable>

    // Useful for debug
    //afterEachGroup { println(factory.state.journal) }

    Feature("Transferring shipment to cargo bay") {

        beforeEachScenario {
            factory = FactoryAggregate(FactoryState(emptyList()))
            exceptions = mutableListOf()
        }

        Scenario("An empty shipment comes to the cargo bay") {
            Given("An employee 'Yoda' assigned to the factory") {
                val events = listOf(EmployeeAssignedToFactory("Yoda"))
                factory = FactoryAggregate(FactoryState(events))
            }

            When("An empty shipment comes to the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    transferShipmentToCargoBay("some shipment", emptyList(), factory.state)
                }
            }

            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }

            And("The error message should contain \"Empty shipments are not accepted!\" ") {
                assertTrue { exceptions.first().message?.contains("Empty shipments are not accepted!")!! }
            }
        }

        Scenario("An empty shipment come into an empty factory") {
            Given("An empty factory") {
                factory = FactoryAggregate(FactoryState(emptyList()))
            }
            When("An empty shipment comes to the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    transferShipmentToCargoBay("some shipment", emptyList(), factory.state)
                }
            }

            Then("There should be an error") {
                assertTrue("There is an exception") { exceptions.isNotEmpty() }
            }

            And("The error message should contain \"there has to be somebody at the factory in order to accept the shipment\" ") {
                assertTrue("The exception") { exceptions.first().message?.contains("there has to be somebody at the factory in order to accept the shipment")!! }
            }
        }

        Scenario("A shipment come into an empty factory") {
            Given("An empty factory") {

            }

            When("An empty shipment comes to the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    transferShipmentToCargoBay("some shipment", emptyList(), factory.state)
                }
            }

            Then("There should be an error") {
                assertTrue("There is an exception") { exceptions.isNotEmpty() }
            }

            And("The error message should contain \"there has to be somebody at the factory in order to accept the shipment\" ") {
                assertTrue("The message contains ${exceptions.first().message}") {
                    exceptions.first().message?.contains("there has to be somebody at the factory in order to accept the shipment")!!
                }
            }
        }

        Scenario("There are already two shipments") {

            Given("There is an employee assigned to the factory and two shipments waiting in the cargo bay") {
                val events = listOf(
                    EmployeeAssignedToFactory("Chewbacca"),
                    ShipmentTransferredToCargoBay("shipment-11", listOf(CarPartPack("engine", 3))),
                    ShipmentTransferredToCargoBay("shipment-12", listOf(CarPartPack("wheels", 40)))
                )
                factory = FactoryAggregate(FactoryState(events))
            }

            When("A new shipment comes to the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    transferShipmentToCargoBay("shipment-13", listOf(CarPartPack("bmw6", 6)), factory.state)
                }
            }

            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }

            And("The error message should contain \"More than two shipments can't fit\" ") {
                assertTrue("The message contains ${exceptions.first().message}") {
                    exceptions.first().message?.contains("More than two shipments can't fit into this cargo bay")!!
                }
            }
        }

        Scenario("A shipment comes to a factory with an employee assigned and 1 shipment of in the cargo bay") {
            Given("A factory with an employee assigned and 1 shipment in the cargo bay") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Chewbacca"),
                            ShipmentTransferredToCargoBay("shipment-55", listOf(CarPartPack("wheels", 5)))
                        )
                    )
                )
            }

            When("A shipment comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    transferShipmentToCargoBay(
                        "shipment-56",
                        listOf(CarPartPack("engine", 6), CarPartPack("chassis", 2)),
                        factory.state
                    )
                }

            }

            Then("The shipment is transferred to the cargo bay") {
                assertTrue {
                    factory.state.journal.contains(
                        ShipmentTransferredToCargoBay(
                            "shipment-56",
                            listOf(CarPartPack("engine", 6), CarPartPack("chassis", 2))
                        )
                    )
                }
            }
        }

        Scenario("A shipment of 5 wheels and 7 engines comes to the factory") {
            Given("A factory with an employee assigned and 1 shipment in the cargo bay") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Chewbacca"),
                            ShipmentTransferredToCargoBay("shipment-58", listOf(CarPartPack("chassis", 3)))
                        )
                    )
                )
            }
            When("A shipment of 5 wheels and 7 engines comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    transferShipmentToCargoBay(
                        "shipment-56",
                        listOf(CarPartPack("wheels", 5), CarPartPack("engines", 7)),
                        factory.state
                    )
                }

            }
            Then("The shipment is transferred to the cargo bay") {
                assertTrue {
                    factory.state.journal.contains(
                        ShipmentTransferredToCargoBay(
                            "shipment-56",
                            listOf(CarPartPack("wheels", 5), CarPartPack("engines", 7))
                        )
                    )
                }
            }
            And("A curse word has been uttered by one of the employee") {
                assertTrue {
                    factory.state.journal.filterIsInstance<CurseWordUttered>().isNotEmpty()
                }
            }
        }
    }

    Feature("Assign employee to the factory") {
        beforeEachScenario {
            factory = FactoryAggregate(FactoryState(emptyList()))
            exceptions = mutableListOf()
        }
        Scenario("Assign employee to an empty factory") {
            Given("An empty factory") {
                factory = FactoryAggregate(FactoryState(emptyList()))

            }
            When("An employee named \"Fry\" comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    assignEmployeeToFactory("Fry", factory.state)
                }
            }
            Then("Fry is assigned to the factory") {
                assertTrue {
                    factory.state.journal.contains(EmployeeAssignedToFactory("Fry"))
                }
            }
        }

        Scenario("An already assigned employee is assigned again") {
            Given("A factory where an employee named \"Fry\" is assigned") {
                factory = FactoryAggregate(FactoryState(listOf(EmployeeAssignedToFactory("Fry"))))
            }

            When("An employee named \"Fry\" comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    assignEmployeeToFactory("Fry", factory.state)
                }
            }

            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }

            And("The error message should contain \"the name of Fry only one can have\" ") {
                assertTrue("The message contains ${exceptions.first().message}") {
                    exceptions.first().message?.contains("the name of Fry only one can have")!!
                }
            }
        }

        Scenario("Bender comes to the factory") {
            When("An employee named Bender is assigned to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    assignEmployeeToFactory("Bender", factory.state)
                }
            }

            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }

            And("The error message should contain \"Guys with name 'bender' are trouble\" ") {
                assertTrue("The message contains ${exceptions.first().message}") {
                    exceptions.first().message?.contains("Guys with the name 'bender' are trouble")!!
                }
            }
        }
    }

    Feature("Unload Shipment from the Cargo Bay") {
        beforeEachScenario {
            factory = FactoryAggregate(FactoryState(emptyList()))
            exceptions = mutableListOf()
        }

        Scenario("Order given non-assigned employee to the factory to unload the cargo bay") {
            Given("Chewbacca assigned to the factory and 1 shipment transferred in the cargo bay") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Chewbacca"),
                            ShipmentTransferredToCargoBay("shipment-1", listOf(CarPartPack("chassis", 4)))
                        )
                    )
                )
            }
            When("There is an order given to Yoda to unload the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    unloadShipmentFromCargoBay("Yoda", factory.state)
                }

            }
            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }
            And("The error should contains \"Yoda must be assigned to the factory\"") {
                assertTrue { exceptions.any { it.message?.contains("Yoda must be assigned to the factory")!! } }
            }
        }

        Scenario("No employee assigned to the factory to unload the cargo bay") {
            Given("No employee assigned to the factory and 1 shipment transferred in the cargo bay") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            ShipmentTransferredToCargoBay("shipment-1", listOf(CarPartPack("chassis", 4)))
                        )
                    )
                )
            }
            When("There is an order given to Yoda to unload the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    unloadShipmentFromCargoBay("Yoda", factory.state)
                }

            }
            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }
            And("The error should contains \"Yoda must be assigned to the factory\"") {
                assertTrue { exceptions.any { it.message?.contains("Yoda must be assigned to the factory")!! } }
            }
        }

        Scenario("No shipment to unload") {

            Given("Chewbacca assigned to the factory") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Chewbacca"),
                        )
                    )
                )
            }

            When("There is an order given to Chewbacca to unload the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    unloadShipmentFromCargoBay("Chewbacca", factory.state)
                }

            }

            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }

            And("The error should contains \"Yoda must be assigned to the factory\"") {
                assertTrue { exceptions.any { it.message?.contains("There should be a shipment to unload")!! } }
            }
        }

        Scenario("Order an assigned employee to unload a shipment from the cargo bay") {

            Given("Chewbacca assigned to the factory and there is a shipment of 4 chassis in the cargo bay") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Chewbacca"),
                            ShipmentTransferredToCargoBay("shipment-1", listOf(CarPartPack("chassis", 4)))
                        )
                    )
                )
            }

            When("There is an order given to Chewbacca to unload the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    unloadShipmentFromCargoBay("Chewbacca", factory.state)
                }

            }

            Then("Chewbacca unloaded the cargo bay") {
                assertTrue {
                    factory.state.journal.contains(
                        ShipmentUnloadedFromCargoBay("Chewbacca", listOf(CarPartPack("chassis", 4)))
                    )
                }
            }
        }

        Scenario("Order an assigned employee to unload two shipments from the cargo bay") {

            Given("Chewbacca assigned to the factory and there is a shipment of 4 chassis and another shipment of 2 wheels and 3 engines in the cargo bay") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Chewbacca"),
                            ShipmentTransferredToCargoBay("shipment-1", listOf(CarPartPack("chassis", 4))),
                            ShipmentTransferredToCargoBay(
                                "shipment-1",
                                listOf(CarPartPack("wheel", 2), CarPartPack("engine", 3))
                            ),

                            )
                    )
                )
            }

            When("There is an order given to Chewbacca to unload the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    unloadShipmentFromCargoBay("Chewbacca", factory.state)
                }

            }

            Then("Chewbacca unloaded the cargo bay from 3 chassis, 2 wheels and 3 engines") {
                assertTrue {
                    factory.state.journal.contains(
                        ShipmentUnloadedFromCargoBay(
                            "Chewbacca", listOf(
                                CarPartPack("chassis", 4),
                                CarPartPack("wheel", 2),
                                CarPartPack("engine", 3),
                            )
                        )
                    )
                }
            }
        }

        Scenario("Order an assigned employee to unload two shipments with common items from the cargo bay") {

            Given("Chewbacca assigned to the factory and there is a shipment of 4 chassis and another shipment of 2 wheels and 3 chassis in the cargo bay") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Chewbacca"),
                            ShipmentTransferredToCargoBay("shipment-1", listOf(CarPartPack("chassis", 4))),
                            ShipmentTransferredToCargoBay(
                                "shipment-1",
                                listOf(CarPartPack("wheel", 2), CarPartPack("chassis", 3))
                            ),
                        )
                    )
                )
            }

            When("There is an order given to Chewbacca to unload the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    unloadShipmentFromCargoBay("Chewbacca", factory.state)
                }

            }

            Then("Chewbacca unloaded the cargo bay from 4-chassis pack, 2 wheels-pack and 3-chassis pack") {
                assertTrue {
                    factory.state.journal.contains(
                        ShipmentUnloadedFromCargoBay(
                            "Chewbacca", listOf(
                                CarPartPack("chassis", 4),
                                CarPartPack("wheel", 2),
                                CarPartPack("chassis", 3),
                            )
                        )
                    )
                }
            }
        }

    }

    Feature("Produce a car") {
        beforeEachScenario {
            factory = FactoryAggregate(FactoryState(emptyList()))
            exceptions = mutableListOf()
        }

        Scenario("Order to a non assigned employee to produce a model T car") {
            Given("Yoda assigned to the factory") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Yoda")
                        )
                    )
                )
            }
            When("Order given to Chewbacca to produce a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    produceCar("Chewbacca", CarModel.MODEL_T, factory.state)
                }
            }
            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }
            And("The error should contains the message \"Chewbacca must be assigned to the factory\" ") {
                assertTrue {
                    exceptions.any {
                        it.message?.contains("Chewbacca must be assigned to the factory")!!
                    }
                }
            }
        }

        Scenario("Order to build a model T car but there is not enough parts") {
            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels and 5 bits and pieces") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Yoda"),
                            ShipmentTransferredToCargoBay(
                                "shipment-1",
                                listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5)
                                )
                            ),
                            ShipmentUnloadedFromCargoBay(
                                "Yoda", listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5)
                                )
                            )
                        )
                    )
                )
            }
            When("Yoda is ordered to build a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    produceCar("Yoda", CarModel.MODEL_T, factory.state)
                }
            }
            Then("Then should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }

            And("The error should contains the message \"There is not enough part to build ${CarModel.MODEL_T} car\" ") {
                assertTrue {
                    exceptions.any {
                        it.message?.contains("There is not enough part to build ${CarModel.MODEL_T} car")!!
                    }
                }
            }
        }

        Scenario("Order to build a model V car but there is not enough parts") {
            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels and 5 bits and pieces") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Yoda"),
                            ShipmentTransferredToCargoBay(
                                "shipment-1",
                                listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5)
                                )
                            ),
                            ShipmentUnloadedFromCargoBay(
                                "Yoda", listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5)
                                )
                            )
                        )
                    )
                )
            }
            When("Yoda is ordered to build a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    produceCar("Yoda", CarModel.MODEL_V, factory.state)
                }
            }
            Then("Then should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }

            And("The error should contains the message \"There is not enough part to build ${CarModel.MODEL_V} car\" ") {
                assertTrue {
                    exceptions.any {
                        it.message?.contains("There is not enough part to build ${CarModel.MODEL_V} car")!!
                    }
                }
            }
        }

        Scenario("Order to build a model T car and there is enough parts") {
            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Yoda"),
                            ShipmentTransferredToCargoBay(
                                "shipment-1",
                                listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5),
                                    CarPartPack("engine", 2),
                                )
                            ),
                            ShipmentUnloadedFromCargoBay(
                                "Yoda", listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5),
                                    CarPartPack("engine", 2),
                                )
                            )
                        )
                    )
                )
            }
            When("Yoda is ordered to produce a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    produceCar("Yoda", CarModel.MODEL_T, factory.state)
                }
            }
            Then("Then a model T car is built") {
                assertTrue {
                    factory.state.journal.contains(
                        CarProduced(
                            "Yoda",
                            CarModel.MODEL_T,
                            CarModel.neededParts(CarModel.MODEL_T)
                        )
                    )
                }
            }
        }

        Scenario("Order to build a model V car and there is enough parts") {
            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Yoda"),
                            ShipmentTransferredToCargoBay(
                                "shipment-1",
                                listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5),
                                    CarPartPack("engine", 2),
                                )
                            ),
                            ShipmentUnloadedFromCargoBay(
                                "Yoda", listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5),
                                    CarPartPack("engine", 2),
                                )
                            )
                        )
                    )
                )
            }
            When("Yoda is ordered to produce a model V car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    produceCar("Yoda", CarModel.MODEL_V, factory.state)
                }
            }
            Then("Then a model V car is built") {
                assertTrue {
                    factory.state.journal.contains(
                        CarProduced(
                            "Yoda",
                            CarModel.MODEL_V,
                            CarModel.neededParts(CarModel.MODEL_V)
                        )
                    )
                }
            }
        }

        Scenario("Order to build a model T car and checking the remaining parts") {
            Given("Yoda and Luke assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Yoda"),
                            EmployeeAssignedToFactory("Luke"),
                            ShipmentTransferredToCargoBay(
                                "shipment-1",
                                listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5),
                                    CarPartPack("engine", 2),
                                )
                            ),
                            ShipmentUnloadedFromCargoBay(
                                "Yoda", listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5),
                                    CarPartPack("engine", 2),
                                )
                            )
                        )
                    )
                )
            }
            And("Model T car requires 2 wheels, 1 engine, and 2 bits and pieces parts to produce") {

            }
            And("Model V car requires 1 chassis, 2 wheels, 1 engine and 2 bits and pieces parts to produce") {

            }
            When("Yoda is ordered to produce a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    produceCar("Yoda", CarModel.MODEL_T, factory.state)
                }
            }
            When("Luke is ordered to produce a model V car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    produceCar("Luke", CarModel.MODEL_V, factory.state)
                }
            }
            Then("Then a model T car is built") {
                assertTrue {
                    factory.state.journal.contains(
                        CarProduced(
                            "Yoda",
                            CarModel.MODEL_T,
                            CarModel.neededParts(CarModel.MODEL_T)
                        )
                    )
                }
            }
            And("There is 2 chassis left") {
                assertTrue {
                    factory.state.stock.getOrDefault("chassis", 0) == 2
                }
            }

            And("There is 1 wheels left") {
                assertTrue {
                    factory.state.stock.getOrDefault("wheels", 0) == 1
                }
            }

            And("There is 1 bits and pieces left") {
                assertTrue {
                    factory.state.stock.getOrDefault("bits and pieces", 0) == 1
                }
            }

            And("There is 0 engine left") {
                assertTrue {
                    factory.state.stock.getOrDefault("engine", 0) == 0
                }
            }
        }

        Scenario("Not enough stock to build a new car after some have already been built") {
            Given("Yoda assigned to the factory, 3 chassis, 5 wheels 5 bits and pieces and 2 engines shipment transferred and unloaded and 2 model T cars built") {
                factory = FactoryAggregate(
                    FactoryState(
                        listOf(
                            EmployeeAssignedToFactory("Yoda"),
                            ShipmentTransferredToCargoBay(
                                "shipment-1",
                                listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5),
                                    CarPartPack("engine", 2),
                                )
                            ),
                            ShipmentUnloadedFromCargoBay(
                                "Yoda", listOf(
                                    CarPartPack("chassis", 3),
                                    CarPartPack("wheels", 5),
                                    CarPartPack("bits and pieces", 5),
                                    CarPartPack("engine", 2),
                                )
                            ),
                            CarProduced("Yoda", CarModel.MODEL_T, CarModel.neededParts(CarModel.MODEL_T)),
                            CarProduced("Yoda", CarModel.MODEL_T, CarModel.neededParts(CarModel.MODEL_T)),
                        )
                    )
                )
            }
            And("Model T car requires 2 wheels, 1 engine, and 2 bits and pieces parts to produce") {

            }
            When("Yoda is ordered to build a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    produceCar("Yoda", CarModel.MODEL_T, factory.state)
                }
            }
            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }
            And("The error should contains the message \"There is not enough part to build ${CarModel.MODEL_T} car\" ") {
                assertTrue {
                    exceptions.any {
                        it.message?.contains("There is not enough part to build ${CarModel.MODEL_T} car")!!
                    }
                }
            }
        }
    }
})

fun runWithCatchAndAddToExceptionList(throwableList: MutableList<Throwable>, block: () -> Unit) {
    runCatching {
        block()
    }.onFailure {
        throwableList.add(it)
    }
}
