package com.deflexicon.bot;

import java.util.HashMap;

import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;

public class BotManager implements ChatMessageListener
{
	private HashMap<String, Bot> allBots;

	public BotManager()
	{
		Logger.log("Initializing BotManager");
		allBots = new HashMap<String, Bot>();

		connectToSkype();
	}

	private void connectToSkype()
	{
		Logger.log("Connecting BotManager to Skype");
		try
		{
			Logger.log("Running on Skype " + Skype.getVersion() + " as user " + Skype.getProfile().getId());
			Skype.addChatMessageListener(this);
		} catch (SkypeException e)
		{
			Logger.log("Couldn't connect to Skype");
			Logger.log(e.getMessage());
			System.exit(1);
		}
		Logger.log("Monitoring chats to add Bots");
	}

	@Override
	public void chatMessageReceived(ChatMessage chatMessage) throws SkypeException
	{
		String chatId = chatMessage.getChat().getId();

		Chat currentChat = chatMessage.getChat();
		User whoSaidLastMessage = chatMessage.getSender();
		String message = chatMessage.getContent();

		Logger.log("Chatroom ID: " + chatId);
		Logger.log(whoSaidLastMessage.getId() + ": " + message);
		Logger.log("Message Type: " + chatMessage.getType() + "\n");

		if (chatMessage.getType() == ChatMessage.Type.ADDEDMEMBERS)
		{
			if (!allBots.containsKey(chatId))
			{
				Logger.log("Need new bot for Chat ID: " + chatId);
				Bot b = new Bot(chatId, currentChat);
				allBots.put(chatId, b);
			}
		} else if (chatMessage.getType() == ChatMessage.Type.LEFT)
		{
			Logger.log("Someone left the chat.");
			// check if the chat has any remaining users (active?)
			// if not, destroy the bot and remove it from the hashmap
			// and leave the chat if possible
		}
	}

	@Override
	public void chatMessageSent(ChatMessage sentChatMessage) throws SkypeException
	{

	}
}
