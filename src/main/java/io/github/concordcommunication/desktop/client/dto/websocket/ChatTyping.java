package io.github.concordcommunication.desktop.client.dto.websocket;

public record ChatTyping(
		long userId,
		long channelId,
		Long threadId,
		long sentAt
) {}
