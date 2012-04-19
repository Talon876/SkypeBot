package com.deflexicon.bot.modulehelpers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.deflexicon.bot.Logger;

public class YoutubeDataExtractor
{
	private Pattern youtubeUrlPattern = Pattern.compile("http://\\w{0,3}.?youtube+\\.\\w{2,3}/watch\\?v=[\\w-]{11}");
	private Pattern videoPropertiesPattern = Pattern.compile("<meta property=\\\"([\\w:]+)\\\" content=\\\"(.+)\\\">");
	private String url = "";
	private String dataUrl = "http://gdata.youtube.com/feeds/api/videos/";
	//private String videoId = "";

	HashMap<String, String> videoProperties = new HashMap<String, String>();

	public YoutubeDataExtractor(String url)
	{
		this.url = url;
		Matcher matcher = youtubeUrlPattern.matcher(url);
		String videoId = url.substring(url.length() - 11, url.length());
		System.out.println("Video ID: " + videoId);
		dataUrl += videoId;
		if (matcher.find() == false)
		{
			Logger.log("This doesn't appear to be a valid Youtube video link.");
		} else
		{
			scrapeData();
		}
	}

	private void scrapeData()
	{
		Logger.log("\nAttempting to scrape with url: " + dataUrl);
		try
		{
			URL videoUrl = new URL(url);
			InputStream is = null;
			DataInputStream dis;
			String s;
			is = videoUrl.openStream();
			dis = new DataInputStream(new BufferedInputStream(is));
			Matcher matcher;
			while ((s = dis.readLine()) != null)
			{
				matcher = videoPropertiesPattern.matcher(s);
				if (matcher.find())
				{
					videoProperties.put(matcher.group(1), matcher.group(2));
				}
			}
		} catch (Exception x)
		{
			x.printStackTrace();
		}
		Logger.log("Scraped " + url + "\n");

	}

	public String getUrl()
	{
		return videoProperties.get("og:url");
	}

	public String getVideoTitle()
	{
		return videoProperties.get("og:title");
	}

	public String getThumbnailLink()
	{
		return videoProperties.get("og:image");
	}

	public String getVideoProperty(String property)
	{
		return videoProperties.get(property);
	}

	public String getDescription()
	{
		return videoProperties.get("og:description");
	}
}
