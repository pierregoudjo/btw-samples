internal fun doPaperWork(workName: String) {
    println(" > Work: Papers... $workName ...")
}

internal fun doRealWork(workName: String) {
    println(" > Work: heavy stuff... $workName ...")
}

internal fun echoCommand(message: String) {
    println("?> Command: $message".yellow())
}

internal fun fail(message: String) {
    println(":> $message".red())
    throw IllegalStateException(message)
}

internal fun String.red() = "\u001B[41m$this\u001B[0m"
internal fun String.green() = "\u001B[32m$this\u001B[0m"
internal fun String.yellow() = "\u001B[33m$this\u001B[0m"
