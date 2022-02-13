package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import entity.PlayerMP;
import item.ItemManager;
import net.GameClient;
import net.GameServer;
import net.Pkt02Disconnect;
import net.Pkt08ServerTick;
import structure.StructuresManager;
import tile.TileManager;

public class Game extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	// Screen
	public JFrame window;

	private final int originalTileSize = 16;
	private final int scale = 3;
	public final int tileSize = originalTileSize * scale;
	public final int playerSize = tileSize / 2;

	private int FPS = 60;
	private boolean running = false;
	private BufferedImage cursor;

	// World
	public long randSeed;
	public Random rand;
	public int maxWorldCol;
	public int maxWorldRow;
	public int worldWidth;
	public int worldHeight;
	public int numberOfBuildings;
	public int numberOfCrates;
	public int numberOfObstructions;

	public WindowHandler windowHandler;
	public TileManager tileM;
	public ItemManager itemM;
	public StructuresManager structM;
	public Screen screen;
	private List<PlayerMP> playerList;
	public UI ui;
	public PlayerMP player;
	Sound sound = new Sound();

	public KeyHandler keys = new KeyHandler(this);
	public MouseHandler mouse = new MouseHandler(this);

	// Server
	public GameClient socketClient;
	public GameServer socketServer;

	// Game State
	public boolean loading = false;
	public int gameState = 0;
	public final int titleState = 0;
	public final int waitState = 1;
	public final int playState = 2;
	public final int endState = 3;

	public Game() {

		screen = new Screen(this);
		player = new PlayerMP(this, keys, mouse, null, null, -1);
		ui = new UI(this);

		randSeed = System.currentTimeMillis();
		rand = new Random(randSeed);
		playerList = new ArrayList<PlayerMP>();

		this.setPreferredSize(new Dimension(screen.screenWidth, screen.screenHeight));
		this.setBackground(Color.BLACK);
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.addKeyListener(keys);
		this.addMouseMotionListener(mouse);
		this.addMouseWheelListener(mouse);
		this.addMouseListener(mouse);

		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle("IDK");

		window.add(this, BorderLayout.CENTER);
		window.pack();

		window.setLocationRelativeTo(null);
		window.setVisible(true);
		gameState = titleState;

		windowHandler = new WindowHandler(this);
		try {
			cursor = ImageIO.read(getClass().getResourceAsStream("/cursor/crosshair.png"));
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Cursor c = toolkit.createCustomCursor(cursor, getLocation(), "img");
			this.setCursor(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startGameThread() {
		// Start game thread
		running = true;
		new Thread(this).start();

	}

	// GAME LOOP
	public void run() {

		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		int timer = 0;
		int frames = 0;

		while (running) {

			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			timer += currentTime - lastTime;
			lastTime = currentTime;

			if (delta >= 1) {
				update();
				delta--;

				repaint();
				frames++;
			}

			if (timer >= 1000000000) {
				window.setTitle("Name: " + player.getUsername() + " FPS: " + frames + " SEED: " + randSeed);
				timer = 0;
				frames = 0;
			}
		}

	}

	public void loadDefaults() {
		// Load world defaults based on game state
		loading = true;
		tileM = new TileManager(this);
		maxWorldCol = tileM.maxWorldCol;
		maxWorldRow = tileM.maxWorldRow;
		worldWidth = tileSize * maxWorldCol;
		worldHeight = tileSize * maxWorldRow;
		if (gameState == playState) {
			numberOfBuildings = 50;
			numberOfCrates = 100;
			numberOfObstructions = 100;
		} else if (gameState == waitState) {
			numberOfBuildings = 10;
			numberOfCrates = 0;
			numberOfObstructions = 15;
		}
		itemM = new ItemManager(this);
		structM = new StructuresManager(this);
		loading = false;
	}

	public synchronized List<PlayerMP> getPlayers() {
		return playerList;
	}

	public void clearPlayers() {
		playerList = new ArrayList<PlayerMP>();
	}

	// UPDATE ALL ASSETS
	public void update() {
		if (!loading) {
			if (gameState != titleState) {
				for (PlayerMP p : getPlayers())
					p.update();

				if (socketServer != null) { socketServer.update(); }
				ui.update();
			}
		}
	}

	// RENDER ALL ASSETS
	public void paintComponent(Graphics g) {
		// Increase performance settings
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		if (!loading)
			screen.render(g2);
		g2.dispose();

	}
	
	//sound methods
	public void playMusic(int i) {
		sound.setFile(i);
		sound.play();
		sound.loop();
	}
	public void stopMusic() {
		sound.stop();
	}
	public void playSE(int i) {
		sound.setFile(i);
		sound.play();
	}

	public static void main(String[] args) {
		// Activate OpenGL
		System.setProperty("sun.java2d.opengl", "True");
		new Game().startGameThread();
	}

}
