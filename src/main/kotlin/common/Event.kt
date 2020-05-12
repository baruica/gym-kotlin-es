package common

import java.time.ZonedDateTime

abstract class Event {
    // identify the message that needs to be acted upon
    val messageName: String? = null

    // identify the message that needs to be acted upon
    val serviceWhichOwnsTheContractOfTheMessage: String? = null

    // allows for parsing of different versions, in case of breaking changes
    val messageVersion: String? = null

    // allow to build a causal chain of each command in a system
    val messageId: MessageId? = null

    // allows for checking for errors in distributed processes
    val correlationId: CorrelationId? = null

    // allow to build a causal chain of each command in a system
    val causationId: CausationId? = null

    // for context (along with data)
    val utcTimestamp: ZonedDateTime? = null

    // for security and auth
    val authenticationToken: String? = null

    // For commands this is taken from query (similarly to an ETag), and used in all messages for idempotency
    val entityVersion: String? = null

    // [Event-only] for context (along with data)
    val resourceWhichRaisedTheEvent: String? = null
}

data class CausationId(val id: String)
data class CorrelationId(val id: String)
data class MessageId(val id: String)
