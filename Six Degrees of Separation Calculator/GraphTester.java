import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.io.*;

/**
 * Code to test a GraphSearchEngine implementation.
 * Most of the tests have longer paths that could be valid, to ensure that the shortest path is received.
 */
public class GraphTester {
	// The GraphSearchEngine implementation to test. 
	private GraphSearchEngine _searchEngine;
	
	// The IMDBGraph to test GraphSearchEngine implementation on.
	private IMDBGraph _graph;
	
	/**
	 * Creates a new GraphSearchEngineImpl and loads in the data before running each test.
	 */
	@BeforeEach
	void setup () {
		// Creates a new GraphSearchEngineImpl.
		_searchEngine = new GraphSearchEngineImpl();
		
		// Loads in the data into the IMDBGraph before running each test.
		try {
			System.out.println("Load data for test:");
			_graph = new IMDBGraphImpl(IMDBGraphImpl.IMDB_DIRECTORY + "/testActors.tsv",
					IMDBGraphImpl.IMDB_DIRECTORY + "/testMovies.tsv");
			System.out.println();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			assertTrue(false);
			return;
		}
	}
	
	/**
	 * Basic method for testing the route from one actor to another.
	 * @param actorName1 name of the starting actor
	 * @param actorName2 name of the target actor
	 * @param correctNames list of the names of the actors and movies in the correct shortest path
	 */
	private void testBasicShortestPath (String actorName1, String actorName2, List<String> correctNames) {
		// Name of the starting actor.
		final Node actor1 = _graph.getActor(actorName1);
		
		// Name of the target actor.
		final Node actor2 = _graph.getActor(actorName2);
		
		// List of the names of the actors and movies in the correct shortest path to be tested.
		final List<Node> shortestPath = _searchEngine.findShortestPath(actor1, actor2);
		
		// Test that the resulting list is null when the given list is.
		if (correctNames == null) {
			assertEquals(correctNames, shortestPath);
			return;
		}
		
		// Test that the resulting list and given list have the same length.
		assertEquals(correctNames.size(), shortestPath.size());
		
		// Test that the resulting list and given list have the same names.
		int idx = 0;
		for (Node node : shortestPath) {
			assertEquals(correctNames.get(idx++), node.getName());
		}
	}
	
	/**
	 * Test case when there is one movie between the actors.
	 */
	@Test
	@Timeout(5)
	void testPathLength3 () {
		List<String> correctNames = Arrays.asList("Actor1", "Movie1", "Actor2");
		testBasicShortestPath("Actor1", "Actor2", correctNames);
	}
	
	/**
	 * Test case when there is no path between the actors
	 */
	@Test
	@Timeout(5)
	void testNoPath () {
		List<String> correctNames = null;
		testBasicShortestPath("Actor1", "Actor3", correctNames);
	}
	
	/**
	 * Test case when the actor is finding a route to itself.
	 */
	@Test
	@Timeout(5)
	void testPathLength1 () {
		List<String> correctNames = Arrays.asList("Actor1");
		testBasicShortestPath("Actor1", "Actor1", correctNames);
	}
	
	/**
	 * Test case when there is two movies between the actors and there are two shortest routes.
	 */
	@Test
	@Timeout(5)
	void twoOptionsTestPathLength5 () {
		List<String> correctNames = Arrays.asList("Actor1", "Movie1", "Actor4", "Movie3", "Actor6");
		testBasicShortestPath("Actor1", "Actor6", correctNames);
	}
	
	/**
	 * Test case when there is three movies between the actors and there are three shortest routes.
	 */
	@Test
	@Timeout(5)
	void threeOptionsTestPathLength7 () {
		List<String> correctNames = Arrays.asList("Actor7", "Movie1", "Actor4", 
				"Movie3", "Actor6", "Movie5", "Actor9");
		testBasicShortestPath("Actor7", "Actor9", correctNames);
	}
}
