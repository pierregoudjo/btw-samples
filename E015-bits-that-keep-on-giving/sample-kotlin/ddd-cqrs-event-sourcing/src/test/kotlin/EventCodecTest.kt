import dev.forkhandles.tuples.tuple
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import java.time.LocalDateTime
import java.util.*

class EventCodecTest : DescribeSpec({
    describe("create function") {
        it("returns an event codec") {

            val encFn = { event: String ->
                tuple("A String", event.toByteArray(), ByteArray(3), UUID.randomUUID(), LocalDateTime.now())
            }
            val decFn = { timelineEvent: TimeLineEvent<ByteArray> ->
                String(timelineEvent.eventData.data)
            }

            val codec = create(encFn, decFn)


            checkAll<String> { event ->
                val eventData = codec.encode(event)
                val timeLineEvent = TimeLineEvent(eventData, 0)
                val eventEncoded = codec.decode(timeLineEvent)

                println("$event =>> $eventEncoded")

                event shouldBe eventEncoded
            }
        }
    }
})
