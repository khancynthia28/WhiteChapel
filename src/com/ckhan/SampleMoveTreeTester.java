package com.ckhan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eareddick.whitechapel.Answer;
import org.eareddick.whitechapel.DetectiveMove.AttemptArrest;
import org.eareddick.whitechapel.DetectiveMove.SearchClues;
import org.eareddick.whitechapel.DetectiveMoveResult;
import org.eareddick.whitechapel.DetectiveMoveResult.AttemptArrestResult;
import org.eareddick.whitechapel.DetectiveMoveResult.SearchCluesResult;
import org.eareddick.whitechapel.GameBoard;
import org.eareddick.whitechapel.test.EnsureAssertions;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import com.ckhan.MyMoveTree.Vertex;

/**
 * Contains sample test cases for the MoveTree methods.
 * @author Anwar Reddick
 *
 */
public class SampleMoveTreeTester {

	/**
	 * Raises an error if assertions are not enabled.
	 */
	static {
		EnsureAssertions.ensureAssertions();
	}

	/**
	 * The game board.
	 */
	private final GameBoard gameBoard;

	/**
	 * Initializes fields.
	 */
	public SampleMoveTreeTester() {
		gameBoard = GameBoard.SINGLETON;
	}

	/**
	 * Sample test case.
	 * @throws CycleFoundException if there's a coding error or internal
	 * JGraphT error.
	 */
	public void testProcessJackMove1() throws CycleFoundException {
		MyMoveTree mt = new MyMoveTree(gameBoard, "C27");

		// From 27, Jack could have moved to ...
		// 26, 44, 79, 46, 28, 29, 48, 45, 47
		// Store these temporarily in a set.
		Set<String> destinationLabels = new HashSet<>();
		destinationLabels.add("C26");
		destinationLabels.add("C44");
		destinationLabels.add("C79");
		destinationLabels.add("C46");
		destinationLabels.add("C28");
		destinationLabels.add("C29");
		destinationLabels.add("C48");
		destinationLabels.add("C45");
		destinationLabels.add("C47");

		Set<Vertex> destinations = new HashSet<>();
		destinations.add(new Vertex("C26"));
		destinations.add(new Vertex("C44"));
		destinations.add(new Vertex("C79"));
		destinations.add(new Vertex("C46"));
		destinations.add(new Vertex("C28"));
		destinations.add(new Vertex("C29"));
		destinations.add(new Vertex("C48"));
		destinations.add(new Vertex("C45"));
		destinations.add(new Vertex("C47"));

		// Initialize our expected result.
		MyMoveTree expectedResult = new MyMoveTree(gameBoard, "C27");
		Vertex root = expectedResult.getRoot();
		// Add each destination to the expected result.
		// And add a directed edge from the root to each destination.
		for (Vertex v : destinations) {
			expectedResult.addVertex(v);
			expectedResult.addDagEdge(root, v);
		}
		// update the leaves
		expectedResult.setLeaves(destinations);

		// We're now done with building the expected result.
		int i =0 ;
		while(i < 2) {
			mt.processJackMove(null);
			printLeaves(mt.getLeaves());
			i++;
		}

		// compare mt to the expected result.
		//assert mt.equals(expectedResult);

		assert (mt.getRoot().getLabel().equals(expectedResult.getRoot().
				getLabel()));
		for (Vertex mtV : mt.getLeaves()) {
			assert destinationLabels.contains(mtV.getLabel());
		}
		
		//Process the detectives move
		List<String> searchOrder = new ArrayList<String>();
		searchOrder.add("C45");
		SearchClues searchClues = new SearchClues(new ArrayList<String>(),searchOrder);
		Map<String, Answer> clueAnswers = new HashMap<String, Answer>();
		clueAnswers.put("C45", Answer.YES);
		DetectiveMoveResult detMoveResult = 
				new SearchCluesResult(searchClues,clueAnswers);
		
		/*System.out.println("Print desc----->");
		printLeaves(mt.getDescendants(mt, new Vertex("C45")));
		System.out.println("Print desc end ----->");
		
		System.out.println("Print Vertex----->");
		printLeaves(mt.vertexSet());
		System.out.println("Print Vertex end ----->");*/
		
		mt.processDetectiveMoveResult(detMoveResult);
		
		printLeaves(mt.vertexSet());
		
		AttemptArrest arrest = new AttemptArrest(new ArrayList<String>(),"C62");
		detMoveResult = new AttemptArrestResult(arrest,true);
		
		mt.processDetectiveMoveResult(detMoveResult);
	}
	
	private void printLeaves(Set<Vertex> leaves) {
		for(Vertex leaf : leaves) {
			System.out.print(leaf.getLabel() + " : " );
			
		}
		System.out.println("--------------------->");
	}
	
	
	public static void main(String[] args) throws Exception {
		new SampleMoveTreeTester().testProcessJackMove1();
	}

}
