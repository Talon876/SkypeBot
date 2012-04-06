package com.deflexicon.bot.modulehelpers;

import java.util.ArrayList;

public class Question
{
	private String questionText;
	private ArrayList<String> acceptedAnswers;
	private int pointValue;

	public Question()
	{
		this.questionText = "";
		acceptedAnswers = new ArrayList<String>();
	}

	public Question(String questionText)
	{
		this.questionText = questionText;
		acceptedAnswers = new ArrayList<String>();
	}

	public int getPointValue()
	{
		return pointValue;
	}

	public void setPointValue(int pointValue)
	{
		this.pointValue = pointValue;
	}

	public String getQuestionText()
	{
		return questionText;
	}

	public void setQuestionText(String questionText)
	{
		this.questionText = questionText;
	}

	public void addAnswer(String acceptedAnswer)
	{
		acceptedAnswers.add(acceptedAnswer.toLowerCase());
	}

	public boolean isCorrect(String theirAnswer)
	{
		for (String answer : acceptedAnswers)
		{
			if (GoogleSpellcheck.isAcceptable(theirAnswer, answer))
			{
					return true;
			}
		}
		return false;
	}
}
