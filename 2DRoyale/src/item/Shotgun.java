package item;

import java.awt.Rectangle;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.Player;
import main.Game;
import net.Pkt06Shoot;

public class Shotgun extends SuperWeapon {
	public Shotgun(Game game, Player player) {
		super(game, player);

		this.name = "Shotgun";
		this.typeId = 2;
		this.damage = 10;
		this.speed = 10;
		this.fireRate = 20;
		this.range = 6 * Game.tileSize;
		this.bulletSpread = 5; // in degrees
		this.bulletSize = 8;

		this.imgOffset = -3;
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
		this.entityArea.height = 18;
		this.entityArea.width = 18;
		this.entityArea.x = imgIconWidth / 2 - entityArea.width / 2;
		this.entityArea.y = imgIconHeight / 2 - entityArea.height / 2;
	}

	@Override
	public void shoot() {
		fireRateTick++;
		if (fireRateTick == fireRate) {
			// Spawn bullet at the player's location
			int worldX = game.player.getWorldX() + Game.playerSize / 2 - bulletSize / 2;
			int worldY = game.player.getWorldY() + Game.playerSize / 2 - bulletSize / 2;
			double angle = Math.atan2(game.player.getMouseX() - game.player.getScreenX(), game.player.getMouseY() - game.player.getScreenY());

			// Spawn 5 bullets with incrementing spread angles
			for (int i = -2; i < 3; i++) {
				double spreadRad = Math.toRadians(bulletSpread * i);
				// Update server on this shoot event
				new Pkt06Shoot(game.player.getUsername(), this.id, angle + spreadRad, worldX, worldY).sendData(game.socketClient);
			}

			fireRateTick = 0;
			if(game.getGameState() == Game.playState) 
				game.soundHandler.playSound(3);			
		}
	}
}
