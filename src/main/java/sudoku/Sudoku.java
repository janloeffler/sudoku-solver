package sudoku;

import java.util.Random;

public class Sudoku {

	private boolean debugMode = true;
	private SudokuField field = null;

	/**
	 * Create an empty sudoku field.
	 */
	public Sudoku() {
		field = new SudokuField();
	}

	/**
	 * Create a sudoku field with prefilled values.
	 *
	 * @param predefinedFields
	 *            Amount of fields that should be prefilled
	 */
	public Sudoku(final int predefinedFields) {
		generate(predefinedFields);
	}

	/**
	 * Load sudoku field from String.
	 *
	 * @param sudokuField
	 */
	public Sudoku(final String sudokuField) {
		if (sudokuField != null) {
			field = SudokuField.loadFromString(sudokuField);
		}
	}

	/**
	 * Create a sudoku field with prefilled values.
	 *
	 * @param predefinedFields
	 *            Amount of fields that should be prefilled
	 */
	private void generate(final int predefinedFields) {
		field = new SudokuField();

		Random r = new Random();
		byte fieldsFilled = 0;
		int tries = 0;
		while (fieldsFilled < predefinedFields) {
			byte row = (byte) r.nextInt(SudokuField.MAX);
			byte column = (byte) r.nextInt(SudokuField.MAX);

			if (field.getValue(row, column) == SudokuField.EMPTY) {
				byte value = (byte) (r.nextInt(SudokuField.MAX) + 1);
				while (!field.isOption(row, column, value)) {
					value = (byte) (r.nextInt(SudokuField.MAX) + 1);
					tries++;

					// prevent an infinite loop for unsolvable sudokus.
					if (tries > (9 * 9 * 9))
						return;
				}

				field.setValue(row, column, value);
				fieldsFilled++;
			}
		}
	}

	/**
	 * @return the debug
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Print the Sudoku field to System.out.
	 */
	public void print() {
		field.print();
	}

	/**
	 * @param debug
	 *            the debug to set
	 */
	public void setDebugMode(final boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * Solve the current Sudoku field.
	 *
	 * @return true if solved or false if not solvable
	 */
	public boolean solve() {
		int depth = 1;
		while (field.countRemaining() > 0) {

			SudokuPosition pos = field.getNextEmptyPosition(null);
			while (pos != null) {
				byte numOptions = field.countOptions(pos);

				// check all options that are allowed with current depth
				while ((0 < numOptions) && (numOptions <= depth)) {
					byte newValue = field.getNextOption(pos);
					if (newValue == SudokuField.EMPTY) {

						if (debugMode) {
							System.out.println("no options left for " + pos);
						}

						return false;
					}

					SudokuField backupField = field.clone();
					field.setValue(pos, newValue);

					if (debugMode) {
						field.print(pos);
					}

					// call solve again -> if result = true, then return true
					// and puzzle is solved
					if (solve())
						return true;
					else {
						// else undo change
						field = backupField;
						field.removeOption(pos, newValue);

						if (debugMode) {
							System.out.println("UNDO value \"" + newValue + "\" at " + pos);
						}

						numOptions = field.countOptions(pos);
					}
				}

				// no options anymore but also not solve --> return false
				if (numOptions == 0)
					return false;

				// get next free position and quit loop if we are at the end of
				// the field
				pos = field.getNextEmptyPosition(pos);
			}

			if (depth < SudokuField.MAX) {
				depth++;
			} else {
				if (debugMode) {
					System.out.println("-> NOT SOLVABLE");
				}

				return false;
			}
		}

		if (debugMode) {
			System.out.println("*** SOLVED ***");
		}

		return true;
	}

	/**
	 * Print Sudoku field to String.
	 */
	@Override
	public String toString() {
		return field.toString();
	}
}
