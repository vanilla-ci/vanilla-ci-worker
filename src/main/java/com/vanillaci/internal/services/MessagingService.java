package com.vanillaci.internal.services;

import com.vanillaci.internal.messaging.*;
import com.vanillaci.internal.util.*;
import org.jetbrains.annotations.*;

import java.util.function.*;

/**
 * @author Joel Johnson
 */
public class MessagingService {
	@NotNull private final VanillaCiConfig config;
	@NotNull private final String expression;

	private final int totalWeight;

	public MessagingService(@NotNull VanillaCiConfig config) {
		this.config = config;
		this.totalWeight = config.getWeight();
		this.expression = config.getExpression();

		//TODO grab a connection from the messaging module as a parameter?
	}

	/**
	 * Listens for messages on the given queue. Calls the given consumer when any message is found matching the given expression.
	 * @param queueName The name of the queue to subscribe to.
	 * @param queueExpression The expression describing the type of messages you're listening for.
	 *                           e.g.: When subscribing to the work queue, you
	 *                           might be interested only in work that requires a Linux machine with weight < 5.
	 *                           The expression would define that criteria.
	 * @param messageReceivedEvent When a message on the given queue that matches the given expression is called, this consumer will be called, passing in the received, deserialized message.
	 * @param <MESSAGE> The type of message expected. If the message received cannot be deserialized into this type, an exception will be logged and the message will be ignored.
	 */
	public <MESSAGE> void subscribeToQueue(String queueName, Expression queueExpression, Consumer<MESSAGE> messageReceivedEvent) {

	}

	/**
	 * Listens for messages on the given topic. Calls the given consumer when any message is found matching the given expression.
	 * @param topicName The name of the topic to subscribe to.
	 * @param topicExpression The expression describing the type of messages you're listening for.
	 *                           e.g.: When subscribing to the work queue, you
	 *                           might be interested only in work that requires a Linux machine with weight < 5.
	 *                           The expression would define that criteria.
	 * @param messageReceivedEvent When a message on the given topic that matches the given expression is called, this consumer will be called, passing in the received, deserialized message.
	 * @param <MESSAGE> The type of message expected. If the message received cannot be deserialized into this type, an exception will be logged and the message will be ignored.
	 */
	public <MESSAGE> void subscribeToTopic(String topicName, Expression topicExpression, Consumer<MESSAGE> messageReceivedEvent) {

	}

	/**
	 * Adds the given message to the given queue.
	 */
	public <MESSAGE> void addMessageToQueue(MESSAGE message, String queueName) {

	}

	/**
	 * Adds the given message to the given topic.
	 */
	public <MESSAGE> void addMessageToTopic(MESSAGE message, String topicName) {

	}
}
