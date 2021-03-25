import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

interface Aggregate<DomainEvent> {
    val journal: PersistentList<DomainEvent>
}

fun <DomainEvent> apply(
    newEvents: List<DomainEvent>,
    oldEvents: List<DomainEvent>
): List<DomainEvent> = newEvents.toPersistentList() + oldEvents.toPersistentList()
