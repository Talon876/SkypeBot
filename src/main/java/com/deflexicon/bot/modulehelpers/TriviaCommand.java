package com.deflexicon.bot.modulehelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import com.deflexicon.bot.Logger;

public class TriviaCommand implements CommandListener
{
	private ArrayList<CommandListener> commandListeners = new ArrayList<CommandListener>();
	private static String[] recognizedCommands = { "start", "stop", "top", "help", "catlist" };
	private String command = "";
	private HashMap<String, Integer> map = new HashMap<String, Integer>();
	private String[] arguments;

	public TriviaCommand()
	{

	}

	public TriviaCommand(String command, String[] arguments)
	{
		String[] args = arguments.clone(); // just in case arguments gets
											// changed elsewhere
		this.command = command;
		this.arguments = args;
		for(int i = 0; i < map.size(); i++)
		{
			map.put(recognizedCommands[i], i);
		}
	}

	public void addCommandListener(CommandListener cl)
	{
		commandListeners.add(cl);
	}

	public void removeCommandListener(CommandListener cl)
	{
		commandListeners.remove(cl);
	}

	public String getCommand()
	{
		return command;
	}

	public String[] getArguments()
	{
		return arguments;
	}

	public void process()
	{
		switch (map.get(command))
		{
		case 0: //start
			commandStart();
			break;

		case 1: //stop
			commandStop();
			break;

		case 2: //top
			commandTop();
			break;

		case 3: //help
			commandHelp();
			break;

		case 4: //catlist
			commandCatlist();
			break;
		}
	}

	public static boolean isValidCommand(String text) throws Exception
	{
		TriviaCommand tc = null;

		tc = createCommand(text);

		if (tc == null)
		{
			return false;
		} else
		{
			Logger.log("Created Command: " + tc.toString());
			return true;
		}

	}

	public static TriviaCommand createCommand(String text) throws Exception
	{
		TriviaCommand tc = null;
		// check if it starts with a !
		if (text.charAt(0) != '!')
		{
			throw new Exception("'" + text + "' was not recognized as a command. Try !help");
		} else
		{
			String command = stringHasCommand(text);

			if (!command.isEmpty())
			{
				// System.out.println("Command: " + command);
				String argString = text.replace("!" + command, "").trim();

				// System.out.println("Arg String: " + argString);
				ArrayList<String> args = new ArrayList<String>();
				Stack<Character> stack = new Stack<Character>();
				boolean quoted = false;

				for (int i = 0; i < argString.length(); i++)
				{
					char currChar = argString.charAt(i);

					if (currChar != '"')
					{
						if (quoted)
						{
							stack.push(currChar);
						} else if (!quoted)
						{
							if (currChar != ' ')
							{
								stack.push(currChar);
							}
							if ((currChar == ' ' || (i == argString.length() - 1)) && stack.size() > 0)
							{
								StringBuilder sb = new StringBuilder();
								while (!stack.isEmpty())
								{
									sb.append(stack.pop());
								}
								args.add(sb.reverse().toString().trim());
								stack.clear();
							}
						}
					} else if (currChar == '"')
					{
						if (quoted) // ended quote, get arg
						{
							StringBuilder sb = new StringBuilder();
							while (!stack.isEmpty())
							{
								sb.append(stack.pop());
							}
							args.add(sb.reverse().toString().trim());
							stack.clear();
						}

						quoted = !quoted;
					}

				}
				// System.out.println("Arg List: " + args.toString());

				String[] arguments = new String[args.size()];
				arguments = args.toArray(arguments);
				tc = new TriviaCommand(command, arguments);

			} else
			{
				throw new Exception("'" + text + "' was not recognized as a command. Try !help");
			}

		}

		return tc;
	}

	@Override
	public String toString()
	{
		return command + " " + Arrays.toString(arguments);
	}

	private static String stringHasCommand(String text)
	{
		for (int i = 0; i < recognizedCommands.length; i++)
		{
			if (text.indexOf(recognizedCommands[i]) == 1)
			{
				return recognizedCommands[i];
			}
		}

		return "";
	}

	private void commandStart()
	{
		String questionSet;
		int categoryCap;
		ArrayList<String> categoryWhitelist;

		if (arguments.length == 0)
		{
			questionSet = "default.xml";
			categoryCap = Integer.MAX_VALUE;
			categoryWhitelist = new ArrayList<String>();
			commandStart(this, questionSet, categoryCap, categoryWhitelist);
		} else if (arguments.length == 1)
		{
			questionSet = arguments[0];
			categoryCap = Integer.MAX_VALUE;
			categoryWhitelist = new ArrayList<String>();
			commandStart(this, questionSet, categoryCap, categoryWhitelist);
		} else if (arguments.length == 2)
		{
			questionSet = arguments[0];
			if (arguments[1].equals("-1") || arguments[1].equals("0") || arguments[1].equals("nocap"))
			{
				categoryCap = Integer.MAX_VALUE;
			} else
			{
				try
				{
					categoryCap = Integer.parseInt(arguments[1]);
				} catch (Exception x)
				{
					categoryCap = Integer.MAX_VALUE;
				}
			}
			categoryWhitelist = new ArrayList<String>();
			commandStart(this, questionSet, categoryCap, categoryWhitelist);
		} else
		{
			questionSet = arguments[0];
			if (arguments[1].equals("-1") || arguments[1].equals("0") || arguments[1].equals("nocap"))
			{
				categoryCap = Integer.MAX_VALUE;
			} else
			{
				try
				{
					categoryCap = Integer.parseInt(arguments[1]);
				} catch (Exception x)
				{
					categoryCap = Integer.MAX_VALUE;
				}
			}
			categoryWhitelist = new ArrayList<String>();
			for (int i = 2; i < arguments.length; i++)
			{
				categoryWhitelist.add(arguments[i]);
			}
			commandStart(this, questionSet, categoryCap, categoryWhitelist);
		}

	}

	private void commandStop()
	{
		commandStop(this);
	}

	private void commandTop()
	{
		int n;

		if (arguments.length == 0)
		{
			n = 5;
			commandTop(this, n);
		} else
		{
			try
			{
				n = Integer.parseInt(arguments[0]);
			} catch (Exception x)
			{
				n = 5;
			}
			commandTop(this, n);

		}

	}

	private void commandHelp()
	{
		commandHelp(this);
	}

	private void commandCatlist()
	{
		String questionSet;
		if (arguments.length == 0)
		{
			questionSet = "default.xml";
			commandCatlist(this, questionSet);
		} else
		{
			questionSet = arguments[0];
			commandCatlist(this, questionSet);
		}
	}

	@Override
	public void commandStart(TriviaCommand sender, String questionSet, int categoryCap, ArrayList<String> categoryWhitelist)
	{
		for (CommandListener cl : commandListeners)
		{
			cl.commandStart(sender, questionSet, categoryCap, categoryWhitelist);
		}
	}

	@Override
	public void commandStop(TriviaCommand sender)
	{
		for (CommandListener cl : commandListeners)
		{
			cl.commandStop(sender);
		}
	}

	@Override
	public void commandTop(TriviaCommand sender, int n)
	{
		for (CommandListener cl : commandListeners)
		{
			cl.commandTop(sender, n);
		}
	}

	@Override
	public void commandHelp(TriviaCommand sender)
	{
		for (CommandListener cl : commandListeners)
		{
			cl.commandHelp(sender);
		}
	}

	@Override
	public void commandCatlist(TriviaCommand sender, String questionSet)
	{
		for (CommandListener cl : commandListeners)
		{
			cl.commandCatlist(sender, questionSet);
		}
	}

	public static String getHelpText()
	{
		String help = "Available Commands:\r\n" + "!start - Starts trivia\r\n" + "!stop - Stops trivia early\r\n"
				+ "!top - Displays the top 5 players\r\n" + "!help - Shows this message\r\n"
				+ "!catlist - Shows all categories in a question set";

		return help;
	}
}
