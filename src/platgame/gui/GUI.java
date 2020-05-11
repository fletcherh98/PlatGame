package platgame.gui;

import platgame.io.Window;
import platgame.io.Input;
import org.joml.Vector2f;
import platgame.world.World;
import platgame.render.*;

public class GUI {
	private Shader shader;
	private Camera camera;
	private TileSheet sheet;
	private HealthBar hb;
	public Button b;
        WeaponSwap w;
        int type;
	
	public GUI(Window window, World world, int type) {
                this.type = type;
		shader = new Shader("gui");
		camera = new Camera(window.getWidth(), window.getHeight());
		sheet = new TileSheet("gui.png", 9);
                if (type == 0)
		b = new Button(new Vector2f(-32, -32), new Vector2f(84, 84));
                else{
                hb = new HealthBar(world);
                w = new WeaponSwap(world, new Vector2f(window.getWidth()/32, -32));
                }
	}
	
	public void resizeCamera(Window window) {
		camera.setProjection(window.getWidth(), window.getHeight());
                
	}
	
	public void update(Input input) {
		if (type == 0)
                b.update(input);
                else{
                    hb.update();
                    w.update();
                }
	}
	
	public void render() {
		shader.bind();
                if (type == 0)
		b.render(camera, sheet, shader);
                else{
                    hb.render(camera, sheet, shader);
                    w.render(camera, sheet, shader);
                }
	}
        public void deleteGUI(){
            shader = null;
            camera = null;
            sheet = null;
            hb = null;
            b = null;
            w = null;
        }
}
