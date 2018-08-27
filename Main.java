import java.awt.*;
import java.awt.event.*;


public class Main extends Frame implements WindowListener, MineSweeperGUI {

	private MineSweeper ms;
	private Button[][] tileTable;

	private static final Font f = new Font("serif", Font.BOLD,16);
	private final ResultDialog resultDialog = new ResultDialog(this, "Result", this);
	private final StartMenu startMenu;
	private MineSweeper hard, normal, easy;
	private int mode;
	private Panel Time;
	private Panel Mine;
	private int useWindowWidth, useWindowHeight;
	private Label time, reset, bomb;

	public void setMode(int mode) {
		this.mode = mode;
	}

	public Main() {
		super("MineSweeper"); // Frameクラスのコンストラクタ 引数はタイトル
		time = new Label();
		reset = new Label();
		bomb = new Label();
		this.bomb.setFont(new Font("Arial", Font.BOLD, 60));
		this.bomb.setAlignment(Label.RIGHT);
		this.setResizable(false);
		this.Time = new Panel();
		this.Mine = new Panel();
		this.useWindowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		this.useWindowHeight = Toolkit.getDefaultToolkit().getScreenSize().height - 150;
		this.hard = new MineSweeper(16, 30, 99, useWindowWidth, useWindowHeight);
		this.normal = new MineSweeper(16, 16, 40, useWindowWidth, useWindowHeight);
		this.easy = new MineSweeper(9, 9, 10, useWindowWidth, useWindowHeight);
		this.startMenu = new StartMenu("Start Menu", this);
	}

	public static void main(String[] args) {
		new Main();
	}

	public void runInit() {
		switch (this.mode) {
			case 1:
				this.ms = this.easy;
				break;
			case 2:
				this.ms = this.normal;
				break;
			case 3:
				this.ms = this.hard;
				break;
		}
		init();
	}

	private void init() {
		this.tileTable = new Button[ms.getHeight()][ms.getWidth()];
		this.addWindowListener(this); // ウインドウの登録
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(grid);
		this.Time.setPreferredSize(new Dimension(ms.getWidth() * ms.getButtonSize(), 100));
		this.Time.setSize(ms.getWidth() * ms.getButtonSize(), 100);
		this.Time.setBackground(Color.RED);
		this.Mine.setLayout(new GridLayout(ms.getHeight(), ms.getWidth()));
		for (int i = 0; i < ms.getHeight(); i++) {
			for (int j = 0; j < ms.getWidth(); j++) {
				Button tile = new Button();
				tile.setBackground(Color.LIGHT_GRAY); // カラーの設定
				tile.setFont(f);
				tile.addMouseListener(new MouseEventHandler(ms, this, j, i));
				tileTable[i][j] = tile;
				this.Mine.add(tile);
			}
		}
		this.Mine.setPreferredSize(new Dimension(ms.getWidth() * ms.getButtonSize(), ms.getHeight() * ms.getButtonSize()));
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		grid.setConstraints(this.Time, c);
		this.add(this.Time);
		c.gridy = 1;
		c.weighty = 3;
		grid.setConstraints(this.Mine, c);
		this.add(this.Mine);

		this.Time.setLayout(grid);
		time.setBackground(Color.GREEN);
		reset.setBackground(Color.GRAY);
		bomb.setBackground(Color.WHITE);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 5;
		grid.setConstraints(time, c);
		this.Time.add(time);
		c.weightx = 3;
		c.gridx = 1;
		grid.setConstraints(reset, c);
		this.Time.add(reset);
		c.weightx = 5;
		c.gridx = 2;
		grid.setConstraints(bomb, c);
		this.bomb.setForeground(Color.RED);
		this.setBombNum(this.ms.getBomb());
		this.Time.add(bomb);
		this.setSize(this.getPreferredSize());
		this.setVisible(true);
		this.repaint();
	}

	public void Initialization() {
		ms.reset();
		this.removeAll();
		this.Time.removeAll();
		this.Mine.removeAll();
		runInit();
		this.repaint();
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void setTextToTile(int x, int y, String text) {
		this.tileTable[y][x].setLabel(text);
	}

	@Override
	public void setTileTableBackgroundColor(int x, int y, boolean clickBomb) {
		if (clickBomb)
			this.tileTable[y][x].setBackground(Color.RED);
		else
			this.tileTable[y][x].setBackground(Color.GRAY);
	}

	@Override
	public void setTextStringColor(int x, int y, boolean bool) {
		if (bool) {
			this.tileTable[y][x].setForeground(Color.MAGENTA);
		} else {
			switch(this.ms.getTableValue(x, y)) {
				case 1:
					this.tileTable[y][x].setForeground(Color.BLUE);
					break;
				case 2:
					this.tileTable[y][x].setForeground(Color.GREEN);
					break;
				case 3:
					this.tileTable[y][x].setForeground(Color.RED);
					break;
				case 4:
					this.tileTable[y][x].setForeground(Color.PINK);
					break;
				case 5:
					this.tileTable[y][x].setForeground(Color.ORANGE);
					break;
				case 6:
					this.tileTable[y][x].setForeground(Color.CYAN);
					break;
				case 7:
					this.tileTable[y][x].setForeground(Color.BLACK);
					break;
				case 8:
					this.tileTable[y][x].setForeground(Color.GRAY);
					break;
			}
		}
	}

	@Override
	public void setBombNum(int num) {
		String text;
		if (num <= 0)
			text = "00";
		else 
			if (num / 10 == 0) {
				text = "0";
				text += String.valueOf(num);
			} else {
				text = String.valueOf(num);
			}
		bomb.setText(text);
	}

	@Override
	public void win() {
		resultDialog.showDialog("Win !!!", this);
	}

	@Override
	public void lose() {
		resultDialog.showDialog("Lose ...", this);
	}
}




class MouseEventHandler implements MouseListener {

	MineSweeper ms;
	MineSweeperGUI msgui;
	int x, y;

	MouseEventHandler(MineSweeper ms, MineSweeperGUI msgui, int x, int y) {
		this.ms = ms;
		this.msgui = msgui;
		this.x = x;
		this.y = y;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		switch (e.getButton()) {
			case MouseEvent.BUTTON1: {
										 // マウスの左ボタンが押された時
										 ms.openTile(x, y, msgui);
									 } break;
			case MouseEvent.BUTTON2: {
										 // マウスの真ん中のボタンが押された時
									 } break;
			case MouseEvent.BUTTON3: {
										 // マウスの右ボタンが押された時
										 ms.setFlag(x, y, msgui);
									 } break;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}

class StartMenu extends Frame implements WindowListener {
	Label label;
	Button easy, normal, hard;

	StartMenu(String title, Main mode) {
		super(title);
		Font f = new Font("serif", Font.BOLD, 30);
		this.addWindowListener(this); // ウインドウの登録
		this.setSize(250, 400);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - 250) / 2;
		int y = (screen.height - 400) / 2;
		this.setLocation(x, y);
		this.setLayout(new GridLayout(4, 1));
		label = new Label("MineSweeper");
		label.setAlignment(Label.CENTER);
		easy = new Button("easy");
		normal = new Button("normal");
		hard = new Button("hard");
		label.setFont(f);
		label.setForeground(Color.WHITE);
		label.setBackground(Color.BLACK);
		easy.setFont(f);
		normal.setFont(f);
		hard.setFont(f);
		label.setSize(250, 100);
		easy.setSize(250, 100);
		normal.setSize(250, 100);
		hard.setSize(250, 100);
		easy.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				mode.setMode(1);
				setVisible(false);
				mode.runInit();
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		normal.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				mode.setMode(2);
				setVisible(false);
				mode.runInit();
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		hard.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				mode.setMode(3);
				setVisible(false);
				mode.runInit();
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		this.add(label);
		this.add(easy);
		this.add(normal);
		this.add(hard);
		this.setVisible(true);
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
}

class ResultDialog extends Dialog {

	Label label;
	Button btn1, btn2, btn3, btn4;
	Main copy;

	public ResultDialog(Frame owner, String title, Main main) {
		super(owner, title);
		this.copy = main;
		setLayout(new GridLayout(6, 1));  // 矩形にする  2行1列
		Panel p1 = new Panel(); // 新しいパネルを作成
		label = new Label();
		p1.add(label); // p1にlabelを追加
		this.add(p1); // 
		btn1 = new Button();
		btn2 = new Button();
		btn3 = new Button();
		btn4 = new Button();
		btn1.setLabel("easy");
		btn2.setLabel("normal");
		btn3.setLabel("hard");
		btn4.setLabel("exit");
		btn1.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				copy.setVisible(false);
				copy.setMode(1);
				copy.Initialization();
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		btn2.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				copy.setVisible(false);
				copy.setMode(2);
				copy.Initialization();
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		btn3.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				copy.setVisible(false);
				copy.setMode(3);
				copy.Initialization();
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		btn4.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		Panel p3 = new Panel();
		Panel p4 = new Panel();
		Panel p5 = new Panel();
		Panel p6 = new Panel();
		p3.add(btn1);
		p4.add(btn2);
		p5.add(btn3);
		p6.add(btn4);
		this.add(p3);
		this.add(p4);
		this.add(p5);
		this.add(p6);
	}

	public void showDialog(String message, Frame obj) {
		this.setSize(250, 200);
		int x = obj.getX() + (obj.getWidth() / 2) - (this.getWidth() / 2);
		int y = obj.getY() + (obj.getHeight() - this.getHeight()) / 2;
		this.setLocation(x, y);
		this.label.setText(message);
		this.setVisible(true);
	}
}

