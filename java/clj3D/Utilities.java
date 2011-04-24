package clj3D;

import java.util.Queue;
import org.lwjgl.opengl.GL11;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class Utilities {
	
	
	public static void swapCulling(Geometry geom){
		
		Matrix4f worldMatrix = geom.getWorldMatrix();
        float[] f1 = worldMatrix.getColumn(0);
		float[] f2 = worldMatrix.getColumn(1);
		float[] f3 = worldMatrix.getColumn(2);
        Vector3f v1 = new Vector3f(f1[0], f1[1], f1[2]);
        Vector3f v2 = new Vector3f(f2[0], f2[1], f2[2]);
        Vector3f v3 = new Vector3f(f3[0], f3[1], f3[2]);

        Vector3f cross = v1.cross(v2);
        float result = cross.dot(v3);

        if(result > 0)
        	geom.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        else
        	geom.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

	}
	
	public static void traverseAndSwap(Queue<Spatial> queue){
		
		if(!queue.isEmpty()){
			Spatial node = queue.remove();
			
			if(node instanceof Geometry)
				swapCulling((Geometry)node);
			else
				queue.addAll(((Node)node).getChildren());
				
			traverseAndSwap(queue);
		}	
	}
}
