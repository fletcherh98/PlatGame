package platgame.world;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.joml.*;

import platgame.collision.AABB;
import platgame.entity.*;
import platgame.io.Window;
import platgame.render.Camera;
import platgame.render.Shader;

public class World {
	private int viewX;
	private int viewY;
	private byte[] tiles;
	private AABB[] bounding_boxes;
	List<Entity> entities;
        List<Entity> addingE = new ArrayList<>();
	public int width;
	public int height;
	private int scale;
        public Player p1;
        public boolean levelComplete = false;
        public boolean ending = false;
	
	private Matrix4f world;
	
	public World(String world, Camera camera) {
		try {
                        InputStream is1 = new BufferedInputStream(getClass().getResourceAsStream("/levels/" + world + "/tiles.png"));
                        InputStream is2 = new BufferedInputStream(getClass().getResourceAsStream("/levels/" + world + "/entities.png"));
                        BufferedImage tile_sheet = ImageIO.read(is1);
			BufferedImage entity_sheet = ImageIO.read(is2);
			
			width = tile_sheet.getWidth();
			height = tile_sheet.getHeight();
			scale = 32;
			
			this.world = new Matrix4f().setTranslation(new Vector3f(0));
			this.world.scale(scale);
			
			
                        
			
			
			tiles = new byte[width * height];
			bounding_boxes = new AABB[width * height];
			entities = new ArrayList<>();
			
			Transform transform;
			int defaultRCTS = tile_sheet.getRGB(width-1, 0);
                        int defaultR = ((defaultRCTS >> 16) & 0xFF);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
                                    /* there's a bizarre bug where if image has less than 16 distinct rgb values,
                                    the bufferedImage "flattens" and makes them go to nearest 3rd value floored (eg 7,0,0 becomes 5,0,0)
                                    i dont know what causes this or how to prevent it other than having some dummy pixels in level picture 
                                    with rgb r values that will go to default 0 */
                                        int colorTileSheet = tile_sheet.getRGB(x, y);                                       
                                        int colorEntitySheet = entity_sheet.getRGB(x, y);                                       
					int red = ((colorTileSheet >> 16) & 0xFF);
					int entity_index = (colorEntitySheet >> 16) & 0xFF;
					int entity_alpha = (colorEntitySheet >> 24) & 0xFF;
					
					
					Tile t;
					try {
						t = Tile.tiles[red];
					}
					catch (ArrayIndexOutOfBoundsException e) {
                                                t = null;
                                                if (defaultR < Tile.tiles.length)
                                                t = Tile.tiles[defaultR];
						 //default background tile is whatever tile is at the top right
					}
					
					if (t != null) setTile(t, x, y);
                                       
                                       
					
					if (entity_alpha > 0) {
						transform = new Transform();
						transform.pos.x = x * 2;
						transform.pos.y = -y * 2;
                                                
                                                //entity index: 1 = player, 2-4 = enemy types, 5 = enemy boss, 6-8 = items, 9-11 npcs
						switch (entity_index) {
							case 1 :							// Player
								Player player = new Player(transform);
								entities.add(player);
                                                                p1 = player;
								camera.getPosition().set(transform.pos.mul(-scale, new Vector3f()));
								break;
                                                        case 2 :
                                                                Enemy enemy = new Enemy(transform, (byte)0);
                                                                entities.add(enemy);
                                                                break;
                                                        case 3 :
                                                                Enemy enemy2 = new Enemy(transform, (byte)1);
                                                                entities.add(enemy2);
                                                                break;
                                                        case 4 :
                                                                Enemy enemy3 = new Enemy(transform, (byte)2);
                                                                entities.add(enemy3);
                                                                break;
                                                        case 5: 
                                                                Enemy boss = new Enemy(transform, (byte)3);
                                                                entities.add(boss);
                                                                break;
                                                        case 6:
                                                                Item car = new Item(transform, 0, null);
                                                                entities.add(car);
                                                                break;
                                                        case 7:
                                                                Item ufo = new Item(transform, 1, null);
                                                                entities.add(ufo);
                                                                break;
                                                        case 8:
                                                                Item orb = new Item(transform, 2, null);
                                                                entities.add(orb);
                                                                break;
                                                        case 9:
                                                                NPC npc = new NPC(transform, (byte)0);
                                                                entities.add(npc); 
                                                                break;
                                                        case 10:
                                                                NPC npc2 = new NPC(transform, (byte)1);
                                                                entities.add(npc2);
                                                                break;
                                                        case 11:
                                                                NPC npc3 = new NPC(transform, (byte)2);
                                                                entities.add(npc3);
                                                                break;
                                                        case 12:
                                                                NPC endpc = new NPC(transform, (byte)2);
                                                                endpc.has = false;
                                                                entities.add(endpc);
                                                                break;    
							default :
								break;
						}
					}
				}
                               
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public World() {
		width = 64;
		height = 64;
		scale = 16;
		
		tiles = new byte[width * height];
		bounding_boxes = new AABB[width * height];
		
		world = new Matrix4f().setTranslation(new Vector3f(0));
		world.scale(scale);
	}
	
	public void calculateView(Window window) {
		viewX = (window.getWidth() / (scale * 2)) + 4;
		viewY = (window.getHeight() / (scale * 2)) + 4;
	}
	
	public Matrix4f getWorldMatrix() {
		return world;
	}
	
	public void render(TileRenderer render, Shader shader, Camera cam) {
		int posX = (int) cam.getPosition().x / (scale * 2);
		int posY = (int) cam.getPosition().y / (scale * 2);
		
		for (int i = 0; i < viewX; i++) {
			for (int j = 0; j < viewY; j++) {
				Tile t = getTile(i - posX - (viewX / 2) + 1, j + posY - (viewY / 2));
				if (t != null) render.renderTile(t, i - posX - (viewX / 2) + 1, -j - posY + (viewY / 2), shader, world, cam);
			}
		}
		
		for (Entity entity : entities) {
			entity.render(shader, cam, this);
		}
	}
	
	public void update(float delta, Window window, Camera camera) {
		
                while(!addingE.isEmpty()){
                    entities.add(addingE.remove(addingE.size()-1));
                }
                
		for (int i = 0; i < entities.size(); i++) {
                        if(!(entities.get(i).isProjectile && entities.get(i).type == 0))
			entities.get(i).collideWithTiles(this);
			for (int j = 0; j < entities.size(); j++) {
                                if(j!=i /*|| entities.get(i).stopCollision*/)
				entities.get(i).collideWithEntity(entities.get(j));
			}       //if you use this stopCollision flag, when the flag is active, the entity shoots up in the air????
                                //no idea what causes that
		}
                for (Entity entity : entities) {
			entity.update(delta, window, camera, this);
                        
                        if (entity instanceof Player){
                            if (entity.levelComplete){
                                levelComplete = true;
                                    }
                        }
		}
                for(int i = entities.size()-1; i >= 0; i--){
                    if(entities.get(i).readyForRemoval)
                        entities.remove(i);
                }
                
	}
	
	public void correctCamera(Camera camera, Window window) {
		Vector3f pos = camera.getPosition();
		
		int w = -width * scale * 2;
		int h = height * scale * 2;
		
		if (pos.x > -(window.getWidth() / 2) + scale) pos.x = -(window.getWidth() / 2) + scale;
		if (pos.x < w + (window.getWidth() / 2) + scale) pos.x = w + (window.getWidth() / 2) + scale;
		
		if (pos.y < (window.getHeight() / 2) - scale) pos.y = (window.getHeight() / 2) - scale;
		if (pos.y > h - (window.getHeight() / 2) - scale) pos.y = h - (window.getHeight() / 2) - scale;
	}
	
	public void setTile(Tile tile, int x, int y) {
		tiles[x + y * width] = tile.getId();
		if (tile.isSolid()) {
			bounding_boxes[x + y * width] = new AABB(new Vector2f(x * 2, -y * 2), new Vector2f(1, 1));
		}
		else {
			bounding_boxes[x + y * width] = null;
		}
	}
	
	public Tile getTile(int x, int y) {
		try {
			return Tile.tiles[tiles[x + y * width]];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public AABB getTileBoundingBox(int x, int y) {
		try {
			return bounding_boxes[x + y * width];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public int getScale() {
		return scale;
	}

        public void addEntity(Entity e){
            addingE.add(e);
            
        }
}
