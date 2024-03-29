package client.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import client.activities.GameActivity;
import client.controller.ClientGameHandler;
import shared.entities.Arrow;
import shared.entities.Boss;
import shared.entities.Entity;
import shared.entities.Player;
import shared.entities.Skeleton;
import shared.entities.Slime;
import shared.general.Level;
import shared.tiles.Empty;
import shared.tiles.Tile;
import shared.tiles.Wall;

/**
 * View that draws the game
 * @author Jelmer Firet
 */

public class GameView extends View {
	public static Lock renderItemLock = new ReentrantLock();
	Bitmap screen;
	Canvas temporary;
	Rect src;
	Rect des;
	private List<RenderItem> renderItems = new ArrayList<>();
	private Level level;

	/**
	 * Constructs objects used for drawing to prevent them from being constructed every tick
	 * @author Jelmer Firet
	 */
	public void init(){
		int width = getWidth();
		int height = getHeight();
		screen = Bitmap.createBitmap(width/4,height/4,
			Bitmap.Config.ARGB_8888);
		temporary = new Canvas(screen);
		src = new Rect(0,0,width/4,height/4);
		des = new Rect(0,0,width,height);
	}

	/**
	 * Construct a new GameView
	 * @param context context to pass to View constructor
	 * @author Jelmer Firet
	 */
	public GameView(Context context) {
		super(context);
	}

	/**
	 * Draws the level and all entities to the screen
	 * @param canvas The Canvas to draw the level and entities on.
	 */
	@Override
	public void onDraw(Canvas canvas){
		if (screen == null) {
			this.init();
			return;
		}
		if (level == null) {
			Paint paint = new Paint();
			paint.setTextSize(80);
			canvas.drawText("Loading...",100,100,paint);
			return;
		}

		canvas.drawColor(Color.BLACK);
		Player player = ClientGameHandler.handler.getPlayer();
		int offsetX,offsetY;
		synchronized (player){
			offsetX = (getWidth()/8-(int)(Tile.WIDTH*player.getX()));
			offsetY = (getHeight()/8+(int)(Tile.HEIGHT*player.getY()));
		}
		renderItemLock.lock();
		for (RenderItem object:renderItems){
			object.renderTo(temporary,offsetX,offsetY);
		}
		renderItemLock.unlock();
		canvas.drawBitmap(screen,src, des,new Paint());
	}

	/**
	 * Update the RenderItems to draw the new Level instead of an older one
	 * @param level The level to draw
	 */
	public void updateLevel(Level level){
		if (screen == null) {
			return;
		}
		this.level = level;
		Player player = ClientGameHandler.handler.getPlayer();
		int minX,maxX,minY,maxY;
		synchronized (player){
			minX = -2+((int)(Tile.WIDTH*player.getX())-screen.getWidth()/2)/Tile.WIDTH;
			maxX =  2+((int)(Tile.WIDTH*player.getX())+screen.getWidth()/2)/Tile.WIDTH;
			minY = -2+((int)(Tile.HEIGHT*player.getY())-screen.getHeight()/2)/Tile.HEIGHT;
			maxY =  2+((int)(Tile.HEIGHT*player.getY())+screen.getHeight()/2)/Tile.HEIGHT;
		}
		renderItemLock.lock();
		renderItems.clear();
		for (int idx = 0;idx<level.getNumEntity();idx++){
			renderItems.addAll(level.getEntityAt(idx).getRenderItem());
		}

		for (int tileX = minX;tileX<=maxX;tileX++){
			for (int tileY = minY;tileY<=maxY;tileY++){
				renderItems.addAll(level.getTileAt(tileX,tileY)
					.getRenderItem(tileX,tileY));
			}
		}
		Collections.sort(renderItems);
		renderItemLock.unlock();
	}
}
