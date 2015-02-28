import java.util.Arrays;

public class NonogramBoard {
	
	private TileStatus[][] board;
	private int size;
	
	public static enum TileStatus {
		EMPTY,
		FILLED,
		MARKED
	}
	
	public NonogramBoard(TileStatus[][] board) {
		this.board = board;
		this.size = board.length;
	}
	
	public int getSize() {
		return size;
	}
	
	public TileStatus[][] getBoard() {
		return board;
	}
	
	public int[][] getRowNumbers() {
		int[][] rowNumbers = new int[size][size / 2 + 1];
		for (int i = 0; i < size; i++) {
			Arrays.fill(rowNumbers[i], 0);
		}
		for (int i = 0; i < size; i++) {
			int numSections = 0;
			boolean previousTileWasFilled = true;
			for (int j = 0; j < size; j++) {
				if (board[i][j] == TileStatus.FILLED) {
					rowNumbers[i][numSections]++;
					previousTileWasFilled = true;
				}
				if (board[i][j] == TileStatus.EMPTY && previousTileWasFilled) {
					previousTileWasFilled = false;
					numSections++;
				}
			}
		}
		return removeZeros(rowNumbers);
	}
	
	public int[][] getColumnNumbers() {
		int[][] columnNumbers = new int[size][size / 2 + 1];
		for (int i = 0; i < size; i++) {
			int numSections = 0;
			boolean previousTileWasFilled = true;
			for (int j = 0; j < size; j++) {
				if (board[j][i] == TileStatus.FILLED) {
					columnNumbers[i][numSections]++;
					previousTileWasFilled = true;
				}
				if (board[j][i] == TileStatus.EMPTY && previousTileWasFilled) {
					previousTileWasFilled = false;
					numSections++;
				}
			}
		}
		return removeZeros(columnNumbers);
	}
	
	private static int[][] removeZeros(int[][] sideNumbers) {
		int[][] trimmedSideNumbers = new int[sideNumbers.length][];
		for (int i = 0; i < sideNumbers.length; i++) {
			int numEntries = 0;
			for (int j = 0; j < sideNumbers.length / 2 + 1; j++) {
				if (sideNumbers[i][j] != 0) {
					numEntries++;
				}
			}
			trimmedSideNumbers[i] = new int[numEntries];
			int entryNumber = 0;
			for (int j = 0; j < sideNumbers.length / 2 + 1; j++) {
				if (sideNumbers[i][j] != 0) {
					trimmedSideNumbers[i][entryNumber] = sideNumbers[i][j];
					entryNumber++;
				}
			}
		}
		return trimmedSideNumbers;
	}
	
	public static void main(String[] args) {
		NonogramBoard board = new NonogramBoard(new TileStatus[][]{{TileStatus.FILLED, TileStatus.EMPTY, TileStatus.FILLED, TileStatus.FILLED, TileStatus.FILLED},
																   {TileStatus.EMPTY, TileStatus.EMPTY, TileStatus.FILLED, TileStatus.EMPTY, TileStatus.FILLED},
																   {TileStatus.EMPTY, TileStatus.EMPTY, TileStatus.FILLED, TileStatus.EMPTY, TileStatus.EMPTY},
																   {TileStatus.EMPTY, TileStatus.EMPTY, TileStatus.EMPTY, TileStatus.EMPTY, TileStatus.EMPTY},
																   {TileStatus.FILLED, TileStatus.FILLED, TileStatus.FILLED, TileStatus.FILLED, TileStatus.FILLED}});
		for (int i = 0; i < board.getColumnNumbers().length; i++) {
			System.out.println(Arrays.toString(board.getColumnNumbers()[i]));
			System.out.println(Arrays.toString(board.getRowNumbers()[i]));
		}
	}
}
