import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class StreamNameTest : DescribeSpec({
    describe("create") {
        it("create a streamName for a given category and aggregateId ") {
            val category = "Basket"
            val aggregateId = "001"

            create(category, aggregateId) shouldBe StreamName("Basket-001")
        }

        it("create a streamName for a given category and aggregate id even if the id contain dashes") {
            val category = "Basket"
            val aggregateId = "aggregate-1"
            create(category, aggregateId) shouldBe StreamName("Basket-aggregate-1")
            create(category, aggregateId).streamName shouldBe "Basket-aggregate-1"
        }

        it("create a streamName for a given category and a UUID") {
            val uuid = "101cdf52-8bf6-11eb-8dcd-0242ac130003"
            val category = "Basket"
            create(category, uuid) shouldBe StreamName("Basket-101cdf52-8bf6-11eb-8dcd-0242ac130003")
        }


        it("should throw an IllegalArgumentException if the category contains a dash") {
            val category = "Basket-o"
            val aggregateId = "001"

            shouldThrow<IllegalArgumentException> { create(category, aggregateId) }
        }

        it("should throw if the category is empty string") {
            val category = ""
            val aggregateId = "001"

            shouldThrow<IllegalArgumentException> { create(category, aggregateId) }
        }
    }
    describe("compose") {
        it("create a StreamName given a category and a list of aggregateId elements") {
            val category = "Basket"
            val aggregateIds = listOf("username", "basketnumber")
            compose(category, aggregateIds) shouldBe StreamName("Basket-username_basketnumber")
        }

        it(
            "create a streamName given a category and a list of" +
                    " aggregateId elements even if some elements contains dashes"
        ) {
            val category = "Basket"
            val aggregateIds = listOf("aggregate-1", "stream-1")
            compose(category, aggregateIds) shouldBe StreamName("Basket-aggregate-1_stream-1")
        }

        it("should throw if an aggregateId Element contains an underscore") {
            val category = "Basket"
            val aggregateIds = listOf("aggregate_1", "stream_1")
            shouldThrow<IllegalArgumentException> { compose(category, aggregateIds) }
        }
    }

    describe("parse") {
        it("parse a stream name string (string composed of a category and an aggregate id) into a StreamName object") {
            val streamName = "Basket-1"
            parse(streamName) shouldBe StreamName("Basket-1")
        }

        it("should throw if the stream name doesn't contains a separator between the category and the aggregate id") {
            val streamName = "Basket"
            shouldThrow<java.lang.IllegalArgumentException> { parse(streamName) }
        }
    }

    describe("splitCategoryAndId") {
        it("split the stream name into its category and aggregate id ") {
            val streamName = "Basket-1"
            splitCategoryAndId(streamName) shouldBe ("Basket" to "1")
        }
        it("split the stream name into its category and aggregate id (multiple id elements) ") {
            val streamName = "Basket-aggregate-1_stream-1"
            splitCategoryAndId(streamName) shouldBe ("Basket" to "aggregate-1_stream-1")
        }
        it("split the stream name into its category and aggregate id (UUID) ") {
            val streamName = "Basket-101cdf52-8bf6-11eb-8dcd-0242ac130003"
            splitCategoryAndId(streamName) shouldBe ("Basket" to "101cdf52-8bf6-11eb-8dcd-0242ac130003")
        }
        it("throws if the is no category and aggregate id in the string") {
            val streamName = "the_super_stream_name"
            shouldThrow<IllegalArgumentException> { splitCategoryAndId(streamName) }
        }
    }

    describe("idElements") {
        it("give back the id elements") {
            val streamName = "1"
            idElements(streamName) shouldBe listOf("1")
        }
        it("give back the id elements (multiple)") {
            val streamName = "aggregate-1_stream-1"
            idElements(streamName) shouldBe listOf("aggregate-1", "stream-1")
        }
        it("give back the id elements (UUID)") {
            val streamName = "101cdf52-8bf6-11eb-8dcd-0242ac130003"
            idElements(streamName) shouldBe listOf("101cdf52-8bf6-11eb-8dcd-0242ac130003")
        }
    }

    describe("splitCategoryAndIdElements") {
        it("gives back the category and the id elements") {
            val streamName = "Basket-1"
            splitCategoryAndIdElements(streamName) shouldBe ("Basket" to listOf("1"))
        }
        it("split the stream name into its category and aggregate id (multiple id elements) ") {
            val streamName = "Basket-aggregate-1_stream-1"
            splitCategoryAndIdElements(streamName) shouldBe ("Basket" to listOf("aggregate-1","stream-1"))
        }
        it("split the stream name into its category and aggregate id (UUID) ") {
            val streamName = "Basket-101cdf52-8bf6-11eb-8dcd-0242ac130003"
            splitCategoryAndIdElements(streamName) shouldBe ("Basket" to listOf("101cdf52-8bf6-11eb-8dcd-0242ac130003"))
        }
        it("throws if the is no category and aggregate id in the string") {
            val streamName = "the_super_stream_name"
            shouldThrow<IllegalArgumentException> { splitCategoryAndIdElements(streamName) }
        }
    }
})
