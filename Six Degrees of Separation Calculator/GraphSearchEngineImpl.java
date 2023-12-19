import java.util.*;

/**
 * Implements the GraphSearchEngine interface.
 */
public class GraphSearchEngineImpl implements GraphSearchEngine {
	public GraphSearchEngineImpl () {
	}
	
	/**
	 * Create a list of the nodes in the shortest path from the starting node to the target node. 
	 * @param start the node to start the search from.
	 * @param target the node to search for.
	 * @return the shortest list of nodes from the start to the target in the graph.
	 */
	public ArrayList<Node> findShortestPath (Node start, Node target) {
		// A map with nodes as keys and the parent nodes to the keys as values.
		HashMap<Node, Node> parent = new HashMap<Node, Node>();
		
		// A list of the visited nodes.
		ArrayList<Node> visited = new ArrayList<Node>();
		
		// The list of nodes in the shortest path from the starting node to the target node (or null if no path). 
		ArrayList<Node> answer = new ArrayList<Node>();
		answer = null;
		
		// A list that keeps track of which node to check next.
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(start);
		
		while (queue.size() != 0) {
			// Pull the first value of the queue out.
			Node current = queue.poll();
			
			// Test if the current node is the target and trace its path back to the start if so. 
			if (current.equals(target)) {
				answer = tracePath(parent, start, target);
				break;
			}
			
			// If the current node has not been visited:
			// add its neighbors to the queue,
			// add its neighbors to the parent map,
			// and add it to visited.
			else if (!visited.contains(current)) {
				for (Node node : current.getNeighbors()) {
					if (!parent.containsKey(node)) {
						parent.put(node, current);
					}
					queue.add(node);
				};
				visited.add(current);
			}
		}
		
		// Return the final list of nodes.
		return answer;
	}
	
	/**
	 * Create a list of the path traced from the starting node to the target node in a map.
	 * @param parent a map with nodes as keys and parent nodes as values.  
	 * @param start the node to start the search from.
	 * @param target the node to search for.
	 * @return the list of nodes from the start to the target in the given map.
	 */
	private ArrayList<Node> tracePath (HashMap<Node, Node> parent, Node start, Node target) {
		// The list of nodes which traced the path from the starting node to the target node in parent.
		ArrayList<Node> answer = new ArrayList<Node>();
		answer.add(target);
		
		// Back traces the from each value to the next and adds the values to the front of answer
		// until the value is the starting value.
		while (answer.get(0) != start) {
			answer.add(0, parent.get(answer.get(0)));
		}
		
		// Return the final list of nodes.
		return answer;
	}
}
