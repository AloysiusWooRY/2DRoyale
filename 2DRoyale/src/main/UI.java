package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import entity.PlayerMP;
import item.SuperWeapon;

public class UI {

	Game game;
	Graphics2D g2;
	Font maruMonica;
	public boolean messageOn = false;
	public boolean gameFinished = false;
	ArrayList<String> message = new ArrayList<>();
	ArrayList<Integer> messageCounter = new ArrayList<>();
	public int commandNum = 0;
	public int titleScreenState = 0;
	public String name = "";
	public String temp = "";
	public String ipAddress = "";
	public BufferedImage healthImage, killCounterImage, remainingPlayersImage;
	public int countDownSeq = 0;
	int tempcounter = 6;
	public int countdown;
	public boolean option;
	public int optionScreen;
	public boolean endingSound;
	
	public int kills = 0;
	public boolean win;
	public int waitingPlayerCount;
	public int playingPlayerCount;

	public UI(Game game) {
		this.game = game;

		try {
			InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
			maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			healthImage = ImageIO.read(getClass().getResourceAsStream("/UI/HP.png"));
			killCounterImage = ImageIO.read(getClass().getResourceAsStream("/UI/killcounter.png"));
			remainingPlayersImage = ImageIO.read(getClass().getResourceAsStream("/UI/remainingplayers.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void update() {
		playingPlayerCount = waitingPlayerCount = 0;
		for(PlayerMP p: game.getPlayers()) {
			if(p.playerState == game.playState)
				playingPlayerCount++;
			if(p.playerState == game.waitState)
				waitingPlayerCount++;
		}				
	}

	public void draw(Graphics2D g2) {

		this.g2 = g2;
		g2.setFont(maruMonica);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(Color.white);

		// Title State
		if (game.gameState == game.titleState) {
			drawTitleScreen();
		}
		// Play State
		int playerCount;
		if (game.gameState == game.waitState || game.gameState == game.playState) {
			endingSound = false;
			if(playingPlayerCount > 1) {				
				playerCount = playingPlayerCount;
			} else {
				playerCount = waitingPlayerCount;
			}
			drawHP();
			drawInventory();
			drawKillsStat(this.kills);
			drawPlayerCount(playerCount);
			drawMessage();
			drawCountdown();
			drawOption(option);
			//start game message
			if (game.gameState == game.waitState)
				drawWaitMessage();
			
		}
		// End state
		
		if (game.gameState == game.endState) {
			//true if player wins, false if player loses
			drawEndGame(win);
		}
		

	}

	public void drawTitleScreen() {

		if (titleScreenState == 0) {
			g2.setColor(new Color(0, 0, 0));
			g2.fillRect(0, 0, game.screen.screenWidth, game.screen.screenHeight);
			// Title Name
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
			String text = "2D Royale";
			int x = getXforCenteredText(text);
			int y = game.tileSize * 3;

			// Shadow
			g2.setColor(Color.gray);
			g2.drawString(text, x + 5, y + 5);

			// Main Color
			g2.setColor(Color.white);
			g2.drawString(text, x, y);

			// Menu
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

			text = "START";
			x = getXforCenteredText(text);
			y += game.tileSize * 4;
			g2.drawString(text, x, y);
			if (commandNum == 0) {
				g2.drawString(">", x - game.tileSize, y);
			}

			text = "HOW TO PLAY";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 1) {
				g2.drawString(">", x - game.tileSize, y);
			}

			text = "PLAYERS CONTROL";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 2) {
				g2.drawString(">", x - game.tileSize, y);
			}

			text = "QUIT";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 3) {
				g2.drawString(">", x - game.tileSize, y);
			}
		} else if (titleScreenState == 1) {
			g2.setColor(Color.white);
			g2.setFont(g2.getFont().deriveFont(42F));

			String text = "Objectives";
			int x = getXforCenteredText(text);
			int y = game.tileSize * 3;
			g2.drawString(text, x, y);

			text = "- Kill everyone and be the last man standing";
			x = getXforCenteredText(text);
			y += game.tileSize * 2;
			g2.drawString(text, x, y);

			text = "- Search around for weapons and stay in the restricted area";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);

			text = "Good luck";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);

			text = "Back to Main Menu";
			x = getXforCenteredText(text);
			y += game.tileSize * 2;
			g2.drawString(text, x, y);
			g2.drawString(">", x - game.tileSize, y);
		} else if (titleScreenState == 2) {
			g2.setColor(Color.white);
			g2.setFont(g2.getFont().deriveFont(42F));

			String text = "Players Control";
			int x = getXforCenteredText(text);
			int y = game.tileSize * 3;
			g2.drawString(text, x, y);

			text = "- Use WASD to move your character around";
			x = getXforCenteredText(text);
			y += game.tileSize * 2;
			g2.drawString(text, x, y);

			text = "- Use E to pick and drop weapons";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);

			text = "- Left-click to aim and shoot";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);

			text = "Back to Main Menu";
			x = getXforCenteredText(text);
			y += game.tileSize * 2;
			g2.drawString(text, x, y);
			g2.drawString(">", x - game.tileSize, y);
		} else if (titleScreenState == 3) {
			g2.setColor(Color.white);
			g2.setFont(g2.getFont().deriveFont(42F));

			String text = "Do you want to run the server?";
			int x = getXforCenteredText(text);
			int y = game.tileSize * 3;
			g2.drawString(text, x, y);

			text = "YES";
			x = getXforCenteredText(text);
			y += game.tileSize * 4;
			g2.drawString(text, x, y);
			if (commandNum == 0) {
				g2.drawString(">", x - game.tileSize, y);
			}

			text = "NO";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 1) {
				g2.drawString(">", x - game.tileSize, y);
			}

			text = "Back";
			x = getXforCenteredText(text);
			y += game.tileSize * 3;
			g2.drawString(text, x, y);
			if (commandNum == 2) {
				g2.drawString(">", x - game.tileSize, y);
			}
		} else if (titleScreenState == 4) {
			g2.setColor(Color.white);
			g2.setFont(g2.getFont().deriveFont(42F));

			String text = "Enter your nickname:";
			int x = getXforCenteredText(text);
			int y = game.tileSize * 3;
			g2.drawString(text, x, y);

			g2.drawRect(game.tileSize * 6, game.tileSize * 5, game.screen.screenWidth - game.tileSize * 12, game.tileSize * 2);
			x = getXforCenteredText(name);
			y += game.tileSize * 3;
			g2.drawString(name, x, y);

			text = "Let's go";
			x = getXforCenteredText(text);
			y += game.tileSize * 3;
			g2.drawString(text, x, y);
			g2.drawString(">", x - game.tileSize, y);
		} else if (titleScreenState == 5) {
			g2.setColor(Color.white);
			g2.setFont(g2.getFont().deriveFont(42F));

			String text = "Enter server IP:";
			int x = getXforCenteredText(text);
			int y = game.tileSize * 3;
			g2.drawString(text, x, y);

			g2.drawRect(game.tileSize * 6, game.tileSize * 5, game.screen.screenWidth - game.tileSize * 12, game.tileSize * 2);
			x = getXforCenteredText(ipAddress);
			y += game.tileSize * 3;
			g2.drawString(ipAddress, x, y);

			text = "Ok";
			x = getXforCenteredText(text);
			y += game.tileSize * 3;
			g2.drawString(text, x, y);
			if (commandNum == 0) {
				g2.drawString(">", x - game.tileSize, y);
			}
			
			text = "Copy From Keyboard";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 1) {
				g2.drawString(">", x - game.tileSize, y);
			}
		}

	}
	
	//function to align text in middle
	public int getXforCenteredText(String text) {
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		int x = game.screen.screenWidth / 2 - length / 2;
		return x;
	}

	public void drawHP() {
		// display health logo
		g2.drawImage(healthImage, game.tileSize, game.tileSize * 16, 25, 25, null);
		// display health bar
		Color c = new Color(255, 0, 30);
		g2.setColor(c);
		g2.fillRect(game.tileSize * 2, game.tileSize * 10, (int) (game.player.health * 2), game.tileSize / 2);
	}

	public void drawInventory() {

		// frame
		int frameX = game.tileSize;
		int frameY = game.tileSize * 5;
		int frameWidth = game.tileSize * 2;
		int frameHeight = game.tileSize * 5;
		drawSubWindow(frameX, frameY, frameWidth, frameHeight);

		// slot
		final int slotXstart = frameX + 24;
		final int slotYstart = frameY + 20;
		
		//draw player's items in inventory
		for(int i = 0; i < game.player.getWeapons().length; i++ ) {
            SuperWeapon weap = game.player.getWeapons()[i];
            if (weap != null)
                g2.drawImage(weap.entityImg, slotXstart, slotYstart + game.tileSize * i + game.tileSize / 2 - weap.imgIconHeight/2, 50, 20, null);
        } 
		// cursor
		int cursorX = slotXstart;
		int cursorY = slotYstart + game.tileSize * (game.player.playerWeapIndex);
		int cursorWidth = game.tileSize;
		int cursorHeight = game.tileSize;

		// draw cursor
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);
	}
	
	//draw sub translucent window
	public void drawSubWindow(int x, int y, int width, int height) {
		Color c = new Color(0, 0, 0, 100);
		g2.setColor(c);
		g2.fillRoundRect(x, y, width, height, 10, 10);
	}
	
	//draw kill count
	public void drawKillsStat(int kills) {
		int x = game.screen.screenWidth - game.tileSize*4;
		int y =  game.tileSize;
		//make box
		drawSubWindow(x, y, game.tileSize*3, game.tileSize);
		//draw knife
		g2.drawImage(killCounterImage, x + 8, y + 12, 25, 25, null);
		//draw string
		String text = ": " + kills;
		g2.setColor(Color.white);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
		g2.drawString(text, x + 30, y + 30);
		
	}
	
	//draw player count
	public void drawPlayerCount(int playerCount){
		
		int x = game.screen.screenWidth - game.tileSize*4 + 85;
		int y = game.tileSize + 14;
		//draw player count image
		g2.drawImage(remainingPlayersImage, x, y, 20, 20, null);
		//draw string
		String text = ": " + playerCount;
		g2.setColor(Color.white);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
		g2.drawString(text, x + 22, y + 16);
		
	}
	
	//countdown timer interface
	public void drawCountdown() {
		if (game.gameState == game.playState && countdown > 0) {
			if(tempcounter != countdown) {
				tempcounter = countdown;
				game.playSE(6);
			}
			
			String text = "Game Starting in " + countdown;
			int x = getXforCenteredText(text);
			int y = game.tileSize * 3;
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 26F));
			g2.setColor(Color.black);
			g2.drawString(text, x + 2, y + 2);
			g2.setColor(Color.white);
			g2.drawString(text, x, y);
			
		}
	}
	
	//display option screen when esc is pressed
	public void drawOption(boolean option) {
		if (option) {
			int x = game.tileSize*6;
			int y =  game.tileSize*2;
			//make box
			drawSubWindow(x, y, game.screen.screenWidth - x*2, game.screen.screenHeight - y*6);
			String text = "Back To Game";
			x = getXforCenteredText(text);
			y += game.tileSize + 30;
			g2.setColor(Color.white);
			g2.drawString(text, x, y);
			if (commandNum == 0) 
				g2.drawString(">", x - game.tileSize, y);
			
			text = "Back to Main Menu";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 1) 
				g2.drawString(">", x - game.tileSize, y);
			
			text = "Quit Game";
			x = getXforCenteredText(text);
			y += game.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 2) 
				g2.drawString(">", x - game.tileSize, y);
		}
	}
	
	//host message to start game
	public void drawWaitMessage() {
		String text;
		if(game.socketServer != null) 
			text = "You are the host! Press F to start game!";
		else 
			text = "Waiting for host to start game!";

		int x = getXforCenteredText(text);
		int y = game.tileSize;
		g2.setColor(Color.black);
		g2.drawString(text, x + 2, y + 2);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
	}	
	
	// for scrolling killing feed
	public void addMessage(String text) {
		message.add(text);
		messageCounter.add(0);
		// add this line to the killed function -> game.iu.addMessage("blank killed blank")
		
	}
	public void drawMessage() {
		int messageX = game.screen.screenWidth - game.tileSize*4;
		int messageY = game.tileSize*4;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
		
		for(int i = 0; i < message.size(); i++) {
			if(message.get(i) != null) {
				
				g2.setColor(Color.black);
				g2.drawString(message.get(i),messageX+2, messageY+2);
				g2.setColor(Color.white);
				g2.drawString(message.get(i),messageX, messageY);
			
				int counter = messageCounter.get(i) + 1; //messageCounter++
				messageCounter.set(i,  counter); //set the counter to the array
				messageY += 50;
				
				if(messageCounter.get(i) > 180) {
					message.remove(i);
					messageCounter.remove(i);
				}
			}
		}
	}
	
	//end game screen
	public void drawEndGame(boolean win) {
		g2.setColor(Color.white);
		g2.setFont(g2.getFont().deriveFont(42F));
		String text;
		int x;
		int y;

		if (win) {
			text = "You Win!";
			x = getXforCenteredText(text);
			y = game.tileSize * 3;
			g2.drawString(text, x, y);
			if (endingSound == false) {
				game.playSE(10);
				endingSound = true;
			}
			
		}
		else {
			text = "You Died!";
			x = getXforCenteredText(text);
			y = game.tileSize * 3;
			g2.drawString(text, x, y);
			if (endingSound == false) {
				game.playSE(11);
				endingSound = true;
			}
		}
		
		//get kills from player
		text = "Number of Kills: " + this.kills;
		x = getXforCenteredText(text);
		y += game.tileSize * 3;
		g2.drawString(text, x, y);
		
		//get position of player from the length of array
		text = "Position: #" + (this.playingPlayerCount + 1);
		x = getXforCenteredText(text);
		y += game.tileSize;
		g2.drawString(text, x, y);
		
		text = "Back to Lobby";
		x = getXforCenteredText(text);
		y += game.tileSize * 3;
		g2.drawString(text, x, y);
		if (commandNum == 0) 
			g2.drawString(">", x - game.tileSize, y);
		
		text = "Back to Main Menu";
		x = getXforCenteredText(text);
		y += game.tileSize;
		g2.drawString(text, x, y);
		if (commandNum == 1) 
			g2.drawString(">", x - game.tileSize, y);
	}
}
