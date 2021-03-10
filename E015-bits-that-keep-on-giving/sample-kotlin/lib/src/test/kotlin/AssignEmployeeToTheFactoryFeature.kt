import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldNot
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

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
                    state = assignEmployeeToFactory(Employee("Fry"))(state)
                }
            }
            Then("Fry is assigned to the factory") {
                state.journal shouldContain EmployeeAssignedToFactory(Employee("Fry"))
            }
        }

        Scenario("An already assigned employee is assigned again") {
            Given("A factory where an employee named \"Fry\" is assigned") {
                state = FactoryState(listOf(EmployeeAssignedToFactory(Employee("Fry"))))

            }

            When("An employee named \"Fry\" comes to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = assignEmployeeToFactory(Employee("Fry"))(state)
                }
            }

            Then("There should be an error") {
                exceptions shouldNot beEmpty()
            }

            And("The error message should contain \"the name of Fry only one can have\" ") {
                exceptions shouldHaveSingleElement { it.message?.contains("the name of Fry only one can have")!! }
            }
        }

        Scenario("Bender comes to the factory") {
            Given("An empty factory") {
            }
            When("An employee named Bender is assigned to the factory") {
                runWithCatchAndAddToExceptionList(exceptions) {
                    state = assignEmployeeToFactory(Employee("Bender"))(state)
                }
            }

            Then("There should be an error") {
                exceptions shouldNot beEmpty()
            }

            And("The error message should contain \"Guys with name 'bender' are trouble\" ") {
                exceptions shouldHaveSingleElement { it.message?.contains("Guys with the name 'bender' are trouble")!! }
            }
        }
    }
})
