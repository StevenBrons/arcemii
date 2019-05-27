package shared.entities;

import java.util.ArrayList;
import java.util.List;

import client.view.RenderItem;

/**
 * The class that handles rendering and actions of Sako, the player character
 * @author Jelmer Firet
 */
public class Player extends Entity {

	private String name = "Player#" + (int)(Math.random()*99999);
	private int xPos, yPos;
	private int xVel, yVel;
	private int color;

	public Player(){
	}

	/**
	 * Initialises the Player class
	 * @param x the x position of the feet of the player (game pixels)
	 * @param y the y position of the feet of the player (game pixels)
	 * @param color the color of the player's hat: 0=blue,1=green,2=grey,3=red
	 * @author Jelmer Firet
	 */
	public Player(int x, int y, int color){
		this.xPos = x;
		this.yPos = y;
		this.color = color;
	}

	/**
	 * Sets the velocity of the player
	 * @param dx the x velocity of the player (game pixels)
	 * @param dy the y velocity of the player (game pixels)
	 * @author Jelmer Firet
	 */
	public void setVelocity(int dx, int dy){
		this.xVel = dx;
		this.yVel = dy;
	}

	/**
	 * @return the render item associated with this player
	 * @author Jelmer Firet
	 */
	@Override
	public List<RenderItem> getRenderItem(){
		List<RenderItem> result = new ArrayList<>();
		RenderItem renderItem;
		if (xVel*xVel+yVel*yVel>5){
			switch (color) {
				case 0: renderItem = new RenderItem(
						"player/playerBlueWalking", xPos, yPos, 0.5, 1.0);break;
				case 1: renderItem = new RenderItem(
						"player/playerGreenWalking", xPos, yPos, 0.5, 1.0);break;
				case 2: renderItem = new RenderItem(
						"player/playerGreyWalking", xPos, yPos, 0.5, 1.0);break;
				case 3: renderItem = new RenderItem(
						"player/playerRedWalking", xPos, yPos, 0.5, 1.0);break;
				default: renderItem = new RenderItem(
						"player/playerBlueWalking", xPos, yPos, 0.5, 1.0);break;
			}
		}
		else{
			switch (color) {
				case 0: renderItem = new RenderItem(
						"player/playerBlueIdle", xPos, yPos, 0.5, 1.0);break;
				case 1: renderItem = new RenderItem(
						"player/playerGreenIdle", xPos, yPos, 0.5, 1.0);break;
				case 2: renderItem = new RenderItem(
						"player/playerGreyIdle", xPos, yPos, 0.5, 1.0);break;
				case 3: renderItem = new RenderItem(
						"player/playerRedIdle", xPos, yPos, 0.5, 1.0);break;
				default: renderItem = new RenderItem(
						"player/playerBlueIdle", xPos, yPos, 0.5, 1.0);break;
			}
		}
		if (xVel < 0){
			renderItem.setFlip(true);
		}
		result.add(renderItem);
		return result;
	}

	/**
	 * Set the name of the player.
	 * @param name
	 * @author Bram Pulles
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * @return name of the player.
	 * @author Bram Pulles
	 */
	public String getName(){
		return name;
	}

	/**
	 * @return x position of this player (game pixels)
	 * @author Jelmer Firet
	 */
	public int getxPos() {
		return xPos;
	}

	/**
	 * @return y position of this player (game pixels)
	 * @author Jelmer Firet
	 */
	public int getyPos() {
		return yPos;
	}
}
