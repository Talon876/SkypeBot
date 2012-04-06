package com.deflexicon.bot.modulehelpers;

public class TriviaArgs
{
	private Question newQuestion;
	private Question oldQuestion;

	public Question getNewQuestion()
	{
		return newQuestion;
	}

	public void setNewQuestion(Question newQuestion)
	{
		this.newQuestion = newQuestion;
	}

	public Question getOldQuestion()
	{
		return oldQuestion;
	}

	public void setOldQuestion(Question oldQuestion)
	{
		this.oldQuestion = oldQuestion;
	}

	public TriviaArgs()
	{
	}
}
