
package platgame.entity;
import org.joml.Vector2f;
import platgame.io.Window;
import platgame.render.Camera;
import platgame.render.Animation;
import platgame.world.World;

public class NPC extends Entity{
    public boolean has = true;
    public static final int ANIM_NEUTRAL = 0;
    public static final int ANIM_PRESENT = 1;
    public static final int ANIM_SIZE = 2;
    
    public NPC(Transform transform, byte t){
        super(ANIM_SIZE, transform);
        type = t;
        setAnimation(ANIM_NEUTRAL, new Animation(1, 1, "npc/neutral"));
        setAnimation(ANIM_PRESENT, new Animation(2, 2, "npc/present"));
    }
    public void update(float delta, Window window, Camera camera, World world){
        float distancex = world.p1.transform.pos.x - transform.pos.x;
        float distancey = world.p1.transform.pos.y - transform.pos.y;
        if(Math.abs(distancex) < 4.0f && Math.abs(distancey) < 4.0f && world.p1.interacting && has){
            has = false;
            stopCollision = true;
            switch(type){
                case 0: world.addEntity(new Item(new Transform(), 0, this));
                    break;
                case 1: world.addEntity(new Item(new Transform(), 1, this));
                    break;
                case 2: world.p1.health = 6;
            }
        }
        if (has)
            useAnimation(ANIM_NEUTRAL);
        else
            
            useAnimation(ANIM_PRESENT);
    }
    
    public void deleteEntity(){}
}