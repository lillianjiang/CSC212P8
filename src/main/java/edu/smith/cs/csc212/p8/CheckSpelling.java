package edu.smith.cs.csc212.p8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CheckSpelling {
	/**
	 * Read all lines from the UNIX dictionary.
	 * @return a list of words!
	 */
	public static List<String> loadDictionary() {
		long start = System.nanoTime();
		List<String> words;

		try {
			words = Files.readAllLines(new File("src/main/resources/words").toPath());
			
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " entries in " + time +" seconds.");
		return words;
	}
	
	/**
	 * load the book into class
	 * @return string of the word
	 */
	public static List<String> loadbook() {
		List<String> bookwordlist;
		String book = new String();

		try {
			bookwordlist = Files.readAllLines(new File("src/main/resources/heartofdarkness").toPath());

		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}

		for (String w : bookwordlist) {
			book = book+w+" ";
		}
		return WordSplitter.splitTextToWords(book);
	}

	public static List<String> misspelled(List<String> words, Collection<String> dictionary) {
		List<String> mixed = new ArrayList<>();
		for (String w : words) {
			if (dictionary.contains(w)) {
				continue;
			} else {
				mixed.add(w);
			}
		}
		return mixed;
	}

	/**
	 * This method looks for all the words in a dictionary.
	 * @param words - the "queries"
	 * @param dictionary - the data structure.
	 */
	public static void timeLookup(List<String> words, Collection<String> dictionary) {
		long startLookup = System.nanoTime();
		List<String> mixed = new ArrayList<>();
		int found = 0;
		for (String w : words) {
			if (dictionary.contains(w)) {
				found++;
			}
		}
		long endLookup = System.nanoTime();
		double fractionFound = found / (double) words.size();
		double timeSpentPerItem = (endLookup - startLookup) / ((double) words.size());
		int nsPerItem = (int) timeSpentPerItem;
		System.out.println(dictionary.getClass().getSimpleName()+": Lookup of items found="+fractionFound+" time="+nsPerItem+" ns/item");
	}
	
	public static void fillperitem(long starttime, long endtime, Collection<String> dictionary) {
		double filltimeperitem =  (endtime-starttime)/(double) dictionary.size();
		int fillperitem = (int) filltimeperitem;
		System.out.println(dictionary.getClass().getSimpleName()+": fill time per item ="+fillperitem +" ns/item");
	}
	
	public static void main(String[] args) {
		// --- Load the dictionary.
		List<String> listOfWords = loadDictionary();
		
		System.out.println("------------------------------------------------------");
		System.out.println("------How long does it take to fill each data structure?-----");
		// --- Create a bunch of data structures for testing:
		long startTreeTime = System.nanoTime();
		TreeSet<String> treeOfWords = new TreeSet<>(listOfWords);
		long endTreeTime = System.nanoTime();
		System.out.println("TreeSet: fill up takes "+ (endTreeTime-startTreeTime)/1e9 + " seconds");
		
		long starttree = System.nanoTime();
		TreeSet<String> tree = new TreeSet<>();
		for(String w : listOfWords) {
			tree.add(w);
		}
		long endtree = System.nanoTime();
		System.out.println("TreeSet calling add: fill up takes "+(endtree-starttree)/1e9+" seconds");
		
		long startHashTime = System.nanoTime();
		HashSet<String> hashOfWords = new HashSet<>(listOfWords);
		long endHashTime = System.nanoTime();
		System.out.println("HashSet: fill up takes "+ (endHashTime-startHashTime)/1e9 + " seconds");
		
		long starthash = System.nanoTime();
		HashSet<String> hash = new HashSet<>();
		for(String w : listOfWords)
			hash.add(w);
		long endhash = System.nanoTime();
		System.out.println("HashSet calling add: fill up takes "+(endhash-starthash)/1e9 + " seconds" );
		
		long startSSLSTime = System.nanoTime();
		SortedStringListSet bsl = new SortedStringListSet(listOfWords);
		long endSSLSTime = System.nanoTime();
		System.out.println("SortedStringListSet: fill up takes "+ (endSSLSTime-startSSLSTime)/1e9 + " seconds");
		
		long startCharTime = System.nanoTime();
		CharTrie trie = new CharTrie();
		for (String w : listOfWords) {
			trie.insert(w);
		}
		long endCharTime = System.nanoTime();
		System.out.println("CharTrie: fill up takes "+ (endCharTime-startCharTime)/1e9 + " seconds");
		
		long startLHTime = System.nanoTime();
		LLHash hm100k = new LLHash(100000);
		for (String w : listOfWords) {
			hm100k.add(w);
		}
		long endLHTime = System.nanoTime();
		System.out.println("LLHash: fill up takes "+ (endLHTime-startLHTime)/1e9 + " seconds");
		
		System.out.println("------------------------------------------------------");
		System.out.println("------Plot insertion time per element for each of these data structures--------");
		fillperitem(startTreeTime,endTreeTime,treeOfWords);
		fillperitem(startHashTime,endHashTime,hashOfWords);
		fillperitem(startSSLSTime,endSSLSTime,bsl);
		fillperitem(startCharTime,endCharTime,trie);
		fillperitem(startLHTime,endLHTime,hm100k);
		System.out.println("------------------------------------------------------");
		
		// --- Make sure that every word in the dictionary is in the dictionary:
		timeLookup(listOfWords, treeOfWords);
		timeLookup(listOfWords, hashOfWords);
		timeLookup(listOfWords, bsl);
		timeLookup(listOfWords, trie);
		timeLookup(listOfWords, hm100k);
		
		System.out.println("------------------------------------------------------");
		
		// --- Create a dataset of mixed hits and misses:
			
		for (int j = 0; j < 2; j++) {
			System.out.println("Warm-up, j=" + j);
			for (int i = 0; i <= 10; i++) {
				double fraction = i / 10.0;
				List<String> hitsAndMisses = createMixedDataset(listOfWords, 10000, fraction);
				timeLookup(hitsAndMisses, treeOfWords);
				timeLookup(hitsAndMisses, hashOfWords);
				timeLookup(hitsAndMisses, bsl);
				timeLookup(hitsAndMisses, trie);
				timeLookup(hitsAndMisses, hm100k);
			}
		}
		System.out.println("------------------------------------------------------");
		
		// --- linear list timing:
		// Looking up in a list is so slow, we need to sample:
		System.out.println("Start of list: ");
		timeLookup(listOfWords.subList(0, 1000), listOfWords);
		System.out.println("End of list: ");
		timeLookup(listOfWords.subList(listOfWords.size()-100, listOfWords.size()), listOfWords);
		
	
		// --- print statistics about the data structures:
		System.out.println("Count-Nodes: "+trie.countNodes());
		System.out.println("Count-Items: "+hm100k.size());

		System.out.println("Count-Collisions[100k]: "+hm100k.countCollisions());
		System.out.println("Count-Used-Buckets[100k]: "+hm100k.countUsedBuckets());
		System.out.println("Load-Factor[100k]: "+hm100k.countUsedBuckets() / 100000.0);

		
		System.out.println("log_2 of listOfWords.size(): "+listOfWords.size());
		
		System.out.println("----------Heart of Darkness---------");
		List<String> book = loadbook();
		timeLookup(book,treeOfWords);
		timeLookup(book, hashOfWords);
		timeLookup(book, bsl);
		timeLookup(book, trie);
		timeLookup(book, hm100k);
		System.out.println("The mis-spelled words are " + misspelled(book, treeOfWords));
		System.out.println("-------------------------------------");
		
		System.out.println("Done!");
	}


	private static List<String> createMixedDataset(List<String> listOfWords, int i, double fraction) {
		List<String> mixeddata = new ArrayList<>();
		int hit = (int) (i * fraction);
		int j = 0;
		while (j < i) {
			if (j < hit) {
				mixeddata.add(listOfWords.get(j));
				j++;
			} else {
				mixeddata.add(listOfWords.get(j) + "xyz");
				j++;
			}
		}
		return mixeddata;
	}
	
	

	
	
	
}