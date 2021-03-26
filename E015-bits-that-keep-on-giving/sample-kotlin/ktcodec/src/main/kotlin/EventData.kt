import java.time.LocalDateTime
import java.util.*

data class EventData<Format>(
    val eventType: String,
    val data: Format,
    val metadata: Format?,
    val eventId: UUID = UUID.randomUUID(),
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
