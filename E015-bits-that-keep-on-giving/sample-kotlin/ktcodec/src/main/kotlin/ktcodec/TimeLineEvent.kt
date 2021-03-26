package ktcodec

data class TimeLineEvent<Format>(
    val eventData: EventData<Format>,
    val index: Long
)
