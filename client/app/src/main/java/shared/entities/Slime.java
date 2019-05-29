package shared.entities;

import java.util.ArrayList;
import java.util.List;

import shared.entities.Entity;
import client.view.RenderItem;
import shared.general.Level;
import shared.tiles.Tile;

/**
 * Class that handles actions and rendering of a slime
 * @author Jelmer Firet
 */
public class Slime extends Entity {
	/**
	 * Constructs a new slime
	 * @param x x position of the bottom of the slime (game pixels)
	 * @param y y position of the bottom of the slime (game pixels)
	 * @author Jelmer Firet
	 */
	public Slime(double x,double y){
		super(x,y);
	}

	/**
	 * Sets the velocity of the slime
	 * @param dx x velocity of the slime (game pixels)
	 * @param dy y velocity of the slime (game pixels)
	 * @author Jelmer Firet
	 */
	public void setVelocity(double dx, double dy){
		this.xVel = dx;
		this.yVel = dy;
	}

	/**
	 * @return the RenderItem associated with this slime
	 * @author Jelmer Firet
	 */
	@Override
	public List<RenderItem> getRenderItem(){
		List<RenderItem> result = new ArrayList<>();
		RenderItem renderItem;
		if (xVel*xVel+yVel*yVel > 5){
			renderItem = new RenderItem("slime/redSlimeJump",
					(int)(Tile.WIDTH*xPos), (int)(Tile.HEIGHT*yPos),0.5,1.0);
		}
		else{
			renderItem = new RenderItem("slime/testSlime",
					(int)(Tile.WIDTH*xPos), (int)(Tile.HEIGHT*yPos),0.5,1.0);
		}
		if (xVel < 0){
			renderItem.setFlip(true);
		}
		result.add(renderItem);
		return result;
	}

	@Override
	public void invokeAll(Level level) {
	}

	@Override
	public boolean update(Level level) {
		return false;
	}
}
