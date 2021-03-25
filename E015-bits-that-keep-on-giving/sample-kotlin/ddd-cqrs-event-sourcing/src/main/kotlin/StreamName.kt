inline class StreamName(val streamName: String)

private const val DASH = '-'
private const val UNDERSCORE = '_'

fun create(category: String, aggregateId: String): StreamName =
    when {
        category.contains(DASH) -> throw IllegalArgumentException("category should not contain embedded '-' symbol")
        category.isEmpty() || aggregateId.isEmpty() -> throw java.lang.IllegalArgumentException(
            "Category and aggregateId should be non empty"
        )
        else -> StreamName("${category}${DASH}${aggregateId}")
    }

fun compose(category: String, aggregateIdElements: List<String>): StreamName {
    val aggregateId = aggregateIdElements.reduce { acc, curr ->
        if (curr.contains(UNDERSCORE))
            throw IllegalArgumentException("aggregate id should not contain '_' symbol")
        else
            "${acc}${UNDERSCORE}${curr}"
    }
    return create(category, aggregateId)
}

fun parse(rawStreamName: String): StreamName =
    if (!rawStreamName.contains(DASH))
        throw java.lang.IllegalArgumentException("StreamName $rawStreamName should contain an '-'")
    else
        StreamName(rawStreamName)

fun splitCategoryAndId(rawStreamName: String): Pair<String, String> {
    val parts = rawStreamName.split(DASH, ignoreCase = false, limit = 2)

    if (parts.size != 2)
        throw java.lang.IllegalArgumentException("StreamName $rawStreamName should contain an '-'")
    else
        return (parts[0] to parts[1])
}

fun idElements(aggregateId: String): List<String> = aggregateId.split(UNDERSCORE)

fun splitCategoryAndIdElements(rawStreamName: String): Pair<String, List<String>> {
    val parts = splitCategoryAndId(rawStreamName)
    val idElements = idElements(parts.second)
    return parts.first to idElements
}
