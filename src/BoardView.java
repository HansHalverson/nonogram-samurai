import javax.swing.*;

import java.awt.*;
import java.awt.geom.Line2D;

public class BoardView extends JFrame {
	
	BoardModel board;
	Tile[][] tileGrid;
	JPanel columnNumbers, rowNumbers, boardPanel;
	JMenuBar menuBar;
	BoardController controller;
		
	public BoardView(BoardModel boardModel) {
		board = boardModel;
		tileGrid = new Tile[boardModel.getTargetBoard().length][boardModel.getTargetBoard().length];
		NonogramBoard.TileStatus[][] nonogram = boardModel.getTargetBoard();
		setTitle("Nonogram Samurai");
		BoardController controller = new BoardController(boardModel, this);
		this.controller = controller;
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		menuBar = new JMenuBar();
		JMenu modeMenu = new JMenu("Modes");
		menuBar.add(modeMenu);
		JMenuItem playMode = new JMenuItem("Play");
		playMode.getAccessibleContext().setAccessibleDescription("Enter play mode");
		playMode.addActionListener(controller.newPlayModeListener());
		modeMenu.add(playMode);
		JMenuItem createMode = new JMenuItem("Create");
		createMode.getAccessibleContext().setAccessibleDescription("Enter creative mode");
		createMode.addActionListener(controller.newCreateModeListener());
		modeMenu.add(createMode);
		JMenu solveMenu = new JMenu("Solve");
		JMenuItem solve = new JMenuItem("Solve");
		solve.addActionListener(controller.newSolveListener());
		solveMenu.add(solve);
		menuBar.add(solveMenu);
		setJMenuBar(menuBar);
		
		columnNumbers = new JPanel();
		rowNumbers = new JPanel();
		add(columnNumbers, BorderLayout.NORTH);
		add(rowNumbers, BorderLayout.WEST);
		updateNumbers();
		
		boardPanel = new JPanel();
		boardPanel.setBackground(Color.black);
		resetTiles();
		mainPanel.add(boardPanel, BorderLayout.CENTER);
		
		add(mainPanel);
		setMinimumSize(new Dimension(500, 525));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		setVisible(true);
	}
	
	public void updateNumbers() {
		columnNumbers.removeAll();
		columnNumbers.setLayout(new GridLayout(1, board.getTargetBoard().length + 1, 3, 3));
		columnNumbers.add(new JPanel());
		rowNumbers.removeAll();
		rowNumbers.setLayout(new GridLayout(board.getTargetBoard().length, 1, 3, 3));
		for (int i = 0; i < board.getTargetBoard().length; i++) {
			JPanel columnPane = new JPanel();
			columnPane.setLayout(new BoxLayout(columnPane, BoxLayout.Y_AXIS));
			JPanel rowPane = new JPanel();
			rowPane.setLayout(new BoxLayout(rowPane, BoxLayout.X_AXIS));
			for (int j = 0; j < board.getNonogram().getColumnNumbers()[i].length; j++) {
				int n = board.getNonogram().getColumnNumbers()[i][j];
				if (n != 0) {
					columnPane.add(new JLabel(String.valueOf(n)));
				}
			}
			for (int j = 0; j < board.getNonogram().getRowNumbers()[i].length; j++) {
				int n = board.getNonogram().getRowNumbers()[i][j];
				if (n != 0) {
					rowPane.add(new JLabel(String.valueOf(n)));
				}
			}
			columnNumbers.add(columnPane);
			rowNumbers.add(rowPane);
			revalidate();
		}
		
	}
	
	public void updateBoard() {
		for (int i = 0; i < board.getPlayerBoard().length; i++) {
			for (int j = 0; j < board.getPlayerBoard().length; j++) {
				Tile tile = tileGrid[i][j];
				if (tile.getTileStatus() == NonogramBoard.TileStatus.EMPTY) {
					tile.setBackground(Color.white);
				} else if (tile.getTileStatus() == NonogramBoard.TileStatus.FILLED) {
					tile.setBackground(Color.darkGray);
				} else if (tile.getTileStatus() == NonogramBoard.TileStatus.MARKED) {
					tile.setBackground(Color.white);
				}
			}
		}
	}
	
	public void updateView() {
		updateNumbers();
		updateBoard();
		if (controller.isGameOver() && controller.currentMode == BoardController.GameMode.PLAY_MODE) {
			System.out.println("Game Over: You Win!");
		}
	}
	
	public void resetTiles() {
		tileGrid = new Tile[board.getTargetBoard().length][board.getTargetBoard().length];
		boardPanel.removeAll();
		boardPanel.setLayout(new GridLayout(board.getTargetBoard().length, board.getTargetBoard().length, 3, 3));
		for (int i = 0; i < board.getTargetBoard().length; i++) {
			for (int j = 0; j < board.getTargetBoard().length; j++) {
				Tile tile = new Tile(i, j);
				tileGrid[i][j] = tile;
				tile.addMouseListener(controller.tileListener());
				tile.setBackground(Color.white);
				tile.setPreferredSize(new Dimension(10, 10));
				boardPanel.add(tile);
				tileGrid[i][j] = tile;
			}
		}
	}
	
	public class Tile extends JPanel {
		
		private int row;
		private int column;
		private NonogramBoard.TileStatus tileStatus;
		
		public Tile(int row, int column) {
			super();
			this.row = row;
			this.column = column;
			tileStatus = NonogramBoard.TileStatus.EMPTY;
		}
		
		public int getRow() {
			return row;
		}
		
		public int getColumn() {
			return column;
		}
		
		public NonogramBoard.TileStatus getTileStatus() {
			return tileStatus;
		}
		
		public void setRow(int row) {
			this.row = row;
		}
		
		public void setColumn(int column) {
			this.column = column;
		}
		
		public void setTileStatus(NonogramBoard.TileStatus tileStatus) {
			this.tileStatus = tileStatus;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (this.tileStatus == NonogramBoard.TileStatus.MARKED) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2.setColor(Color.gray);
				g2.drawLine(7, 7, this.getWidth() - 7, this.getHeight() - 7);
				g2.drawLine(7, this.getHeight() - 7, this.getWidth() - 7, 7);
			}
		}
	}
	
	public void selectBoardPopup() {
		JFrame selectBoardFrame = new JFrame("Board Selector");
		JTextField textField = new JTextField("Input board name");
		textField.addActionListener(controller.newSelectBoardListener());
		JPanel panel = new JPanel();
		panel.add(textField);
		selectBoardFrame.add(panel);
		selectBoardFrame.setMinimumSize(new Dimension(200, 60));
		selectBoardFrame.setVisible(true);
	}
	
	public void createBoardPopup() {
		JMenu menu = new JMenu("Save");
		JMenuItem save = new JMenuItem("Save");
		menu.add(save);
		menuBar.add(menu);
		save.addActionListener(controller.newSaveButtonListener());
		JFrame createBoardPopup = new JFrame("Board Creator");
		JTextField boardNameField = new JTextField("Board name here");
		JTextField boardSizeField = new JTextField("Size");
		JButton createButton  = new JButton("Create");
		createButton.addActionListener(controller.newCreateButtonListener());
		JPanel panel = new JPanel();
		panel.add(boardNameField);
		panel.add(boardSizeField);
		panel.add(createButton);
		createBoardPopup.add(panel);
		boardNameField.setPreferredSize(new Dimension(200, 25));
		boardSizeField.setPreferredSize(new Dimension(50, 25));
		//selectBoardFrame.setMinimumSize(new Dimension)
		createBoardPopup.pack();
		createBoardPopup.setVisible(true);
	}
	
	public void setBoardModel(BoardModel boardModel) {
		board = boardModel;
	}
	
	public static void main(String[] args) {
		NonogramBoard.TileStatus[][] tiles = {{NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY},
											  {NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY},
											  {NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY},
											  {NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY},
											  {NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY},
											  {NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY},
											  {NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY},
											  {NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY},
											  {NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY},
											  {NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY,NonogramBoard.TileStatus.FILLED, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY, NonogramBoard.TileStatus.EMPTY}};
		NonogramBoard nonogram = new NonogramBoard(tiles);
		new BoardView(new BoardModel (nonogram));
	}
}
