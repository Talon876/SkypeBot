package com.deflexicon.bot.modulehelpers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

public class GoogleSpellcheck
{
	private static String url = "http://didyoumean.info/api?tld=com&q=";

	/**
	 * Checks the given string against Google's Did You Mean value and returns the result. If there is no result, it returns the same string.
	 * 
	 * @param s
	 * @return
	 */
	public static String correct(String string)
	{
		try
		{
			URL videoUrl = new URL(url + URLEncoder.encode(string, "UTF-8"));
			InputStream is = null;
			DataInputStream dis;
			String s;
			is = videoUrl.openStream();
			dis = new DataInputStream(new BufferedInputStream(is));
			s = dis.readLine();
			if (s != null)
				return s;
			else
				return string;

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		return string;
	}

	public static boolean isAcceptable(String phrase, String correctPhrase)
	{
		boolean pass = false;
		phrase = phrase.trim();
		correctPhrase = correctPhrase.trim();

		if (phrase.trim().equalsIgnoreCase(correctPhrase.trim()))
		{
			pass = true;
			return pass;
		}

		for (int i = 0; i < correctPhrase.length(); i++)
		{
			if (Character.isDigit(correctPhrase.charAt(i)))
			{
				pass = false;
				return pass;
			}
		}

		int length = correctPhrase.length();
		int ed = GoogleSpellcheck.getLevenshteinDistance(phrase, correctPhrase);
		System.out.println("ED: " + ed);
		double ratio = (double) ed / (double) length;
		System.out.println("ratio: " + ratio);

		if (ratio <= 0.1d)
		{
			pass = true;
		}
		if (length < 6 && ed <= 1)
		{
			pass = true;
		}
		if (length < 10 && ed <= 2)
		{
			pass = true;
		}
		if (length >= 10 && ed <= 5)
		{
			pass = true;
		}
		if (ed > length / 2)
		{
			pass = false;
		}

		if (pass == false && ratio <= .35d)
		{
			phrase = GoogleSpellcheck.correct(phrase);
			if (phrase.equalsIgnoreCase(correctPhrase))
			{
				System.out.println("Used google's correction: " + phrase);
				pass = true;
			}
		}

		return pass;
	}

	// http://www.merriampark.com/ldjava.htm
	public static int getLevenshteinDistance(String s, String t)
	{
		if (s == null || t == null)
		{
			throw new IllegalArgumentException("Strings must not be null");
		}
		int n = s.length();
		int m = t.length();

		if (n == 0)
		{
			return m;
		}
		else if (m == 0)
		{
			return n;
		}

		int p[] = new int[n + 1];
		int d[] = new int[n + 1];
		int temp[];

		int i;
		int j;

		char t_j;

		int cost;

		for (i = 0; i <= n; i++)
		{
			p[i] = i;
		}

		for (j = 1; j <= m; j++)
		{
			t_j = t.charAt(j - 1);
			d[0] = j;

			for (i = 1; i <= n; i++)
			{
				cost = s.charAt(i - 1) == t_j ? 0 : 1;

				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
			}

			temp = p;
			p = d;
			d = temp;
		}
		return p[n];
	}
}
