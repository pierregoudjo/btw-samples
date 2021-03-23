import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.string.shouldContain
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object ProduceACarFeature : Spek({
    Feature("Produce a car") {

        Scenario("Order to a non assigned employee to produce a model T car") {
            lateinit var state: FactoryState
            lateinit var exception: Throwable

            Given("Yoda assigned to the factory") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda"))
                    )
                )
            }

            When("Order given to Chewbacca to produce a model T car, There should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    produceCar(Employee("Chewbacca"), CarModel.MODEL_T)(state)
                }
            }

            Then("The error should contains the message \"Chewbacca must be assigned to the factory\" ") {
                exception.message shouldContain "Chewbacca must be assigned to the factory"
            }
        }

        Scenario("Order to build a model T car but there is not enough parts") {
            lateinit var state: FactoryState
            lateinit var exception: Throwable

            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels and 5 bits and pieces") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(
                                    CarPartPackage(CarPart("chassis"), 3),
                                    CarPartPackage(CarPart("wheel"), 5),
                                    CarPartPackage(CarPart("bits and pieces"), 5)
                                )
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            Employee("Yoda"), listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5)
                            )
                        )
                    )
                )

            }
            When("Yoda is ordered to build a model T car, There should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    produceCar(Employee("Yoda"), CarModel.MODEL_T)(state)
                }

            }

            Then(
                "The error should contains the message \"There is not " +
                        "enough part to build ${CarModel.MODEL_T} car\" "
            ) {
                exception.message shouldContain "There is not enough part to build ${CarModel.MODEL_T} car"
            }
        }

        Scenario("Order to build a model V car but there is not enough parts") {
            lateinit var state: FactoryState
            lateinit var exception: Throwable
            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels and 5 bits and pieces") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(
                                    CarPartPackage(CarPart("chassis"), 3),
                                    CarPartPackage(CarPart("wheel"), 5),
                                    CarPartPackage(CarPart("bits and pieces"), 5)
                                )
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            Employee("Yoda"), listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5)
                            )
                        )
                    )
                )

            }
            When("Yoda is ordered to build a model T car, There should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    produceCar(
                        Employee("Yoda"),
                        CarModel.MODEL_V
                    )(state)
                }
            }

            Then(
                "The error should contains the message \"There is not " +
                        "enough part to build ${CarModel.MODEL_V} car\" "
            ) {
                exception.message shouldContain "There is not enough part to build ${CarModel.MODEL_V} car"
            }
        }

        Scenario("Order to build a model T car and there is enough parts") {
            lateinit var state: FactoryState
            lateinit var events: List<Event>

            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(
                                    CarPartPackage(CarPart("chassis"), 3),
                                    CarPartPackage(CarPart("wheel"), 5),
                                    CarPartPackage(CarPart("bits and pieces"), 5),
                                    CarPartPackage(CarPart("engine"), 2),
                                )
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            Employee("Yoda"), listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5),
                                CarPartPackage(CarPart("engine"), 2),
                            )
                        )
                    )
                )

            }
            When("Yoda is ordered to produce a model T car") {
                events = produceCar(Employee("Yoda"), CarModel.MODEL_T)(state)
            }
            Then("Then a model T car is built") {

                events shouldContain CarProduced(
                    Employee("Yoda"),
                    CarModel.MODEL_T,
                    CarModel.neededParts(CarModel.MODEL_T)
                )
            }
        }

        Scenario("Order to build a model V car and there is enough parts") {
            lateinit var state: FactoryState
            lateinit var events: List<Event>

            Given("Yoda is assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines") {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(
                                    CarPartPackage(CarPart("chassis"), 3),
                                    CarPartPackage(CarPart("wheel"), 5),
                                    CarPartPackage(CarPart("bits and pieces"), 5),
                                    CarPartPackage(CarPart("engine"), 2),
                                )
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            Employee("Yoda"), listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5),
                                CarPartPackage(CarPart("engine"), 2),
                            )
                        )
                    )
                )

            }
            When("Yoda is ordered to produce a model V car") {
                events = produceCar(Employee("Yoda"), CarModel.MODEL_V)(state)
            }
            Then("Then a model V car is built") {
                events shouldContain CarProduced(
                    Employee("Yoda"),
                    CarModel.MODEL_V,
                    CarModel.neededParts(CarModel.MODEL_V)
                )
            }
        }

        Scenario("Order to build a model T car and checking the remaining parts") {
            lateinit var state: FactoryState
            lateinit var intermediateState: FactoryState
            lateinit var events: List<Event>

            Given(
                "Yoda and Luke assigned to the factory and there is 3 chassis, 5 wheels 5 bits and pieces and 2 engines"
            ) {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda")),
                        EmployeeAssignedToFactory(Employee("Luke")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(
                                    CarPartPackage(CarPart("chassis"), 3),
                                    CarPartPackage(CarPart("wheel"), 5),
                                    CarPartPackage(CarPart("bits and pieces"), 5),
                                    CarPartPackage(CarPart("engine"), 2),
                                )
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            Employee("Yoda"), listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5),
                                CarPartPackage(CarPart("engine"), 2),
                            )
                        )
                    )
                )

            }
            And("Model T car requires 2 wheels, 1 engine, and 2 bits and pieces parts to produce") {

            }
            And("Model V car requires 1 chassis, 2 wheels, 1 engine and 2 bits and pieces parts to produce") {

            }
            When("Yoda is ordered to produce a model T car and Luke is ordered to produce a model V car") {
                val produceCarEvents = produceCar(Employee("Yoda"), CarModel.MODEL_T)(state)
                intermediateState = apply(produceCarEvents, state)
                events = produceCar(Employee("Luke"), CarModel.MODEL_V)(intermediateState)
                state = apply(events, intermediateState)
            }

            Then("There is 2 chassis left") {
                state.inventory shouldContain (CarPart("chassis") to 2)
            }

            And("There is 1 wheels left") {
                state.inventory shouldContain (CarPart("wheel") to 1)
            }

            And("There is 1 bits and pieces left") {
                state.inventory shouldContain (CarPart("bits and pieces") to 1)
            }

            And("There is 0 engine left") {
                state.inventory shouldContain (CarPart("engine") to 0)
            }
        }

        Scenario("Not enough stock to build a new car after some have already been built") {
            lateinit var state: FactoryState
            lateinit var exception: Throwable

            Given(
                "Yoda assigned to the factory, 3 chassis, 5 wheels 5 bits and pieces and 2 engines " +
                        "shipment transferred and unpacked and 2 model T cars built"
            ) {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda")),
                        EmployeeAssignedToFactory(Employee("Luke")),
                        EmployeeAssignedToFactory(Employee("Lea")),
                        ShipmentTransferredToCargoBay(
                            Shipment(
                                "shipment-1",
                                listOf(
                                    CarPartPackage(CarPart("chassis"), 3),
                                    CarPartPackage(CarPart("wheel"), 5),
                                    CarPartPackage(CarPart("bits and pieces"), 5),
                                    CarPartPackage(CarPart("engine"), 2),
                                )
                            )
                        ),
                        ShipmentUnpackedInCargoBay(
                            Employee("Yoda"), listOf(
                                CarPartPackage(CarPart("chassis"), 3),
                                CarPartPackage(CarPart("wheel"), 5),
                                CarPartPackage(CarPart("bits and pieces"), 5),
                                CarPartPackage(CarPart("engine"), 2),
                            )
                        ),
                        CarProduced(Employee("Yoda"), CarModel.MODEL_T, CarModel.neededParts(CarModel.MODEL_T)),
                        CarProduced(Employee("Luke"), CarModel.MODEL_T, CarModel.neededParts(CarModel.MODEL_T)),
                    )
                )

            }
            And("Model T car requires 2 wheels, 1 engine, and 2 bits and pieces parts to produce") {

            }
            When("Yoda is ordered to build a model T car, There should be an error") {
                exception = shouldThrow<IllegalStateException> { produceCar(Employee("Lea"), CarModel.MODEL_T)(state) }
            }
            And(
                "The error should contains the message \"There is not enough part to build ${CarModel.MODEL_T} car\" "
            ) {
                exception.message shouldContain "There is not enough part to build ${CarModel.MODEL_T} car"
            }
        }

        Scenario("An employee may only produce a car once a day") {
            lateinit var state: FactoryState
            lateinit var exception: Throwable

            Given(
                "Yoda an employee of the factory who has already built a car today" +
                        " and enough part left on the shelf to build a model T car"
            ) {
                state = FactoryState(
                    listOf(
                        EmployeeAssignedToFactory(Employee("Yoda")),
                        ShipmentUnpackedInCargoBay(
                            Employee("Yoda"),
                            listOf(
                                CarPartPackage(CarPart("wheel"), 60),
                                CarPartPackage(CarPart("engine"), 40),
                                CarPartPackage(CarPart("bits and pieces"), 20)
                            )
                        ),
                        CarProduced(Employee("Yoda"), CarModel.MODEL_T, CarModel.neededParts(CarModel.MODEL_T))

                    )
                )
            }
            When("Yoda is ordered to build a car") {
                exception = shouldThrow<IllegalStateException> { produceCar(Employee("Yoda"), CarModel.MODEL_T)(state) }
            }

            Then("The error should contains a message \"Yoda may only produce a car once a day\"") {
                exception.message shouldContain "Yoda may only produce a car once a day"
            }
        }
    }

})
