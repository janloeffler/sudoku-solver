package sudoku;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.joda.time.DateTime;

public class Main {

	private static final @NonNull String TEST_PLAN_FILE = "sudoku_test_plan.txt";
	private static final int TEST_SUDOKUS = 1000;

	/**
	 * Generate n test fields / games.
	 *
	 * @param numSudokus
	 *            Amount of games to be generated.
	 * @return List of generated games.
	 */
	private static @NonNull List<Sudoku> generateTestPlan(final int numSudokus) {
		final @NonNull List<Sudoku> sudokus = new ArrayList<Sudoku>(numSudokus);

		final List<Integer> predefinedCells = new ArrayList<Integer>(6);
		predefinedCells.add(Sudoku.PREDEFINED_VERY_EASY);
		predefinedCells.add(Sudoku.PREDEFINED_EASY);
		predefinedCells.add(Sudoku.PREDEFINED_MEDIUM);
		predefinedCells.add(Sudoku.PREDEFINED_HARD);
		predefinedCells.add(Sudoku.PREDEFINED_VERY_HARD);
		predefinedCells.add(Sudoku.PREDEFINED_EXTREME);

		int percent10 = 10;
		int switchType = (int) Math.ceil((double) numSudokus / (double) predefinedCells.size());

		System.out.println("Generate test plan: " + numSudokus + " sudokus");

		for (int i = 0; i < numSudokus; i++) {
			int index = (int) Math.floor(i / switchType);
			try {
				Sudoku sudoku = new Sudoku(predefinedCells.get(index));
				sudokus.add(sudoku);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			int percent = (i + 1) * 100 / numSudokus;
			if (percent >= percent10) {
				System.out.print(".");
				percent10 += 10;
			}
		}
		System.out.println("DONE");

		return sudokus;
	}

	/**
	 * Load games from text file.
	 *
	 * @param filename
	 *            File containing fields.
	 * @return List of loaded games.
	 */
	private static @NonNull List<Sudoku> loadFromFile(@NonNull final String filename) {
		@NonNull
		List<Sudoku> sudokus = new LinkedList<Sudoku>();

		StringBuilder sb = new StringBuilder(280);
		int lineCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;

			System.out.println("Load test plan from \"" + filename + "\"");

			while ((line = br.readLine()) != null) {
				// process the line.
				if (line.startsWith("+") || line.startsWith("|")) {
					sb.append(line + System.lineSeparator());
					lineCount++;

					if (lineCount >= 13) {
						sudokus.add(new Sudoku(sb.toString()));
						lineCount = 0;
						sb = new StringBuilder(280);
					}
				}
			}
		} catch (IOException e) {
		}

		return sudokus;
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// testSudoku();
		@NonNull
		List<Sudoku> sudokus = loadFromFile(TEST_PLAN_FILE);
		if (sudokus.isEmpty()) {
			sudokus = generateTestPlan(TEST_SUDOKUS);
			saveToFile(sudokus, TEST_PLAN_FILE);
		}
		solveSudokus(sudokus);
	}

	/**
	 * Save list of games to text file.
	 *
	 * @param sudokus
	 * @param filename
	 */
	private static void saveToFile(@NonNull final List<Sudoku> sudokus, @NonNull final String filename) {

		System.out.println("Save test plan with " + sudokus.size() + " runs to \"" + filename + "\"");

		PrintWriter writer;
		try {
			writer = new PrintWriter(filename, "UTF-8");

			for (Sudoku sudoku : sudokus) {
				writer.println(sudoku.toString());
			}

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test the sudoku by creating a puzzle and solving it afterwards.
	 *
	 * @param sudokus
	 */
	private static void solveSudokus(@NonNull final List<Sudoku> sudokus) {
		int solved = 0;
		int unsolvable = 0;
		int numSudokus = sudokus.size();
		int percent10 = 10;
		DateTime startTime = DateTime.now();
		Map<Integer, DateTime> stopWatch = new HashMap<Integer, DateTime>();
		Map<Integer, Integer> gameTypes = new HashMap<Integer, Integer>();

		System.out.println("Start solving " + numSudokus + " sudokus");
		for (int i = 0; i < numSudokus; i++) {
			Sudoku sudoku = sudokus.get(i);
			int predefinedCells = sudoku.numCellsFilled();
			DateTime duration = DateTime.now();

			if (sudoku.solve()) {
				solved++;
			} else {
				unsolvable++;
			}

			if (stopWatch.containsKey(predefinedCells)) {
				stopWatch.put(predefinedCells, stopWatch.get(predefinedCells).plus(DateTime.now().minus(duration.getMillis()).getMillis()));
				gameTypes.put(predefinedCells, gameTypes.get(predefinedCells) + 1);
			} else {
				stopWatch.put(predefinedCells, DateTime.now().minus(duration.getMillis()));
				gameTypes.put(predefinedCells, 1);
			}

			int percent = (i + 1) * 100 / numSudokus;
			if (percent >= percent10) {
				System.out.print(".");
				percent10 += 10;
			}
		}
		System.out.println("DONE");

		DateTime duration = DateTime.now().minus(startTime.getMillis());
		System.out.println("Finished all " + numSudokus + " sudokus in " + (duration.getMillis() / 1000) + " sec");
		System.out.println("-> " + solved + " solved");
		System.out.println("-> " + unsolvable + " not solvable");

		for (int predefinedCells : gameTypes.keySet()) {
			System.out.println("-> " + gameTypes.get(predefinedCells) + " sudokus with " + predefinedCells + " cells: "
					+ (stopWatch.get(predefinedCells).getMillis() / gameTypes.get(predefinedCells)) + " ms per sudoku");
		}
	}

	/**
	 * Test the sudoku by creating a puzzle and solving it afterwards.
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static void testSudoku() throws Exception {
		Sudoku sudoku = new Sudoku(Sudoku.PREDEFINED_HARD);
		sudoku.setDebugMode(true);
		sudoku.print();

		if (!sudoku.solve()) {
			System.out.println("*** NOT SOLVABLE ***");
		}
	}
}
