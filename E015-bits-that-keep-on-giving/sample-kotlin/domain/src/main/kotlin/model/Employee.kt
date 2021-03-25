package model

typealias Employees = List<Employee>

inline class Employee(private val employeeName: String) {
    override fun toString(): String {
        return employeeName
    }
}
