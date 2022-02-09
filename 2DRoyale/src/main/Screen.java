package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.imageio.ImageIO;

import entity.PlayerMP;
import item.SuperWeapon;
import structure.Building;
import structure.Crate;

public class Screen {

	private Game game;

	public final int maxScreenCol = 24;
	public final int maxScreenRow = 18;
	public final int screenWidth;
	public final int screenHeight;
	private BufferedImage minimapBack, megamapLobby, megamapGame;

	public Screen(Game game) {
		this.game = game;
		this.screenWidth = game.tileSize * maxScreenCol;
		this.screenHeight = game.tileSize * maxScreenRow;
		try {
			minimapBack = ImageIO.read(getClass().getResourceAsStream("/UI/minimap_back.png"));
			megamapLobby = ImageIO.read(getClass().getResourceAsStream("/maps/lobbyMega.png"));
			megamapGame = ImageIO.read(getClass().getResourceAsStream("/maps/olympusMega.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void render(Graphics2D g2) {

		if (game.gameState == game.waitState || game.gameState == game.playState) {
			renderWorld(g2);
			renderBuildings(g2);
			renderCrates(g2);
			renderItems(g2);
			for (PlayerMP p : game.getPlayers())
				p.renderBullets(g2);
			for (PlayerMP p : game.getPlayers())
				p.render(g2);
			renderGas(g2);

			if (game.keys.map)
				renderMegamap(g2);
			else
				renderMinimap(g2);
		}

		game.ui.draw(g2);

	}

	private void renderMegamap(Graphics2D g2) {
		int megamapLength = 600;
		int megamapHeight = 600;
		int megamapRenderAtX = game.player.screenX - megamapLength / 2;
		int megamapRenderAtY = game.player.screenY - megamapHeight / 2;
		int playerSize = 9;
		
		int playerMapX = (int)Math.round((double)game.player.worldX / game.worldWidth * megamapLength);
		int playerMapY = (int)Math.round((double)game.player.worldY / game.worldHeight * megamapHeight);

		// Draw Map
		if(game.gameState == game.waitState)
			g2.drawImage(megamapLobby, megamapRenderAtX, megamapRenderAtY, megamapLength, megamapHeight, null);
		else
			g2.drawImage(megamapGame, megamapRenderAtX, megamapRenderAtY, megamapLength, megamapHeight, null);
		
		// Draw player sprite (decent quality)
				g2.drawImage(game.player.sprite, megamapRenderAtX + playerMapX - (playerSize/2),
						megamapRenderAtY + playerMapY - (playerSize/2), playerSize, playerSize, null);
	}

	// NOTE: not in final release due to performance issues
	// Current still here for picture taking purposes
	// Remove before final release
	private void renderMegamapObsolete(Graphics2D g2) {
		int megamapLength = 600;
		int megamapHeight = 600;
		int megamapRenderAtX = 150;
		int megamapRenderAtY = 150;
		int megamapTileSizeX = megamapLength / game.maxWorldCol;
		int megamapTileSizeY = megamapHeight / game.maxWorldRow;

		int playerTileX = game.player.worldX / game.tileSize;
		int playerTileY = game.player.worldY / game.tileSize;

		int megamapX = 0;
		int megamapY = 0;

		// Render blocks
		for (int y = 0; y < game.maxWorldRow; y++) {
			megamapX = 0;
			for (int x = 0; x < game.maxWorldCol; x++) {
				int tileNum = game.tileM.mapTileNum[x][y][0];

				if (game.tileM.mapTileNum[x][y][1] == 1)
					g2.drawImage(game.tileM.tile[tileNum].image, megamapRenderAtX + megamapX,
							megamapRenderAtY + megamapY, megamapTileSizeX, megamapTileSizeY, null);
				else
					g2.drawImage(game.tileM.tile[tileNum].image, megamapRenderAtX + megamapX + megamapTileSizeX,
							megamapRenderAtY + megamapY, -megamapTileSizeX, megamapTileSizeY, null);
				megamapX += megamapTileSizeX;
			}
			megamapY += megamapTileSizeY;
		}

		megamapX = 0;
		megamapY = 0;
		// Render buildings
//		for (int y = 0; y < game.maxWorldRow; y++) {
//			megamapX = 0;
//			for (int x = 0; x < game.maxWorldCol; x++) {
//				if (game.structM.buildingOccupiesTile[x][y] == 1)
//					g2.drawImage(game.tileM.tile[6].image, megamapRenderAtX + megamapX, megamapRenderAtY + megamapY,
//							megamapTileSizeX, megamapTileSizeY, null);
//				megamapX += megamapTileSizeX;
//			}
//			megamapY += megamapTileSizeY;
//		}

		// Draw player sprite (decent quality)
//		g2.drawImage(game.player.sprite, megamapRenderAtX + (playerTileX * megamapTileSizeX),
//				megamapRenderAtY + (playerTileY * megamapTileSizeY), megamapTileSizeX, megamapTileSizeY, null);

	}

	private void renderMinimap(Graphics2D g2) {
		int minimapRadius = 18;
		int minimapTileSize = 4;
		int minimapRenderAtX = 70;
		int minimapRenderAtY = 70;
		int minimapBorderSize = (int) (minimapRadius * 1.25);
		int minimapBackSize = (minimapRadius * 2 + 1) * minimapTileSize + (minimapBorderSize * 2);
		int minimapBackRenderAtX = minimapRenderAtX - minimapBorderSize;
		int minimapBackRenderAtY = minimapRenderAtY - minimapBorderSize;

		// Draw minimap back
		g2.drawImage(minimapBack, minimapBackRenderAtX, minimapBackRenderAtY, minimapBackSize, minimapBackSize, null);

		int playerTileX = game.player.worldX / game.tileSize;
		int playerTileY = game.player.worldY / game.tileSize;

		// For making minimap stop at border
		int xLowerBound = playerTileX - minimapRadius;
		int xUpperBound = playerTileX + minimapRadius;
		int yLowerBound = playerTileY - minimapRadius;
		int yUpperBound = playerTileY + minimapRadius;

		int xOffset = 0;
		int yOffset = 0;

		if (xLowerBound < 0) {
			xOffset = -xLowerBound;
			xLowerBound = 0;
		}
		if (xUpperBound < 0) {
			xOffset = -xLowerBound;
			xUpperBound = 0;
		}
		if (yLowerBound < 0) {
			yOffset = -yLowerBound;
			yLowerBound = 0;
		}
		if (yUpperBound < 0) {
			yOffset = -yLowerBound;
			yUpperBound = 0;
		}

		if (xLowerBound > game.maxWorldCol - 1)
			xLowerBound = game.maxWorldCol - 1;
		if (xUpperBound > game.maxWorldCol - 1)
			xUpperBound = game.maxWorldCol - 1;
		if (yLowerBound > game.maxWorldRow - 1)
			yLowerBound = game.maxWorldCol - 1;
		if (yUpperBound > game.maxWorldRow - 1)
			yUpperBound = game.maxWorldCol - 1;

		int minimapX = 0;
		int minimapY = 0;

		// Draw void fill
		for (int y = 0; y < minimapRadius * 2 + 1; y++) {
			minimapX = 0;
			for (int x = 0; x < minimapRadius * 2 + 1; x++) {
				g2.drawImage(game.tileM.tile[7].image, minimapRenderAtX + minimapX, minimapRenderAtY + minimapY,
						minimapTileSize, minimapTileSize, null);
				minimapX += minimapTileSize;
			}
			minimapY += minimapTileSize;
		}

		minimapX = 0;
		minimapY = 0;

		// Draw tiles
		for (int y = yLowerBound; y < yUpperBound + 1; y++) {
			minimapX = 0;
			for (int x = xLowerBound; x < xUpperBound + 1; x++) {
				int tileNum = game.tileM.mapTileNum[x][y][0];

				if (game.tileM.mapTileNum[x][y][1] == 1)
					g2.drawImage(game.tileM.tile[tileNum].image,
							minimapRenderAtX + minimapX + (xOffset * minimapTileSize),
							minimapRenderAtY + minimapY + (yOffset * minimapTileSize), minimapTileSize, minimapTileSize,
							null);
				else {
					if (game.tileM.mapTileNum[x][y][0] != 7)
						g2.drawImage(game.tileM.tile[tileNum].image,
								minimapRenderAtX + minimapX + minimapTileSize + (xOffset * minimapTileSize),
								minimapRenderAtY + minimapY + (yOffset * minimapTileSize), -minimapTileSize,
								minimapTileSize, null);
				}

				minimapX += minimapTileSize;
			}
			minimapY += minimapTileSize;
		}

		minimapX = 0;
		minimapY = 0;

		// Draw buildings
		for (int y = yLowerBound; y < yUpperBound + 1; y++) {
			minimapX = 0;
			for (int x = xLowerBound; x < xUpperBound + 1; x++) {
				if (game.structM.buildingOccupiesTile[x][y] == 1)
					g2.drawImage(game.tileM.tile[6].image, minimapRenderAtX + minimapX + (xOffset * minimapTileSize),
							minimapRenderAtY + minimapY + (yOffset * minimapTileSize), minimapTileSize, minimapTileSize,
							null);

				minimapX += minimapTileSize;
			}
			minimapY += minimapTileSize;
		}

		// Draw player sprite (terrible quality)
		g2.drawImage(game.player.sprite, minimapRenderAtX + (minimapRadius * minimapTileSize),
				minimapRenderAtY + (minimapRadius * minimapTileSize), minimapTileSize, minimapTileSize, null);
	}

	private void renderWorld(Graphics2D g2) {
		int worldCol = 0;
		int worldRow = 0;

		while (worldCol < game.maxWorldCol && worldRow < game.maxWorldRow) {

			int tileNum = game.tileM.mapTileNum[worldCol][worldRow][0];
			int worldX = worldCol * game.tileSize;
			int worldY = worldRow * game.tileSize;
			int gameX = worldX - game.player.worldX + game.player.screenX;
			int gameY = worldY - game.player.worldY + game.player.screenY;

			if (worldX + game.tileSize > game.player.worldX - game.player.screenX
					&& worldX - game.tileSize < game.player.worldX + game.player.screenX
					&& worldY + game.tileSize > game.player.worldY - game.player.screenY
					&& worldY - game.tileSize < game.player.worldY + game.player.screenY) {
				if (game.tileM.mapTileNum[worldCol][worldRow][1] == 1)
					g2.drawImage(game.tileM.tile[tileNum].image, gameX, gameY, game.tileSize, game.tileSize, null);
				else
					g2.drawImage(game.tileM.tile[tileNum].image, gameX + game.tileSize, gameY, -game.tileSize,
							game.tileSize, null);
			}

			worldCol++;

			if (worldCol == game.maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
		}
	}

	private void renderGas(Graphics2D g2) {
		int worldCol = 0;
		int worldRow = 0;

		while (worldCol < game.maxWorldCol && worldRow < game.maxWorldRow) {

			int worldX = worldCol * game.tileSize;
			int worldY = worldRow * game.tileSize;
			int gameX = worldX - game.player.worldX + game.player.screenX;
			int gameY = worldY - game.player.worldY + game.player.screenY;

			if (worldX + game.tileSize > game.player.worldX - game.player.screenX
					&& worldX - game.tileSize < game.player.worldX + game.player.screenX
					&& worldY + game.tileSize > game.player.worldY - game.player.screenY
					&& worldY - game.tileSize < game.player.worldY + game.player.screenY) {
				if (game.tileM.mapTileNum[worldCol][worldRow][2] == 1)
					g2.drawImage(game.tileM.gasTile.image, gameX, gameY, game.tileSize, game.tileSize, null);
			}

			worldCol++;

			if (worldCol == game.maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
		}
	}

	private void renderBuildings(Graphics2D g2) {
		int worldCol, worldRow;
		Building[] building = game.structM.building;
		int buildingTileSize = game.structM.buildingTileSize;

		for (int i = 0; i < game.numberOfBuildings; i++) {
			worldCol = 0;
			worldRow = 0;
			while (worldCol < building[i].boundingBox.width / buildingTileSize
					&& worldRow < building[i].boundingBox.height / buildingTileSize) {

				int[] rowNum = building[i].buildingTileNum.get(worldRow);
				int tileNum = rowNum[worldCol];
				int worldX = building[i].boundingBox.x + (worldCol * buildingTileSize);
				int worldY = building[i].boundingBox.y + (worldRow * buildingTileSize);
				int gameX = worldX - game.player.worldX + game.player.screenX;
				int gameY = worldY - game.player.worldY + game.player.screenY;

				if (tileNum != 0) {
					if (worldX + game.tileSize > game.player.worldX - game.player.screenX
							&& worldX - game.tileSize < game.player.worldX + game.player.screenX
							&& worldY + game.tileSize > game.player.worldY - game.player.screenY
							&& worldY - game.tileSize < game.player.worldY + game.player.screenY) {
						g2.drawImage(game.structM.tile[tileNum].image, gameX, gameY, buildingTileSize, buildingTileSize,
								null);
					}
				}

				worldCol++;

				if (worldCol == building[i].boundingBox.width / buildingTileSize) {
					worldCol = 0;
					worldRow++;
				}
			}
		}
	}

	private void renderCrates(Graphics2D g2) {
		List<Crate> crates = game.structM.crates;
		int crateTileSize = game.structM.crateTileSize;

		for (int i = 0; i < crates.size(); i++) {
			Crate crate = crates.get(i);
			int worldX = crate.collisionBoundingBox.x;
			int worldY = crate.collisionBoundingBox.y;
			int gameX = worldX - game.player.worldX + game.player.screenX;
			int gameY = worldY - game.player.worldY + game.player.screenY;

			if (worldX + game.tileSize > game.player.worldX - game.player.screenX
					&& worldX - game.tileSize < game.player.worldX + game.player.screenX
					&& worldY + game.tileSize > game.player.worldY - game.player.screenY
					&& worldY - game.tileSize < game.player.worldY + game.player.screenY) {
				g2.drawImage(game.structM.obstruction[crate.crateTileNum].image, gameX, gameY, crateTileSize,
						crateTileSize, null);
			}
		}
	}

	private void renderItems(Graphics2D g2) {
		List<SuperWeapon> worldWeapons = game.itemM.worldWeapons;
		for (int i = 0; i < worldWeapons.size(); i++) {

			SuperWeapon weap = worldWeapons.get(i);

			int worldX = weap.worldX;
			int worldY = weap.worldY;
			int gameX = worldX - game.player.worldX + game.player.screenX;
			int gameY = worldY - game.player.worldY + game.player.screenY;

			if (worldX + game.tileSize > game.player.worldX - game.player.screenX
					&& worldX - game.tileSize < game.player.worldX + game.player.screenX
					&& worldY + game.tileSize > game.player.worldY - game.player.screenY
					&& worldY - game.tileSize < game.player.worldY + game.player.screenY) {
				Color c = new Color(255, 255, 50);
				g2.setColor(c);
				g2.setStroke(new BasicStroke(2));
				g2.drawOval(gameX + weap.entityArea.x, gameY + weap.entityArea.y, 18, 18);
				g2.drawImage(weap.entityImg, gameX, gameY, weap.imgIconWidth, weap.imgIconHeight, null);
			}
		}

	}

}