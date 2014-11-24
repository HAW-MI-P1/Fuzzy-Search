package de.haw.fuzzy;

public interface Fuzzy {
	/**
	 * 
	 * @param word to find synonyms for.
	 * @param opt set the type of fuzzy search. <br>
	 * 0x01: Search for synonym sets<br>
	 * 0x02: Search for similar words<br>
	 * 0x04: Search for substrings containing this words<br>
	 * 0x08: Search for the words at the beginning<br>
	 * 0x0F: Enable all search options<br>
	 * @return Array of all matches.<br>
	 */
	public String[] getSynonym(String word, int opt);
	
}
