import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertTrue

object TransferringShipmentToCargoBay : Spek({
    lateinit var exceptions: MutableList<Throwable>
    lateinit var state: FactoryState

    // Useful for debug
    // afterEachGroup { println(state.journal) }

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

            And(
                "The error message should contain \"there has to be somebody " +
                        "at the factory in order to accept the shipment\" "
            ) {
                assertTrue("The exception") {
                    exceptions.first().message?.contains(
                        "there has to be " +
                                "somebody at the factory in order to accept the shipment"
                    )!!
                }
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

            And(
                "The error message should contain \"there has to be somebody " +
                        "at the factory in order to accept the shipment\" "
            ) {
                assertTrue("The message contains ${exceptions.first().message}") {
                    exceptions.first().message?.contains(
                        "there has to be somebody" +
                                " at the factory in order to accept the shipment"
                    )!!
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

})
