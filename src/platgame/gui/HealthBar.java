package platgame.gui;

import org.joml.Matrix4f;
import platgame.Assets;
import platgame.render.Camera;
import platgame.render.Shader;
import platgame.render.TileSheet;
import platgame.world.World;
public class HealthBar {
    int hNum;
    World world;
    private static Matrix4f transform = new Matrix4f();

    public HealthBar(World world){
        this.world = world;
         hNum = world.p1.health;
    }
    public void update(){
        hNum = world.p1.health;
    }
    public void render(Camera camera, TileSheet sheet, Shader shader){
        int i;
        for(i = 0; i < world.p1.health-1; i+=2){
            transform.identity().translate(-250+30*i, 200, 0).scale(16, 16, 1); 
            shader.setUniform("projection", camera.getProjection().mul(transform));
            sheet.bindTile(shader, 0, 5);
            Assets.getModel().render();
        }
        if (world.p1.health % 2 == 1){
            transform.identity().translate(-250+30*i+1, 200, 0).scale(16, 16, 1); 
            shader.setUniform("projection", camera.getProjection().mul(transform));
            sheet.bindTile(shader, 1, 5);
            Assets.getModel().render();
        }
    }
}
