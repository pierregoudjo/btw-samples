import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertTrue

object ProduceACarFeature : Spek({
    lateinit var exceptions: MutableList<Throwable>
    lateinit var state: FactoryState
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

            And(
                "The error should contains the message \"There is not " +
                        "enough part to build ${CarModel.MODEL_T} car\" "
            ) {
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

            And(
                "The error should contains the message \"There is not " +
                        "enough part to build ${CarModel.MODEL_V} car\" "
            ) {
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
            Given(
                "Yoda and Luke assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines"
            ) {
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
            Given(
                "Yoda assigned to the factory, 3 chassis, 5 wheels 5 bits and pieces and 2 engines " +
                        "shipment transferred and unpacked and 2 model T cars built"
            ) {
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
            And(
                "The error should contains the message \"There is not enough part to build ${CarModel.MODEL_T} car\" "
            ) {
                assertTrue {
                    exceptions.any {
                        it.message?.contains("There is not enough part to build ${CarModel.MODEL_T} car")!!
                    }
                }
            }
        }

        Scenario("An employee may only produce a car once a day") {
            Given(
                "Yoda an employee of the factory who has already built a car today" +
                        " and enough part left on the shelf to build a model T car"
            ) {
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
