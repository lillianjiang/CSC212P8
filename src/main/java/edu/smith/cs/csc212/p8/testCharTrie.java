package edu.smith.cs.csc212.p8;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class testCharTrie {

	@Test
	public void testcountNode() {
		CharTrie trie = new CharTrie();
		List<String> word = new ArrayList<>();
		String[] words = {"access","bill","apple","computer","science"};
		for(int i=0;i<words.length;i++) {
			word.add(words[i]);
		}
		for (String w : word) {
			trie.insert(w);
		}
		Assert.assertEquals(31, trie.countNodes());
	}

}
