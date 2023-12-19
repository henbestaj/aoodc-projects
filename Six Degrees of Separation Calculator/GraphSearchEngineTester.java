import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.io.*;

/**
 * Code to test an <tt>GraphSearchEngine</tt> implementation.
 */
public class GraphSearchEngineTester {
	@Test
	@Timeout(5)
	void testShortestPath1 () {
		final GraphSearchEngine searchEngine = new GraphSearchEngineImpl();
		final IMDBGraph graph;
		try {
			graph = new IMDBGraphImpl(IMDBGraphImpl.IMDB_DIRECTORY + "/testActors.tsv", IMDBGraphImpl.IMDB_DIRECTORY + "/testMovies.tsv");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			assertTrue(false);
			return;
		}
		final Node actor1 = graph.getActor("Kris");
		final Node actor2 = graph.getActor("Sandy");
		final List<Node> shortestPath = searchEngine.findShortestPath(actor1, actor2);
		assertEquals(5, shortestPath.size());
		final String[] correctNames = { "Kris", "Blah2", "Sara", "Blah3", "Sandy" };
		int idx = 0;
		for (Node node : shortestPath) {
			assertEquals(correctNames[idx++], node.getName());
		}
	}
}
