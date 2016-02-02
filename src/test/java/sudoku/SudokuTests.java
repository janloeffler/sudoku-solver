package sudoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SudokuTests {
	private SudokuField createField() {
		final SudokuField field = new SudokuField();
		field.setValue((byte) 0, (byte) 0, (byte) 1);
		field.setValue((byte) 1, (byte) 1, (byte) 2);
		field.setValue((byte) 2, (byte) 2, (byte) 3);
		field.setValue((byte) 3, (byte) 3, (byte) 4);
		field.setValue((byte) 4, (byte) 4, (byte) 5);
		field.setValue((byte) 5, (byte) 5, (byte) 6);
		field.setValue((byte) 6, (byte) 6, (byte) 7);
		field.setValue((byte) 7, (byte) 7, (byte) 8);
		field.setValue((byte) 8, (byte) 8, (byte) 9);

		return field;
	}

	@SuppressWarnings("null")
	@Test
	public void testCloneField() {
		SudokuField field1 = new SudokuField();
		SudokuField field2 = new SudokuField(field1);

		assertEquals(field1.toString(), field2.toString());

		field1 = createField();
		field2 = new SudokuField(field1);

		assertEquals(field1.toString(), field2.toString());
	}

	@Test
	public void testGetColumnBySectorPos() {
		assertTrue(SudokuField.getColumnBySectorPos(1, 0) == 0);
		assertTrue(SudokuField.getColumnBySectorPos(1, 1) == 1);
		assertTrue(SudokuField.getColumnBySectorPos(1, 2) == 2);
		assertTrue(SudokuField.getColumnBySectorPos(1, 3) == 0);
		assertTrue(SudokuField.getColumnBySectorPos(1, 4) == 1);
		assertTrue(SudokuField.getColumnBySectorPos(1, 5) == 2);
		assertTrue(SudokuField.getColumnBySectorPos(1, 6) == 0);
		assertTrue(SudokuField.getColumnBySectorPos(1, 7) == 1);
		assertTrue(SudokuField.getColumnBySectorPos(1, 8) == 2);

		assertTrue(SudokuField.getColumnBySectorPos(3, 0) == 6);
		assertTrue(SudokuField.getColumnBySectorPos(3, 1) == 7);
		assertTrue(SudokuField.getColumnBySectorPos(3, 2) == 8);
		assertTrue(SudokuField.getColumnBySectorPos(3, 3) == 6);
		assertTrue(SudokuField.getColumnBySectorPos(3, 4) == 7);
		assertTrue(SudokuField.getColumnBySectorPos(3, 5) == 8);
		assertTrue(SudokuField.getColumnBySectorPos(3, 6) == 6);
		assertTrue(SudokuField.getColumnBySectorPos(3, 7) == 7);
		assertTrue(SudokuField.getColumnBySectorPos(3, 8) == 8);

		assertTrue(SudokuField.getColumnBySectorPos(8, 0) == 3);
		assertTrue(SudokuField.getColumnBySectorPos(8, 1) == 4);
		assertTrue(SudokuField.getColumnBySectorPos(8, 2) == 5);
		assertTrue(SudokuField.getColumnBySectorPos(8, 3) == 3);
		assertTrue(SudokuField.getColumnBySectorPos(8, 4) == 4);
		assertTrue(SudokuField.getColumnBySectorPos(8, 5) == 5);
		assertTrue(SudokuField.getColumnBySectorPos(8, 6) == 3);
		assertTrue(SudokuField.getColumnBySectorPos(8, 7) == 4);
		assertTrue(SudokuField.getColumnBySectorPos(8, 8) == 5);

		assertTrue(SudokuField.getColumnBySectorPos(1, 4) == 1);
		assertTrue(SudokuField.getColumnBySectorPos(2, 4) == 4);
		assertTrue(SudokuField.getColumnBySectorPos(3, 4) == 7);
		assertTrue(SudokuField.getColumnBySectorPos(4, 4) == 1);
		assertTrue(SudokuField.getColumnBySectorPos(5, 4) == 4);
		assertTrue(SudokuField.getColumnBySectorPos(6, 4) == 7);
		assertTrue(SudokuField.getColumnBySectorPos(7, 4) == 1);
		assertTrue(SudokuField.getColumnBySectorPos(8, 4) == 4);
		assertTrue(SudokuField.getColumnBySectorPos(9, 4) == 7);
	}

	@Test
	public void testGetNextCell() {
		assertTrue(SudokuField.getNextPosition(new SudokuPosition(0, 0)).equals(new SudokuPosition(0, 1)));
		assertTrue(SudokuField.getNextPosition(new SudokuPosition(0, 1)).equals(new SudokuPosition(0, 2)));
		assertTrue(SudokuField.getNextPosition(new SudokuPosition(1, 0)).equals(new SudokuPosition(1, 1)));
		assertTrue(SudokuField.getNextPosition(new SudokuPosition(0, 8)).equals(new SudokuPosition(1, 0)));
		assertTrue(SudokuField.getNextPosition(new SudokuPosition(8, 0)).equals(new SudokuPosition(8, 1)));
	}

	@Test
	public void testGetRowBySectorPos() {
		assertTrue(SudokuField.getRowBySectorPos(1, 0) == 0);
		assertTrue(SudokuField.getRowBySectorPos(1, 1) == 0);
		assertTrue(SudokuField.getRowBySectorPos(1, 2) == 0);
		assertTrue(SudokuField.getRowBySectorPos(1, 3) == 1);
		assertTrue(SudokuField.getRowBySectorPos(1, 4) == 1);
		assertTrue(SudokuField.getRowBySectorPos(1, 5) == 1);
		assertTrue(SudokuField.getRowBySectorPos(1, 6) == 2);
		assertTrue(SudokuField.getRowBySectorPos(1, 7) == 2);
		assertTrue(SudokuField.getRowBySectorPos(1, 8) == 2);

		assertTrue(SudokuField.getRowBySectorPos(3, 0) == 0);
		assertTrue(SudokuField.getRowBySectorPos(3, 1) == 0);
		assertTrue(SudokuField.getRowBySectorPos(3, 2) == 0);
		assertTrue(SudokuField.getRowBySectorPos(3, 3) == 1);
		assertTrue(SudokuField.getRowBySectorPos(3, 4) == 1);
		assertTrue(SudokuField.getRowBySectorPos(3, 5) == 1);
		assertTrue(SudokuField.getRowBySectorPos(3, 6) == 2);
		assertTrue(SudokuField.getRowBySectorPos(3, 7) == 2);
		assertTrue(SudokuField.getRowBySectorPos(3, 8) == 2);

		assertTrue(SudokuField.getRowBySectorPos(8, 0) == 6);
		assertTrue(SudokuField.getRowBySectorPos(8, 1) == 6);
		assertTrue(SudokuField.getRowBySectorPos(8, 2) == 6);
		assertTrue(SudokuField.getRowBySectorPos(8, 3) == 7);
		assertTrue(SudokuField.getRowBySectorPos(8, 4) == 7);
		assertTrue(SudokuField.getRowBySectorPos(8, 5) == 7);
		assertTrue(SudokuField.getRowBySectorPos(8, 6) == 8);
		assertTrue(SudokuField.getRowBySectorPos(8, 7) == 8);
		assertTrue(SudokuField.getRowBySectorPos(8, 8) == 8);

		assertTrue(SudokuField.getRowBySectorPos(1, 5) == 1);
		assertTrue(SudokuField.getRowBySectorPos(2, 5) == 1);
		assertTrue(SudokuField.getRowBySectorPos(3, 5) == 1);
		assertTrue(SudokuField.getRowBySectorPos(4, 5) == 4);
		assertTrue(SudokuField.getRowBySectorPos(5, 5) == 4);
		assertTrue(SudokuField.getRowBySectorPos(6, 5) == 4);
		assertTrue(SudokuField.getRowBySectorPos(7, 5) == 7);
		assertTrue(SudokuField.getRowBySectorPos(8, 5) == 7);
		assertTrue(SudokuField.getRowBySectorPos(9, 5) == 7);
	}

	@Test
	public void testGetSector() {
		assertTrue(SudokuField.getSector(0, 0) == 1);
		assertTrue(SudokuField.getSector(0, 1) == 1);
		assertTrue(SudokuField.getSector(0, 2) == 1);
		assertTrue(SudokuField.getSector(0, 3) == 2);
		assertTrue(SudokuField.getSector(0, 4) == 2);
		assertTrue(SudokuField.getSector(0, 5) == 2);
		assertTrue(SudokuField.getSector(0, 6) == 3);
		assertTrue(SudokuField.getSector(0, 7) == 3);
		assertTrue(SudokuField.getSector(0, 8) == 3);

		assertTrue(SudokuField.getSector(1, 0) == 1);
		assertTrue(SudokuField.getSector(2, 0) == 1);
		assertTrue(SudokuField.getSector(3, 0) == 4);
		assertTrue(SudokuField.getSector(4, 0) == 4);
		assertTrue(SudokuField.getSector(5, 0) == 4);
		assertTrue(SudokuField.getSector(6, 0) == 7);
		assertTrue(SudokuField.getSector(7, 0) == 7);
		assertTrue(SudokuField.getSector(8, 0) == 7);

		assertTrue(SudokuField.getSector(1, 1) == 1);
		assertTrue(SudokuField.getSector(2, 2) == 1);
		assertTrue(SudokuField.getSector(3, 3) == 5);
		assertTrue(SudokuField.getSector(4, 4) == 5);
		assertTrue(SudokuField.getSector(5, 5) == 5);
		assertTrue(SudokuField.getSector(6, 6) == 9);
		assertTrue(SudokuField.getSector(7, 7) == 9);
		assertTrue(SudokuField.getSector(8, 8) == 9);
	}

	@Test
	public void testLoadFieldFromByteArray() {
		SudokuField field1 = new SudokuField();
		String fieldAsText1 = field1.toString();
		SudokuField field2 = new SudokuField(field1.getField());

		assertEquals(fieldAsText1, field2.toString());

		field1 = createField();
		fieldAsText1 = field1.toString();
		field2 = new SudokuField(field1.getField());

		assertEquals(fieldAsText1, field2.toString());
	}

	@SuppressWarnings("null")
	@Test
	public void testLoadFieldFromString() {
		SudokuField field1 = new SudokuField();
		String fieldAsText1 = field1.toString();
		SudokuField field2 = SudokuField.loadFromString(fieldAsText1);

		assertEquals(fieldAsText1, field2.toString());

		field1 = createField();
		fieldAsText1 = field1.toString();
		field2 = SudokuField.loadFromString(fieldAsText1);

		assertEquals(fieldAsText1, field2.toString());
	}
}