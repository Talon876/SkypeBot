package com.deflexicon.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.deflexicon.bot.modulehelpers.ModuleLoader;
import com.deflexicon.bot.modules.Module;
import com.skype.Chat;
import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;

public class Bot implements ChatMessageListener
{
	private ArrayList<Module> botModules;
	protected Chat chat;
	protected User whoSaidLastMessage;
	protected String chatIdBeingMonitored;
	protected ModuleLoader moduleLoader;

	public Bot(String idToMonitor, Chat chat)
	{
		this.chatIdBeingMonitored = idToMonitor;
		Logger.log("Initializing Bot to monitor chat id: " + chatIdBeingMonitored);
		botModules = new ArrayList<Module>();
		moduleLoader = new ModuleLoader();
		connectToSkype();
		this.chat = chat;
		reply("Hello. Type !help if you don't know what to do.");
	}

	private void connectToSkype()
	{
		Logger.log("Bot is Connecting");
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
		loadModulesFromFile("modules.config");
		Logger.log("Ready");
	}

	@Override
	public void chatMessageReceived(ChatMessage chatMessage) throws SkypeException
	{
		Logger.log("ID: " + chatMessage.getChat().getId() + "");
		if (chatMessage.getChat().getId().equals(chatIdBeingMonitored))
		{
			// Chat currentChat = chatMessage.getChat();
			// whoSaidLastMessage = chatMessage.getSender();
			String message = chatMessage.getContent();
			//
			// Logger.log("Chatroom ID: " + currentChat.getId());
			// Logger.log(whoSaidLastMessage.getId() + ": " + message);
			//
			// this.chat = currentChat;

			for (Module module : botModules)
			{
				if (!message.isEmpty())
				{
					module.chatMessageReceived(chatMessage);
				}
			}
		}
	}

	public User getWhoSaidLastMessage()
	{
		return whoSaidLastMessage;
	}

	@Override
	public void chatMessageSent(ChatMessage chatMessage) throws SkypeException
	{

	}

	public void reply(String message)
	{
		if (chat != null)
		{
			try
			{
				chat.send(message);
			} catch (SkypeException e)
			{
				Logger.log("Could not reply: " + e.getMessage());
			}
		}
	}

	public void addModule(Module newModule)
	{
		if (!botModules.contains(newModule))
		{
			Logger.log("Subscribing bot to " + newModule.getClass().getName());
			botModules.add(newModule);
			newModule.setOwner(this);
		}
	}

	public void removeModule(Module module)
	{
		Logger.log("Unsubscribing bot from " + module.getClass().getName());
		botModules.remove(module);
	}

	public void removeAllModules()
	{
		Logger.log("Unsubscribing bot from all modules");
		for (Module module : botModules)
		{
			removeModule(module);
		}
	}

	public void loadModulesFromFile(String fileName)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			Logger.log("Loading commands from " + fileName);
			String line;
			while ((line = br.readLine()) != null)
			{
				moduleLoader.processCommand(line, this);
			}
		} catch (Exception x)
		{
			Logger.log("Error loading modules from file: " + x.getMessage());
			File file;
			file = new File("modules.config");
			if (!file.exists())
			{
				try
				{
					file.createNewFile();
					Logger.log("Created modules.config file");
				} catch (IOException e)
				{
					Logger.log("Error creating modules.config file: " + e.getMessage());
				}
			}
		}
	}
}
