import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object UnpackShipmentInTheCargoBayFeature : Spek({

    Feature("Unpack Shipments in the Cargo Bay") {

        Scenario("Order given non-assigned employee to the factory to unpack shipments in the cargo bay") {
            lateinit var exception: Throwable
            lateinit var state: FactoryState
            Given("Chewbacca assigned to the factory and 1 shipment transferred in the cargo bay") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Chewbacca")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(CarPartPackage(CarPart("chassis"), 4))
                            )
                        )
                    )
                )

            }
            When("There is an order given to Yoda to unpack shipments in the cargo bay, There should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    unpackAndInventoryShipmentInCargoBay(Employee("Yoda"))(state)
                }

            }
            Then("The error should contains \"Yoda must be assigned to the factory\"") {
                exception.message shouldContain ("Yoda must be assigned to the factory")

            }
        }

        Scenario("No employee assigned to the factory to unpack the cargo bay") {
            lateinit var exception: Throwable
            lateinit var state: FactoryState
            Given("No employee assigned to the factory and 1 shipment transferred to the cargo bay") {
                state = FactoryState(
                    listOf(
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(CarPartPackage(CarPart("chassis"), 4))
                            )
                        )
                    )
                )

            }
            When("There is an order given to Yoda to unpack shipments in the cargo bay, There should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    unpackAndInventoryShipmentInCargoBay(Employee("Yoda"))(state)
                }
            }

            Then("The error should contains \"Yoda must be assigned to the factory\"") {
                exception.message shouldContain ("Yoda must be assigned to the factory")
            }
        }

        Scenario(
            "There must be at least 1 shipment in the cargo bay when the employee" +
                    " is ordered to unpack the shipments in the cargo bay"
        ) {
            lateinit var exception: Throwable
            lateinit var state: FactoryState
            Given("Chewbacca assigned to the factory") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Chewbacca")),
                    )
                )

            }

            When("an order is given to Chewbacca to unpack shipments in the cargo bay, There should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    unpackAndInventoryShipmentInCargoBay(Employee("Chewbacca"))(state)
                }
            }

            Then("The error should contains \"There should be a shipment to unpack\"") {
                exception.message shouldContain ("There should be a shipment to unpack")
            }
        }

        Scenario("Order an assigned employee to unpack a shipment in the cargo bay") {

            lateinit var events: List<Event>
            lateinit var state: FactoryState

            Given(
                "Chewbacca assigned to the factory and there is a shipment of 4 chassis transferred to the cargo bay"
            ) {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Chewbacca")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(CarPartPackage(CarPart("chassis"), 4))
                            )
                        )
                    )
                )

            }

            When("There is an order given to Chewbacca to unpack the cargo bay") {
                events = unpackAndInventoryShipmentInCargoBay(Employee("Chewbacca"))(state)
            }

            Then("Chewbacca unpacked shipment in the cargo bay") {
                events shouldContain ShipmentUnpackedInCargoBay(
                    Employee("Chewbacca"),
                    listOf(CarPartPackage(CarPart("chassis"), 4))
                )
            }
        }

        Scenario("Order an assigned employee to unpack two shipments in the cargo bay") {
            lateinit var events: List<Event>
            lateinit var state: FactoryState
            Given(
                "Chewbacca assigned to the factory and there is a shipment of 4 chassis " +
                        "and another shipment of 2 wheels and 3 engines in the cargo bay"
            ) {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Chewbacca")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(CarPartPackage(CarPart("chassis"), 4))
                            )
                        ),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(CarPartPackage(CarPart("wheel"), 2), CarPartPackage(CarPart("engine"), 3))
                            )
                        ),

                        )
                )

            }

            When("There is an order given to Chewbacca to unpack shipment the cargo bay") {
                events = unpackAndInventoryShipmentInCargoBay(Employee("Chewbacca"))(state)

            }

            Then("Chewbacca unpack 3 chassis, 2 wheel and 3 engines") {
                events shouldContain ShipmentUnpackedInCargoBay(
                    Employee("Chewbacca"), listOf(
                        CarPartPackage(CarPart("chassis"), 4),
                        CarPartPackage(CarPart("wheel"), 2),
                        CarPartPackage(CarPart("engine"), 3),
                    )
                )
            }
        }

        Scenario("Order an assigned employee to unpack two shipments with common items from the cargo bay") {
            lateinit var events: List<Event>
            lateinit var state: FactoryState
            Given(
                "Chewbacca assigned to the factory and there is a shipment of 4" +
                        " chassis and another shipment of 2 wheels and 3 chassis in the cargo bay"
            ) {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Chewbacca")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(CarPartPackage(CarPart("chassis"), 4))
                            )
                        ),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(CarPartPackage(CarPart("wheel"), 2), CarPartPackage(CarPart("chassis"), 3))
                            )
                        ),
                    )
                )

            }

            When("There is an order given to Chewbacca to unpack shipment in the cargo bay") {
                events = unpackAndInventoryShipmentInCargoBay(Employee("Chewbacca"))(state)
            }

            Then("Chewbacca unpacked the cargo bay with a 4-chassis pack, 2 wheels-pack and 3-chassis pack") {
                events shouldContain ShipmentUnpackedInCargoBay(
                    Employee("Chewbacca"), listOf(
                        CarPartPackage(CarPart("chassis"), 4),
                        CarPartPackage(CarPart("wheel"), 2),
                        CarPartPackage(CarPart("chassis"), 3),
                    )
                )
            }
        }

        Scenario("An employee may unpack shipments in the cargo bay once a day") {
            lateinit var exception: Throwable
            lateinit var state: FactoryState
            Given("Yoda already unpack shipments the cargo bay today") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(CarPartPackage(CarPart("wheel"), 5))
                            )
                        ),
                        ShipmentUnpackedInCargoBay(Employee("Yoda"), listOf(CarPartPackage(CarPart("wheel"), 5))),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-2",
                                listOf(CarPartPackage(CarPart("chassis"), 2))
                            )
                        )
                    )
                )
            }

            When("Yoda is ordered to unpack the shipment in the cargo bay, There should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    unpackAndInventoryShipmentInCargoBay(Employee("Yoda"))(state)
                }
            }

            And(
                "The error message should contains \" Yoda may only unpack and inventory all " +
                        "Shipments in the CargoBay once a day \" "
            ) {
                exception.message shouldContain
                        "Yoda may only unpack and inventory all Shipments in the CargoBay once a day"
            }
        }

    }

})
