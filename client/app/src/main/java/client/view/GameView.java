package client.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shared.entities.Entity;
import shared.entities.Player;
import shared.entities.Slime;
import shared.general.Level;
import shared.tiles.Empty;
import shared.tiles.Tile;

public class GameView extends View {
    public GameView(Context context) {
        super(context);
        Tile grass = new Empty();
        Entity player = new Player();
        Entity slime = new Slime();
        for (int x = 0;x<5;x++){
            for (int y = 0;y<5;y++){
                drawObjects.add(grass.getRenderItem(Tile.WIDTH*x,Tile.HEIGHT*y));
            }
        }
        drawObjects.add(player.getRenderItem());
        drawObjects.add(slime.getRenderItem());
    }

    private List<RenderItem> drawObjects = new ArrayList<>();

    @Override
    public void onDraw(Canvas canvas){
        Collections.sort(drawObjects);
        for (RenderItem object: drawObjects){
            object.renderTo(canvas);
        }
    }

    public void updateLevel(Level level){
        drawObjects.clear();
        for (int x = 0;x<level.getWidth();x++){
            for (int y = 0;y<level.getHeight();y++){
                drawObjects.add(level.getTileAt(x,y).getRenderItem(24* Tile.WIDTH,
                        24*Tile.HEIGHT+Tile.TOPOFFSET));
            }
        }
        for (int idx = 0;idx<level.getNumEntity();idx++){
            drawObjects.add(level.getEntityAt(idx).getRenderItem());
        }
    }
}