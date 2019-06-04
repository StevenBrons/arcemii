package shared.entities;

import java.util.ArrayList;
import java.util.List;

import client.view.RenderItem;
import shared.abilities.Range;
import shared.general.Level;
import shared.tiles.Tile;

import static java.lang.Math.atan2;

/**
 * Class that handles actions and rendering of a Skeleton
 * @author Jelmer Firet
 */
public class Skeleton extends Entity {
	private boolean shooting;
	private Range rangedAttack = new Range();

	/**
	 * Constructs a new skeleton
	 * @param x the x position of the feet of the skeleton (game pixels)
	 * @param y the y position of the feet of the skeleton (game pixels)
	 * @author Jelmer Firet
	 */
	public Skeleton(double x,double y){
		super(x,y);
	}


	/**
	 * Sets whether this skeleton is shooting an arrow
	 * @param shooting describes if the skeleton is shooting
	 * @author Jelmer Firet
	 */
	public void setShooting(boolean shooting){
		this.shooting = shooting;
	}

	/**
	 * @return the RenderItem associated with the current state of this skeleton
	 * @author Jelmer Firet
	 */
	@Override
	public List<RenderItem> getRenderItem(){
		List<RenderItem> result = new ArrayList<>();
		RenderItem renderItem;
		if (shooting){
			renderItem = new RenderItem("skeleton/skeletonShooting",
					(int)(Tile.WIDTH*xPos), -(int)(Tile.HEIGHT*yPos),0.5,1.0,2);
		}
		else if (xVel*xVel+yVel*yVel>0.005){
			renderItem = new RenderItem("skeleton/skeletonWalking",
					(int)(Tile.WIDTH*xPos), -(int)(Tile.HEIGHT*yPos),0.5,1.0,2);
		}
		else{
			renderItem = new RenderItem("skeleton/skeletonIdle",
					(int)(Tile.WIDTH*xPos), -(int)(Tile.HEIGHT*yPos),0.5,1.0,2);
		}
		if (xVel < 0){
			renderItem.setFlip(true);
		}
		result.add(renderItem);
		return result;
	}

	@Override
	public void invokeAll(Level level) {
		this.actions.clear();
		if (rangedAttack.available()){
			for (int i = 0;i<level.getNumEntity();i++){
				Entity player = level.getEntityAt(i);
				if (player instanceof Player && level.freeLine(xPos,yPos,player.getX(),player.getY())){
					invoke(rangedAttack.invoke(atan2(player.getY()-yPos,player.getX()-xPos)));
				}
			}
		}
	}


}
