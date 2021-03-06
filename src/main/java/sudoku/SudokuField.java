package sudoku;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.jdt.annotation.NonNull;

public class SudokuField {
	/**
	 * Number of cells.
	 */
	public static final byte CELLS = 81;

	/**
	 * Value "0" for empty cell.
	 */
	public static final byte EMPTY = 0;

	/**
	 * Max value = 9 for a number or column or row count.
	 */
	public static final byte MAX = 9;

	/**
	 * Min value = 1 for a number.
	 */
	public static final byte MIN = 1;

	/**
	 *
	 * @param size
	 * @param defaultValue
	 * @return
	 */
	private static boolean[] createNewBoolArray(final byte size, final boolean defaultValue) {
		boolean[] array = new boolean[size];
		Arrays.fill(array, defaultValue);
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
			Arrays.fill(array[row], defaultValue);
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
			sector = sector - 7;
		} else if (sector > 3) {
			sector = sector - 4;
		} else {
			sector = sector - 1;
		}

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
	public static SudokuPosition getNextPosition(final SudokuPosition pos) {
		if (pos == null)
			return SudokuPosition.START;

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

	/**
	 * Store the field. Empty cells contain a 0 as number.
	 */
	private final byte[][] field;

	/**
	 * Store all remaining options per cell.
	 */
	private final boolean[][][] options;

	/**
	 * Store amount of cells that are still empty.
	 */
	private int remainingEmptyCells = 0;

	/**
	 * Store remaining options for each cell.
	 */
	private final byte[][] remainingOptions;

	/**
	 *
	 */
	public SudokuField() {
		field = new byte[MAX][MAX];
		remainingOptions = new byte[MAX][MAX];
		options = new boolean[MAX][MAX][MAX];
		remainingEmptyCells = CELLS;

		for (byte row = 0; row < MAX; row++) {
			Arrays.fill(field[row], EMPTY);
			Arrays.fill(remainingOptions[row], MAX);

			for (byte column = 0; column < MAX; column++) {
				Arrays.fill(options[row][column], true);
			}
		}
	}

	/**
	 * Constructor.
	 *
	 * @param predefinedField
	 */
	public SudokuField(final @NonNull byte[][] predefinedField) {
		this();

		// if field should be preset, set all values
		for (byte row = 0; row < MAX; row++) {
			for (byte column = 0; column < MAX; column++) {
				setValue(row, column, predefinedField[row][column]);
			}
		}
	}

	/**
	 * Constructor.
	 *
	 * @param fromField
	 */
	public SudokuField(final @NonNull SudokuField fromField) {
		field = fromField.field.clone();
		options = fromField.options.clone();
		remainingOptions = fromField.remainingOptions.clone();
		remainingEmptyCells = fromField.remainingEmptyCells;

		for (byte row = 0; row < MAX; row++) {
			field[row] = fromField.field[row].clone();
			remainingOptions[row] = fromField.remainingOptions[row].clone();
			options[row] = fromField.options[row].clone();
			for (byte column = 0; column < MAX; column++) {
				options[row][column] = fromField.options[row][column].clone();
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public SudokuPosition getBestEmptyPosition() {
		List<SudokuPosition> positions = new LinkedList<SudokuPosition>();
		byte minOptions = MAX + 1;

		for (byte row = 0; row < MAX; row++) {
			for (byte column = 0; column < MAX; column++) {
				if (field[row][column] == EMPTY) {
					byte optionCount = remainingOptions[row][column];

					if (optionCount == 0)
						return null;
					else if (optionCount == 1)
						return new SudokuPosition(row, column);
					else if (optionCount < minOptions) {
						minOptions = optionCount;
						positions.add(0, new SudokuPosition(row, column));
					}
				}
			}
		}

		return positions.get(0);
	}

	/**
	 * Return copy of the field.
	 *
	 * @return
	 */
	public byte[][] getField() {
		byte[][] newField = field.clone();

		for (byte row = 0; row < MAX; row++) {
			newField[row] = field[row].clone();
		}

		return newField;
	}

	/**
	 * Return the next empty cell as row and column index.
	 *
	 * @param pos
	 *            position to start
	 * @return cell that is empty or null if no empty cell can be found
	 */
	public SudokuPosition getNextEmptyPosition(final SudokuPosition pos) {
		if (remainingEmptyCells == 0)
			return null;

		// if pos == null, test start position first
		if (pos == null) {
			if (field[SudokuPosition.START.getRow()][SudokuPosition.START.getColumn()] == EMPTY)
				return SudokuPosition.START;
		}

		SudokuPosition nextEmptyPosition = pos;

		int i = 0;
		while (i < CELLS) {
			nextEmptyPosition = getNextPosition(nextEmptyPosition);

			if (nextEmptyPosition == null) {
				nextEmptyPosition = SudokuPosition.START;
			}

			if ((nextEmptyPosition != null) && (field[nextEmptyPosition.getRow()][nextEmptyPosition.getColumn()] == EMPTY))
				return nextEmptyPosition;

			i++;
		}

		return null;
	}

	/**
	 * Return the first value that would be valid for this cell.
	 *
	 * @param row
	 * @param column
	 * @return value of next option or EMPTY (0) if no options left
	 */
	public byte getNextOption(final byte row, final byte column) {
		boolean[] opt = options[row][column];
		for (byte value = 0; value < MAX; value++) {
			if (opt[value])
				return (byte) (value + 1);
		}

		return EMPTY;
	}

	/**
	 * Return the first value that would be valid for this cell.
	 *
	 * @param pos
	 * @return value of next option or EMPTY (0) if no options left
	 */
	public byte getNextOption(final @NonNull SudokuPosition pos) {
		return getNextOption(pos.getRow(), pos.getColumn());
	}

	/**
	 * Returns an empty position.
	 * 
	 * @return
	 */
	public SudokuPosition getRandomEmptyPosition() {
		if (remainingEmptyCells == 0)
			return null;

		Random r = new Random();

		byte row = (byte) r.nextInt(SudokuField.MAX);
		byte column = (byte) r.nextInt(SudokuField.MAX);

		int tries = 0;
		while (field[row][column] != SudokuField.EMPTY) {
			if (tries > 5)
				return getNextEmptyPosition(null);

			row = (byte) r.nextInt(SudokuField.MAX);
			column = (byte) r.nextInt(SudokuField.MAX);
			tries++;
		}

		return new SudokuPosition(row, column);
	}

	/**
	 * Return a random value that would be valid for this cell.
	 *
	 * @param row
	 * @param column
	 * @return value of next option or EMPTY (0) if no options left
	 */
	public byte getRandomOption(final byte row, final byte column) {
		byte max = remainingOptions[row][column];

		if (max == 0)
			return EMPTY;

		// choose random position of valid remaining options
		Random r = new Random();
		byte n = (byte) r.nextInt(max);

		boolean[] opt = options[row][column];

		// search the chosen remaining option and return it
		for (byte value = 0; value < MAX; value++) {
			if (opt[value]) {
				if (n == 0)
					return (byte) (value + 1);
				else {
					n--;
				}
			}
		}

		return EMPTY;
	}

	/**
	 * Return a random value that would be valid for this cell.
	 *
	 * @param pos
	 * @return value of next option or EMPTY (0) if no options left
	 */
	public byte getRandomOption(final @NonNull SudokuPosition pos) {
		return getRandomOption(pos.getRow(), pos.getColumn());
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
	 * Check if current field is completely empty.
	 *
	 * @return True if field is empty.
	 */
	public boolean isEmpty() {
		return remainingEmptyCells == CELLS;
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
	 * Returns amount of cells that are already filled out.
	 *
	 * @return
	 */
	public int numCellsFilled() {
		return CELLS - remainingEmptyCells;
	}

	/**
	 * Count remaining empty cells.
	 *
	 * @return Number of empty cells.
	 */
	public int numRemainingEmptyCells() {
		return remainingEmptyCells;
	}

	/**
	 * Count all options of specified cell.
	 *
	 * @param row
	 *            current row [0-8] to place the number
	 * @param column
	 *            current cell [0-8] to place the number
	 * @return Number of all available options of specified cell.
	 */
	public byte numRemainingOptions(final byte row, final byte column) {
		return remainingOptions[row][column];
	}

	/**
	 * Count all options of specified cell.
	 *
	 * @param pos
	 * @return Number of all available options of specified cell.
	 */
	public byte numRemainingOptions(final @NonNull SudokuPosition pos) {
		return remainingOptions[pos.getRow()][pos.getColumn()];
	}

	/**
	 * Count all options of all empty cells.
	 *
	 * @return Number of all available options of all empty cells.
	 */
	public int numRemainingOptionsTotal() {
		int num = 0;
		for (byte row = 0; row < MAX; row++) {
			for (byte column = 0; column < MAX; column++) {
				num += remainingOptions[row][column];
			}
		}

		return num;
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
		System.out.println(toString(highlightRow, highlightColumn, true));
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
		if (options[row][column][value - 1]) {
			options[row][column][value - 1] = false;
			remainingOptions[row][column]--;
		}
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
			remainingEmptyCells--;
			remainingOptions[row][column] = 0;

			// reset all options for the current cell
			Arrays.fill(options[row][column], false);

			byte valueIndex = (byte) (value - 1);
			for (byte i = 0; i < MAX; i++) {
				// remove currentValue from currentRow
				if (options[i][column][valueIndex]) {
					options[i][column][valueIndex] = false;
					remainingOptions[i][column]--;
				}

				// remove currentValue from currentColumn
				if (options[row][i][valueIndex]) {
					options[row][i][valueIndex] = false;
					remainingOptions[row][i]--;
				}

				// remove currentValue from currentSector
				byte currentRow = getRowBySectorPos(sector, i);
				byte currentColumn = getColumnBySectorPos(sector, i);
				if (options[currentRow][currentColumn][valueIndex]) {
					options[currentRow][currentColumn][valueIndex] = false;
					remainingOptions[currentRow][currentColumn]--;
				}
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
		return toString((byte) -1, (byte) -1, false);
	}

	/**
	 * Print Sudoku to String.
	 *
	 * @param showInfoText
	 */
	public String toString(final boolean showInfoText) {
		return toString((byte) -1, (byte) -1, showInfoText);
	}

	/**
	 * Print Sudoku to String.
	 *
	 * @param highlightRow
	 * @param highlightColumn
	 * @param showInfoText
	 */
	public String toString(final byte highlightRow, final byte highlightColumn, final boolean showInfoText) {
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
		if (showInfoText) {
			sb.append("Remaining: " + numRemainingEmptyCells() + ", Options: " + numRemainingOptionsTotal());
			sb.append(System.lineSeparator());
		}

		return sb.toString();
	}
}
