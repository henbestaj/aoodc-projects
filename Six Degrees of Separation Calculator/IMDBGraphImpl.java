import java.io.*;
import java.util.*;
import java.util.zip.*;

public class IMDBGraphImpl implements IMDBGraph {
	// Set this to the directory path containing the IMDB files. On Linux/Mac OS,
	// this might be: "/Users/sarah/IMDB". On Windows, this might be:
	// "C:/Users/sarah/IMDB" (These are made-up examples but give a sense
	// of the required syntax).
	public static final String IMDB_DIRECTORY =
			"C:/Users/henbe/Downloads/Worcester Polytechnic Institute/B23/CS 2103/Project2.zip_expanded";
	private static final int PROGRESS_FREQUENCY = 10000;

	private static class IMDBNode implements Node {
		private final String _name;
		private final Collection<IMDBNode> _neighbors;

		public IMDBNode (String name) {
			_name = name;

			// Note: could also use an ArrayList, but LinkedList is likely slightly
			// more efficient in this program.
			_neighbors = new LinkedList<IMDBNode>();
		}

		public String getName () {
			return _name;
		}

		public Collection<IMDBNode> getNeighbors () {
			return _neighbors;
		}
	}

	private final Map<String, IMDBNode> _actorNamesToNodes = new HashMap<>();
	private final Map<String, IMDBNode> _movieNamesToNodes = new HashMap<>();

	/**
	 * Returns a name (based on the specified name) that is guaranteed to be unique
	 * within the specified map.
	 * @param name the actor or movie name.
	 * @param map the map within which to ensure uniqueness.
	 * @return a guaranteed unique name.
	 */
	private static String ensureUniqueName (String name, Map<String, IMDBNode> map) {
		String finalName = name;
		int counter = 2;
		while (map.containsKey(finalName)) {
			finalName = name + " " + counter;
			counter++;
		}
		return finalName;
	}

	/**
	 * Returns the movies in the dataset.
	 * @param name the movie name.
	 * @return the movies in the dataset.
	 */
	public Node getMovie (String name) {
		return _movieNamesToNodes.get(name);
	}

	/**
	 * Returns the actors (of any gender) in the dataset.
	 * @param name the actor name.
	 * @return the actors in the dataset.
	 */
	public Node getActor (String name) {
		return _actorNamesToNodes.get(name);
	}

	/**
	 * Loads the actor data contained in the specified file.
	 * @param filename full path to the actor data file.
	 * @param idsToTitles a map from the movie ID to the movie title.
	 */
	private void processActors (String filename, Map<String, String> idsToTitles) throws IOException {
		InputStream inputStream = new FileInputStream(filename);
		if (filename.endsWith(".gz")) {
			inputStream = new GZIPInputStream(inputStream);
		}
		final Scanner s = new Scanner(inputStream, "ISO-8859-1");

		int idx = 0;
		s.nextLine();  // skip first line
		while (s.hasNextLine()) {
			final String line = s.nextLine();
			final String[] fields = line.split("\t");
			final int NUM_REQUIRED_FIELDS = 6;
			if (fields.length >= NUM_REQUIRED_FIELDS) {
				final String name = fields[1];
				final String profession = fields[4];
				final String[] knownFor = fields[5].split(",");

				// Only worry about actors (of any gender).
				if (profession.contains("actor") || profession.contains("actress")) {
					// Show progress
					if (idx++ % PROGRESS_FREQUENCY == 0) {
						System.out.println(name);
					}

					// Give each person with the same name a unique "finalName".
					final String finalName = ensureUniqueName(name, _actorNamesToNodes);

					// Create a new node for the actor and add them to _actorNamesToNodes.
					final IMDBNode actorNode = new IMDBNode(finalName);
					_actorNamesToNodes.put(finalName, actorNode);
					
					// Set the neighbors of the new actor node appropriately.
					// Also set the actor to be a neighbor of each of the actor's movies.
					for (String movie : knownFor) {
						if (_movieNamesToNodes.get(idsToTitles.get(movie)) != null) {
							actorNode._neighbors.add(_movieNamesToNodes.get(idsToTitles.get(movie)));
							_movieNamesToNodes.get(idsToTitles.get(movie))._neighbors.add(actorNode);
						}
					}
				}
			}
		}
	}

	/**
	 * Loads the movie title data contained in the specified file.
	 * @param filename full path to the movie title file.
	 * @return a map from the movie ID to the movie title.
	 */
	private Map<String, String> processTitles (String filename) throws IOException {
		final Map<String, String> idsToTitles = new HashMap<>();

		InputStream inputStream = new FileInputStream(filename);
		if (filename.endsWith(".gz")) {
			inputStream = new GZIPInputStream(inputStream);
		}
		final Scanner s = new Scanner(inputStream, "ISO-8859-1");

		int idx = 0;
		s.nextLine();  // skip first line
		while (s.hasNextLine()) {
			final String line = s.nextLine();
			final String[] fields = line.split("\t");
			final String movieId = fields[0];
			final int NUM_REQUIRED_FIELDS = 3;
			if (fields.length >= NUM_REQUIRED_FIELDS) {
				final String type = fields[1];
				if (type.contains("movie")) {
					final String title = fields[2];
					if (idx++ % PROGRESS_FREQUENCY == 0) {
						System.out.println(title);
					}

					final String finalTitle = ensureUniqueName(title, _movieNamesToNodes);
					final IMDBNode movie = new IMDBNode(finalTitle);
					_movieNamesToNodes.put(finalTitle, movie);
					// Associate the movieId with the title
					idsToTitles.put(movieId, finalTitle);
				}
			}
		}
		
		return idsToTitles;
	}

	/**
	 * Creates a new IMDB graph by parsing the specified data files.
	 * @param actorsFilename full path to the actor data file.
	 * @param titlesFilename full path to the movie titles file.
	 */
	public IMDBGraphImpl (String actorsFilename, String titlesFilename) throws IOException {
		// Load the movies & actors from the data files.
		// First load the movie titles.
		final Map<String, String> idsToTitles = processTitles(titlesFilename);

		// Now parse the actors.
		processActors(actorsFilename, idsToTitles);
	}

	/**
	 * Returns the list of movies.
	 * @return the list of movies.
	 */
	public Collection<? extends Node> getMovies () {
		return _movieNamesToNodes.values();
	}

	/**
	 * Returns the list of actors.
	 * @return the list of actors.
	 */
	public Collection<? extends Node> getActors () {
		return _actorNamesToNodes.values();
	}

	/**
	 * Simple interactive program that asks user for two actors and then lists
	 * a shortest path (if it exists).
	 */
	public static void main (String[] args) {
		try {
			final IMDBGraph graph = new IMDBGraphImpl(IMDB_DIRECTORY + "/name.basics.tsv.gz",
			                                          IMDB_DIRECTORY + "/title.basics.tsv.gz");
			System.out.println(graph.getActors().size());
			
			final GraphSearchEngine graphSearcher = new GraphSearchEngineImpl();
			while (true) {
				final Scanner s = new Scanner(System.in);
				System.out.println("Actor 1:");
				final String actorName1 = s.nextLine().trim();
				System.out.println("Actor 2:");
				final String actorName2 = s.nextLine().trim();
				final Node node1 = graph.getActor(actorName1);
				final Node node2 = graph.getActor(actorName2);
				if (node1 != null && node2 != null) {
					List<Node> shortestPath = graphSearcher.findShortestPath(node1, node2);
					System.out.println(node1 + " " + node2);
					if (shortestPath != null) {
						for (Node node : shortestPath) {
							System.out.println(node.getName());
						}
					} else {
						System.out.println("No path");
					}
				}
			}
		} catch (IOException ioe) {
			System.out.println("Couldn't load data");
		}
	}
}
