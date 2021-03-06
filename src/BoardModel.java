import java.util.Arrays;

public class BoardModel {
	
	private NonogramBoard.TileStatus[][] playerBoard;
	private NonogramBoard.TileStatus[][] targetBoard;
	private NonogramBoard nonogram;
	
	public BoardModel(NonogramBoard nonogram) {
		this.nonogram = nonogram;
		targetBoard = nonogram.getBoard();
		playerBoard = new NonogramBoard.TileStatus[nonogram.getSize()][nonogram.getSize()];
		resetPlayerBoard();
	}
	
	public NonogramBoard.TileStatus[][] getPlayerBoard() {
		return playerBoard;
	}
	
	public NonogramBoard.TileStatus[][] getTargetBoard() {
		return targetBoard;
	}
	
	public void resetPlayerBoard() {
		for (int i = 0; i < nonogram.getSize(); i++) {
			Arrays.fill(playerBoard[i], NonogramBoard.TileStatus.EMPTY);
		}
	}
	
	public NonogramBoard getNonogram() {
		return nonogram;
	}
}
