package tile;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Game;

public class TileManager {

	private Game game;
	public Tile[] tile;
	public int mapTileNum[][][];

	public TileManager(Game game) {

		this.game = game;
		tile = new Tile[20];			//Currently we just store 20 types of tiles

		mapTileNum = new int[game.maxWorldCol][game.maxWorldRow][2];	//create 2d array of the dimension of the world (units: tileSize)
		
		getTileImage();					//populate tile array
		loadMap("/maps/picture.png");	//load map
		
	}

	private void getTileImage() {
		
		//load png image [\res\tiles] and store into tile arr
		try {

			tile[0] = new Tile();
			tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass1.png"));
			
			tile[1] = new Tile();
			tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass2.png"));
			
			tile[2] = new Tile();
			tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass3.png"));
			
			tile[3] = new Tile();
			tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grasstall.png"));
			
			tile[4] = new Tile();
			tile[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/earth1.png"));
			
			tile[5] = new Tile();
			tile[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/earth2.png"));

			tile[6] = new Tile();
			tile[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wall.png"));
			tile[6].collision = true;

			tile[7] = new Tile();
			tile[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water.png"));
			tile[7].collision = true;
			
			tile[8] = new Tile();
			tile[8].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tree.png"));
			tile[8].collision = true;
			
			tile[9] = new Tile();
			tile[9].image = ImageIO.read(getClass().getResourceAsStream("/tiles/sand.png"));
			
			tile[10] = new Tile();
			tile[10].image = ImageIO.read(getClass().getResourceAsStream("/tiles/missing.png"));
			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void loadMap(String filePath) {
		try {		
			BufferedImage img = ImageIO.read(getClass().getResourceAsStream(filePath));
			
			for (int y = 0; y < game.maxWorldCol; y++) {
		         for (int x = 0; x < game.maxWorldRow; x++) {
		            //Retrieving contents of a pixel
		            int pixel = img.getRGB(x,y);
		            //Creating a Color object from pixel value
		            Color color = new Color(pixel, true);
		            //Retrieving the R G B values
		            int red = color.getRed();
		            int green = color.getGreen();
		            int blue = color.getBlue();
		            
		            double roll = Math.random();
		            
		            mapTileNum[x][y][1] = game.rand.nextInt(2);
		            if(red == 34 && green == 177 && blue == 76) {	//grass
		            	if(roll < (double)1/2)
		            		mapTileNum[x][y][0] = 0;        		
		            	else if(roll < (double)2/3)
		            		mapTileNum[x][y][0] = 1;
		            	else if(roll < (double)5/6)
		            		mapTileNum[x][y][0] = 2;
		            	else
		            		mapTileNum[x][y][0] = 3;
		            }
		            else if(red == 0 && green == 0 && blue == 0) {		//wall
		            	mapTileNum[x][y][0] = 6;
		            }
		            else if(red == 0 && green == 162 && blue == 232) {	//woter
		            	mapTileNum[x][y][0] = 7;
		            }
		            else if(red == 185 && green == 122 && blue == 87) {	//earth
		            	if(roll < 0.50)
		            		mapTileNum[x][y][0] = 4;
		            	else
		            		mapTileNum[x][y][0] = 5;
		            }
		            else if(red == 181 && green == 230 && blue == 29) {	//tree
		            	mapTileNum[x][y][0] = 8;
		            }
		            else if(red == 239 && green == 228 && blue == 176) {	//sand
		            	mapTileNum[x][y][0] = 9;
		            }
		            else {											//missing texture
		            	mapTileNum[x][y][0] = 10;
		            }
		         }
		      }
			
//			for (int y = 0; y < game.maxWorldCol; y++) {
//		         for (int x = 0; x < game.maxWorldRow; x++) {
//		        	 System.out.print(mapTileNum[x][y][0]);
//		         }
//		         System.out.println();
//		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
