import java.util.Random;

interface MineSweeperGUI {
    public void setTextToTile(int x, int y, String text);
	public void setTextStringColor(int x, int y, boolean bool);
	public void setTileTableBackgroundColor(int x, int y, boolean clickBomb);
	public void setBombNum(int bomb);
    public void win();
    public void lose();
}

public class MineSweeper {

    private final int height;
    private final int width;
    private final int numberOfTiles;
    private final int numberOfBombs;
    private final int[][] table; // 周りの爆弾の数または爆弾
	private final Random rnd; // クラス
	private final int[] searchX = {-1, 0, 1};
	private final int[] searchY = {-1, 0, 1};

	private boolean[][] map; // クリックされたかどうか true:クリックされてない
	private boolean[][] flagMap; // フラグが立っているかどうか true:フラグが立っている
	private final int SAFE; // 地雷以外の数(定数)
	private int safe; // 爆弾以外の数
	private boolean firstClickFlag;
	private int buttonSize;
	private int bomb;


    public MineSweeper(int height, int width, int numberOfBombs, int screenWidth, int screenHeight) {
        this.height = height;
        this.width = width;
        this.numberOfTiles = height * width; // マスの数
        this.numberOfBombs = numberOfBombs; // 爆弾の数
        this.table = new int[height][width];
		this.rnd = new Random();
		this.map = new boolean[height][width];
		this.flagMap = new boolean[height][width];
		this.safe = height * width - numberOfBombs;
		this.SAFE = this.safe;
		this.firstClickFlag = true;
		this.buttonSize = screenHeight / height;
		this.buttonSize = Math.min((screenWidth / width), this.buttonSize);
		this.buttonSize = Math.min(50, this.buttonSize);
		this.buttonSize -= this.buttonSize % 5;
		this.bomb = numberOfBombs;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				this.map[i][j] = true;
			}
		}
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

	public int getTableValue(int x, int y) {
		return this.table[y][x];
	}

	public int getButtonSize() {
		return this.buttonSize;
	}

	public int getBomb() {
		return this.bomb;
	}

    void initTable(int _x, int _y) {
        setBombs(_x, _y);
        
        // implement
		// 各マスの周りの爆弾の数を調べる
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				if (table[i][j] == -2)
					table[i][j] = 0;
				if (table[i][j] == -1)
					continue; // 爆弾のマスの場合調べる必要はない
				for (int k = 0; k < 3; k++) { // x座標
					int x = j + searchX[k];
					if (x < 0 || this.width <= x) // 範囲外
						continue;
					for (int h = 0; h < 3; h++) { // y座標
						if (searchX[k] == 0 && searchY[h] == 0)
							continue;
						int y = i + searchY[h];
						if (y < 0 || this.height <= y) // 範囲外
							continue;
						if (this.table[y][x] == -1)
							this.table[i][j]++;
					}
				}
			}
		}
	}

	void reset() {
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				table[y][x] = 0;
				map[y][x] = true;
				flagMap[y][x] = false;
			}
		}
		this.safe = this.SAFE;
		this.firstClickFlag = true;
		this.bomb = this.numberOfBombs;
	}


    // table中にnumberOfBombsの値だけランダムに爆弾を設置する．
    void setBombs(int _x, int _y) {
        // implement
		for (int i = 0; i < 3; i++) {
			int y = _y + searchY[i];
			if (y < 0 || y >= this.height)
				continue;
			for (int j = 0; j < 3; j++) {
				int x = _x + searchX[j];
				if (x < 0 || x >= this.width)
					continue;
				this.table[y][x] = -2;
			}
		}
		int count = 0;
		while(count < this.numberOfBombs) {
			int x = rnd.nextInt(this.width);
			int y = rnd.nextInt(this.height);
			if (this.table[y][x] != -1 && this.table[y][x] != -2) {
				this.table[y][x] = -1;
				count++;
			} 
		}
    }

    // 左クリックしたときに呼び出される．
    public void openTile(int x, int y, MineSweeperGUI gui) {
        // implement
		if (this.firstClickFlag && !this.flagMap[y][x]) {
			this.firstClickFlag = false;
			initTable(x, y);
		}
		if (this.table[y][x] == -1 && !this.flagMap[y][x]) {
			this.openAllTiles(gui);
			gui.setTileTableBackgroundColor(x, y, true);
			gui.lose();
		} else if (map[y][x] && !flagMap[y][x]){
			gui.setTileTableBackgroundColor(x, y, false);
			if (table[y][x] != 0) {
				gui.setTextStringColor(x, y, false); // 文字のカラーを決める
				gui.setTextToTile(x, y, String.valueOf(table[y][x])); // 文字の表示
			} else 
				gui.setTextToTile(x, y, ""); // 文字の表示
			this.map[y][x] = false;
			this.safe--; // 爆弾以外の数を減らす
			if (this.table[y][x] == 0) 
				this.openSurroundings(x, y, gui);
		}
		if (this.safe == 0) {
			this.openAllTiles(gui);
			gui.win();
		}
    }

	// 0をオープンした時に周りをオープンする
	public void openSurroundings(int x, int y, MineSweeperGUI gui) {
		for (int i = 0; i < 3; i++) {
			int _y = y + searchY[i];
			if (_y < 0 || this.height <= _y)
				continue;
			for (int j = 0; j < 3; j++) {
				if (i == 1 && j == 1)
					continue;
				int _x = x + searchX[j];
				if (_x < 0 || this.width <= _x)
					continue;
				openTile(_x, _y, gui);
			}
		}

	}

    // 右クリックされたときに呼び出される．
    public void setFlag(int x, int y, MineSweeperGUI gui) {
        // implement
		if (this.map[y][x]) {
			this.flagMap[y][x] = !this.flagMap[y][x];
			if (this.flagMap[y][x]) {
				this.bomb--;
				gui.setBombNum(bomb);
				gui.setTextStringColor(x, y, true);
				gui.setTextToTile(x, y, "F");
			} else {
				this.bomb++;
				gui.setBombNum(bomb);
				gui.setTextToTile(x, y, "");
			}
		}
    }

    private void openAllTiles(MineSweeperGUI gui) {
        // implement
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				String str = String.valueOf(this.table[i][j]);
				this.map[i][j] = false;
				if (str.equals("-1")) {
					gui.setTileTableBackgroundColor(j, i, false);
					gui.setTextStringColor(j, i, true);
					gui.setTextToTile(j, i, "B");
				} else {
					gui.setTileTableBackgroundColor(j, i, false);
					if (!str.equals("0")) {
						gui.setTextStringColor(j, i, false);
						gui.setTextToTile(j, i, str);
					} else
						gui.setTextToTile(j, i, "");
				}
			}
		}
    }
}
