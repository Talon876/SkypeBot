package com.deflexicon.bot.modulehelpers;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.deflexicon.bot.Logger;

public class SimpleXMLErrorHandler implements ErrorHandler
{
	public void warning(SAXParseException e) throws SAXException
	{
		Logger.log(e.getMessage());
	}

	public void error(SAXParseException e) throws SAXException
	{
		Logger.log(e.getMessage());
	}

	public void fatalError(SAXParseException e) throws SAXException
	{
		Logger.log(e.getMessage());
	}
}
