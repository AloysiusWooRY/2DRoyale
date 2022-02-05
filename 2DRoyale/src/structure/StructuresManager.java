package structure;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.Game;
import tile.Tile;

public class StructuresManager {

	private Game game;
	public int buildingTileSize = 16;

	public Tile[] tile;
	public Building[] building;

	public StructuresManager(Game game) {

		this.game = game;
		tile = new Tile[10];
		building = new Building[game.numberOfBuildings];

		getTileImage(); // populate tile array
		loadBuildings(game.numberOfBuildings);

	}

	public void loadBuildings(int numberOfBuildings) {
		int placedBuildings = 0;
		int failedAttempts = 0; //debug variable
		
		while(placedBuildings < numberOfBuildings) {	// prevent buildings from spawning on top of each other
			boolean failed = false;
			Building tryBuilding = new Building("/blueprint/building1.txt", buildingTileSize);
			int randomX = (int) (Math.random() * (game.tileSize * game.maxWorldCol - tryBuilding.boundingBox.width));
			int randomY = (int) (Math.random() * (game.tileSize * game.maxWorldRow - tryBuilding.boundingBox.height));
			
			int topLeftTileX = randomX / game.tileSize;	// int will floor the value
			int topLeftTileY = randomY / game.tileSize;	// int will floor the value
			int rows = (int) Math.ceil(1.0 * tryBuilding.boundingBox.height / game.tileSize);
			int cols = (int) Math.ceil(1.0 * tryBuilding.boundingBox.width / game.tileSize);
			
			for(int x = topLeftTileX; x < topLeftTileX + cols; x++) {
				System.out.println("x:" + x);
				for(int y = topLeftTileY; y < topLeftTileY + rows; y++) {
					
					System.out.println("y:" + y);
					if(game.tileM.tile[game.tileM.mapTileNum[x][y][0]].collision) {
						failed = true;
						failedAttempts++;
						break;
					}
				}
				if(failed == true)
					break;
			}
			
			if(failed == true)
				continue;
			
			for (int i = 1; i <= placedBuildings; i++) {
				if (randomX < building[i-1].boundingBox.x + building[i-1].boundingBox.width && randomX + tryBuilding.boundingBox.width > building[i-1].boundingBox.x
						&& randomY < building[i-1].boundingBox.y + building[i-1].boundingBox.height && randomY + tryBuilding.boundingBox.height > building[i-1].boundingBox.y) {
					failed = true;
					failedAttempts++;
					break;
				}
			}
			
			if(failed == true)
				continue;
			
			tryBuilding.boundingBox.x = randomX;
			tryBuilding.boundingBox.y = randomY;
			
			building[placedBuildings] = tryBuilding;
			placedBuildings++;
		}
		
		System.out.println("Building collisions: " + failedAttempts);
	}

	private void getTileImage() {

		try {

			tile[0] = new Tile();
			tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/marble.png"));

			tile[1] = new Tile();
			tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wall.png"));
			tile[1].collision = true;

			tile[2] = new Tile();
			tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wallHL.png"));
			tile[2].collision = true;

			tile[3] = new Tile();
			tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wallHC.png"));
			tile[3].collision = true;

			tile[4] = new Tile();
			tile[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wallHR.png"));
			tile[4].collision = true;

			tile[5] = new Tile();
			tile[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wallVT.png"));
			tile[5].collision = true;

			tile[6] = new Tile();
			tile[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wallVC.png"));
			tile[6].collision = true;

			tile[7] = new Tile();
			tile[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wallVB.png"));
			tile[7].collision = true;

			tile[8] = new Tile();
			tile[8].image = ImageIO.read(getClass().getResourceAsStream("/tiles/earth1.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void render(Graphics2D g2) {

	}

	public boolean hasCollided(int xa, int ya, int entityLeftWorldX, int entityRightWorldX, int entityTopWorldY,
			int entityBottomWorldY) {

		int buildingIndex;
		for (buildingIndex = 0; buildingIndex < building.length; buildingIndex++) {
			int structX = building[buildingIndex].boundingBox.x;
			int structY = building[buildingIndex].boundingBox.y;
			int structWidth = building[buildingIndex].boundingBox.width;
			int structHeight = building[buildingIndex].boundingBox.height;

			if (entityLeftWorldX < structX + structWidth && entityRightWorldX > structX
					&& entityTopWorldY < structY + structHeight && entityBottomWorldY > structY) {
				int checkLimitX = building[buildingIndex].boundingBox.width / buildingTileSize - 1;
				int checkLimitY = building[buildingIndex].boundingBox.height / buildingTileSize - 1;

				// get coords of player relative to top left of bounding box
				int entityLeftCol = (entityLeftWorldX - building[buildingIndex].boundingBox.x) / buildingTileSize;
				int entityRightCol = (entityRightWorldX - building[buildingIndex].boundingBox.x) / buildingTileSize;
				int entityTopRow = (entityTopWorldY - building[buildingIndex].boundingBox.y) / buildingTileSize;
				int entityBottomRow = (entityBottomWorldY - building[buildingIndex].boundingBox.y) / buildingTileSize;

				// if the player's adjacent tile is not within bounding box, e.g. going down and
				// facing tile 12,
				// will be out of bound, so set it to face tile 11. Will not interfere with
				// collision, if
				// the player reached tile 11, they can path through that tile to begin with.
				if (entityLeftCol > checkLimitX)
					entityLeftCol = entityRightCol;
				if (entityRightCol > checkLimitX)
					entityRightCol = entityLeftCol;
				if (entityTopRow > checkLimitY)
					entityTopRow = entityBottomRow;
				if (entityBottomRow > checkLimitY)
					entityBottomRow = entityTopRow;

				int tileNum1 = 0, tileNum2 = 0;
				int[] rowNum;

				if (ya < 0) { // UP
					rowNum = building[buildingIndex].buildingTileNum.get(entityTopRow);
					tileNum1 = rowNum[entityLeftCol];
					tileNum2 = rowNum[entityRightCol];
				}
				if (ya > 0) { // DOWN
					rowNum = building[buildingIndex].buildingTileNum.get(entityBottomRow);
					tileNum1 = rowNum[entityLeftCol];
					tileNum2 = rowNum[entityRightCol];
				}
				if (xa < 0) { // LEFT
					rowNum = building[buildingIndex].buildingTileNum.get(entityTopRow);
					tileNum1 = rowNum[entityLeftCol];
					rowNum = building[buildingIndex].buildingTileNum.get(entityBottomRow);
					tileNum2 = rowNum[entityLeftCol];
				}
				if (xa > 0) { // RIGHT
					rowNum = building[buildingIndex].buildingTileNum.get(entityTopRow);
					tileNum1 = rowNum[entityRightCol];
					rowNum = building[buildingIndex].buildingTileNum.get(entityBottomRow);
					tileNum2 = rowNum[entityRightCol];
				}
				if (tile[tileNum1].collision || tile[tileNum2].collision)
					return true;
			}

		}

		return false;

	}
}