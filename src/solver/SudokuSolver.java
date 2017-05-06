package solver ;

/*
 * Program to solve Sudoku puzzle efficiently
 * Used basic row, column and block checking
 * Applying Backtracking (bruteforce) only if above technique is not successful
 * Upcoming: Randomized next move 
*/

import java.io.File ;
import java.io.FileNotFoundException ;
import java.util.HashSet ;
import java.util.Scanner ;

public class SudokuSolver {
	
	private int[][] puzzle ;				// Stores the puzzle
	private HashSet<Integer>[] rows ;		// Array of 9 hashsets for storing row elements
	private HashSet<Integer>[] cols ;		// Array of 9 hashsets for storing column elements
	private HashSet<Integer>[] blocks ;		// Array of 9 hashsets for storing block (3 X 3) elements
	private HashSet<Integer>[][] cells ;	// 9 X 9 matrix of hashsets for storing possible elements 
	private int remaining ;					// # of unfilled elements in the puzzle 
	
	SudokuSolver() {
		this.puzzle = new int[9][9] ;
		readPuzzle() ;
		remaining = countRemaining() ;
		printPuzzle() ;
		initializeSets() ;
	}

	private void readPuzzle() {
		// String filePath = "E:/Java/TempWS/SudokuSolver/src/solver/easy.txt" ;
		String filePath = "E:/Java/TempWS/SudokuSolver/src/solver/medium.txt" ;
		Scanner sc = null ;
		
		try {
			sc = new Scanner(new File(filePath) ) ;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace() ;
		}
		
		for(int row=0 ; row<9 ; row++) {
			String st = sc.nextLine() ;
			for(int col=0 ; col<9 ; col++) {
				puzzle[row][col] = (int)st.charAt(col) - 48 ;
			}
		}
	}

	private void printPuzzle() {
		System.out.println("Remaining = " + remaining) ;
		for(int row=0 ; row<9 ; row++) {
			for(int col=0 ; col<9 ; col++) {
				System.out.print(puzzle[row][col] + " ") ;
				if(col%3 == 2) {
					System.out.print(" ") ;
				}
			}
			System.out.println() ;
			if(row%3 == 2) {
				System.out.println() ;
			}
		}
	}

	public int[][] solveSudoku() {

		boolean isChanged = true ;
		for(int i=0 ; i<remaining && isChanged ; i++) {
			isChanged = false ;
			for(int row=0 ; row<9 ; row++) {
				for(int col=0 ; col<9 ; col++) {
					if(puzzle[row][col] != 0) {
						continue ;
					}
					isChanged = applyBasicTechnique(row, col) ;
				}
			}
		}
		
		if(remaining > 0) {
			applyBacktracking(0, 0) ;
			remaining = 0 ;
		}
		printPuzzle() ;
		return puzzle ;
	}
	
	// Simply find if there is one number is not present in row, col or block sets
	private boolean applyBasicTechnique(final int row, final int col) {
		boolean isFound = false ;
		
		HashSet<Integer> currSet = cells[row][col] ;
		
		currSet = removeElements(currSet, rows[row]) ;
		currSet = removeElements(currSet, cols[col]) ;
		
		int blockIndex = getBlockIndex(row, col) ;
		currSet = removeElements(currSet, blocks[blockIndex]) ;
		
		if(currSet.size() == 1) {
			int val = getValue(currSet) ;
			puzzle[row][col] = val ;
			rows[row].add(val) ;
			cols[col].add(val) ;
			blocks[blockIndex].add(val) ;
			remaining-- ;
			isFound = true ;
		}
		
		return isFound ;
	}
	
	// The brute-force method. Applying all combinations
	private boolean applyBacktracking(int row, int col) {
		
		if(col == 9) {
			col = 0 ;
			row++ ;
			if(row == 9) {					// Base condition: When reached the last cell
				return true ;
			}
		}
		
		if (puzzle[row][col] != 0)			// Value already filled
			return applyBacktracking(row, col + 1);

		// Trying all permutations on the empty cell
		for (int value = 1; value <= 9; ++value) {
			if (isValid(value, row, col)) {
				puzzle[row][col] = value;
				rows[row].add(value) ;
				cols[col].add(value) ;
				blocks[getBlockIndex(row, col)].add(value) ;
				
				if (applyBacktracking(row, col + 1)) {
					return true;
				}
				else {
					rows[row].remove(value) ;
					cols[col].remove(value) ;
					blocks[getBlockIndex(row, col)].remove(value) ;
				}
			}
		}
		
		puzzle[row][col] = 0; // reset on backtrack
		return false;
	}

	private boolean isValid(int value, int row, int col) {
		if(	rows[row].contains(value) ||
			cols[col].contains(value) ||
			blocks[getBlockIndex(row, col)].contains(value) )
		{
			return false ;
		}
		else {
			return true ;
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeSets() {
		
		rows = new HashSet[9] ;
		cols = new HashSet[9] ;
		blocks = new HashSet[9] ;
		cells = new HashSet[9][9] ;
		
		for(int i=0 ; i<9 ; i++) {
			rows[i] = new HashSet<>() ;
			cols[i] = new HashSet<>() ;
			blocks[i] = new HashSet<>() ;
		}
		
		for(int row=0 ; row<9 ; row++) {
			for(int col=0 ; col<9 ; col++) {
				if(puzzle[row][col] != 0) {
					rows[row].add(puzzle[row][col]) ;
					cols[col].add(puzzle[row][col]) ;
					
					int blockIndex = getBlockIndex(row, col) ;
					blocks[blockIndex].add(puzzle[row][col]) ;
				}
				else {
					cells[row][col] = makeSet() ;
				}
			}
		}
	}
	
	private int countRemaining() {
		int counter = 81 ;
		
		for(int i=0 ; i<9 ; i++) {
			for(int  j=0 ; j<9 ; j++) {
				if(puzzle[i][j] != 0) {
					counter-- ;
				}
			}
		}
		return counter ;
	}
	
	private HashSet<Integer> removeElements(HashSet<Integer> srcSet, HashSet<Integer> removeSet) {
		
		for(int curr: removeSet) {
			if(srcSet.contains(curr)) {
				srcSet.remove(curr) ;
			}
		}
		return srcSet ;
	}
	
	private int getValue(HashSet<Integer> currSet) {
		int value = 0 ;
		
		for(int val: currSet) {
			value = val ;
		}
		return value ;
	}
	
	private HashSet<Integer> makeSet() {
		HashSet<Integer> currSet = new HashSet<>() ;
		for(int i=1 ; i<=9 ; i++) {
			currSet.add(i) ;
		}
		return currSet ;
	}

	private int getBlockIndex(int row, int col) {
		return (3* (row/3) + col/3) ;
	}
	
	public static void main(String[] args) {
		SudokuSolver solver = new SudokuSolver() ;
		solver.solveSudoku() ;
	}
}
