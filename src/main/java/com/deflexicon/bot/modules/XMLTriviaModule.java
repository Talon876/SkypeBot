package com.deflexicon.bot.modules;

import java.util.ArrayList;

import com.deflexicon.bot.modulehelpers.CommandListener;
import com.deflexicon.bot.modulehelpers.Trivia;
import com.deflexicon.bot.modulehelpers.TriviaArgs;
import com.deflexicon.bot.modulehelpers.TriviaCommand;
import com.deflexicon.bot.modulehelpers.TriviaListener;
import com.skype.SkypeException;

public class XMLTriviaModule extends Module implements TriviaListener, CommandListener
{
	Trivia trivia = null;
	String currentPlayerId = "";

	@Override
	protected void processMessageContents(String messageContent) throws SkypeException
	{
		currentPlayerId = this.lastReceivedMessage.getSenderId();

		if (isAttemptedCommand(messageContent))
		{
			try
			{
				if (TriviaCommand.isValidCommand(messageContent))
				{
					doCommand(messageContent);
				}
			} catch (Exception e)
			{
				reply(e.getMessage());
			}
		} else
		{
			if (trivia != null)
			{
				trivia.addPlayer(currentPlayerId);
				trivia.checkAnswer(currentPlayerId, messageContent);
			}
		}

	}

	private boolean isAttemptedCommand(String input)
	{
		return input.charAt(0) == '!';
	}

	private void doCommand(String input)
	{
		TriviaCommand command = null;
		try
		{
			command = TriviaCommand.createCommand(input);
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		if (command != null)
		{
			command.addCommandListener(this);
			command.process();
		}
	}

	@Override
	public void commandStart(TriviaCommand sender, String questionSet, int categoryCap, ArrayList<String> categoryWhitelist)
	{
		if (trivia == null)
		{
			trivia = new Trivia();
			trivia.addTriviaListener(this);
			try
			{
				trivia.startTrivia(questionSet, categoryCap, categoryWhitelist);
				String message = "Welcome to Trivia!\n";
				message += "Answer questions to earn points. The question's point value is represented in the square brackets at the beginning of the question.\n";
				message += "This Round:\n";
				message += trivia.getGameInfo();
				reply(message);
				trivia.begin();
			} catch (Exception e)
			{
				reply("Error starting trivia: " + e.getMessage());
				trivia = null;
			}
		} else
		{
			reply("Trivia has already started. To stop trivia prematurely, use !stop");
		}
	}

	@Override
	public void commandStop(TriviaCommand sender)
	{
		if (trivia != null)
		{
			trivia.triviaEnded(trivia);
		}
	}

	@Override
	public void commandTop(TriviaCommand sender, int n)
	{
		if (trivia != null)
		{
			String highscores = "\n";
			highscores += "-- Top " + n + " Highscores--\n";
			highscores += trivia.getTopXList(n);
			reply(highscores);
		} else
		{
			reply("For !top to work, trivia must be started. To start trivia, use !start");
		}
	}

	@Override
	public void commandHelp(TriviaCommand sender)
	{
		reply(TriviaCommand.getHelpText());
	}

	@Override
	public void commandCatlist(TriviaCommand sender, String questionSet)
	{
		reply("That command hasn't been implemented yet.");
	}

	@Override
	public void questionChanged(Trivia sender, TriviaArgs t)
	{
		reply("[" + t.getNewQuestion().getPointValue() + "p] " + t.getNewQuestion().getQuestionText());
	}

	@Override
	public void questionAnswered(Trivia sender, String player, String theirAnswer, boolean correct)
	{
		if (correct)
		{
			reply(player + " earned " + sender.getCurrentQuestionScore() + " points.");
		}
	}

	@Override
	public void triviaEnded(Trivia sender)
	{
		reply("Round Over!");
		try
		{
			doCommand("!top " + sender.getPlayerAmount());
		} catch (Exception x)
		{

		}
		trivia = null;
	}
}
