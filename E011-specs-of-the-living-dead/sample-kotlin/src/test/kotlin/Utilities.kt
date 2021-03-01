fun runWithCatchAndAddToExceptionList(throwableList: MutableList<Throwable>, block: () -> Unit) {
    runCatching {
        block()
    }.onFailure {
        throwableList.add(it)
    }
}
