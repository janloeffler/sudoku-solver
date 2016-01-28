package sudoku;

public final class SudokuPosition {
	/**
	 * Sudoku start position on top-left (0, 0).
	 */
	public static final SudokuPosition START = new SudokuPosition(0, 0);
	private final byte column;
	private final byte row;

	/**
	 * Create a new position.
	 *
	 * @param row
	 *            index of row from 0 to 8.
	 * @param column
	 *            index of column from 0 to 8.
	 */
	public SudokuPosition(final int row, final int column) {
		if (row < 0 || row > 8)
			throw new IndexOutOfBoundsException("row has to be between 0 and 8");

		if (column < 0 || column > 8)
			throw new IndexOutOfBoundsException("column has to be between 0 and 8");

		this.row = (byte) row;
		this.column = (byte) column;
	}

	@Override
	public SudokuPosition clone() {
		return new SudokuPosition(row, column);
	}

	public boolean equals(final SudokuPosition pos) {
		return (pos != null) && (pos.getRow() == row) && (pos.getColumn() == column);
	}

	public byte getColumn() {
		return column;
	}

	public SudokuPosition getNextPosition() {
		return SudokuField.getNextPosition(this);
	}

	public byte getRow() {
		return row;
	}

	@Override
	public String toString() {
		return "row " + (row + 1) + " column " + (column + 1);
	}
}
