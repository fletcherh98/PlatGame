
package platgame.gui;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import platgame.Assets;
import platgame.render.Camera;
import platgame.render.Shader;
import platgame.render.TileSheet;
import platgame.world.World;

public class WeaponSwap {
    static final int STATE_SWORD = 0;
    static final int STATE_GUN = 1;
    World w;
    Vector2f pos;
    int selectedState;
    private static Matrix4f transform = new Matrix4f();
    public WeaponSwap(World world, Vector2f position){
        selectedState = STATE_SWORD;
        pos = position;
        w = world;

    }
    public void update(){
        if (w.p1.weaponState)
            selectedState = STATE_GUN;
        else
            selectedState = STATE_SWORD;
    }
    public void render(Camera camera, TileSheet sheet, Shader shader){
        for (int i = 0; i < 8; i++){
            int j = i;
            if(j >= 4)
                j++;
        transform.identity().translate(pos.x+30*j, 200, 0).scale(16, 16, 1); 
        shader.setUniform("projection", camera.getProjection().mul(transform));
        if(selectedState == STATE_GUN)
            sheet.bindTile(shader, i, 3);
        else
            sheet.bindTile(shader, i, 4);
        Assets.getModel().render();
        }
    }
}
