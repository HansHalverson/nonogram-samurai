import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;


public class NonogramSolver {
	
	private BoardController controller;
	private BoardView view;
	
	private NonogramBoard.TileStatus[][] currentBoard;
	private NonogramBoard.TileStatus[][] targetBoard;
	private int[][] rowNumbers;
	private int[][] colNumbers;
	
	
	public NonogramSolver(BoardController controller) {
		this.controller = controller;
		this.view = controller.view;
		targetBoard = controller.board.getTargetBoard();
		colNumbers = controller.board.getNonogram().getColumnNumbers();
		rowNumbers = controller.board.getNonogram().getRowNumbers();
	}
	
	public void solve() {
		view.resetTiles();
		view.updateView();
		controller.board.resetPlayerBoard();
		currentBoard = controller.board.getPlayerBoard();
		
		//Initialize knownTiles to all 2's, which represent unknown tiles
		int[][] knownTiles = new int[currentBoard.length][currentBoard.length];
		for (int[] line : knownTiles) {
			Arrays.fill(line, 2);
		}
		
		while(controller.isGameOver() == false) {
			for (int i = 0; i < rowNumbers.length; i++) {
				int[] knownInRow = knownTiles[i];
				ArrayList<int[]> options = generateOptions(rowNumbers[i], currentBoard.length);
				ArrayList<int[]> filtered = filterPossibilities(options, knownInRow);
				int[] condensed = condenseOptions(filtered);
				for (int j = 0; j < knownTiles.length; j++) {
					if (condensed[j] != knownTiles[i][j]) {
						knownTiles[i][j] = condensed[j];
						if (condensed[j] == 1) {
							currentBoard[i][j] = NonogramBoard.TileStatus.FILLED;
							view.tileGrid[i][j].setTileStatus(NonogramBoard.TileStatus.FILLED);
							view.updateView();
						}
					}
				}
			}
			for (int i = 0; i < colNumbers.length; i++) {
				int[] knownInCol = getColumn(i, knownTiles);
				ArrayList<int[]> options = generateOptions(colNumbers[i], currentBoard.length);
				ArrayList<int[]> filtered = filterPossibilities(options, knownInCol);
				int[] condensed = condenseOptions(filtered);
				for (int j = 0; j < knownTiles.length; j++) {
					if (condensed[j] != knownTiles[j][i]) {
						knownTiles[j][i] = condensed[j];
						if (condensed[j] == 1) {
							currentBoard[j][i] = NonogramBoard.TileStatus.FILLED;
							view.tileGrid[j][i].setTileStatus(NonogramBoard.TileStatus.FILLED);
							view.updateView();
						}
					}
				}
			}
		}
	}
	
	public static int[] getColumn(int col, int[][] grid) {
		int[] column = new int[grid.length];
		for (int i = 0; i < grid.length; i++) {
			column[i] = grid[i][col];
		}
		return column;
	}
	
	/**
	 * Takes the numbers in a row or column and generates all tile possibilities
	 * @param numbers - the numbers in a row or column (as an int[])
	 * @param length - the length of the row or column
	 * @return all the possible tile combinations in the row/column
	 */
	public static ArrayList<int[]> generateOptions(int[] numbers, int length) {
		ArrayList<int[]> options = new ArrayList<int[]>();
		int[] offsets = new int[numbers.length];
		if (numbers.length != 0) {
			Arrays.fill(offsets, 1);
			offsets[0] = 0;
		} else {
			int[] toReturn = new int[length];
			Arrays.fill(toReturn, 0);
			options.add(toReturn);
			return options;
		}
		while (true) {
			int[] option = new int[length];
			Arrays.fill(option, 0);
			int current = offsets[0];
			try {
				for (int i = 0; i < numbers.length; i++) {
					Arrays.fill(option, current, current + numbers[i], 1);
					if (i + 1 < numbers.length) {
						current = current + offsets[i + 1] + numbers[i];
					}
				}
				options.add(option);
				offsets[offsets.length - 1]++;
			} catch (ArrayIndexOutOfBoundsException e) {
				offsets = updateOffsets(offsets);
				if (offsets == null) {
					break;
				}
			}
		}
		return options;
	}
	
	/**
	 * Updates offsets when there is overflow
	 * @param offsets - a int[] of the offsets
	 * @return the new offsets
	 */
	public static int[] updateOffsets(int[] offsets) { 
		int lastOne = 0;
		for (int i = 1; i < offsets.length; i++) {
			if (offsets[i] > 1) {
				lastOne = i;
			}
		}
		if (lastOne != 0) {
			offsets[lastOne - 1]++;
			Arrays.fill(offsets, lastOne, offsets.length, 1);
			return offsets;
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param possiblities - all current possibilities in the row/column
	 * @param current - the known tiles positions in the row/column
	 * @return all possibilities that fit with the known tiles
	 */
	public static ArrayList<int[]> filterPossibilities(ArrayList<int[]> possiblities, int[] known) {
		ArrayList<int[]> filtered = new ArrayList<int[]>();
		for (int[] possibility : possiblities) {
			boolean arrayFits = true;
			for (int i = 0; i < possibility.length; i++) {
				if ((known[i] == 1 && possibility[i] != 1) ||
					(known[i] == 0 && possibility[i] != 0)) {
					arrayFits = false;
				}
			}
			if (arrayFits) {
				filtered.add(possibility);
			}
		}
		return filtered;
	}
	
	/**
	 * Condenses all possibilities into the known tiles
	 * @param possibilities - all possible tile combinations
	 * @return the known tiles in the line
	 */
	public static int[] condenseOptions(ArrayList<int[]> possibilities) {
		if (possibilities.isEmpty()) {
			return null;
		}
		int length = possibilities.get(0).length;
		int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			int type = possibilities.get(0)[i];
			for (int[] possibility : possibilities) {
				if (type != possibility[i]) {
					type = 2;
				}
			}
			result[i] = type;
		}
		return result;
	}
}
