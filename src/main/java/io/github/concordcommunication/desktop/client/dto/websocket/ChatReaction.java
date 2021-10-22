package io.github.concordcommunication.desktop.client.dto.websocket;

public record ChatReaction(
		long chatId,
		String reaction,
		boolean adding
) {}
