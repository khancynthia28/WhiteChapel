package com.ckhan;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eareddick.whitechapel.Answer;
import org.eareddick.whitechapel.DetectiveMoveResult;
import org.eareddick.whitechapel.DetectiveMoveResult.AttemptArrestResult;
import org.eareddick.whitechapel.DetectiveMoveResult.SearchCluesResult;
import org.eareddick.whitechapel.Detectives;
import org.eareddick.whitechapel.Edge;
import org.eareddick.whitechapel.GameBoard;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.ckhan.MyMoveTree.Vertex;

public class MyMoveTree extends DirectedAcyclicGraph<Vertex, Edge> {

	/**
	 * The vertex holding the "kill spot" label.
	 */
	protected final Vertex root;

	/**
	 * Vertices containing the position labels on which Jack might be at the
	 * current moment.
	 */
	protected HashSet<Vertex> leaves = new HashSet<>();

	/**
	 * The GameBoard.
	 */
	protected final GameBoard gameboard;

	/**
	 * Initializes fields. Initializes the root vertex with rootLabel.
	 * @param gameboard The GameBoard
	 * @param rootLabel The label of the kill spot position.
	 */
	public MyMoveTree(final GameBoard gameboard, final String rootLabel) {
		super(Edge.class);
		assert gameboard != null;
		assert rootLabel != null;
		this.gameboard = gameboard;
		root = new Vertex(rootLabel);
		addVertex(root);
		leaves.add(root);
	}

	/**
	 * Returns the leaves.
	 * @return the leaves.
	 */
	public Set<Vertex> getLeaves() {
		return leaves;
	}
	
	public void setLeaves(Set<Vertex> leaves) {
		this.leaves = (HashSet<Vertex>) leaves;
	}

	/**
	 * Returns the root vertex.
	 * @return the root vertex.
	 */
	public Vertex getRoot() {
		return root;
	}

	/**
	 * Updates this MoveTree to reflect the new positions that Jack could have
	 * moved to, when doing a single, simple move. Ignore positions that Jack
	 * cannot move through due to not being able to move through detectives.
	 * <p>For each leaf, find the adjacent circle vertices that Jack could have
	 * moved to, and create edges from the old leaf to new MoveTree vertices.
	 * Then update the {@link #leaves} field accordingly.
	 * </p>
	 * @param detectives The position of the detectives.
	 */
	public void processJackMove(Detectives detectives) {
		Set<Vertex> tempLeaves = new HashSet<Vertex>();
		for(Vertex currLeaf : getLeaves()) {
			Set<Edge> edges = this.gameboard.getCircleGraph().edgesOf(currLeaf.getLabel());
			for(Edge edge : edges) {
				String newLeafLabel = findConnectedVertex(edge, currLeaf.getLabel());
				if(newLeafLabel != null) {
					Vertex newLeaf = new Vertex(newLeafLabel);
					//if(!this.containsVertex(newLeaf)) {
						addVertex(newLeaf);
						addEdge(currLeaf, newLeaf);
						tempLeaves.add(newLeaf);
					//}
				}
			}
		}
		this.setLeaves(tempLeaves);
	}
	
	
	private String findConnectedVertex(Edge edge , String sourceVertex) {
		String tarVertex = null;
		try {
			tarVertex = edge.getTargetVertex(sourceVertex);
		} catch(Exception ex) {
			
		}
		return tarVertex;
	}

	/**
	 * Updates this MoveTree by removing paths that Jack could not have
	 * possibly taken based on result.
	 *
	 * @param result The result of a detective's questioning.
	 */
	public void processDetectiveMoveResult(DetectiveMoveResult result) {
		if(result instanceof SearchCluesResult) {
			Map<String,Answer> clueAnswers =
					((SearchCluesResult) result).getClueAnswers();
			Iterator keyItr = clueAnswers.keySet().iterator();
			while(keyItr.hasNext()) {
				String vertexName = (String) keyItr.next();
				Answer ans = clueAnswers.get(vertexName);
				if(ans.equals(Answer.YES)) {
					Vertex removeLeaf = new Vertex(vertexName);
					if(this.containsVertex(removeLeaf)) {
						removeLeaf(root,removeLeaf.getLabel(),new HashSet<Vertex>());
					}
				}
			}
		} else if(result instanceof AttemptArrestResult) {
			if(((AttemptArrestResult) result).getAnswer()) {
				Vertex removeLeaf = new Vertex(((AttemptArrestResult) result).getMove().getTargetCircle());
				if(this.containsVertex(removeLeaf)) {
					removeLeaf(root,removeLeaf.getLabel(),new HashSet<Vertex>());
				}
			}
		}
	}
	
	private void removeLeaf(Vertex currentRoot,String removeVertex,Set<Vertex> removeSet) {
		if(currentRoot.getLabel().equals(removeVertex) && !currentRoot.equals(root)) {
			removeHelper(currentRoot);
			removeSet.add(currentRoot);
			return;
		}
		if(getDescendants(this, currentRoot) == null) {
			return;
		}
		Iterator<Vertex> itr = getDescendants(this, currentRoot).iterator();
		while(itr.hasNext()) {
			Vertex nextVertex =  itr.next();
			removeLeaf(nextVertex, removeVertex, removeSet);
		}
		for(Vertex v : removeSet) {
			removeVertex(v);
		}
	}
	
	private void removeHelper(Vertex currentRoot) {
		if(getDescendants(this, currentRoot) == null) {
			getLeaves().remove(currentRoot);
			return;
		}
		Iterator<Vertex> itr = getDescendants(this, currentRoot).iterator();
		while(itr.hasNext()) {
			Vertex nextVertex = (Vertex) itr.next();
			removeHelper(nextVertex);
		}
	}
	

	/**
	 * Models a vertex of a MoveTree.
	 *
	 * @author Anwar Reddick
	 *
	 */
	public static class Vertex {

		/**
		 * The position label that matches a GameBoard circle vertex.
		 */
		protected final String label;

		/**
		 * Whether the detectives found clues at the position represented by
		 * this vertex.
		 */
		protected boolean foundClues;

		/**
		 * Initializes fields.
		 *
		 * @param label The position label that matches a GameBoard circle
		 * vertex.
		 */
		public Vertex(final String label) {
			this.label = label;
		}

		/**
		 * Returns the label.
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * Returns whether the detectives found clues at the position
		 * represented by this vertex.
		 * @return whether the detectives found clues at the position
		 * represented by this vertex.
		 */
		public boolean isFoundClues() {
			return foundClues;
		}

		/**
		 * Sets whether the detectives found clues at the position
		 * represented by this vertex.
		 * @param foundClues whether the detectives found clues at the position
		 * represented by this vertex.
		 */
		public void setFoundClues(final boolean foundClues) {
			this.foundClues = foundClues;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Vertex other = (Vertex) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			return true;
		}

	}

}
