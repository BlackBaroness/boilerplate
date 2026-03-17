package io.github.blackbaroness.boilerplate.broker.impl

import io.github.blackbaroness.boilerplate.broker.MessageBroker
import io.github.blackbaroness.boilerplate.broker.ReceivedMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

class CompositeMessageBroker<TRANSPORT>(
    val brokers: List<MessageBroker<TRANSPORT>>,
) : BaseMessageBroker<TRANSPORT>() {

    constructor(vararg brokers: MessageBroker<TRANSPORT>) : this(brokers.toList())

    override suspend fun <MESSAGE : Any> publish(
        topic: String,
        message: MESSAGE,
        messageClass: KClass<MESSAGE>,
        serializer: KSerializer<MESSAGE>?,
    ) {
        brokers.forEach { broker ->
            broker.publish(topic, message, messageClass, serializer)
        }
    }

    override fun <MESSAGE : Any> subscribe(
        topic: String,
        messageClass: KClass<MESSAGE>,
        serializer: KSerializer<MESSAGE>?,
    ): Flow<ReceivedMessage<TRANSPORT, MESSAGE>> {
        return brokers.map { it.subscribe(topic, messageClass, serializer) }.merge()
    }
}
