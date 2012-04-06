package com.deflexicon.bot.modules;

import com.deflexicon.bot.Logger;
import com.skype.SkypeException;

public class UMadModule extends Module
{

	@Override
	protected void initialize()
	{
		// TODO Auto-generated method stub
		super.initialize();
	}

	@Override
	protected void processMessageContents(String messageContent) throws SkypeException
	{
		double caps = countCapitals(messageContent);
		double length = messageContent.length();
		if (caps / length > .5 && length >= 4)
		{
			Logger.log("Detected " + Math.floor((caps / length) * 100) + "% capital letters, over the threshold of 50%");
			Logger.log("Asking if they are mad");
			reply("UMAD?");
		}
	}

	private int countCapitals(String messageContent)
	{
		int caps = 0;
		for (int i = 0; i < messageContent.length(); i++)
		{
			if (Character.isUpperCase(messageContent.charAt(i)))
			{
				caps++;
			}
		}
		return caps;
	}

}
