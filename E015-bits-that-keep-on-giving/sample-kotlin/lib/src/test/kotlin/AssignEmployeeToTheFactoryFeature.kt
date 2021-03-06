import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertTrue

object AssignEmployeeToTheFactoryFeature : Spek({
    lateinit var exceptions: MutableList<Throwable>
    lateinit var state: FactoryState
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
                    state = assignEmployeeToFactory(Employee("Fry"), state)
                }
            }
            Then("Fry is assigned to the factory") {
                assertTrue {
                    state.journal.contains(EmployeeAssignedToFactory(Employee("Fry")))
                }
            }
        }

        Scenario("An already assigned employee is assigned again") {
            Given("A factory where an employee named \"Fry\" is assigned") {
                state = FactoryState(listOf(EmployeeAssignedToFactory(Employee("Fry"))))

            }

            When("An employee named \"Fry\" comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = assignEmployeeToFactory(Employee("Fry"), state)
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
                    state = assignEmployeeToFactory(Employee("Bender"), state)
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
})
