package platgame.entity;

import org.joml.*;

import platgame.Assets;
import platgame.collision.AABB;
import platgame.collision.Collision;
import platgame.io.Window;
import platgame.render.*;
import platgame.world.World;

public abstract class Entity {
	protected AABB bounding_box;
	// private Texture texture;
	protected Animation[] animations;
        public boolean stopCollision = false;
	private int use_animation;
        public byte type;
        public boolean readyForRemoval = false;
        public boolean isPlayer = false;
        public boolean isItem = false;
        public boolean isEnemy = false;
        public boolean isProjectile = false;
        public boolean isNPC = false;
        public boolean interacting = false;
        public boolean levelComplete = false;
	protected boolean isCollidingT = false;
        protected boolean isCollidingTPer = false;
        protected Entity collidingEntity = null;
        protected byte collisionTimerE = 0;
        protected boolean collidingSide = false;
        protected int direction = 1;    //0 is left, 1 is right
        protected boolean directionC = false;
        protected byte directionCCounter = 0;
        boolean projectileActive;
        public boolean lockout = false;
	protected Transform transform;
        Projectile p;
	
	public Entity(int max_animations, Transform transform) {
		this.animations = new Animation[max_animations];
		
		this.transform = transform;
		this.use_animation = 0;
		
		bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(transform.scale.x, transform.scale.y));
	}
	
	protected void setAnimation(int index, Animation animation) {
		animations[index] = animation;
	}
	
	public void useAnimation(int index) {
		this.use_animation = index;
	}
	
	public void move(Vector2f direction) {
		transform.pos.add(new Vector3f(direction, 0));
		
		bounding_box.getCenter().set(transform.pos.x, transform.pos.y);
	}
	
	public void collideWithTiles(World world) {
		AABB[] boxes = new AABB[25];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				boxes[i + j * 5] = world.getTileBoundingBox((int) (((transform.pos.x / 2) + 0.5f) - (5 / 2)) + i, (int) (((-transform.pos.y / 2) + 0.5f) - (5 / 2)) + j);
			}
		}
		
		AABB box = null;
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i] != null) {
				if (box == null) box = boxes[i];
				
				Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
				Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
				
				if (length1.lengthSquared() > length2.lengthSquared()) {
					box = boxes[i];
				}
			}
		}
		if (box != null) {
			Collision data = bounding_box.getCollision(box);
			if (data.isIntersecting) {
				collidingSide = bounding_box.correctPosition(box, data);
                                
				transform.pos.set(bounding_box.getCenter(), 0);
                            isCollidingT = true;
                            isCollidingTPer = true;
                        }else {                          
                            if(isCollidingT)
                                isCollidingT = false;
                            else
                                isCollidingTPer = false;
                                
                        }
                        
                        
			
			for (int i = 0; i < boxes.length; i++) {
				if (boxes[i] != null) {
					if (box == null) box = boxes[i];
					
					Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
					Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
					
					if (length1.lengthSquared() > length2.lengthSquared()) {
						box = boxes[i];
					}
				}
			}
			
			data = bounding_box.getCollision(box);
			if (data.isIntersecting) {
				bounding_box.correctPosition(box, data);
				transform.pos.set(bounding_box.getCenter(), 0);
                        }
		}
                
	}
	
                
                
	public abstract void update(float delta, Window window, Camera camera, World world);
	
	public void render(Shader shader, Camera camera, World world) {
		Matrix4f target = camera.getProjection();
		target.mul(world.getWorldMatrix());
		
		shader.bind();
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", transform.getProjection(target));
		animations[use_animation].bind(0);
		Assets.getModel().render();
	}
	
	public void collideWithEntity(Entity entity) {
		Collision collision = bounding_box.getCollision(entity.bounding_box);
		
		if (collision.isIntersecting) {
                        collisionTimerE = 0;
                        collidingEntity = entity;
			collision.distance.x /= 2;
			collision.distance.y /= 2;
			
			bounding_box.correctPosition(entity.bounding_box, collision);
			transform.pos.set(bounding_box.getCenter().x, bounding_box.getCenter().y, 0);
			
			entity.bounding_box.correctPosition(bounding_box, collision);
			entity.transform.pos.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y, 0);
		}else{
                    collisionTimerE++;
                    if(collisionTimerE > 10)
                        collidingEntity = null;
                }
	}
        public void spawnProjectile(Projectile p, World world){
            world.addEntity(p);
        }
        abstract public void deleteEntity();
        
}

