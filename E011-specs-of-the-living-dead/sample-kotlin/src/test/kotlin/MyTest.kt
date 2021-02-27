import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertTrue

object MyTest : Spek({
    lateinit var exceptions: MutableList<Throwable>
    lateinit var state: FactoryState

    // Useful for debug
    //afterEachGroup { println(state.journal) }

    Feature("Transferring shipment to cargo bay") {

        beforeEachScenario {
            state = FactoryState(emptyList())
            exceptions = mutableListOf()
        }

        Scenario("An empty shipment comes to the cargo bay") {
            Given("An employee 'Yoda' assigned to the factory") {
                val events = listOf(EmployeeAssignedToFactory("Yoda"))
                state = FactoryState(events)

            }

            When("An empty shipment comes to the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = transferShipmentToCargoBay("some shipment", emptyList(), state)
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
            }
            When("An empty shipment comes to the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = transferShipmentToCargoBay("some shipment", emptyList(), state)
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
                    state = transferShipmentToCargoBay("some shipment", emptyList(), state)
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
                    ShipmentTransferredToCargoBay("shipment-11", listOf(CarPart("engine", 3))),
                    ShipmentTransferredToCargoBay("shipment-12", listOf(CarPart("wheel", 40)))
                )
                state = FactoryState(events)

            }

            When("A new shipment comes to the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = transferShipmentToCargoBay("shipment-13", listOf(CarPart("bmw6", 6)), state)
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
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Chewbacca"),
                        ShipmentTransferredToCargoBay("shipment-55", listOf(CarPart("wheel", 5)))
                    )
                )

            }

            When("A shipment comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = transferShipmentToCargoBay(
                        "shipment-56",
                        listOf(CarPart("engine", 6), CarPart("chassis", 2)),
                        state
                    )
                }

            }

            Then("The shipment is transferred to the cargo bay") {
                assertTrue {
                    state.journal.contains(
                        ShipmentTransferredToCargoBay(
                            "shipment-56",
                            listOf(CarPart("engine", 6), CarPart("chassis", 2))
                        )
                    )
                }
            }
        }

        Scenario("A shipment of 5 wheels and 7 engines comes to the factory and the employee curse") {
            Given("A factory with an employee assigned and 1 shipment in the cargo bay") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Chewbacca"),
                        ShipmentTransferredToCargoBay("shipment-58", listOf(CarPart("chassis", 3)))
                    )
                )

            }
            When("A shipment of 5 wheel and 7 engines comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = transferShipmentToCargoBay(
                        "shipment-56",
                        listOf(CarPart("wheel", 5), CarPart("engines", 7)),
                        state
                    )
                }

            }
            Then("The shipment is transferred to the cargo bay") {
                assertTrue {
                    state.journal.contains(
                        ShipmentTransferredToCargoBay(
                            "shipment-56",
                            listOf(CarPart("wheel", 5), CarPart("engines", 7))
                        )
                    )
                }
            }
            And("A curse word has been uttered by one of the employee") {
                assertTrue {
                    state.journal.filterIsInstance<CurseWordUttered>().isNotEmpty()
                }
            }
        }
    }

    Feature("Assign employee to the factory") {
        beforeEachScenario {
            state = FactoryState(emptyList())

            exceptions = mutableListOf()
        }
        Scenario("Assign employee to an empty factory") {
            Given("An empty factory") {
            }
            When("An employee named \"Fry\" comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = assignEmployeeToFactory("Fry", state)
                }
            }
            Then("Fry is assigned to the factory") {
                assertTrue {
                    state.journal.contains(EmployeeAssignedToFactory("Fry"))
                }
            }
        }

        Scenario("An already assigned employee is assigned again") {
            Given("A factory where an employee named \"Fry\" is assigned") {
                state = FactoryState(listOf(EmployeeAssignedToFactory("Fry")))

            }

            When("An employee named \"Fry\" comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = assignEmployeeToFactory("Fry", state)
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
            Given("An empty factory") {
            }
            When("An employee named Bender is assigned to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = assignEmployeeToFactory("Bender", state)
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

    Feature("Unpack Shipments in the Cargo Bay") {
        beforeEachScenario {
            state = FactoryState(emptyList())

            exceptions = mutableListOf()
        }

        Scenario("Order given non-assigned employee to the factory to unpack shipments in the cargo bay") {
            Given("Chewbacca assigned to the factory and 1 shipment transferred in the cargo bay") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Chewbacca"),
                        ShipmentTransferredToCargoBay("shipment-1", listOf(CarPart("chassis", 4)))
                    )
                )

            }
            When("There is an order given to Yoda to unpack shipments in the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = unpackAndInventoryShipmentInCargoBay("Yoda", state)
                }

            }
            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }
            And("The error should contains \"Yoda must be assigned to the factory\"") {
                assertTrue { exceptions.any { it.message?.contains("Yoda must be assigned to the factory")!! } }
            }
        }

        Scenario("No employee assigned to the factory to unpack the cargo bay") {
            Given("No employee assigned to the factory and 1 shipment transferred to the cargo bay") {
                state = FactoryState(
                    listOf(
                        ShipmentTransferredToCargoBay("shipment-1", listOf(CarPart("chassis", 4)))
                    )
                )

            }
            When("There is an order given to Yoda to unpack shipments in the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = unpackAndInventoryShipmentInCargoBay("Yoda", state)
                }

            }
            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }
            And("The error should contains \"Yoda must be assigned to the factory\"") {
                assertTrue { exceptions.any { it.message?.contains("Yoda must be assigned to the factory")!! } }
            }
        }

        Scenario("There must be at least 1 shipment in the cargo bay when the employee is ordered to unpack the shipments in the cargo bay") {

            Given("Chewbacca assigned to the factory") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Chewbacca"),
                    )
                )

            }

            When("There is an order given to Chewbacca to unpack shipments in the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = unpackAndInventoryShipmentInCargoBay("Chewbacca", state)
                }

            }

            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }

            And("The error should contains \"There should be a shipment to unpack\"") {
                assertTrue { exceptions.any { it.message?.contains("There should be a shipment to unpack")!! } }
            }
        }

        Scenario("Order an assigned employee to unpack a shipment in the cargo bay") {

            Given("Chewbacca assigned to the factory and there is a shipment of 4 chassis transferred to the cargo bay") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Chewbacca"),
                        ShipmentTransferredToCargoBay("shipment-1", listOf(CarPart("chassis", 4)))
                    )
                )

            }

            When("There is an order given to Chewbacca to unpack the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = unpackAndInventoryShipmentInCargoBay("Chewbacca", state)
                }

            }

            Then("Chewbacca unpacked shipment in the cargo bay") {
                assertTrue {
                    state.journal.contains(
                        ShipmentUnpackedInCargoBay("Chewbacca", listOf(CarPart("chassis", 4)))
                    )
                }
            }
        }

        Scenario("Order an assigned employee to unpack two shipments in the cargo bay") {

            Given("Chewbacca assigned to the factory and there is a shipment of 4 chassis and another shipment of 2 wheels and 3 engines in the cargo bay") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Chewbacca"),
                        ShipmentTransferredToCargoBay("shipment-1", listOf(CarPart("chassis", 4))),
                        ShipmentTransferredToCargoBay(
                            "shipment-1",
                            listOf(CarPart("wheel", 2), CarPart("engine", 3))
                        ),

                        )
                )

            }

            When("There is an order given to Chewbacca to unpack shipment the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = unpackAndInventoryShipmentInCargoBay("Chewbacca", state)
                }

            }

            Then("Chewbacca unpack 3 chassis, 2 wheel and 3 engines") {
                assertTrue {
                    state.journal.contains(
                        ShipmentUnpackedInCargoBay(
                            "Chewbacca", listOf(
                                CarPart("chassis", 4),
                                CarPart("wheel", 2),
                                CarPart("engine", 3),
                            )
                        )
                    )
                }
            }
        }

        Scenario("Order an assigned employee to unpack two shipments with common items from the cargo bay") {

            Given("Chewbacca assigned to the factory and there is a shipment of 4 chassis and another shipment of 2 wheels and 3 chassis in the cargo bay") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Chewbacca"),
                        ShipmentTransferredToCargoBay("shipment-1", listOf(CarPart("chassis", 4))),
                        ShipmentTransferredToCargoBay(
                            "shipment-1",
                            listOf(CarPart("wheel", 2), CarPart("chassis", 3))
                        ),
                    )
                )

            }

            When("There is an order given to Chewbacca to unpack shipment in the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = unpackAndInventoryShipmentInCargoBay("Chewbacca", state)
                }

            }

            Then("Chewbacca unpacked the cargo bay with a 4-chassis pack, 2 wheels-pack and 3-chassis pack") {
                assertTrue {
                    state.journal.contains(
                        ShipmentUnpackedInCargoBay(
                            "Chewbacca", listOf(
                                CarPart("chassis", 4),
                                CarPart("wheel", 2),
                                CarPart("chassis", 3),
                            )
                        )
                    )
                }
            }
        }

        Scenario("An employee may unpack shipments in the cargo bay once a day") {
            Given("Yoda already unpack shipments the cargo bay today") {
                state = FactoryState(listOf(
                    EmployeeAssignedToFactory("Yoda"),
                    ShipmentTransferredToCargoBay("Yoda", listOf(CarPart("wheel", 5))),
                    ShipmentUnpackedInCargoBay("Yoda", listOf(CarPart("wheel", 5))),
                    ShipmentTransferredToCargoBay("Yoda", listOf(CarPart("chassis", 2)))
                ))
            }

            When("Yoda is ordered to unpack the shipment in the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = unpackAndInventoryShipmentInCargoBay("Yoda", state)
                }
            }
            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }
            And("The error message should contains \" Yoda may only unpack and inventory all Shipments in the CargoBay once a day \" ") {
                assertTrue { exceptions.first().message?.contains("Yoda may only unpack and inventory all Shipments in the CargoBay once a day")!! }
            }
        }

    }

    Feature("Produce a car") {
        beforeEachScenario {
            state = FactoryState(emptyList())

            exceptions = mutableListOf()
        }

        Scenario("Order to a non assigned employee to produce a model T car") {
            Given("Yoda assigned to the factory") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Yoda")
                    )
                )

            }
            When("Order given to Chewbacca to produce a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = produceCar("Chewbacca", CarModel.MODEL_T, state)
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
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Yoda"),
                        ShipmentTransferredToCargoBay(
                            "shipment-1",
                            listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5)
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            "Yoda", listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5)
                            )
                        )
                    )
                )

            }
            When("Yoda is ordered to build a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = produceCar("Yoda", CarModel.MODEL_T, state)
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
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Yoda"),
                        ShipmentTransferredToCargoBay(
                            "shipment-1",
                            listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5)
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            "Yoda", listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5)
                            )
                        )
                    )
                )

            }
            When("Yoda is ordered to build a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = produceCar("Yoda", CarModel.MODEL_V, state)
                }
            }
            Then("There should be an error") {
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
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Yoda"),
                        ShipmentTransferredToCargoBay(
                            "shipment-1",
                            listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5),
                                CarPart("engine", 2),
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            "Yoda", listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5),
                                CarPart("engine", 2),
                            )
                        )
                    )
                )

            }
            When("Yoda is ordered to produce a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = produceCar("Yoda", CarModel.MODEL_T, state)
                }
            }
            Then("Then a model T car is built") {
                assertTrue {
                    state.journal.contains(
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
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Yoda"),
                        ShipmentTransferredToCargoBay(
                            "shipment-1",
                            listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5),
                                CarPart("engine", 2),
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            "Yoda", listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5),
                                CarPart("engine", 2),
                            )
                        )
                    )
                )

            }
            When("Yoda is ordered to produce a model V car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = produceCar("Yoda", CarModel.MODEL_V, state)
                }
            }
            Then("Then a model V car is built") {
                assertTrue {
                    state.journal.contains(
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
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Yoda"),
                        EmployeeAssignedToFactory("Luke"),
                        ShipmentTransferredToCargoBay(
                            "shipment-1",
                            listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5),
                                CarPart("engine", 2),
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            "Yoda", listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5),
                                CarPart("engine", 2),
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
                    state = produceCar("Yoda", CarModel.MODEL_T, state)
                }
            }
            When("Luke is ordered to produce a model V car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = produceCar("Luke", CarModel.MODEL_V, state)
                }
            }
            Then("Then a model T car is built") {
                assertTrue {
                    state.journal.contains(
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
                    state.inventory.getOrDefault("chassis", 0) == 2
                }
            }

            And("There is 1 wheels left") {
                assertTrue {
                    state.inventory.getOrDefault("wheel", 0) == 1
                }
            }

            And("There is 1 bits and pieces left") {
                assertTrue {
                    state.inventory.getOrDefault("bits and pieces", 0) == 1
                }
            }

            And("There is 0 engine left") {
                assertTrue {
                    state.inventory.getOrDefault("engine", 0) == 0
                }
            }
        }

        Scenario("Not enough stock to build a new car after some have already been built") {
            Given("Yoda assigned to the factory, 3 chassis, 5 wheels 5 bits and pieces and 2 engines shipment transferred and unpacked and 2 model T cars built") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Yoda"),
                        EmployeeAssignedToFactory("Luke"),
                        EmployeeAssignedToFactory("Lea"),
                        ShipmentTransferredToCargoBay(
                            "shipment-1",
                            listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5),
                                CarPart("engine", 2),
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            "Yoda", listOf(
                                CarPart("chassis", 3),
                                CarPart("wheel", 5),
                                CarPart("bits and pieces", 5),
                                CarPart("engine", 2),
                            )
                        ),
                        CarProduced("Yoda", CarModel.MODEL_T, CarModel.neededParts(CarModel.MODEL_T)),
                        CarProduced("Luke", CarModel.MODEL_T, CarModel.neededParts(CarModel.MODEL_T)),
                    )
                )

            }
            And("Model T car requires 2 wheels, 1 engine, and 2 bits and pieces parts to produce") {

            }
            When("Yoda is ordered to build a model T car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = produceCar("Lea", CarModel.MODEL_T, state)
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

        Scenario("An employee may only produce a car once a day") {
            Given("Yoda an employee of the factory who has already built a car today and enough part left on the shelf to build a model T car") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Yoda"),
                        ShipmentUnpackedInCargoBay(
                            "Yoda",
                            listOf(CarPart("wheel", 60), CarPart("engine", 40), CarPart("bits and pieces", 20))
                        ),
                        CarProduced("Yoda", CarModel.MODEL_T, CarModel.neededParts(CarModel.MODEL_T))

                    )
                )
            }
            When("Yoda is ordered to build a car") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = produceCar("Yoda", CarModel.MODEL_T, state)
                }
            }
            Then("There should be an error ") {
                assertTrue { exceptions.isNotEmpty() }
            }
            Then("The error should contains a message \"Yoda may only produce a car once a day\"") {
                assertTrue { exceptions.first().message?.contains("Yoda may only produce a car once a day")!! }
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
