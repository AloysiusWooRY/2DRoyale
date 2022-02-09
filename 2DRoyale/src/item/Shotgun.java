package item;

import java.awt.Rectangle;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.Player;
import main.Game;
import net.Pkt06Shoot;

public class Shotgun extends SuperWeapon {
	public Shotgun(Player player, Game game) {
		super(player, game);

		this.name = "Shotgun";
		this.typeId = 2;
		this.imgOffset = -3;
		this.speed = 10;
		this.range = 6 * game.tileSize;
		this.bulletSpread = 5; //in degrees
		this.bulletSize = 8;
		this.fireRate = 20;		
		this.damage = 10;

		try {
			this.sprite = ImageIO.read(getClass().getResourceAsStream("/player/shottyhand.png"));
			this.entityImg = ImageIO.read(getClass().getResourceAsStream("/weap/shotty.png"));
			this.bulletImg = ImageIO.read(getClass().getResourceAsStream("/projectile/bullet1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.imgIconWidth = 64;
		double scale = (double) imgIconWidth / entityImg.getWidth();
		this.imgIconHeight = (int) (entityImg.getHeight() * scale);
		
		this.entityArea = new Rectangle();
		entityArea.height = 18;
		entityArea.width = 18;
		entityArea.x = imgIconWidth/2 - entityArea.width/2;
		entityArea.y = imgIconHeight/2 - entityArea.height/2;
	}

	@Override
	public void shoot() {

		fireRateTick++;
		if(fireRateTick == fireRate) {
			int worldX = game.player.worldX + game.playerSize / 2 - bulletSize / 2;
			int worldY = game.player.worldY + game.playerSize / 2 - bulletSize / 2;
			double angle = Math.atan2(game.player.mouseX - game.player.screenX, game.player.mouseY - game.player.screenY);
			
			for(int i = -2; i < 3; i++) {
				double spreadRad = Math.toRadians(bulletSpread*i);				
				Pkt06Shoot shootPacket = new Pkt06Shoot(game.player.getUsername(), this.id, angle+spreadRad, worldX, worldY);
				shootPacket.sendData(game.socketClient);
			}
			
			fireRateTick = 0;
		}	

	}

}
