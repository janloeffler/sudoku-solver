package sudoku;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.joda.time.DateTime;

public class Main {

	private static final @NonNull String TEST_PLAN_FILE = "sudoku_test_plan.txt";

	private static @NonNull List<Sudoku> generateTestPlan(final int games) {
		@NonNull
		List<Sudoku> sudokuFields = new ArrayList<Sudoku>(games);

		System.out.println("Generate test plan: " + games + " runs");

		for (int i = 0; i < games; i++) {
			Sudoku sudoku = new Sudoku(20);
			sudokuFields.add(sudoku);

			if (Math.round((i + 1) * 100 / games) % 10 == 0) {
				System.out.println("   " + (i + 1) + " of " + games + " finished");
			}
		}

		return sudokuFields;
	}

	private static @NonNull List<Sudoku> loadFromFile(@NonNull final String filename) {
		@NonNull
		List<Sudoku> sudokuFields = new LinkedList<Sudoku>();

		StringBuilder sb = new StringBuilder(280);
		int lineCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;

			System.out.println("Load test plan from \"" + filename + "\"");

			while ((line = br.readLine()) != null) {
				// process the line.
				if (!line.trim().isEmpty()) {
					sb.append(line + System.lineSeparator());
					lineCount++;

					if (lineCount == 14) {
						sudokuFields.add(new Sudoku(sb.toString()));
						lineCount = 0;
						sb = new StringBuilder(280);
					}
				}
			}
		} catch (IOException e) {
		}

		return sudokuFields;
	}

	public static void main(String[] args) {
		// testSudoku();
		@NonNull
		List<Sudoku> sudokuFields = loadFromFile(TEST_PLAN_FILE);
		if (sudokuFields.isEmpty()) {
			sudokuFields = generateTestPlan(100);
			saveToFile(sudokuFields, TEST_PLAN_FILE);
		}
		solveSudokus(sudokuFields);
	}

	private static void saveToFile(@NonNull final List<Sudoku> sudokuFields, @NonNull final String filename) {

		System.out.println("Save test plan with " + sudokuFields.size() + " runs to \"" + filename + "\"");

		PrintWriter writer;
		try {
			writer = new PrintWriter(filename, "UTF-8");

			for (Sudoku sudoku : sudokuFields) {
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
	 */
	private static void solveSudokus(@NonNull final List<Sudoku> sudokuFields) {
		int solved = 0;
		int unsolvable = 0;
		int fieldCount = sudokuFields.size();
		DateTime startTime = DateTime.now();

		System.out.println("Start solving " + fieldCount + " games");
		for (int i = 0; i < fieldCount; i++) {
			Sudoku sudoku = sudokuFields.get(i);
			sudoku.setDebugMode(false);
			if (sudoku.solve()) {
				solved++;
			} else {
				unsolvable++;
			}
			if (Math.round((i + 1) * 100 / fieldCount) % 10 == 0) {
				System.out.println("   " + (i + 1) + " of " + fieldCount + " finished");
			}
		}

		DateTime duration = DateTime.now().minus(startTime.getMillis());
		System.out.println("Finished all " + fieldCount + " games in " + (duration.getMillis() / 1000) + " sec");
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
