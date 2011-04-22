/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package clj3D;

import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.bounding.BoundingSphere;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.PointLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
import com.jme3.system.JmeSystem;
import com.jme3.util.BufferUtils;

/**
 * <code>Viewer</code> extends the {@link com.jme3.app.Application}
 * class to provide default functionality like a first-person camera,
 * and an accessible root node that is updated and rendered regularly.
 * Additionally, <code>Viewer</code> will display a statistics view
 * using the {@link com.jme3.app.StatsView} class. It will display
 * the current frames-per-second value on-screen in addition to the statistics.
 * Several keys have special functionality in <code>Viewer</code>:<br/>
 *
 * <table>
 * <tr><td>Esc</td><td>- Close the application</td></tr>
 * <tr><td>C</td><td>- Display the camera position and rotation in the console.</td></tr>
 * <tr><td>M</td><td>- Display memory usage in the console.</td></tr>
 * <tr><td>R</td><td>- Restore camera original position</td></tr>
 * </table>
 */
public abstract class Viewer extends Application {

    protected Node rootNode = new Node("Root Node");
    protected Node guiNode = new Node("Gui Node");
    protected float secondCounter = 0.0f;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected StatsView statsView;
    protected FlyByCamera flyCam;
    protected PointLight cameraLight;
    protected DirectionalLight sunLight;
    
    //Non mostra la schermata iniziale con la scimmia
    protected boolean showSettings = false;
    private AppActionListener actionListener = new AppActionListener();
    protected Node objectsNode;

    private class AppActionListener implements ActionListener {

        public void onAction(String name, boolean value, float tpf) {
            if (!value) {
                return;
            }

            if (name.equals("SIMPLEAPP_Exit")) {
                stop();
            } else if (name.equals("SIMPLEAPP_CameraPos")) {
                if (cam != null) {
                    Vector3f loc = cam.getLocation();
                    Quaternion rot = cam.getRotation();
                    System.out.println("Camera Position: ("
                            + loc.x + ", " + loc.y + ", " + loc.z + ")");
                    System.out.println("Camera Rotation: " + rot);
                    System.out.println("Camera Direction: " + cam.getDirection());
                }
            } else if (name.equals("SIMPLEAPP_Memory")) {
                BufferUtils.printCurrentDirectMemory(null);
            }
            
            else if (name.equals("SIMPLEAPP_ResetPos")) {
                cam.setRotation(new Quaternion(0,1,0,0));
                cam.setLocation(new Vector3f(3, 3, 3));
                cam.lookAt(new Vector3f(0,0,0), new Vector3f(0,0,1));
            }
        }
    }

    public Viewer() {
        super();
        this.cameraLight = new PointLight();
        this.cameraLight.setColor(ColorRGBA.White);
        this.cameraLight.setRadius(10f);
        this.sunLight = new DirectionalLight();
        this.sunLight.setColor(ColorRGBA.White);
        this.sunLight.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
    }

    @Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        boolean loadSettings = false;
        if (settings == null) {
            setSettings(new AppSettings(true));
            loadSettings = true;
        }

        // show settings dialog
        if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
                return;
            }
        }
        //re-setting settings they can have been merged from the registry.
        setSettings(settings);
        super.start();
    }

    /**
     * Retrieves flyCam
     * @return flyCam Camera object
     *
     */
    public FlyByCamera getFlyByCamera() {
        return flyCam;
    }

    /**
     * Retrieves guiNode
     * @return guiNode Node object
     *
     */
    public Node getGuiNode() {
        return guiNode;
    }

    /**
     * Retrieves rootNode
     * @return rootNode Node object
     *
     */
    public Node getRootNode() {
        return rootNode;
    }

    public boolean isShowSettings() {
        return showSettings;
    }

    /**
     * Toggles settings window to display at start-up
     * @param showSettings Sets true/false
     *
     */
    public void setShowSettings(boolean showSettings) {
        this.showSettings = showSettings;
    }
    
    
    public void createAxis(){
        	Line x = new Line(Vector3f.ZERO, Vector3f.UNIT_X);
            Line y = new Line(Vector3f.ZERO, Vector3f.UNIT_Y);
            Line z = new Line(Vector3f.ZERO, Vector3f.UNIT_Z);

            x.setLineWidth(3f);
            y.setLineWidth(3f);
            z.setLineWidth(3f);

            Geometry xg = new Geometry("x axis", x);
            Geometry yg = new Geometry("y axis", y);
            Geometry zg = new Geometry("z axis", z);

            xg.setIgnoreTransform(true);
            yg.setIgnoreTransform(true);
            zg.setIgnoreTransform(true);

            Material mx = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mx.setColor("Color", ColorRGBA.Red);

            Material my = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            my.setColor("Color", ColorRGBA.Green);

            Material mz = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mz.setColor("Color", ColorRGBA.Blue);

            xg.setMaterial(mx);
            yg.setMaterial(my);
            zg.setMaterial(mz);

            rootNode.attachChild(xg);
            rootNode.attachChild(yg);
            rootNode.attachChild(zg);
        }


    @Override
    public void initialize() {
        super.initialize();

        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        viewPort.attachScene(rootNode);
        guiViewPort.attachScene(guiNode);
        createAxis();
        
        cam.setRotation(new Quaternion(0,1,0,0));

        if (inputManager != null) {
            flyCam = new FlyByCamera(cam);
            flyCam.registerWithInput(inputManager);

            if (context.getType() == Type.Display) {
                inputManager.addMapping("SIMPLEAPP_Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
            }

            inputManager.addMapping("SIMPLEAPP_ResetPos", new KeyTrigger(KeyInput.KEY_R));
            inputManager.addListener(actionListener, "SIMPLEAPP_Exit", "SIMPLEAPP_ResetPos");
        }

        cameraLight.setPosition(cam.getLocation());
        rootNode.addLight(cameraLight);
        rootNode.addLight(this.sunLight);
        
        
        // call user code
        simpleInitApp();
        this.getFlyByCamera().setUpVector(Vector3f.UNIT_Z);
        cameraGuessBestPosition();
    }
    
    private void cameraGuessBestPosition() {
    	this.rootNode.setModelBound(new BoundingSphere());
		float radius = ((BoundingSphere)this.rootNode.getWorldBound()).getRadius();
		Vector3f center = ((BoundingSphere)this.rootNode.getWorldBound()).getCenter();
		float cameraNewX = radius + radius/2;
		float cameraNewZ = radius + radius/2;
		float cameraOldY = radius + radius/2; 
		
		//this.cam.getLocation().y;

		this.cam.setLocation(new Vector3f(cameraNewX, cameraOldY, cameraNewZ));
		this.cam.lookAt(center, Vector3f.UNIT_Z);
		this.flyCam.setMoveSpeed(radius);
		this.cameraLight.setPosition(cam.getLocation());
		cam.setFrustumPerspective(45f, (float)cam.getWidth() / cam.getHeight(), 0.1f*radius, 500f*radius);
    }

    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        secondCounter += timer.getTimePerFrame();
        int fps = (int) timer.getFrameRate();
        if (secondCounter >= 1.0f) {
            //fpsText.setText("Frames per second: " + fps);
            secondCounter = 0.0f;
        }

        // update states
        stateManager.update(tpf);

        // simple update and root node
        simpleUpdate(tpf);
        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        stateManager.render(renderManager);
        renderManager.render(tpf);
        simpleRender(renderManager);
        stateManager.postRender();
    }

    public abstract void simpleInitApp();

    public void simpleUpdate(float tpf) {
    	viewPort.setBackgroundColor(new ColorRGBA(185/255.0f,211/255.0f,238/255.0f,1.0f));
    	cameraLight.setPosition(cam.getLocation());
    }

    public void simpleRender(RenderManager rm) {
    }
    
    public void view(Node objNode) {
		
        this.objectsNode = objNode;
        this.start();
	}
    
}
