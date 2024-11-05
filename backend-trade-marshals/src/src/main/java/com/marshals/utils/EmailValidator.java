package com.marshals.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
	
	//To validate an email format
	private static final String EMAIL_REGEX = "^[A-Za-z0-9\\._%+-]+@[A-Za-z0-9\\.-]+\\.[A-Za-z]{2,}$";
	
	public static boolean isValidEmail(String email) {
		Pattern pattern = Pattern.compile(EMAIL_REGEX);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
