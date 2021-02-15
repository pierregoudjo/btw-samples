import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*
import kotlin.collections.HashMap

fun main() {

    println(File("Readme.md").readText())
    println(
        """
    Let's create a new product basket to hold our shopping items and simply
    add some products to it directly via traditonal BLOCKING method calls.
    """
    )

    // Create an instance of the ProductBasket class
    // It's AddProduct method takes the following arguments:
    //   a string with the name of a product we want to buy
    //   and a double number indicating the quantity of that item that we want
    // It then stores that item information in its internal _products Dictonary
    val basket = ProductBasket()

    // Add some products to that shopping basket
    basket.addProduct("butter", 1.0)
    basket.addProduct("pepper", 2.0)

    // The code above just used normal blocking method calls
    // to add items direclty into the ProductBasket object instance.
    // That works pretty well when the ProductBasket object happens to be
    // running in the same process and thread as the requestor, but not so well when our
    // ProductBasket is running on some other machine or set of machines.
    // In a distributed computing environment like that,
    // a better approach to executing method calls on remote objects like our
    // ProductBasket is to usa a message class with messaging infrastructure.

    // A "message" is just a regular class that you define that will be used to
    // store the required data that the remote object's parameters need you to pass
    // into it as arguments.
    // So from our first example, we know that when we call the ProductBasket's
    // AddProduct method, we need to supply name and quantity arguments to it.
    // We did that directly above but now we are going to use a message class
    // to store the values of the name and quantity arguements for us.
    // The AddProductToBasketMessage is a class defined lower in this Program.cs file
    // that will do exactly that for us.

    println(
        """
    Now, to add more stuff to the shopping basket via messaging (instead of a
    direct method call), we create an AddProductToBasketMessage to store our name
    and quantity arguments that will be provided to ProductBasket.AddProduct later
    """
    )

    val message = AddProductToBasketMessage("candles", 5.0)

    println(
        """Now, since we created that message, we will apply its item contents of:
        '$message' by sending it to the product basket to be handled."""
    )

    applyMessage(basket, message)

    println(
        """
    We don't have to send/apply messages immediately.  We can put messages into 
    some queue and send them later if needed.

    Let's define more messages to put in a queue:
    """
    )

    val queue: Queue<Message> = LinkedList()
    queue.offer(AddProductToBasketMessage("Chablis wine", 1.0))
    queue.offer(AddProductToBasketMessage("Shrimps", 10.0))

    for (enquedMessage in queue) {
        println("[Message in Queue is:] * $enquedMessage")
    }

    println(
        """
    This is what temporal decoupling is. Our product basket does not
    need to be available at the same time that we create and memorize
    our messages. This will be extremely important, when we get to
    building systems that balance load and can deal with failures.

    Now that we feel like it, let's send our messages that we put in the
    queue to the ProductBasket:
    """
    )
    while (queue.size > 0) {
        applyMessage(basket, queue.poll())
    }

    println(
        """
    Now let's serialize our message to binary form,
    which allows the message object to travel between processes.
    """
    )

    // Note: In the podcast we mentioned "MessageSerializer" as the code doing
    // the serialization.  That was replaced below with "SimpleNetSerializer"
    // to do the same thing in a simpler way to remove complexity from this sample.
    val msg = AddProductToBasketMessage("rosemary", 1.0)

    val bytes = Json.encodeToString(msg).toByteArray()


    println(
        """
    Let's see how this 'rosmary' message object would look in its binary form:
    """
    )
    println(bytes)
    println(
        """
    And if we tried to open it in a text editor...
    """
    )
    println(bytes.contentToString())

    println(
        """
    Let's read the 'rosmary' message we serialized back into memory.

    The process of reading a serialized object from byte array back into instance in memory
    is called deserialization.
    """
    )

    val readMessage = decodeToAddProductToBasketMessage(bytes)
    println("[Serialized Message was read from bytes:] $readMessage")
    println("Now let's apply that message to the product basket.")
    applyMessage(basket, readMessage)

    println("""Note the readable string content with some 'garbled' binary data!
    Now we'll save (persist) the 'rosmary' message to disk, in file 'message.bin'.

    You can see the message.bin file inside of:
    ' + ${File("message.bin").absolutePath} If you open it with Notepad, you will see 
    the 'rosmary' message waiting on disk for you.
    """)
    File("message.bin").writeBytes(bytes)


    println("""
            Let's read the 'rosmary' message we serialized to file 'message.bin' back into memory.

            The process of reading a serialized object from byte array back into intance in memory
            is called deserialization.
    """)
    File("message.bin").readBytes().also {
        println("[Serialized Message was read from disk:] $readMessage")
        println("Now let's apply that messaage to the product basket.")
        applyMessage(basket, decodeToAddProductToBasketMessage(it))
    }


    println(
        """
    Now you've learned what a message is (just a remote temporally
    decoupled message/method call, that can be persisted and then
    dispatched to the place that handles the request.

    You also learned how to actually serialize a message to a binary form
    and then deserialize it and dispatch it the handler.
    """
    )

    println(
        """
    As you can see, you can use messages for passing information
    between machines, telling a story and also persisting.

    By the way, let's see what we have aggregated in our product basket so far:
    """
    )

    for (total in basket.getProductTotals()) {
        println("  ${total.key}: ${total.value}")
    }

    println("""
            And that is the basics of messaging!

    Stay tuned for more episodes and samples!


    # Home work assignment.

            * For C# developers - implement 'RemoveProductFromBasket'
            * For non-C# developers - implement this code in your favorite platform.

    NB: Don't hesitate to ask questions, if you get any.
    """)

    println("We can now remove 1 item of butter, 1 item of candles")

    applyMessage( basket, RemoveProductFromBasketMessage("butter", 1.0))
    applyMessage( basket, RemoveProductFromBasketMessage("candles", 1.0))

    println("We can now try to remove 1 item of chai tea, that doesn't exist in the basket yet")
    applyMessage( basket, RemoveProductFromBasketMessage("chai tea", 1.0))

    println("Let's see the items that remain in my basket")
    for (total in basket.getProductTotals())
    {
        println("  ${total.key}: ${total.value}")
    }
}

private fun decodeToAddProductToBasketMessage(it: ByteArray) =
    Json.decodeFromString<AddProductToBasketMessage>(String(it))

fun applyMessage(basket: ProductBasket, message: Message) {
    basket.`when`(message)
}

interface Message

class ProductBasket {

    private val products: MutableMap<String, Double> = HashMap()

    fun addProduct(name: String, quantity: Double): Unit {
        val currentQuantity = products.getOrDefault(name, 0.0)
        products[name] = currentQuantity + quantity
        println("Shopping Basket said: I added $quantity unit(s) of '$name'")
    }

    fun removeProduct(name: String, quantity: Double): Unit {
        val currentQuantity = products.getOrDefault(name, 0.0)

        when {
            currentQuantity == 0.0 -> println("Shopping Basket said: I do not hold the $name product you are trying to remove")
            currentQuantity < quantity -> println("Shopping basket said: I cannot remove '$quantity' units of '$name' when I only hold '$currentQuantity'")
            currentQuantity == quantity -> {
                products.remove(name)
                println("Shopping basket said: I removed all units of '$name'")
            }
            else -> {
                val remaining = currentQuantity - quantity
                products[name] = remaining
                println("Shopping basket said: I removed '$quantity' units of '$name'")
            }
        }
    }

    fun `when`(message: Message) {
        when (message) {
            is AddProductToBasketMessage -> `when`(message)
            is RemoveProductFromBasketMessage -> `when`(message)
        }
    }

    private fun `when`(toBasketMessage: AddProductToBasketMessage) {
        print("[Message Applied]: ")
        addProduct(toBasketMessage.name, toBasketMessage.quantity)
    }

    private fun `when`(toBasketMessage: RemoveProductFromBasketMessage) {
        print("[Message applied]: ")
        removeProduct(toBasketMessage.name, toBasketMessage.quantity)
    }

    fun getProductTotals(): Map<String, Double> {
        return products
    }
}


@Serializable
class AddProductToBasketMessage(val name: String, val quantity: Double) : Message {
    override fun toString(): String {
        return "Add $quantity $name to basket"
    }
}

@Serializable
class RemoveProductFromBasketMessage(val name: String, val quantity: Double) : Message {
    override fun toString(): String {
        return "Remove $quantity $name from basket"
    }
}
