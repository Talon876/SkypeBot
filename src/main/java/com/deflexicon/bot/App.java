package com.deflexicon.bot;

import java.io.IOException;
import java.util.Scanner;

import javax.script.ScriptException;

public class App
{
	BotManager botManager;

	public App() throws ScriptException
	{
		botManager = new BotManager();
		Scanner in = new Scanner(System.in);
		String input = "";
		while (!input.equals("exit"))
		{
			input = in.nextLine();
			System.out.println(input);
		}
	}

	public static void main(String[] args) throws IOException, ScriptException
	{
		new App();

		Logger.log("Goodbye!");
	}

}
