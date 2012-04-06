package com.deflexicon.bot.modules;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.deflexicon.bot.modulehelpers.YoutubeDataExtractor;
import com.skype.SkypeException;

public class YoutubeModule extends Module
{
	Pattern youtubeUrlPattern = Pattern.compile("http://\\w{0,3}.?youtube+\\.\\w{2,3}/watch\\?v=[\\w-]{11}");

	@Override
	protected void initialize()
	{
		super.initialize();
	}

	@Override
	protected void processMessageContents(String messageContent) throws SkypeException
	{
		ArrayList<String> links = new ArrayList<String>();

		Matcher matcher = youtubeUrlPattern.matcher(messageContent);
		while (matcher.find())
		{
			links.add(matcher.group());
		}

		if (links.size() > 0) // found youtube link(s)
		{
			System.out.println("Found one or more youtube links: ");
			for (String link : links)
			{
				System.out.println("Found Link: " + link);

			}

			processYoutubeLinks(links);
		}
	}

	private void processYoutubeLinks(ArrayList<String> youtubeLinks) throws SkypeException
	{
		ArrayList<YoutubeDataExtractor> dataForYoutubeLinks = new ArrayList<YoutubeDataExtractor>();

		for (String link : youtubeLinks)
		{
			YoutubeDataExtractor yde = new YoutubeDataExtractor(link);
			dataForYoutubeLinks.add(yde);
		}
		for (YoutubeDataExtractor videoData : dataForYoutubeLinks)
		{
			String theVideoData = "";
			theVideoData += "URL: " + videoData.getUrl() + "\n";
			theVideoData += "Title: " + videoData.getVideoTitle() + "\n";
			theVideoData += "Thumbnail: " + videoData.getThumbnailLink();
			reply(theVideoData);
			System.out.println("Responded to " + videoData.getUrl());
		}
	}

}
