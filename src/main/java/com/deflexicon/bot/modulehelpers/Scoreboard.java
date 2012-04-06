package com.deflexicon.bot.modulehelpers;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.deflexicon.bot.Logger;

public class Scoreboard
{
	HashMap<String, Integer> scores;

	public Scoreboard()
	{
		scores = new HashMap<String, Integer>();
	}

	/**
	 * Adds a player to the scoreboard if they aren't already on it.
	 * 
	 * @param player
	 */
	public void addPlayer(String player)
	{
		if (!scores.containsKey(player))
		{
			Logger.log("Haven't seen " + player + " before. Adding " + player + " to highscores.");
			scores.put(player, 0);
		}
	}

	/**
	 * Returns the score of a specified player. Returns -1 if the player does not exist.
	 * 
	 * @param player
	 *            The player to get the score of
	 * @return The player's score, or -1 if they don't exist in the scoreboard.
	 */
	public int getPlayerScore(String player)
	{
		if (!scores.containsKey(player))
		{
			return -1;
		}
		else
		{
			return scores.get(player);
		}
	}

	/**
	 * Sets a specified player's score to the specified score. Note that this completely erases their previous score.
	 * 
	 * @param player
	 *            The player
	 * @param newScore
	 *            Their new score
	 */
	public void setPlayerScore(String player, int newScore)
	{
		if (!scores.containsKey(player))
		{
			Logger.log("Couldn't update player: " + player + " because they aren't in the scoreboard.");
		}
		else
		{
			scores.put(player, newScore);
		}
	}

	/**
	 * Changes a specified player's score by the specified amount.
	 * 
	 * @param player
	 *            The player
	 * @param deltaScore
	 *            The amount their score should change
	 */
	public void updatePlayerScore(String player, int deltaScore)
	{
		if (!scores.containsKey(player))
		{
			Logger.log("Couldn't update player: " + player + " because they aren't in the scoreboard.");
		}
		else
		{
			scores.put(player, scores.get(player) + deltaScore);
		}
	}
	
	public int getSize()
	{
		return scores.size();
	}

	public void printScoreboard()
	{
		System.out.println("--Scoreboard--");
		for (Entry<String, Integer> entry : scores.entrySet())
		{
			String key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println(key + ": " + value);
		}
	}

	private HashMap<String, Integer> getTopX(int x)
	{
		HashMap<String, Integer> topBoard = sortByValue(scores);
		HashMap<String, Integer> tempBoard = new LinkedHashMap<String, Integer>();

		int c = 0;
		if (x > scores.size())
		{
			x = scores.size();
		}
		for (Entry<String, Integer> entry : topBoard.entrySet())
		{
			if (c < x)
			{
				String key = entry.getKey();
				Integer value = entry.getValue();
				tempBoard.put(key, value);
				c++;
			}
		}
		return tempBoard;
	}

	public String printTopX(int x)
	{
		return hashmapToString(getTopX(x));
	}

	public String hashmapToString(HashMap<String, Integer> map)
	{
		String output = "";
		for (Entry<String, Integer> entry : map.entrySet())
		{
			String key = entry.getKey();
			Integer value = entry.getValue();
			output += key + ": " + value + "\n";
		}
		return output;
	}

	private static HashMap<String, Integer> sortByValue(Map<String, Integer> map)
	{
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
		{
			public int compare(Map.Entry<String, Integer> m1, Map.Entry<String, Integer> m2)
			{
				return (m2.getValue()).compareTo(m1.getValue());
			}
		});
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
		}
		return (HashMap<String, Integer>) result;
	}
}
