package com.deflexicon.bot.modulehelpers;

import java.util.ArrayList;

public interface CommandListener
{
	void commandStart(TriviaCommand sender, String questionSet, int categoryCap, ArrayList<String> categoryWhitelist);

	void commandStop(TriviaCommand sender);

	void commandTop(TriviaCommand sender, int n);

	void commandHelp(TriviaCommand sender);

	void commandCatlist(TriviaCommand sender, String questionSet);
}
