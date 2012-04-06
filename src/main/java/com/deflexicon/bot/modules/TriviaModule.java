package com.deflexicon.bot.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.deflexicon.bot.Logger;
import com.deflexicon.bot.modulehelpers.Question;
import com.skype.SkypeException;

public class TriviaModule extends Module
{
	private boolean triviaStarted = false;
	private String currentPlayerId = "";
	private HashMap<String, Integer> scores;
	private ArrayList<Question> questions;
	private Random random;
	private Question currentQuestion;
	private String theirAnswer;

	@Override
	protected void initialize()
	{
		super.initialize();
		scores = new HashMap<String, Integer>();
		questions = new ArrayList<Question>();
		random = new Random();
	}

	@Override
	protected void processMessageContents(String messageContent) throws SkypeException
	{
		currentPlayerId = owner.getWhoSaidLastMessage().getId();
		if (checkForCommand(messageContent)) // is a command
		{
			if (messageContent.equals("!start"))
			{
				startTrivia();
			} else if (messageContent.equals("!stop"))
			{
				stopTrivia();
			}
		}
		if (triviaStarted)
		{
			Logger.log("Checking answer");
			theirAnswer = messageContent;
			processAnswer();
		}
	}

	private void processAnswer() throws SkypeException
	{
		updatePlayers();
		checkIfCorrect();
	}

	private void checkIfCorrect() throws SkypeException
	{
		if (currentQuestion.isCorrect(theirAnswer))
		{
			Logger.log("Updating " + currentPlayerId + "'s score.");
			scores.put(currentPlayerId, 1 + scores.get(currentPlayerId));
			reportScores();
			askQuestion();
		}
	}

	private void reportScores() throws SkypeException
	{
		Logger.log("Reportin Top 3 Scores");
		String scoreMsg = "-Top 3-\n";
		reply(scoreMsg);
	}

	private void updatePlayers()
	{
		if (!scores.containsKey(currentPlayerId))
		{
			Logger.log("Adding player: " + currentPlayerId);
			scores.put(currentPlayerId, 0);
		}
	}

	private void stopTrivia()
	{
		Logger.log("Trivia Stopped");
		triviaStarted = false;
	}

	private void startTrivia() throws SkypeException
	{
		Logger.log("Trivia Started");
		triviaStarted = true;
		questions.clear();
		populateQuestionsFromFile("set0.quiz");
		Logger.log("Questions Size (2): " + questions.size());
		if (questions.size() > 0)
			askQuestion();
	}

	private void askQuestion() throws SkypeException
	{
		Logger.log("Getting random question");
		currentQuestion = getRandomQuestion();
		reply(currentQuestion.getQuestionText());
		Logger.log("Asked Question: " + currentQuestion.getQuestionText());
	}

	private void populateQuestionsFromFile(String fileName)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			Logger.log("Loading questions from " + fileName);
			String line;
			while ((line = br.readLine()) != null)
			{
				processQuestion(line);
			}
		} catch (Exception x)
		{
			Logger.log("Error loading questions from file: " + x.getMessage());
			x.printStackTrace();
		}
	}

	private void processQuestion(String line)
	{
		Logger.log("Scanning Question: " + line);
		String[] questionAnswers = line.split(";");
		String question = questionAnswers[0];
		Logger.log("Question: " + question);
		String[] answers = questionAnswers[1].split(":");
		Question q1 = new Question(question);
		for (int i = 0; i < answers.length; i++)
		{
			Logger.log("Accepted Answer: " + answers[i]);
			q1.addAnswer(answers[i]);
		}
		questions.add(q1);
		Logger.log("Questions Size: " + questions.size());
	}

	private Question getRandomQuestion()
	{
		return questions.get(random.nextInt(questions.size()));
	}

	private boolean checkForCommand(String messageContent)
	{
		if (messageContent.charAt(0) == '!')
		{
			Logger.log("Trivia command found.");
			return true;
		}
		return false;
	}
}
