package com.deflexicon.bot.modules;

import com.skype.SkypeException;

public class ParrotModule extends Module
{
	@Override
	protected void initialize()
	{
		// TODO Auto-generated method stub
		super.initialize();
	}

	@Override
	public void processMessageContents(String messageContent) throws SkypeException
	{
		reply("[Parrot] " + messageContent);
	}

}
