package com.deflexicon.bot.modules;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.deflexicon.bot.Logger;
import com.skype.SkypeException;

public class MSUModule extends Module
{
	private String url = "http://msufoodmenu.appspot.com/?Body={SELECTION}&smartphone=YES";

	private HashMap<String, Integer> relation = new HashMap<String, Integer>();
	private String menuName = "";

	@Override
	protected void initialize()
	{
		super.initialize();
		relation.put("hannon breakfast", 0);
		relation.put("hannon lunch", 1);
		relation.put("hannon dinner", 2);
		relation.put("harrison breakfast", 3);
		relation.put("harrison lunch", 4);
		relation.put("harrison dinner", 5);
		relation.put("miller breakfast", 6);
		relation.put("miller lunch", 7);
		relation.put("miller dinner", 8);
	}

	@Override
	protected void processMessageContents(String messageContent) throws SkypeException
	{
		if (relation.get(messageContent.toLowerCase()) != null)
		{
			menuName = messageContent;

			downloadInformation(url.replace("{SELECTION}", "" + relation.get(messageContent.toLowerCase())));
		}
	}

	private void downloadInformation(String url) throws SkypeException
	{
		URL u;
		InputStream is = null;
		DataInputStream dis;
		String s;
		StringBuffer food = new StringBuffer("--Menu for " + menuName + "--\n");
		try
		{
			Logger.log("Getting menu from " + url);
			u = new URL(url);

			is = u.openStream();

			dis = new DataInputStream(new BufferedInputStream(is));

			while ((s = dis.readLine()) != null)
			{
				food.append(s + "\n");
			}
			Logger.log(food.toString());

		} catch (MalformedURLException mue)
		{

		} catch (IOException ioe)
		{

		} finally
		{

			try
			{
				is.close();
			} catch (IOException ioe)
			{

			}

		}
		reply(food.toString());
	}
}
