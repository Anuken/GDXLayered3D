package io.anuke.layer3d;

import com.badlogic.gdx.utils.Array;

public class LayerList{
	public Array<LayeredObject> objects = new Array<LayeredObject>();
	
	public LayeredObject get(int index){
		return objects.get(index);
	}
	
	public void add(LayeredObject object){
		object.add();
		objects.add(object);
	}
	
	public void free(){
		for(LayeredObject object : objects)
			object.remove();
	}
}
