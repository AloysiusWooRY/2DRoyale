package net;

public class Pkt01Login implements Packet {

	public int id = 1;
	private String username;

	private int worldX;
	private int worldY;
	private int playerWeapIndex;

	public Pkt01Login(String username, int worldX, int worldY, int playerWeapIndex) {
		this.username = username;
		this.worldX = worldX;
		this.worldY = worldY;
		this.playerWeapIndex = playerWeapIndex;
	}

	public Pkt01Login(byte[] data) {
		String message = new String(data).trim().substring(2);
		String[] dataArr = message.split(",");
		this.username = dataArr[0];
		this.worldX = Integer.parseInt(dataArr[1]);
		this.worldY = Integer.parseInt(dataArr[2]);
		this.playerWeapIndex = Integer.parseInt(dataArr[3]);
	}
	
	@Override
	public void sendData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void sendData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		return ("01"+getUsername()+","+getWorldX()+","+getWorldY()+","+getPlayerWeapIndex()).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getWorldX() {
		return worldX;
	}

	public int getWorldY() {
		return worldY;
	}
	
	public int getPlayerWeapIndex() {
		return playerWeapIndex;
	}

}