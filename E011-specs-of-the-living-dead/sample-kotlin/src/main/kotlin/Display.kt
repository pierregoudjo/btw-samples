const val SLEEP_DURATION: Long = 1000
fun doPaperWork(workName: String) {
    println(" > Work: Papers... $workName ...")
    Thread.sleep(SLEEP_DURATION)
}

fun doRealWork(workName: String) {
    println(" > Work: heavy stuff... $workName ...")
    Thread.sleep(SLEEP_DURATION)
}

fun echoCommand(message: String) {
    println("?> Command: $message".yellow())
}

fun fail(message: String) {
    println(":> $message".red())
    throw IllegalStateException(message)
}

fun String.red() = "\u001B[41m$this\u001B[0m"
fun String.green() = "\u001B[32m$this\u001B[0m"
fun String.yellow() = "\u001B[33m$this\u001B[0m"
