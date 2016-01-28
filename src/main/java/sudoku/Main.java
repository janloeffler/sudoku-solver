package sudoku;

public class Main {

	public static void main(String[] args) {
		// testSudoku();
		meassureSudokuPerformance(100);
	}

	/**
	 * Test the sudoku by creating a puzzle and solving it afterwards.
	 */
	private static void meassureSudokuPerformance(final int testRuns) {
		int solved = 0;
		int unsolvable = 0;

		System.out.println("START TEST: " + testRuns + " runs");
		for (int i = 0; i < testRuns; i++) {
			Sudoku sudoku = new Sudoku(20);
			sudoku.setDebugMode(false);
			if (sudoku.solve()) {
				solved++;
			} else {
				unsolvable++;
			}
			if (Math.round((i + 1) * 100 / testRuns) % 10 == 0) {
				System.out.println("   " + (i + 1) + " of " + testRuns + " finished");
			}
		}
		System.out.println("Statistics:");
		System.out.println("-> " + solved + " solved");
		System.out.println("-> " + unsolvable + " not solvable");

	}

	/**
	 * Test the sudoku by creating a puzzle and solving it afterwards.
	 */
	private static void testSudoku() {
		Sudoku sudoku = new Sudoku(20);
		sudoku.setDebugMode(true);
		sudoku.print();

		if (!sudoku.solve()) {
			System.out.println("*** NOT SOLVABLE ***");
		}
	}
}
