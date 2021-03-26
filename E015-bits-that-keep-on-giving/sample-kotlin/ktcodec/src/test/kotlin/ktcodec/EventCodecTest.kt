package ktcodec

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

            val (encode, decode) = create(encFn, decFn)


            checkAll<String> { event ->
                val eventData = encode(event)
                val timeLineEvent = TimeLineEvent(eventData, 0)
                val eventEncoded = decode(timeLineEvent)
                event shouldBe eventEncoded
            }
        }
    }
})
