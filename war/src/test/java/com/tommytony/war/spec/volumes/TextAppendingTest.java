package com.tommytony.war.spec.volumes;

import static org.junit.Assert.*;

import java.util.Random;

import javolution.text.TextBuilder;

import org.junit.Test;

public class TextAppendingTest {

	@Test
	public void testStringBuilder() {
		StringBuilder builder = new StringBuilder();
		Random a = new Random();
		int b = a.nextInt(18);
		builder.append("This is");
		builder.append(" the string object ");
		builder.append("a");
		builder.append("ppending things. ");
		builder.append(b);
		assertEquals(builder.toString(), "This is the string object appending things. " + b);
	}
	
	@Test
	public void testTextBuilder() {
		TextBuilder builder = new TextBuilder();
		Random a = new Random();
		int b = a.nextInt(18);
		builder.append("This is");
		builder.append(" the string object ");
		builder.append("a");
		builder.append("ppending things. ");
		builder.append(b);
		assertEquals(builder.toString(), "This is the string object appending things. " + b);
	}
}
