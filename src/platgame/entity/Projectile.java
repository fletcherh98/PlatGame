/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platgame.entity;

import org.joml.Vector2f;
import platgame.io.Window;
import platgame.render.Camera;
import platgame.render.Animation;
import platgame.world.World;
public class Projectile extends Entity{
    //type false = melee swing, true = bullet
    boolean hit = false;
    Entity spawnsFrom;
    byte exTimer = -128;
    float v = 2.5f;
    public Projectile(Transform tr, Entity owner, byte t){
        super(1, tr);
        isProjectile = true;
        spawnsFrom = owner;
        direction = spawnsFrom.direction;
                if (spawnsFrom.direction == 0)
                    v *= -1f;
                if(spawnsFrom.isEnemy)
                    v*=1.5f;
        transform.scale.set(owner.transform.scale);
        if (type == 1)
            v = 0;
        transform.pos.x = spawnsFrom.transform.pos.x + v;
        transform.pos.y = spawnsFrom.transform.pos.y;
        //transform.pos.(2.0f, 1.0f, 0);
        
        type = t;
        if (type == 1){
            setAnimation(0, new Animation(1, 2, "projectile/shoot"));
            transform.scale.set(.125f,.125f,1);
            if (spawnsFrom.direction == 0)
                transform.scale.reflect(1,0,0);
        }else
            setAnimation(0, new Animation(1, 2, "projectile/swing"));
        
        useAnimation(0);
       
    }
    
    @Override
	public void update(float delta, Window window, Camera camera, World world) {
            
            if (type == 1){
                Vector2f movement = new Vector2f();
                if (direction == 0)
                movement.add(-40 * delta, 0);
                else
                movement.add(40 * delta, 0);
               move(movement);
                if (exTimer>-70)
                    deleteEntity();
                if (isCollidingTPer)
                    deleteEntity();
                if(collidingEntity != null && collidingEntity != spawnsFrom)
                    deleteEntity();
                
            }else{
                
                if (spawnsFrom.direction == 0){
                    v = -Math.abs(v);
                    if (direction == 1){
                        direction = 0;
                        transform.scale.reflect(1,0,0);
                    }
                    
                }
                if (spawnsFrom.direction == 1){
                    v = Math.abs(v);
                    if (direction == 0){
                        direction = 1;
                        transform.scale.reflect(1,0,0);
                    }    
                }
                if(!spawnsFrom.lockout){
                transform.pos.x = spawnsFrom.transform.pos.x + v; 
                transform.pos.y = spawnsFrom.transform.pos.y; 
                bounding_box.center.set(spawnsFrom.transform.pos.x + v, spawnsFrom.transform.pos.y);
                }else
                    deleteEntity();
                //transform.pos.y = spawnsFrom.transform.pos.y;
                if(exTimer>-60)
                     deleteEntity();    //have timer instead of immediately despawning so that, if bullet proj updates before
            }                           //enemy/player, it won't despawn before it hits its target
            exTimer++;
             
            }

        
        public void deleteEntity(){
            hit = true;
            readyForRemoval = true;
            animations = null;
            bounding_box = null;
            transform = null;
            collidingEntity = null;
            p = null;
            spawnsFrom = null;
        }
}
