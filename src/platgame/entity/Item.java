
package platgame.entity;

import org.joml.Vector2f;
import platgame.io.Window;
import platgame.render.Animation;
import platgame.render.Camera;
import platgame.world.World;

public class Item extends Entity{
    public static int sprite_type;
    public int[] sprite;
    public static final int SPRITE_CAR = 0;
    public static final int SPRITE_UFO = 1;
    public static final int SPRITE_ORB = 2;
    public static final int ANIM_SIZE = 3;
    public Item(Transform transform, int type2, Entity owner){
        super(ANIM_SIZE, transform);
        sprite_type = type2;
        type = (byte)type2;
        isItem = true;
        transform.scale.set(1.5f, 1.5f, 1.0f);
        if(owner != null){
            transform.pos.x = owner.transform.pos.x + 3.0f; 
                transform.pos.y = owner.transform.pos.y; 
                bounding_box.getCenter().set(owner.transform.pos.x + 3.0f, owner.transform.pos.y);
            
        }
        switch(type2){
            case 0:
                setAnimation(SPRITE_CAR, new Animation(1, 1, "item/car"));                
                break;
            case 1:
                setAnimation(SPRITE_UFO, new Animation(1, 1, "item/ufo"));                
                break;
            case 2:
                setAnimation(SPRITE_ORB, new Animation(1, 1, "item/orb"));                
                break;               
        }
                if(type == 0)
                    useAnimation(SPRITE_CAR);
                else if(type == 1)
                    useAnimation(SPRITE_UFO);
                else
                    useAnimation(SPRITE_ORB);
    }
    
    @Override
	public void update(float delta, Window window, Camera camera, World world) {
                Vector2f gravity = new Vector2f();
		if((collidingEntity != null && collidingEntity.isPlayer) || (world.p1.collidingEntity != null && world.p1.collidingEntity.equals(this))){ //has null check so there is no null pointer exception                         
                    world.p1.collidingEntity = this;
                    deleteEntity();
                    
                }     
        }
        public void deleteEntity(){
            readyForRemoval = true;
            animations = null;
            bounding_box = null;
            transform = null;
            collidingEntity = null;
            p = null;
        }
}
