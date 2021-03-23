import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object AssignEmployeeToTheFactoryFeature : Spek({
    lateinit var exception: Throwable

    Feature("Assign employee to the factory") {

        Scenario("Assign employee to an empty factory") {
            lateinit var state: FactoryState
            lateinit var events: List<Event>

            Given("An empty factory") {
                state = FactoryState(emptyList())
            }
            When("An employee named \"Fry\" comes to the factory") {
                events = assignEmployeeToFactory(Employee("Fry"))(state)
            }
            Then("Fry is assigned to the factory") {
                events shouldContain EmployeeAssignedToFactory(Employee("Fry"))
            }
        }

        Scenario("An already assigned employee is assigned again") {
            lateinit var state: FactoryState

            Given("A factory where an employee named \"Fry\" is assigned") {
                state = FactoryState(listOf(EmployeeAssignedToFactory(Employee("Fry"))))

            }

            When("An employee named \"Fry\" comes to the factory, There should be an error") {
                exception = shouldThrow { assignEmployeeToFactory(Employee("Fry"))(state) }
            }

            Then("The error message should contain \"the name of Fry only one can have\" ") {
                exception.message shouldContain "the name of Fry only one can have"
            }
        }

        Scenario("Bender comes to the factory") {
            lateinit var state: FactoryState

            Given("An empty factory") {
                state = FactoryState(emptyList())
            }

            When("An employee named Bender is assigned to the factory, there should be an error") {
                exception = shouldThrow<IllegalStateException> { assignEmployeeToFactory(Employee("Bender"))(state) }
            }

            Then("The error message should contain \"Guys with name 'bender' are trouble\" ") {
                exception.message shouldContain "Guys with the name 'bender' are trouble"
            }
        }
    }
})
