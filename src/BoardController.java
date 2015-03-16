import java.awt.event.*;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class BoardController {
	
	BoardModel board;
	GameMode currentMode;
	BoardView view;
	String boardName;
	
	public BoardController(BoardModel board, BoardView view) {
		this.board = board;
		this.currentMode = GameMode.PLAY_MODE;		
		this.view = view;
	}
	
	public enum GameMode {
		PLAY_MODE,
		CREATE_MODE
	}
	
	public boolean isGameOver() {
		boolean gameOver = true;
		for (int i = 0; i < board.getTargetBoard().length; i++) {
			for (int j = 0; j < board.getTargetBoard().length; j++) {
				NonogramBoard.TileStatus player = board.getPlayerBoard()[i][j];
				NonogramBoard.TileStatus target = board.getTargetBoard()[i][j];
				gameOver = gameOver && ((player == target) || 
						(player == NonogramBoard.TileStatus.MARKED && 
						target == NonogramBoard.TileStatus.EMPTY));
			}
		}
		return gameOver;
	}
	
	public TileListener tileListener() {
		return new TileListener();
	}
	
	private class TileListener implements MouseListener {
		
		public void mouseClicked(MouseEvent e) {}
		
		public void mouseEntered(MouseEvent e) {
			if (e.getButton() == 1) {
				updateTile(e);
			}
		}

		public void mousePressed(MouseEvent e) {
			updateTile(e);
		}

		public void mouseReleased(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}
		
		public void updateTile(MouseEvent e) {
			BoardView.Tile tile = (BoardView.Tile) e.getComponent();
			int row = tile.getRow();
			int column = tile.getColumn();
			if (tile.getTileStatus() == NonogramBoard.TileStatus.EMPTY) {
				if (e.isShiftDown()) {
					board.getPlayerBoard()[row][column] = NonogramBoard.TileStatus.MARKED;
					tile.setTileStatus(NonogramBoard.TileStatus.MARKED);
				} else {
					board.getPlayerBoard()[row][column] = NonogramBoard.TileStatus.FILLED;
					tile.setTileStatus(NonogramBoard.TileStatus.FILLED);
				}
			} else if (tile.getTileStatus() == NonogramBoard.TileStatus.FILLED) {
				if (e.isShiftDown()) {
					board.getPlayerBoard()[row][column] = NonogramBoard.TileStatus.MARKED;
					tile.setTileStatus(NonogramBoard.TileStatus.MARKED);
				} else {
					board.getPlayerBoard()[row][column] = NonogramBoard.TileStatus.EMPTY;
					tile.setTileStatus(NonogramBoard.TileStatus.EMPTY);
				}
			} else {
				board.getPlayerBoard()[row][column] = NonogramBoard.TileStatus.EMPTY;
				tile.setTileStatus(NonogramBoard.TileStatus.EMPTY);
			}
			view.updateView();
		}
	}
	
	public PlayModeListener newPlayModeListener() {
		return new PlayModeListener();
	}
	
	private class PlayModeListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			view.selectBoardPopup();
		}
	}
	
	public CreateModeListener newCreateModeListener() {
		return new CreateModeListener();
	}
	
	private class CreateModeListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			view.createBoardPopup();
		}
	}
	
	public SelectBoardListener newSelectBoardListener() {
		return new SelectBoardListener();
	}
	
	private class SelectBoardListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			String boardName = event.getActionCommand();
			NonogramBoard nonogramBoard = NonogramXML.getSavedNonogram(boardName);
			board = new BoardModel(nonogramBoard);
			view.setBoardModel(board);
			JTextField source = (JTextField) event.getSource();
			JFrame frame = (JFrame) source.getTopLevelAncestor();
			frame.dispose();
			currentMode = GameMode.PLAY_MODE;
			view.resetTiles();
			view.updateView();
		}
	}
	
	public CreateButtonListener newCreateButtonListener() {
		return new CreateButtonListener();
	}
	
	public class CreateButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			JButton button = (JButton) event.getSource();
			JTextField nameField = (JTextField) button.getParent().getComponent(0);
			JTextField sizeField = (JTextField) button.getParent().getComponent(1);
			boardName = nameField.getText();
			int boardSize = Integer.parseInt(sizeField.getText());
			JFrame frame = (JFrame) button.getTopLevelAncestor();
			frame.dispose();
			NonogramBoard.TileStatus[][] tileArray = new NonogramBoard.TileStatus[boardSize][boardSize];
			for (int i = 0; i < boardSize; i++) {
				Arrays.fill(tileArray[i], NonogramBoard.TileStatus.EMPTY);
			}
			NonogramBoard nonogram = new NonogramBoard(tileArray);
			currentMode = GameMode.CREATE_MODE;
			board = new BoardModel(nonogram);
			view.setBoardModel(board);
			view.resetTiles();
			view.updateView();
		}
	}
	
	public SaveButtonListener newSaveButtonListener() {
		return new SaveButtonListener();
	}
	
	private class SaveButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event){
			NonogramBoard.TileStatus[][] tiles = board.getPlayerBoard();
			NonogramBoard toSave = new NonogramBoard(tiles);
			NonogramXML.saveNonogramXML(toSave, boardName);
		}
	}
	
	public SolveListener newSolveListener() {
		return new SolveListener();
	}
	
	private class SolveListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			NonogramSolver solver = new NonogramSolver(BoardController.this);
			solver.solve();
		}
	}
}
