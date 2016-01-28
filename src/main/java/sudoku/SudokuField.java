package sudoku;

import org.eclipse.jdt.annotation.NonNull;

public class SudokuField {
	public static final byte EMPTY = 0;
	public static final byte MAX = 9;
	public static final byte MIN = 1;

	/**
	 *
	 * @param size
	 * @param defaultValue
	 * @return
	 */
	private static boolean[] createNewBoolArray(final byte size, final boolean defaultValue) {
		boolean[] array = new boolean[size];

		for (byte i = 0; i < size; i++) {
			array[i] = defaultValue;
		}

		return array;
	}

	/**
	 *
	 * @param size
	 * @param defaultValue
	 * @return
	 */
	private static boolean[][] createNewBoolArray(final int rowSize, final int columnSize, final boolean defaultValue) {
		boolean[][] array = new boolean[rowSize][columnSize];

		for (int row = 0; row < rowSize; row++) {
			for (int column = 0; column < columnSize; column++) {
				array[row][column] = defaultValue;
			}
		}

		return array;
	}

	/**
	 *
	 * @param sector
	 * @param pos
	 * @return
	 */
	public static byte getColumnBySectorPos(int sector, int pos) {
		if (sector < MIN || sector > MAX)
			throw new IndexOutOfBoundsException("sector has to be between 1 and 9");

		if (pos < 0 || pos > 8)
			throw new IndexOutOfBoundsException("pos has to be between 0 and 8");

		if (sector > 6) {
			sector = sector - 6;
		} else if (sector > 3) {
			sector = sector - 3;
		}
		sector = sector - 1;

		if (pos > 5) {
			pos = pos - 6;
		} else if (pos > 2) {
			pos = pos - 3;
		}

		return (byte) (pos + (sector * 3));
	}

	/**
	 * Return next cell to specified cell.
	 *
	 * @param pos
	 *            current cell
	 * @return cell next to current cell
	 */
	public static SudokuPosition getNextPosition(final @NonNull SudokuPosition pos) {
		byte row = pos.getRow();
		byte column = pos.getColumn();

		if (column < MAX - 1)
			return new SudokuPosition(row, column + 1);
		else {
			if (row < MAX - 1)
				return new SudokuPosition(row + 1, 0);
			else
				return null;
		}
	}

	/**
	 *
	 * @param sector
	 * @param pos
	 * @return
	 */
	public static byte getRowBySectorPos(final int sector, final int pos) {
		if (sector < MIN || sector > MAX)
			throw new IndexOutOfBoundsException("sector has to be between 1 and 9");

		if (pos < 0 || pos > 8)
			throw new IndexOutOfBoundsException("pos has to be between 0 and 8");

		return (byte) ((pos / 3) + (((sector - 1) / 3) * 3));
	}

	/**
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	public static byte getSector(final int row, final int column) {
		if (row < 0 || row > 8)
			throw new IndexOutOfBoundsException("row has to be between 0 and 8");

		if (column < 0 || column > 8)
			throw new IndexOutOfBoundsException("column has to be between 0 and 8");

		return (byte) (((row / 3) * 3) + (column / 3) + 1);
	}

	/**
	 * Load sudoku field from String.
	 *
	 * @param sudokuField
	 */
	public static SudokuField loadFromString(final @NonNull String sudokuField) {
		byte[][] field = new byte[MAX][MAX];
		String[] lines = sudokuField.split(System.lineSeparator());
		for (int row = 0; row < MAX; row++) {
			int rowIndex = 1 + row + (row / 3);
			for (int column = 0; column < MAX; column++) {
				// | 1 2 3 | 4 5 6 |
				int columnIndex = 3 + (column * 3) + (column / 3 * 3);
				char c = lines[rowIndex].charAt(columnIndex);

				if (c == ' ') {
					field[row][column] = EMPTY;
				} else {
					field[row][column] = (byte) Integer.parseInt(String.valueOf(c));
				}
			}
		}

		return new SudokuField(field);
	}

	private byte[][] field;

	private boolean[][][] options;

	private int remaining = 0;

	/**
	 *
	 */
	public SudokuField() {
		initialize();
	}

	/**
	 *
	 * @param field
	 */
	public SudokuField(final @NonNull byte[][] field) {
		initialize(field);
	}

	@Override
	public SudokuField clone() {
		SudokuField newField = new SudokuField(field);

		// we need also to clone the options correctly
		for (byte row = 0; row < MAX; row++) {
			for (byte column = 0; column < MAX; column++) {
				for (byte value = MIN; value <= MAX; value++) {
					if (!options[row][column][value - 1]) {
						newField.removeOption(row, column, value);
					}
				}
			}
		}

		return newField;
	}

	/**
	 * Count all options of all empty cells.
	 *
	 * @return Number of all available options of all empty cells.
	 */
	public int countOptions() {
		int num = 0;
		for (byte row = 0; row < MAX; row++) {
			for (byte column = 0; column < MAX; column++) {
				for (boolean value : options[row][column]) {
					if (value) {
						num++;
					}
				}
			}
		}

		return num;
	}

	/**
	 * Count all options of specified cell.
	 *
	 * @param pos
	 * @return Number of all available options of specified cell.
	 */
	public byte countOptions(final @NonNull SudokuPosition pos) {
		byte num = 0;
		for (boolean value : options[pos.getRow()][pos.getColumn()]) {
			if (value) {
				num++;
			}
		}

		return num;
	}

	/**
	 * Count remaining empty cells.
	 *
	 * @return Number of empty cells.
	 */
	public int countRemaining() {
		return remaining;
	}

	/**
	 * Return the next empty cell as row and column index.
	 *
	 * @param pos
	 *            position to start
	 * @return cell that is empty or null if no empty cell can be found
	 */
	public SudokuPosition getNextEmptyPosition(SudokuPosition pos) {
		// if pos == null, test start position first
		if (pos == null) {
			pos = SudokuPosition.START;
			if (field[pos.getRow()][pos.getColumn()] == EMPTY)
				return pos;
		}

		int i = 0;
		while (i < (MAX * MAX)) {
			pos = getNextPosition(pos);

			if (pos == null || field[pos.getRow()][pos.getColumn()] == EMPTY)
				return pos;

			i++;
		}

		return null;
	}

	/**
	 * Return the first value that would be valid for this cell.
	 *
	 * @param pos
	 * @return value of next option or EMPTY (0) if no options left
	 */
	public byte getNextOption(final @NonNull SudokuPosition pos) {
		byte row = pos.getRow();
		byte column = pos.getColumn();

		for (byte value = MIN; value <= MAX; value++) {
			if (options[row][column][value - 1])
				return value;
		}

		return EMPTY;
	}

	/**
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	public byte getValue(final byte row, final byte column) {
		return field[row][column];
	}

	/**
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	public byte getValue(final @NonNull SudokuPosition pos) {
		return field[pos.getRow()][pos.getColumn()];
	}

	/**
	 * Create an empty sudoku field and initialize all cells with 0.
	 */
	public void initialize() {
		initialize(null);
	}

	/**
	 * Create an empty sudoku field and initialize all cells with 0.
	 *
	 * @throws Exception
	 */
	public void initialize(final byte[][] predefinedField) {
		field = new byte[MAX][MAX];
		options = new boolean[MAX][MAX][MAX];
		remaining = MAX * MAX;

		for (byte row = 0; row < MAX; row++) {
			for (byte column = 0; column < MAX; column++) {
				// reset field
				field[row][column] = EMPTY;

				// reset all options by enabling them
				for (byte value = MIN; value <= MAX; value++) {
					options[row][column][value - 1] = true;
				}
			}
		}

		// if field should be preset, set all values
		if (predefinedField != null) {
			for (byte row = 0; row < MAX; row++) {
				for (byte column = 0; column < MAX; column++) {
					setValue(row, column, predefinedField[row][column]);
				}
			}
		}
	}

	/**
	 * Check if current field is correct and has no invalid values.
	 *
	 * @return True if field is correct and all numbers are found.
	 */
	public boolean isFinished() {
		return isValid(true);
	}

	/**
	 * Checks whether a value is an option for the cell specified by row and
	 * column.
	 *
	 * @param row
	 *            current row [0-8] to place the number
	 * @param column
	 *            current cell [0-8] to place the number
	 * @param value
	 *            the number [1-9] that should be checked
	 */
	public boolean isOption(final byte row, final byte column, final byte value) {
		return options[row][column][value - 1];
	}

	/**
	 * Check if current field is correct and has no invalid values.
	 *
	 * @return True if field is correct.
	 */
	public boolean isValid() {
		return isValid(false);
	}

	/**
	 * Check if current field is correct and has no invalid values.
	 *
	 * @param checkIfFinished
	 *            if set, the method will also check whether all numbers are
	 *            found.
	 * @return True if field is correct.
	 */
	private boolean isValid(final boolean checkIfFinished) {
		boolean[][] columnValues = createNewBoolArray(MAX, MAX, false);
		for (byte row = 0; row < MAX; row++) {
			boolean[] rowValues = createNewBoolArray(MAX, false);

			for (byte column = 0; column < MAX; column++) {
				byte value = field[row][column];

				if (value > EMPTY) {
					if (rowValues[value - 1])
						return false;
					else {
						rowValues[value - 1] = true;
					}

					if (columnValues[column][value - 1])
						return false;
					else {
						columnValues[column][value - 1] = true;
					}
				} else if (checkIfFinished)
					return false;
			}
		}

		return true;
	}

	/**
	 * Print Sudoku to sysout.
	 *
	 * @param sudokuField
	 */
	public void print() {
		print((byte) -1, (byte) -1);
	}

	/**
	 * Print Sudoku to sysout.
	 *
	 * @param sudokuField
	 */
	public void print(final byte highlightRow, final byte highlightColumn) {
		System.out.println(toString(highlightRow, highlightColumn));
	}

	/**
	 * Print Sudoku to sysout.
	 *
	 * @param highlightedCell
	 */
	public void print(final SudokuPosition highlightedCell) {
		print(highlightedCell.getRow(), highlightedCell.getColumn());
	}

	/**
	 * Remove an option from the cell specified by row and column.
	 *
	 * @param row
	 *            current row [0-8] to place the number
	 * @param column
	 *            current cell [0-8] to place the number
	 * @param value
	 *            the number [1-9] that should be removed as option
	 */
	public void removeOption(final byte row, final byte column, final byte value) {
		options[row][column][value - 1] = false;
	}

	/**
	 * Remove an option from the cell specified by row and column.
	 *
	 * @param row
	 *            current row [0-8] to place the number
	 * @param column
	 *            current cell [0-8] to place the number
	 * @param value
	 *            the number [1-9] that should be removed as option
	 */
	public void removeOption(final @NonNull SudokuPosition pos, final byte value) {
		removeOption(pos.getRow(), pos.getColumn(), value);
	}

	/**
	 * Place a number to the cell specified by row and column.
	 *
	 * @param row
	 *            current row [0-8] to place the number
	 * @param column
	 *            current cell [0-8] to place the number
	 * @param value
	 *            the number [1-9] that should be placed
	 */
	public void setValue(final byte row, final byte column, final byte value) {
		field[row][column] = value;
		if (value > EMPTY) {
			byte sector = getSector(row, column);
			remaining--;

			// reset all options for the current cell
			for (byte currentValue = MIN; currentValue <= MAX; currentValue++) {
				options[row][column][currentValue - 1] = false;
			}

			for (byte i = 0; i < MAX; i++) {
				// remove currentValue from currentRow
				options[i][column][value - 1] = false;

				// remove currentValue from currentColumn
				options[row][i][value - 1] = false;

				// remove currentValue from currentSector
				byte currentRow = getRowBySectorPos(sector, i);
				byte currentColumn = getColumnBySectorPos(sector, i);
				options[currentRow][currentColumn][value - 1] = false;
			}
		}
	}

	/**
	 * Place a number to the cell specified by row and column.
	 *
	 * @param row
	 *            current row [0-8] to place the number
	 * @param column
	 *            current cell [0-8] to place the number
	 * @param value
	 *            the number [1-9] that should be placed
	 */
	public void setValue(final @NonNull SudokuPosition pos, final byte value) {
		setValue(pos.getRow(), pos.getColumn(), value);
	}

	/**
	 * Print Sudoku to String.
	 */
	@Override
	public String toString() {
		return toString((byte) -1, (byte) -1);
	}

	/**
	 * Print Sudoku to String.
	 *
	 * @param highlightRow
	 * @param highlightColumn
	 */
	public String toString(final byte highlightRow, final byte highlightColumn) {
		StringBuilder sb = new StringBuilder();
		sb.append("+-----------+-----------+-----------+");
		sb.append(System.lineSeparator());
		for (byte row = 0; row < MAX; row++) {
			sb.append("| ");
			for (byte column = 0; column < MAX; column++) {
				String value = " ";
				if (field[row][column] > EMPTY) {
					value = String.valueOf(field[row][column]);
				}

				if ((row == highlightRow) && (column == highlightColumn)) {
					sb.append("*" + value + "*");
				} else {
					sb.append(" " + value + " ");
				}
				if ((column + 1) % 3 == 0) {
					sb.append(" | ");
				}
			}
			sb.append(System.lineSeparator());
			if ((row + 1) % 3 == 0) {
				sb.append("+-----------+-----------+-----------+");
				sb.append(System.lineSeparator());
			}
		}
		sb.append("Remaining: " + countRemaining() + ", Options: " + countOptions());
		sb.append(System.lineSeparator());

		return sb.toString();
	}
}
