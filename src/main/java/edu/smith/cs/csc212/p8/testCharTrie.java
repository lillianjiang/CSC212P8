package edu.smith.cs.csc212.p8;

import org.junit.Assert;
import org.junit.Test;

public class testCharTrie {

	@Test
	public void testcountNode() {
		CharTrie trie = new CharTrie();
		trie.insert("apple");
		Assert.assertEquals(5,trie.countNodes());
		trie.insert("java");
		Assert.assertEquals(9,trie.countNodes());
		trie.insert("jail");
		Assert.assertEquals(11,trie.countNodes());
		
	}
}
