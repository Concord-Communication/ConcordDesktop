package io.github.concordcommunication.desktop.model;

import java.util.List;

public interface ChannelChatListener {
	void chatsSet(List<Chat> chats);
	void chatsAppended(List<Chat> chats);
	void chatsPrepended(List<Chat> chats);
	void chatsRemoved(List<Chat> chats);
}
