# Commands

- **ProduceCarCommand**(employee: Employee, model: CarModel, units: Number)
- **WorkOvertimeCommand**(employee: Employee, workDay: Date)
- **UnloadCargoBay**(employee: Employee, cargoBay: CargoBay)
- **HireEmployee**(employee: Employee)
- **RecordWords**()

# Events
- **CarBuilt**(employee: Employee, model: CarModel units: Number, date: Date )
- **OvertimeAccepted**(employee: Employee, workDay: Date, date: Date)
-- **OvertimeRejected**(employee: Employee, workday: Date, date: Date)
- **WordUttered**(employee: Employee, words: String, date: Date)
- **CargoSuccessfulyUnloaded**(employee: Employee, cargoBay: CargoBay, parts: Map<Part, Number>, date: Date)
- **CargoUnsuccessfulyUnloaded**(employee: Employee, reason: String, date: Date)
- **EmployeeHired**(employee: Employee, date: Date)
- **EmployeeNotHired**(employee: Employee, reason: String, date: Date)
- **EmployeeFired**(employee: Employee, date: Date)
