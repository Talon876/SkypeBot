package com.deflexicon.bot.modulehelpers;

import java.util.HashMap;

import com.deflexicon.bot.Bot;
import com.deflexicon.bot.Logger;
import com.deflexicon.bot.modules.MSUModule;
import com.deflexicon.bot.modules.MathModule;
import com.deflexicon.bot.modules.Module;
import com.deflexicon.bot.modules.ParrotModule;
import com.deflexicon.bot.modules.TriviaModule;
import com.deflexicon.bot.modules.UMadModule;
import com.deflexicon.bot.modules.XMLTriviaModule;
import com.deflexicon.bot.modules.YoutubeModule;

public class ModuleLoader
{
	private String[] subscribeSynonyms = { "subscribe", "sub", "add", "load" };
	private String[] unsubscribeSynonyms = { "unsubscribe", "usub", "unsub", "unload", "remove" };

	private HashMap<String, Module> modules;

	/**
	 * Needs to be updated with an alias for whatever Modules are created. If it's not added here, bots cannot subscribe to it from the config file or from commands
	 */
	public ModuleLoader()
	{
		modules = new HashMap<String, Module>();

		modules.put("math", new MathModule());
		modules.put("msu", new MSUModule());
		modules.put("parrot", new ParrotModule());
		modules.put("umad", new UMadModule());
		modules.put("youtube", new YoutubeModule());
		modules.put("trivia", new XMLTriviaModule());
		modules.put("badtrivia", new TriviaModule());
	}

	/**
	 * Processes commands that are typed in to the console of the server or loaded from a config file.
	 * @param command
	 * @param bot
	 */
	public void processCommand(String command, Bot bot)
	{
		command = command.toLowerCase();
		String[] tokens = command.split(" ");
		String cmd = tokens[0];
		String arg = tokens[1];

		boolean subscribe = isSubscribeCmd(cmd);
		boolean validArg = validateArgument(arg);
		if (validArg)
		{
			if (subscribe)
			{
				bot.addModule(modules.get(arg));
			} else
			// unsubscribe
			{
				bot.removeModule(modules.get(arg));
			}
		} else
		{
			Logger.log(command + " was invalid.");
		}

	}

	/**
	 * Returns true if the argument is valid (there is a module)
	 * 
	 * @param arg
	 *            The argument (this will be a module name alias such as math, psu, parrot, umad, youtube)
	 * @return true if it is a valid argument
	 */
	private boolean validateArgument(String arg)
	{
		for (String key : modules.keySet())
		{
			if (arg.equals(key))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isSubscribeCmd(String cmd)
	{
		for (int i = 0; i < subscribeSynonyms.length; i++)
		{
			if (subscribeSynonyms[i].equalsIgnoreCase(cmd))
			{
				return true;
			}
		}
		return false;
	}
}
