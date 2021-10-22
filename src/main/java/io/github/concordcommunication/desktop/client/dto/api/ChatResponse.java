package io.github.concordcommunication.desktop.client.dto.api;

import java.util.Map;

public record ChatResponse(
		long id,
		long createdAt,
		long authorId,
		long channelId,
		Long threadId,
		String content,
		boolean edited,
		Map<String, Integer> reactions
) {}
