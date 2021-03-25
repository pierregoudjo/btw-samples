import dev.forkhandles.tuples.Tuple5
import java.time.LocalDateTime
import java.util.*

/**
 * A codec object that cobble together
 * an encode function and a decode function
 */
data class EventCodec<Event, Format> internal constructor(
    val encode: (Event) -> EventData<Format>,
    val decode: (TimeLineEvent<Format>) -> Event?
)

typealias EncodeFn<Event, Format> = (Event) -> Tuple5<String, Format, Format, UUID, LocalDateTime>
typealias DecodeFn<Event, Format> = (TimeLineEvent<Format>) -> Event?

/**
 * Create an EventCodec
 *
 * @param encode Map a domain event to: An event type name, a pair of Format representing the data and metadata
 *               together with the event id and the timestamp
 * @param decode Attempt to map an event stored data back to its corresponding Domain event object
 */
fun <Event : Any, Format : Any> create(
    encode: EncodeFn<Event, Format>,
    decode: DecodeFn<Event, Format>
): EventCodec<Event, Format> {

    val encFn = { event: Event ->
        val (eventType, data, metadata, eventId, timestamp) = encode(event)
        EventData(eventType, data, metadata, eventId, timestamp)
    }

    val decFn = { encoded: TimeLineEvent<Format> ->
        decode(encoded)
    }

    return EventCodec(encFn, decFn)
}
