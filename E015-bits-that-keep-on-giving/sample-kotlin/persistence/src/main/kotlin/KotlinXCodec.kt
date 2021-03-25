import dev.forkhandles.tuples.tuple
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.util.*

inline fun <reified Event : Any> serialize(serializer: KSerializer<Event>) = { event: Event ->
    tuple(
        event::class.qualifiedName!!,
        Json.encodeToString(serializer, event),
        "",
        UUID.randomUUID(),
        LocalDateTime.now()
    )
}

inline fun <reified Event : Any> deserialize(serializer: KSerializer<Event>) = { timelineEvent: TimeLineEvent<String> ->
    Json.decodeFromString(serializer, timelineEvent.eventData.data)
}
