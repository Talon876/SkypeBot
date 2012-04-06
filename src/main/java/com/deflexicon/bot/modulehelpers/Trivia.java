package com.deflexicon.bot.modulehelpers;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.deflexicon.bot.Logger;

public class Trivia implements TriviaListener
{
	private ArrayList<TriviaListener> triviaListeners = new ArrayList<TriviaListener>();
	private TriviaLoader triviaData;
	private Scoreboard scoreboard;
	private Question currentQuestion;

	private boolean triviaStarted = false;

	/**
	 * Starts the trivia
	 * @param questionSet Which questionSet should be used
	 */
	public void startTrivia(String questionSet) throws ParserConfigurationException, IOException, SAXException
	{
		startTrivia(questionSet, Integer.MAX_VALUE, null);
	}

	/**
	 * Starts the trivia
	 * @param questionSet Which questionSet should be used
	 * @param categorySizeCap What is the cap on questions in each category
	 */
	public void startTrivia(String questionSet, int categorySizeCap) throws ParserConfigurationException, IOException, SAXException
	{
		startTrivia(questionSet, categorySizeCap, null);
	}

	/**
	 * Starts the trivia
	 * @param questionSet Which questionSet should be used
	 * @param categorySizeCap What is the cap on questions in each category
	 * @param categoryWhitelist What is the csv whitelist of categories to use
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public void startTrivia(String questionSet, int categorySizeCap, ArrayList<String> categoryWhitelist)
			throws ParserConfigurationException, IOException, SAXException
	{
		if (triviaStarted == false)
		{
			triviaData = new TriviaLoader(questionSet, categorySizeCap, categoryWhitelist);
			scoreboard = new Scoreboard();
			Logger.log("Starting Trivia");
			triviaStarted = true;
		} else
		{
			Logger.log("Cannot start trivia while trivia has already been started");
		}
	}

	/**
	 * Actually Starts the trivia
	 */
	public void begin()
	{
		Logger.log("Trivia Started");
		getNextQuestion();
	}

	/**
	 * Gets the amount of players who have participated so far.
	 * @return The number of players in the scoreboard.
	 */
	public int getPlayerAmount()
	{
		return scoreboard.getSize();
	}

	/**
	 * Adds a player to the scoreboard.
	 * @param playerId The playerId to be added. This is usually the skypeId
	 */
	public void addPlayer(String playerId)
	{
		scoreboard.addPlayer(playerId);
	}

	/**
	 * Gets the next question
	 * @return The text for the next question
	 */
	public String askQuestion()
	{
		return currentQuestion.getQuestionText();
	}

	/**
	 * Gets the next question from the stack
	 */
	private void getNextQuestion()
	{
		Logger.log(triviaData.getNumberOfQuestionsRemaining() + " questions remain (before getting a new question).");

		if (triviaData.getNumberOfQuestionsRemaining() == 0)
		{
			triviaEnded(this);
		} else
		{
			TriviaArgs t = new TriviaArgs();
			t.setOldQuestion(currentQuestion);

			currentQuestion = triviaData.getQuestion();
			t.setNewQuestion(currentQuestion);
			questionChanged(this, t);
		}
	}

	/**
	 * Gets the score for the current question
	 * @return The score
	 */
	public int getCurrentQuestionScore()
	{
		return currentQuestion.getPointValue();
	}

	/**
	 * Checks the player's answer against all possible answers
	 * @param player The player who answered
	 * @param theirAnswer Their answer
	 * @return True if they are correct, false if they are wrong
	 */
	public boolean checkAnswer(String player, String theirAnswer)
	{
		if (currentQuestion.isCorrect(theirAnswer))
		{
			questionAnswered(this, player, theirAnswer, true);
			return true;
		} else
		{
			questionAnswered(this, player, theirAnswer, false);
			return false;
		}
	}

	/**
	 * Checks if the trivia is running
	 * @return true if trivia has started, false if it has not
	 */
	public boolean isTriviaStarted()
	{
		return triviaStarted;
	}

	/**
	 * Adds a TriviaListener to this instance of Trivia
	 * @param tl
	 */
	public void addTriviaListener(TriviaListener tl)
	{
		triviaListeners.add(tl);
	}

	/**
	 * Removes a TriviaListener from this instance of Trivia
	 * @param tl
	 */
	public void removeTriviaListener(TriviaListener tl)
	{
		triviaListeners.remove(tl);
	}

	/**
	 * Fired when the question changes. All triviaListeners questionChanged method is fired
	 */
	public void questionChanged(Trivia sender, TriviaArgs t)
	{
		Logger.log("Question Changed");
		for (TriviaListener tl : triviaListeners)
		{
			tl.questionChanged(sender, t);
		}
	}

	/**
	 * Fired when the question is answered. All triviaListeners questionAnswered method is fired
	 */
	public void questionAnswered(Trivia sender, String player, String theirAnswer, boolean correct)
	{
		Logger.log("Question Answered");
		for (TriviaListener tl : triviaListeners)
		{
			tl.questionAnswered(sender, player, theirAnswer, correct);
		}
		if (correct)
		{
			scoreboard.updatePlayerScore(player, currentQuestion.getPointValue());
			getNextQuestion();
		}
	}

	/**
	 * Gets a string containing the top players in the scoreboard
	 * @param x How many players to return
	 * @return The string to be sent to the chat
	 */
	public String getTopXList(int x)
	{
		return scoreboard.printTopX(x);
	}

	/**
	 * Fired when the trivia ends. All triviaListeners triviaEnded method is fired.
	 */
	public void triviaEnded(Trivia sender)
	{
		Logger.log("Ran out of questions. Trivia Ending");
		triviaStarted = false;
		for (TriviaListener tl : triviaListeners)
		{
			tl.triviaEnded(this);
		}
	}

	/**
	 * Gets the amount of categories for the current trivia session.
	 * @return The number of categories
	 */
	public int getCategoryCount()
	{
		return triviaData.getCategoryCount();
	}

	/**
	 * Gets the amount of questions for the current trivia session.
	 * @return The number of questions
	 */
	public int getQuestionCount()
	{
		return triviaData.getQuestionCount();
	}

	/**
	 * Gets the game info
	 * @return the game info
	 */
	public String getGameInfo()
	{
		return triviaData.getGameInfo();
	}
}
