package clj3D;

import java.util.Queue;
import org.lwjgl.opengl.GL11;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


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
	
	
	public static void mirrorAlong(int axis, Geometry geometry){

    		int i = axis-1;
    		Mesh oldMesh = geometry.getMesh().deepClone();

    		FloatBuffer oldPoints = (FloatBuffer)oldMesh.getBuffer(Type.Position).getData();
    		ShortBuffer oldIndexes = (ShortBuffer)oldMesh.getBuffer(Type.Index).getData();

    		while(i < oldPoints.capacity()){
    			float oldValue = oldPoints.get(i);
    			oldPoints.put(i, -oldValue);
    			i+=3;
    		}


    		//Invert wise order
    		i = 0;
    		while(i < oldIndexes.capacity()){
    			short oldValue = oldIndexes.get(i+2);
    			oldIndexes.put(i+2, oldIndexes.get(i+1));
    			oldIndexes.put(i+1, oldValue);
    			i+=3;
    		}



    		oldMesh.setBuffer(Type.Position, 3, oldPoints);
    		oldMesh.setBuffer(Type.Index, 1, oldIndexes);
    		geometry.setMesh(oldMesh);
    		geometry.updateModelBound();

    	}
	
	
	public static void traverseAndMirror(int axis, Queue<Spatial> queue){
		
		if(!queue.isEmpty()){
			Spatial node = queue.remove();
			
			if(node instanceof Geometry)
				mirrorAlong(axis, (Geometry)node);
			else
				queue.addAll(((Node)node).getChildren());
				
			traverseAndMirror(axis, queue);
		}	
	}
}
