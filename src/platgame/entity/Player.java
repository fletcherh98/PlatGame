package platgame.entity;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import platgame.io.Window;
import platgame.render.Animation;
import platgame.render.Camera;
import platgame.world.World;

public class Player extends Entity {
        boolean carState = false;
        boolean ufoState = false;
        boolean flung = false;
        
        public boolean isJumping = false;
        public boolean hasJumped = false;
        public boolean weaponState = false;
        
        Vector2f spawnloc;
        
        float jumpTime = 1.0f;
        float slideTime = 0.0f;
        public byte health = 6;
        public byte playerTimer = 0;
        int mult = 1;
	public static final int ANIM_IDLE = 0;
	public static final int ANIM_WALK = 1;
        public static final int ANIM_JUMP = 2;
        public static final int ANIM_GLIDE = 3;
        public static final int ANIM_FALL = 4;
        public static final int ANIM_SLIDE = 5;
        public static final int ANIM_SWING = 6;
        public static final int ANIM_SHOOT = 7;
        public static final int ANIM_INT = 8;
        public static final int ANIM_UFO = 9;
        public static final int ANIM_CAR = 10;
        public static final int ANIM_HURT = 11;
        public static final int ANIM_DIE = 12;
        public static final int ANIM_WIN = 13;
	public static final int ANIM_SIZE = 14;
	
	public Player(Transform transform) {
		super(ANIM_SIZE, transform);
                spawnloc = new Vector2f(transform.pos.x, transform.pos.y);
                transform.scale.set(1.5f,1.5f,1);
                isPlayer = true;
		setAnimation(ANIM_IDLE, new Animation(1, 2, "player/neutral"));
		setAnimation(ANIM_WALK, new Animation(4, 2, "player/walk"));
                setAnimation(ANIM_JUMP, new Animation(1, 2, "player/jump"));
                setAnimation(ANIM_SLIDE, new Animation(1, 2, "player/slide"));
                setAnimation(ANIM_GLIDE, new Animation(1, 2, "player/glide"));
                setAnimation(ANIM_FALL, new Animation(1, 2, "player/fall"));
                setAnimation(ANIM_SWING, new Animation(1, 2, "player/swing"));
                setAnimation(ANIM_INT, new Animation(2, 2, "player/interact"));
                setAnimation(ANIM_HURT, new Animation(2, 4, "player/hurt"));
                setAnimation(ANIM_DIE, new Animation(2, 2, "player/die"));
                setAnimation(ANIM_CAR, new Animation(1, 1, "player/car"));
                setAnimation(ANIM_UFO, new Animation(1, 1, "player/ufo"));
                setAnimation(ANIM_WIN, new Animation(1, 2, "player/win"));
                setAnimation(ANIM_SHOOT, new Animation(1, 2, "player/shoot"));
	}
	
	@Override
	public void update(float delta, Window window, Camera camera, World world) {
		Vector2f movement = new Vector2f();
                Vector2f gravity = new Vector2f();
                
                if(collidingEntity != null ){
                    if (collidingEntity.isEnemy || (collidingEntity.isProjectile && !collidingEntity.equals(p))){
                        if(collidingEntity.type == 3 || collidingEntity.isProjectile)
                            flung = true;
                    lockout = true; 
                    ufoState = false;
                    carState = false;
                    }else if (collidingEntity.isItem){
                        switch(collidingEntity.type){
                            case 0:
                                carState = true;
                                ufoState = false;
                                break;
                            case 1:
                                ufoState = true;
                                carState = false;
                                break;
                            case 2:
                                levelComplete = true;
                                break;
                        }
                    }
                }
                if (!window.getInput().isKeyDown(GLFW.GLFW_KEY_F))
                    interacting = false;
                if(lockout){
                    playerTimer++;                       
                    if(playerTimer>30 && health > 0){
                        health--;
                        if(health>0){
                            lockout = false;
                            playerTimer = 0;
                        }
                    }

                    if(playerTimer<30){
                        if (flung)
                            mult = 3;
                        else
                            mult = 1;
                        if(direction == 0)
                            movement.add(6 * mult * delta, (15-playerTimer) * delta);
                        else
                            movement.add(-6 * mult * delta, (15-playerTimer) * delta);
                    }

                    if(playerTimer > 90){
                        playerTimer = 0;
                        lockout = false;
                        flung = false;
                        health = 6;
                        transform.pos.x = spawnloc.x;
                        transform.pos.y = spawnloc.y;
                    }
                }
                
                if(!ufoState && !lockout){gravity.add(0, -25 * delta);}
                if (window.getInput().isKeyDown(GLFW.GLFW_KEY_Q))
                    weaponState = false;
                if (window.getInput().isKeyDown(GLFW.GLFW_KEY_E))
                    weaponState = true;
                
		if (window.getInput().isKeyDown(GLFW.GLFW_KEY_A) && !lockout){ 
                    movement.add(-15 * delta, 0);
                    if (direction == 1) 
                        transform.scale.reflect(1,0,0);
                    direction = 0;
                }
		
		if (window.getInput().isKeyDown(GLFW.GLFW_KEY_D) && !lockout){
                    movement.add(15 * delta, 0);
                    if (direction == 0 /*&& !window.getInput().isKeyDown(GLFW.GLFW_KEY_A)*/)
                        transform.scale.reflect(1,0,0);
                    direction = 1;
                }
                if (!window.getInput().isKeyDown(GLFW.GLFW_KEY_W) && isJumping){
                    hasJumped = true;
                }
		
		if (window.getInput().isKeyDown(GLFW.GLFW_KEY_W) && !hasJumped && !lockout){
                    movement.add(0, 50/ jumpTime * delta );
                    
                    isJumping = true;
                    jumpTime+=.02f;
                    if (50/jumpTime * delta < .15f)
                        hasJumped = true;
                    
                }else{
                    isJumping = false;
                    jumpTime = 1;
                }
                if (isCollidingTPer){
                    hasJumped = false;
                    isJumping = false;
                }
                    if (lockout){
                        if (health > 0)
                            useAnimation(ANIM_HURT);                      
                        else
                            useAnimation(ANIM_DIE);
                    }else if (window.getInput().isKeyDown(GLFW.GLFW_KEY_SPACE) && !weaponState){
                        useAnimation(ANIM_SWING);              
                        if (!projectileActive){
                            proj((byte)0, world);
                            projectileActive = true;
                        }
                    }else if (window.getInput().isKeyDown(GLFW.GLFW_KEY_SPACE) && weaponState){
                        useAnimation(ANIM_SHOOT);
                        if (!projectileActive){
                            proj((byte)1, world);
                            projectileActive = true;
                        }   
                     }else if (movement.y != 0){
                        if (movement.y >= 0.4f)
                            useAnimation(ANIM_JUMP);
                        else
                            useAnimation(ANIM_GLIDE);
                     }else if (collidingSide && isCollidingTPer){
                        useAnimation(ANIM_SLIDE);
                        gravity.add(0, 20 * delta / (1.0f+slideTime));
                        slideTime+=.02f;

                    }else if (movement.x != 0 && isCollidingTPer){
                            useAnimation(ANIM_WALK);
                            slideTime = 0.0f;
                    }else if (!isCollidingTPer)
                        useAnimation(ANIM_FALL);
                    else if (window.getInput().isKeyDown(GLFW.GLFW_KEY_F)){
                        useAnimation(ANIM_INT);
                        interacting = true;
                    }else{ 
                        useAnimation(ANIM_IDLE);
                        slideTime = 0.0f;
                    }
                
                if(carState){
                    movement.mul(1.5f, 1.0f);
                    useAnimation(ANIM_CAR);
                }
                if(ufoState){
                    useAnimation(ANIM_UFO);
                    hasJumped = false;
                    jumpTime = 5.0f;
                    gravity.set(0,0); 
                    if (window.getInput().isKeyDown(GLFW.GLFW_KEY_S)) movement.add(0, -10 * delta); 
                }
                if(p != null && p.hit)
                    projectileActive = false;
		
                if(transform.pos.x > (world.width * 2) + 3 || transform.pos.x < -3 || transform.pos.y > 10 || transform.pos.y < -(world.height * 2) - 3){
                    health = 6;
                        transform.pos.x = spawnloc.x;
                        transform.pos.y = spawnloc.y;
                        carState = false;
                        ufoState = false;
                    
                }
                if (world.ending == true){
                    useAnimation(ANIM_WIN);
                    movement.set(0,0);
                    projectileActive = true;
                }
                move(gravity);
		move(movement);
		camera.getPosition().lerp(transform.pos.mul(-world.getScale(), new Vector3f()), 0.05f);
	}
        public void proj(byte type, World world){
            spawnProjectile(p = new Projectile(new Transform(), this, type), world);}
        public boolean getItem(){
        return false;}
        public void deleteEntity(){}
}
