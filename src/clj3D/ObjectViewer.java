
package clj3D;

public class ObjectViewer extends Viewer {
 
    @Override
    public void simpleInitApp() {
        rootNode.attachChild(this.objectsNode);
    }
}