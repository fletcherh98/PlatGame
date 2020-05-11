
package platgame.entity;
import org.joml.Vector2f;
import platgame.io.Window;
import platgame.render.Camera;
import platgame.render.Animation;
import platgame.world.World;

public class Enemy extends Entity{
    
    public int health = 2;
    public byte deathTimer = 0;
    boolean swinging = false;
    byte hitTimer = 0;
    int cooldown = 0;
    public static final int ANIM_MOVE = 0;
    public static final int ANIM_DIE = 1;
    public static final int ANIM_EXTRA_1 = 2;
    public static final int ANIM_EXTRA_2 = 3;
    public static final int ANIM_SIZE = 4;
    public Enemy(Transform transform, byte type2){
        super(ANIM_SIZE, transform);
        type = type2;   //type: 0 = zombie, 1 = bat, 2 = ghoul, 3 = boss
        isEnemy = true;
 
        if (type != (byte)3)
            transform.scale.set(1.5f,1.5f,1);
              
        if(type == 0){
            setAnimation(ANIM_MOVE, new Animation(2, 2, "enemy/zombie/move"));
            setAnimation(ANIM_DIE, new Animation(2, 2, "enemy/zombie/die"));
        }else if(type == 1){
            setAnimation(ANIM_MOVE, new Animation(4, 2, "enemy/bat/move"));
            setAnimation(ANIM_DIE, new Animation(2, 2, "enemy/bat/die"));
        }else if(type == 2){
            setAnimation(ANIM_MOVE, new Animation(2, 2, "enemy/ghoul/walk"));
            setAnimation(ANIM_DIE, new Animation(2, 2, "enemy/ghoul/die"));
            setAnimation(ANIM_EXTRA_1, new Animation(1, 2, "enemy/ghoul/jump"));
        }else if (type == 3){
            transform.scale.set(3.0f,3.0f,1);
            bounding_box.center.y-=1.0f;
            bounding_box.half_extent.y+=1.0f;
            health = 6;
            setAnimation(ANIM_MOVE, new Animation(1, 2, "enemy/boss/move"));
            setAnimation(ANIM_DIE, new Animation(2, 2, "enemy/boss/die"));
            setAnimation(ANIM_EXTRA_1, new Animation(1, 2, "enemy/boss/swing"));
            setAnimation(ANIM_EXTRA_2, new Animation(2, 2, "enemy/boss/hurt"));
        }
        
            
    }
    @Override
	public void update(float delta, Window window, Camera camera, World world) {
		Vector2f movement = new Vector2f();
                Vector2f gravity = new Vector2f();

            useAnimation(ANIM_MOVE);
            
                
                if(type != (byte)3){
                    if(((collidingSide && isCollidingTPer) || (collidingEntity != null && collidingEntity.isEnemy))&&!directionC){
                    directionC = true;
                    if(direction == 0){
                        direction = 1;
                        transform.scale.reflect(1,0,0);
                    }else{
                        direction = 0;
                        transform.scale.reflect(1,0,0);
                        }                   
                }
                    if(directionC)
                        directionCCounter++;
                    else
                        directionCCounter = 0;

                    if(directionCCounter > 10)
                        directionC=false;
                    if (!lockout){
                    if(direction == 0)
                        movement.add(-5 * delta, 0);
                    if(direction == 1)
                        movement.add(5 * delta, 0);
                    }
                }else{
                    float distancex = world.p1.transform.pos.x - transform.pos.x;
                    if (Math.abs(distancex) < 8.0f && cooldown > 120){
                        spawnProjectile(p = new Projectile(new Transform(), this, (byte)0), world);
                        cooldown = 0;
                        swinging = true;
                    }else
                        cooldown++;
                    if(distancex > 0){
                        movement.add(2 * delta, 0);
                        if (direction == 0)
                            transform.scale.reflect(1,0,0);
                        direction = 1;
                    }else{
                        movement.add(-2 * delta, 0);
                        if (direction == 1)
                            transform.scale.reflect(1,0,0);
                        direction = 0;
                    }
                }
                
                if(type != 1){
                    gravity.add(0, -25 * delta);
                    move(gravity);
                }
		move(movement);
        if (collidingEntity != null && collidingEntity.isProjectile && collidingEntity != this.p && !lockout){
            if(collidingEntity.type == (byte)0)
            health-=2;
            else
                health--;
            lockout = true;
        }
        if(swinging){
             useAnimation(ANIM_EXTRA_1);
             if (cooldown > 60)
                 swinging = false;
        }
        
        if(lockout){
            if(type == 3)
                useAnimation(ANIM_EXTRA_2);
            hitTimer++;
            if(hitTimer > 45){
                lockout = false;
                hitTimer = 0;
            }
        }
        if (health < 1){
            lockout = true;
            useAnimation(ANIM_DIE);
            deathTimer++;
            stopCollision = true;
            }
        
        if (deathTimer >= 60){
            readyForRemoval = true;
             deleteEntity();
            
        }
        }
        public void deleteEntity(){
            animations = null;
            bounding_box = null;
            transform = null;
            collidingEntity = null;
            p = null;
            
        }
}
