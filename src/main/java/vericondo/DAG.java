package vericondo;

import java.util.*;


/**
This class implements directed acylcic graphs.
It is necessary for computing the winner of Ranked Pairs.
*/
public class DAG {

    // nodes in the graph are integers
    private ArrayList<Integer> nodes;

    // edges in the graph are lists of integers for a given node
    private ArrayList<ArrayList<Integer>> edges;

    public DAG() {
        this.nodes = new ArrayList<Integer>();
        this.edges = new ArrayList<ArrayList<Integer>>();
    }

    // add a node to the graph
    public void addNode(int node) {
        // add the node if it is not already in the graph
        if (!nodes.contains(node)) {
            nodes.add(node);
            // the node starts out with no edges
            ArrayList<Integer> neighbours = new ArrayList<Integer>();
            edges.add(neighbours);
        }
    }

    // add an edge to the graph
    public void addEdge(int src, int dst) {
        // only add if both nodes are in the graph
        // and the edge does not form a cycle
        if (nodes.contains(src) && nodes.contains(dst)) {
            if (!hasPath(dst, src)) {
                edges.get(src).add(dst);
            }
        }
    }

    // determine if there is a path from one node in the graph to another
    public boolean hasPath(int src, int dst) {

        // track visited nodes
        HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
        for (int i = 0; i < nodes.size(); i++) {
            int node = nodes.get(i);
            visited.put(node, false);
        }

        // maintain a queue to track nodes to explore from
        ArrayList<Integer> queue = new ArrayList<Integer>();

        // mark starting node as visited
        visited.put(src, true);

        // start exploring from the starting node
        queue.add(src);

        while (queue.size() != 0) {

            // grab a node to explore from
            int node = queue.remove(queue.size() - 1);

            // get the neighbours of the node to explore from
            ArrayList<Integer> node_edges = edges.get(src);
            for (int i = 0; i < node_edges.size(); i++) {
                // if we have found the target then we stop
                int next = node_edges.get(i);
                if (next == dst) {
                    return true;
                }
                // if we have not explored from the neighbours
                // then mark them as visited and explore from them too
                if (!visited.get(next)) {
                    visited.put(next, true);
                    queue.add(next);
                }
            }

        }

        // we did not find a path during the BFS
        return false;

    }

    // compute a topological sort of the DAG
    public ArrayList<Integer> topologicalSort() {

        // the stack maintains the topological ordering of nodes
        Stack<Integer> stack = new Stack<Integer>();

        // track visited nodes
        HashMap<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
        for (int i = 0; i < nodes.size(); i++) {
            int node = nodes.get(i);
            visited.put(node, false);
        }

        // compute a topogical sort from every node
        for (int i = 0; i < nodes.size(); i++) {
            int node = nodes.get(i);
            if (!visited.get(node)) {
                topologicalSort(node, visited, stack);
            }
        }

        // convert the stack to a list
        ArrayList<Integer> sorted = new ArrayList<Integer>();
        while (!stack.empty()) {
            sorted.add(stack.pop());
        }

        return sorted;

    }

    // helper function to topological sort from a given node
    private void topologicalSort(int node, HashMap<Integer, Boolean> visited,
        Stack<Integer> stack) {

        // mark the current node as visited
        visited.put(node, true);

        // recursively topological sort all neighbours to the current node
        ArrayList<Integer> node_edges = edges.get(node);
        for (int i = 0; i < node_edges.size(); i++) {
            int next = node_edges.get(i);
            if (!visited.get(next)) {
                topologicalSort(next, visited, stack);
            }
        }

        // add the current node to the stack once its neighbours have been visited
        stack.push(node);

    }

}
