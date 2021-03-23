import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object TransferringShipmentToCargoBayFeature : Spek({
    Feature("Transferring shipment to cargo bay") {
        Scenario("An empty shipment comes to the cargo bay") {
            lateinit var state: FactoryState
            lateinit var exception: Throwable

            Given("An employee 'Yoda' assigned to the factory") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(
                            Employee("Yoda")
                        )
                    )
                )
            }

            When("An empty shipment comes to the cargo bay, there should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    transferShipmentToCargoBay(
                        Shipment(
                            "some shipment",
                            emptyList()
                        )
                    )(state)
                }
            }

            Then("The error message should contain \"Empty shipments are not accepted!\" ") {
                exception.message shouldContain ("Empty shipments are not accepted!")
            }
        }

        Scenario("An empty shipment come into an empty factory") {
            lateinit var state: FactoryState
            lateinit var exception: Throwable
            Given("An empty factory") {
                state = FactoryState(emptyList())
            }
            When("An empty shipment comes to the cargo bay, there should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    transferShipmentToCargoBay(
                        Shipment(
                            "some shipment",
                            emptyList()
                        )
                    )(state)
                }
            }
            And(
                "The error message should contain \"there has to be somebody " +
                        "at the factory in order to accept the shipment\" "
            ) {
                exception.message shouldContain
                        "there has to be somebody at the factory in order to accept the shipment"
            }
        }

        Scenario("A shipment come into an empty factory") {
            lateinit var state: FactoryState
            lateinit var exception: Throwable

            Given("An empty factory") {
                state = FactoryState(emptyList())
            }

            When("An empty shipment comes to the cargo bay, There should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    transferShipmentToCargoBay(
                        Shipment(
                            "some shipment",
                            emptyList()
                        )
                    )(state)
                }
            }
            And(
                "The error message should contain \"there has to be somebody " +
                        "at the factory in order to accept the shipment\" "
            ) {
                exception.message shouldContain
                        "there has to be somebody at the factory in order to accept the shipment"
            }
        }

        Scenario("There are already two shipments") {
            lateinit var state: FactoryState
            lateinit var exception: Throwable

            Given("There is an employee assigned to the factory and two shipments waiting in the cargo bay") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Chewbacca")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-11",
                                listOf(CarPartPackage(CarPart("engine"), 3))
                            )
                        ),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-12",
                                listOf(CarPartPackage(CarPart("wheel"), 40))
                            )
                        )
                    )
                )

            }

            When("A new shipment comes to the cargo bay, there should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    transferShipmentToCargoBay(
                        Shipment(
                            "shipment-13",
                            listOf(CarPartPackage(CarPart("bmw6"), 6))
                        )
                    )(state)
                }
            }

            And("The error message should contain \"More than two shipments can't fit\" ") {
                exception.message shouldContain ("More than two shipments can't fit into this cargo bay")

            }
        }

        Scenario("A shipment comes to a factory with an employee assigned and 1 shipment of in the cargo bay") {
            lateinit var state: FactoryState
            lateinit var events: List<Event>
            Given("A factory with an employee assigned and 1 shipment in the cargo bay") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Chewbacca")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-55",
                                listOf(CarPartPackage(CarPart("wheel"), 5))
                            )
                        )
                    )
                )

            }

            When("A shipment comes to the factory") {
                events = transferShipmentToCargoBay(
                    Shipment(
                        "shipment-56",
                        listOf(CarPartPackage(CarPart("engine"), 6), CarPartPackage(CarPart("chassis"), 2))
                    )
                )(state)
            }

            Then("The shipment is transferred to the cargo bay") {
                events shouldContain ShipmentTransferredToCargoBay(
                    Shipment(
                        "shipment-56",
                        listOf(CarPartPackage(CarPart("engine"), 6), CarPartPackage(CarPart("chassis"), 2))
                    )
                )

            }
        }

        Scenario("A shipment of 5 wheels and 7 engines comes to the factory and the employee curse") {
            lateinit var state: FactoryState
            lateinit var events: List<Event>
            Given("A factory with an employee assigned and 1 shipment in the cargo bay") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Chewbacca")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-58",
                                listOf(CarPartPackage(CarPart("chassis"), 3))
                            )
                        )
                    )
                )

            }
            When("A shipment of 5 wheel and 7 engines comes to the factory") {
                events = transferShipmentToCargoBay(
                    Shipment(
                        "shipment-56",
                        listOf(CarPartPackage(CarPart("wheel"), 5), CarPartPackage(CarPart("engines"), 7))
                    )
                )(state)

            }
            Then("The shipment is transferred to the cargo bay") {
                events shouldContain ShipmentTransferredToCargoBay(
                    Shipment(
                        "shipment-56",
                        listOf(CarPartPackage(CarPart("wheel"), 5), CarPartPackage(CarPart("engines"), 7))
                    )
                )
            }
            And("A curse word has been uttered by one of the employee") {
                events.filterIsInstance<CurseWordUttered>() shouldNot beEmpty()
            }
        }
    }

})
