package platgame;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL;

import platgame.Assets;
import platgame.gui.GUI;
import platgame.io.Timer;
import platgame.io.Window;
import platgame.render.Camera;
import platgame.render.Shader;
import platgame.world.TileRenderer;
import platgame.world.World;

public class PlatGame {
        boolean startFlag = false;
	public PlatGame() {
		Window.setCallbacks();
		
		if (!glfwInit()) {
			System.err.println("GLFW Failed to initialize!");
			System.exit(1);
		}
		int cnt = 0;
		Window window = new Window();    
		window.setSize(640, 480);
		window.setFullscreen(false);
		window.createWindow("Game");
		
		GL.createCapabilities();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		Camera camera = new Camera(window.getWidth(), window.getHeight());
		glEnable(GL_TEXTURE_2D);
		
		TileRenderer tiles = new TileRenderer();
		Assets.initAsset();
		
		Shader shader = new Shader("shader");
		
		World[] worlds = new World[3];
                World start = new World("start", camera);
                worlds[0] = new World("level_1", camera);
                worlds[1] = new World("level_2", camera);
                worlds[2] = new World("level_3", camera);
                worlds[2].ending = true;
		worlds[0].calculateView(window);
		GUI startGUI = new GUI(window, start, 0);	
		double frame_cap = 1.0 / 60.0;
		
		//double frame_time = 0;
		//int frames = 0;
		
		double time = Timer.getTime();
		double unprocessed = 0;
                for (cnt = 0; cnt < 3; cnt++){
                    GUI gui = new GUI(window, worlds[cnt], 1);
                    while (!window.shouldClose()) {
                            boolean can_render = false;

                            double time_2 = Timer.getTime();
                            double passed = time_2 - time;
                            unprocessed += passed;
                            //frame_time += passed;

                            time = time_2;


                            while (unprocessed >= frame_cap) {

                                    if (window.hasResized()) {
                                            camera.setProjection(window.getWidth(), window.getHeight());
                                            //gui.resizeCamera(window);
                                            glViewport(0, 0, window.getWidth(), window.getHeight());
                                    }

                                    unprocessed -= frame_cap;
                                    can_render = true;

                                    if (window.getInput().isKeyReleased(GLFW_KEY_ESCAPE)) {
                                            glfwSetWindowShouldClose(window.getWindow(), true);
                                    }

                                    if (!startFlag){
                                        start.calculateView(window);
                                        start.update((float) frame_cap, window, camera);
                                        if (startGUI.b.selectedState == 2)
                                            startFlag = true;
                                        startGUI.update(window.getInput());

                                    }else{
                                        worlds[cnt].calculateView(window);
                                        gui.update(null);
                                        worlds[cnt].update((float) frame_cap, window, camera);
                                        worlds[cnt].correctCamera(camera, window);
                                    }
                                    window.update();


                            }

                            if (can_render) {
                                    glClear(GL_COLOR_BUFFER_BIT);								

                                    if (!startFlag){
                                        start.render(tiles, shader, camera);
                                        startGUI.render();
                                    }else{
                                    worlds[cnt].render(tiles, shader, camera);
                                    gui.render();
                                    }
                                    window.swapBuffers();

                            }
                            if (worlds[cnt].levelComplete == true)
                                    break;
                    }
                }
		Assets.deleteAsset();
		
		glfwTerminate();
	}
	
	public static void main(String[] args) {
		new PlatGame();
	}
	
}
