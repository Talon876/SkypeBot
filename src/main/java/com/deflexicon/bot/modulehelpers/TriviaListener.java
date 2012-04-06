package com.deflexicon.bot.modulehelpers;

public interface TriviaListener
{
	void questionChanged(Trivia sender, TriviaArgs t);

	void questionAnswered(Trivia sender, String player, String theirAnswer, boolean correct);

	void triviaEnded(Trivia sender);
}
