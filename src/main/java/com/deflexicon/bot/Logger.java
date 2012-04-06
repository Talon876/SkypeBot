package com.deflexicon.bot;

public class Logger
{
	public static void log(String message)
	{
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		//System.out.print("[" + stackTraceElements[2].getClassName() + "." + stackTraceElements[2].getMethodName() + "()] ");
		System.out.println(message);
	}

	public static void log(String message, String location)
	{
		System.out.println(location + " : " + message);
	}
}
