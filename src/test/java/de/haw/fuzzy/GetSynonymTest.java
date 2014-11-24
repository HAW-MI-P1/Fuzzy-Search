package de.haw.fuzzy;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GetSynonymTest {
	Fuzzy fuzzy;
	
	@Before
	public void setUp() throws Exception {
		this.fuzzy = new FuzzyImpl();
	}

	@Test
	public void testGetSynonym() {
		String[] response = this.fuzzy.getSynonym("peter", 0x0F);

		if(response.length <= 1){ fail(); }
	}
	
	@Test
	public void testGetSynonymWordNull() {
		try {
			this.fuzzy.getSynonym(null, 0x0F);
			fail();
		} 
		catch (Exception e) {}

	}
	
	@Test
	public void testGetSynonymWordEmpty() {
		try {
			this.fuzzy.getSynonym("", 0x0F);
			fail();
		} 
		catch (Exception e) {}

	}

}
