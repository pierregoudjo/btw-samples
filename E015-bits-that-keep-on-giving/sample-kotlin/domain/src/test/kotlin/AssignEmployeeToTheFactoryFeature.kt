import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import model.Employee
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object AssignEmployeeToTheFactoryFeature : Spek({
    lateinit var exception: Throwable

    Feature("Assign employee to the factory") {

        Scenario("Assign employee to an empty factory") {
            lateinit var currentEvents: List<FactoryDomainEvent>
            lateinit var resultingEvents: List<FactoryDomainEvent>

            Given("An empty factory") {
                currentEvents = emptyList()
            }
            When("An employee named \"Fry\" comes to the factory") {
                resultingEvents = fold(currentEvents, AssignEmployeeToFactory(Employee("Fry")))
            }
            Then("Fry is assigned to the factory") {
                resultingEvents shouldContain EmployeeAssignedToFactory(Employee("Fry"))
            }
        }

        Scenario("An already assigned employee is assigned again") {
            lateinit var currentEvents: List<FactoryDomainEvent>

            Given("A factory where an employee named \"Fry\" is assigned") {
                currentEvents = listOf(EmployeeAssignedToFactory(Employee("Fry")))

            }

            When("An employee named \"Fry\" comes to the factory, There should be an error") {
                exception = shouldThrow { fold(currentEvents, AssignEmployeeToFactory((Employee("Fry")))) }
            }

            Then("The error message should contain \"the name of Fry only one can have\" ") {
                exception.message shouldContain "the name of Fry only one can have"
            }
        }

        Scenario("Bender comes to the factory") {
            lateinit var currentEvents: List<FactoryDomainEvent>

            Given("An empty factory") {
                currentEvents = emptyList()
            }

            When("An employee named Bender is assigned to the factory, there should be an error") {
                exception = shouldThrow<IllegalStateException> {
                    fold(currentEvents, AssignEmployeeToFactory(Employee("Bender")))
                }
            }

            Then("The error message should contain \"Guys with name 'bender' are trouble\" ") {
                exception.message shouldContain "Guys with the name 'bender' are trouble"
            }
        }
    }
})
