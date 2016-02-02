package sudoku;

import java.security.InvalidParameterException;

import org.eclipse.jdt.annotation.NonNull;

public class Sudoku {

	/**
	 * Amount of predefined cells for an easy game.
	 */
	public static final int PREDEFINED_EASY = 38;

	/**
	 * Amount of predefined cells for an extreme game.
	 */
	public static final int PREDEFINED_EXTREME = 20;

	/**
	 * Amount of predefined cells for a hard game.
	 */
	public static final int PREDEFINED_HARD = 28;

	/**
	 * Amount of predefined cells for a medium game.
	 */
	public static final int PREDEFINED_MEDIUM = 30;

	/**
	 * Amount of predefined cells for a quick game.
	 */
	public static final int PREDEFINED_VERY_EASY = 48;

	/**
	 * Amount of predefined cells for a very hard game.
	 */
	public static final int PREDEFINED_VERY_HARD = 26;

	private boolean debugMode = false;
	private SudokuField field = null;

	/**
	 * Create an empty sudoku field.
	 */
	public Sudoku() {
		field = new SudokuField();
	}

	/**
	 * Load sudoku field from byte[][].
	 *
	 * @param predefinedField
	 */
	public Sudoku(final byte[][] predefinedField) {
		if (predefinedField != null) {
			field = new SudokuField(predefinedField);
		} else {
			field = new SudokuField();
		}
	}

	/**
	 * Create a sudoku field with prefilled values.
	 *
	 * @param predefinedCells
	 *            Amount of fields that should be prefilled
	 * @throws Exception
	 */
	public Sudoku(final int predefinedCells) throws Exception {
		this();
		if (generate(predefinedCells) == null)
			throw new Exception("Cannot generate a valid field");
	}

	/**
	 * Load sudoku field from String.
	 *
	 * @param predefinedField
	 */
	public Sudoku(final String predefinedField) {
		if (predefinedField != null) {
			field = SudokuField.loadFromString(predefinedField);
		}
	}

	/**
	 * Create a sudoku field with prefilled values.
	 *
	 * @param predefinedCells
	 *            Amount of fields that should be prefilled
	 */
	private SudokuField generate(final int predefinedCells) {
		if (predefinedCells > 60)
			throw new InvalidParameterException("predefinedCells must not be greater than 60");

		for (byte row = 0; row < SudokuField.MAX; row++) {
			for (byte column = 0; column < SudokuField.MAX; column++) {
				while (field.getValue(row, column) == SudokuField.EMPTY) {
					byte value = field.getRandomOption(row, column);
					if (value == SudokuField.EMPTY)
						return null;

					@SuppressWarnings("null")
					SudokuField backupField = new SudokuField(field);
					field.setValue(row, column, value);

					// call solve again -> if result = true, then return true
					// and field is completely filled out
					field = generate(predefinedCells);
					if (field != null)
						return field;
					else {
						// else undo change
						field = backupField;
						field.removeOption(row, column, value);
					}
				}
			}
		}

		// field should be completely filled now. Now clear cell until only
		// amount of requested predefined cells are set
		SudokuField preparedField = new SudokuField();
		while (preparedField.numCellsFilled() < predefinedCells) {
			SudokuPosition pos = preparedField.getRandomEmptyPosition();

			if (pos == null) {
				break;
			} else {
				preparedField.setValue(pos, field.getValue(pos));
			}
		}

		return preparedField;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Returns amount of cells that are already filled out.
	 *
	 * @return
	 */
	public int numCellsFilled() {
		return field.numCellsFilled();
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
		// as long as there are still empty cells
		while (field.numRemainingEmptyCells() > 0) {

			// check empty cells with least options first
			SudokuPosition pos = field.getBestEmptyPosition();

			// if no position found this path is wrong
			if (pos == null)
				return false;
			else {
				byte newValue = field.getNextOption(pos);
				if (newValue == SudokuField.EMPTY) {

					if (debugMode) {
						System.out.println("no options left for " + pos);
					}

					return false;
				}

				@SuppressWarnings("null")
				SudokuField backupField = new SudokuField(field);
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
				}
			}
		}

		if (debugMode) {
			System.out.println("*** SOLVED ***");
		}

		return true;
	}

	/**
	 * Solve the current Sudoku field.
	 *
	 * @return true if solved or false if not solvable
	 */
	public boolean solveA() {
		int depth = 1;

		// as long as there are still empty cells
		while (field.numRemainingEmptyCells() > 0) {

			SudokuPosition pos = field.getNextEmptyPosition(null);
			while (pos != null) {
				byte numOptions = field.numRemainingOptions(pos);

				// check all options that are allowed with current depth
				while ((0 < numOptions) && (numOptions <= depth)) {
					byte newValue = field.getNextOption(pos);
					if (newValue == SudokuField.EMPTY) {

						if (debugMode) {
							System.out.println("no options left for " + pos);
						}

						return false;
					}

					@SuppressWarnings("null")
					@NonNull
					SudokuField backupField = new SudokuField(field);
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

						numOptions = field.numRemainingOptions(pos);
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
