import dev.forkhandles.tuples.Tuple5
import java.time.LocalDateTime
import java.util.*

typealias Encoder<Event, Format> = (Event) -> EventData<Format>

typealias Decoder<Format, Event> = (TimeLineEvent<Format>) -> Event?

/**
 * Create an EventCodec
 *
 * @param encode Map a domain event to: An event type name, a pair of Format representing the data and metadata
 *               together with the event id and the timestamp
 * @param decode Attempt to map an event stored data back to its corresponding Domain event object
 */
fun <Event : Any, Format : Any> create(
    encode: (Event) -> Tuple5<String, Format, Format, UUID, LocalDateTime>,
    decode: (TimeLineEvent<Format>) -> Event?
): Pair<Encoder<Event, Format>, Decoder<Format, Event>> {

    val encFn = { event: Event ->
        val (eventType, data, metadata, eventId, timestamp) = encode(event)
        EventData(eventType, data, metadata, eventId, timestamp)
    }

    val decFn = { encoded: TimeLineEvent<Format> ->
        decode(encoded)
    }

    return encFn to decFn
}
