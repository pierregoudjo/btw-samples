import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertTrue

object UnpackShipmentInTheCargoBayFeature: Spek( {
    lateinit var exceptions: MutableList<Throwable>
    lateinit var state: FactoryState
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

        Scenario(
            "There must be at least 1 shipment in the cargo bay when the employee" +
                    " is ordered to unpack the shipments in the cargo bay"
        ) {

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

            Given(
                "Chewbacca assigned to the factory and there is a shipment of 4 chassis transferred to the cargo bay"
            ) {
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

            Given(
                "Chewbacca assigned to the factory and there is a shipment of 4 chassis " +
                        "and another shipment of 2 wheels and 3 engines in the cargo bay"
            ) {
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

            Given(
                "Chewbacca assigned to the factory and there is a shipment of 4" +
                        " chassis and another shipment of 2 wheels and 3 chassis in the cargo bay"
            ) {
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
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory("Yoda"),
                        ShipmentTransferredToCargoBay("Yoda", listOf(CarPart("wheel", 5))),
                        ShipmentUnpackedInCargoBay("Yoda", listOf(CarPart("wheel", 5))),
                        ShipmentTransferredToCargoBay("Yoda", listOf(CarPart("chassis", 2)))
                    )
                )
            }

            When("Yoda is ordered to unpack the shipment in the cargo bay") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = unpackAndInventoryShipmentInCargoBay("Yoda", state)
                }
            }
            Then("There should be an error") {
                assertTrue { exceptions.isNotEmpty() }
            }
            And(
                "The error message should contains \" Yoda may only unpack and inventory all " +
                        "Shipments in the CargoBay once a day \" "
            ) {
                assertTrue {
                    exceptions.first().message?.contains(
                        "Yoda may only unpack and inventory " +
                                "all Shipments in the CargoBay once a day"
                    )!!
                }
            }
        }

    }

})
