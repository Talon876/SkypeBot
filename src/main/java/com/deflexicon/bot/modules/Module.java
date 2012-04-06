package com.deflexicon.bot.modules;

import com.deflexicon.bot.Bot;
import com.deflexicon.bot.Logger;
import com.skype.ChatMessage;
import com.skype.SkypeException;

public abstract class Module
{
	protected Bot owner;
	protected ChatMessage lastReceivedMessage;

	public void chatMessageReceived(ChatMessage chatMessage) throws SkypeException
	{
		lastReceivedMessage = chatMessage;

		processMessageContents(chatMessage.getContent());
	}

	protected abstract void processMessageContents(String messageContent) throws SkypeException;

	public Bot getOwner()
	{
		return owner;
	}

	public void setOwner(Bot bot)
	{
		this.owner = bot;
		initialize();
	}

	/**
	 * This method is ran as soon as the module gets an owner.
	 */
	protected void initialize()
	{
		Logger.log("Initializing " + getClass().getName());
	}

	/**
	 * Replies to the last chat
	 * 
	 * @param message
	 * @throws SkypeException
	 */
	protected void reply(String message)
	{
		owner.reply(message);
	}

}
