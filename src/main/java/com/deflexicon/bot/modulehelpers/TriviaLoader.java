package com.deflexicon.bot.modulehelpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.deflexicon.bot.Logger;

public class TriviaLoader extends DefaultHandler
{
	private HashMap<String, ArrayList<Question>> category;
	private Question tempQuestion;
	private String tempValue;
	private String currentCategory;
	private String gameBoard;
	private Random random;
	private HashMap<String, Stack<Question>> triviaReady;

	private int categorySizeLimit;
	private ArrayList<String> categoryWhitelist;
	int questionCount = 0;
	int categoryCount = 0;

	public TriviaLoader(String fileLoc) throws ParserConfigurationException, IOException, SAXException
	{
		this(fileLoc, Integer.MAX_VALUE, null);
	}

	public TriviaLoader(String fileLoc, int categorySizeLimit) throws ParserConfigurationException, IOException, SAXException
	{
		this(fileLoc, categorySizeLimit, null);

	}

	public TriviaLoader(String fileLoc, int categorySizeLimit, ArrayList<String> categoryWhiteList) throws ParserConfigurationException,
			IOException, SAXException
	{
		this.categorySizeLimit = categorySizeLimit;
		this.categoryWhitelist = categoryWhiteList;
		if (categoryWhiteList != null)
		{
			// next 4 lines set every element to all lower case
			ListIterator<String> iterator = categoryWhiteList.listIterator();
			while (iterator.hasNext())
			{
				iterator.set(iterator.next().toLowerCase());
			}
		}

		category = new HashMap<String, ArrayList<Question>>();
		parseDocument(fileLoc);
		Logger.log("Finished Parsing " + fileLoc + " with:\nCategory Size Limit: " + categorySizeLimit + "\nCategoryWhiteList: "
				+ ((categoryWhiteList == null) ? "none" : categoryWhiteList.toString()));
		random = new Random();
		setupTrivia();
	}

	private void setupTrivia()
	{
		Logger.log("\nSetting up Trivia");
		triviaReady = new HashMap<String, Stack<Question>>();
		for (Map.Entry<String, ArrayList<Question>> entry : category.entrySet())
		{
			String key = entry.getKey();

			ArrayList<Question> questions = entry.getValue();
			Collections.shuffle(questions);
			Stack<Question> questionQueue = new Stack<Question>();

			for (int i = 0; i < Math.min(categorySizeLimit, questions.size()); i++)
			{
				questionQueue.push(questions.get(i));
				questionCount++;
			}

			if (categoryWhitelist != null && categoryWhitelist.size() > 0)
			{
				if (categoryWhitelist.contains(key.toLowerCase()))
				{
					triviaReady.put(key, questionQueue);
					categoryCount++;
				} else
				{
					Logger.log("Ignoring " + key + " because it was not found in the whitelist.");
				}
			} else
			{
				triviaReady.put(key, questionQueue);
				categoryCount++;
			}

		}
	}

	public Question getQuestionFromCategory(String category)
	{
		Question question = null;
		if (!triviaReady.get(category).isEmpty())
		{
			question = triviaReady.get(category).pop();
			if (triviaReady.get(category).isEmpty())
			{
				triviaReady.remove(category);
			}
		}
		return question;

	}

	public Question getQuestion()
	{

		if (isOutOfQuestions())
		{
			Logger.log("Out of Questions");
			return null;
		} else
		{
			ArrayList<String> categories = getCategories();
			String randomCategory = categories.get(random.nextInt(categories.size()));

			Question q = getQuestionFromCategory(randomCategory);
			return q;
		}
	}

	private boolean isOutOfQuestions()
	{
		boolean outOfQuestions = true;
		for (Map.Entry<String, Stack<Question>> entry : triviaReady.entrySet())
		{
			outOfQuestions &= entry.getValue().isEmpty();
		}
		return outOfQuestions;
	}

	public int getNumberOfQuestionsRemaining()
	{
		int n = 0;

		for (Map.Entry<String, Stack<Question>> entry : triviaReady.entrySet())
		{
			n += entry.getValue().size();
		}

		return n;
	}

	public String getGameBoard()
	{
		return gameBoard;
	}

	public ArrayList<String> getCategories()
	{
		Set<String> ver = triviaReady.keySet();
		ArrayList<String> categories = new ArrayList<String>();
		for (String s : ver)
		{
			categories.add(s);
		}
		return categories;
	}

	private void parseDocument(String xmlFile) throws ParserConfigurationException, IOException, SAXException
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(new SimpleXMLErrorHandler());
			reader.setContentHandler(this);
			if (!(new File(xmlFile).exists()))
			{
				xmlFile = xmlFile + ".xml";
			}
			reader.parse(new InputSource(xmlFile));
		} catch (ParserConfigurationException pce)
		{
			System.out.println("PCE: " + pce.getMessage());
			pce.printStackTrace();
			throw pce;
		} catch (SAXException se)
		{
			System.out.println("SE: " + se.getMessage());
			se.printStackTrace();
			throw se;
		}
	}

	// Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		// reset
		tempValue = "";
		if (qName.equalsIgnoreCase("GameBoard"))
		{
			gameBoard = attributes.getValue("name");
		}
		if (qName.equalsIgnoreCase("Category"))
		{
			String attCategory = attributes.getValue("name");

			if (!category.containsKey(attCategory))
			{
				category.put(attCategory, new ArrayList<Question>());
			}
			currentCategory = attCategory;

		} else if (qName.equalsIgnoreCase("Trivia"))
		{
			tempQuestion = new Question();
			tempQuestion.setPointValue(Integer.parseInt(attributes.getValue("pointvalue")));
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException
	{
		char[] chCopy = ch.clone(); //just in case ch gets changed
		tempValue = new String(chCopy, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equalsIgnoreCase("Trivia"))
		{
			category.get(currentCategory).add(tempQuestion);
			tempQuestion = null;
		} else if (qName.equalsIgnoreCase("Question"))
		{
			tempQuestion.setQuestionText(tempValue);
		} else if (qName.equalsIgnoreCase("Correct"))
		{
			tempQuestion.addAnswer(tempValue);
		} else if (qName.equalsIgnoreCase("Category"))
		{
			currentCategory = "";
		}
	}

	public int getQuestionCount()
	{
		return questionCount;
	}

	public int getCategoryCount()
	{
		return categoryCount;
	}

	public int getNumQuestionsRemaining()
	{
		ArrayList<String> categories = getCategories();
		return categories.size();
	}

	public int getNumCategoriesRemaining()
	{
		ArrayList<String> categories = getCategories();
		return categories.size();
	}

	public String getGameInfo()
	{
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, Stack<Question>> entry : triviaReady.entrySet())
		{
			String category = entry.getKey();
			Stack<Question> questionList = entry.getValue();
			sb.append(category + " (" + questionList.size() + " question" + ((questionList.size() == 1) ? "" : "s") + ")\n");
		}

		return sb.toString();
	}
}
