package com.deflexicon.bot.modules;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.deflexicon.bot.Logger;
import com.skype.SkypeException;

public class MathModule extends Module
{
	ScriptEngine engine;

	@Override
	protected void initialize()
	{
		super.initialize();
		ScriptEngineManager mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("JavaScript");
	}

	@Override
	protected void processMessageContents(String messageContent) throws SkypeException
	{
		String answer = "";
		try
		{
			answer = engine.eval(messageContent).toString();
		} catch (Exception x)
		{

		}
		if (!answer.isEmpty() && !answer.equalsIgnoreCase("true") && !answer.equalsIgnoreCase("false"))
		{
			Logger.log("Message was a JavaScript function, evaluating and returning answer.");
			reply("[Math] " + answer);
		}
	}
}
