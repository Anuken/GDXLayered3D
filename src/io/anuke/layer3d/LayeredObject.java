package io.anuke.layer3d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LayeredObject{
	/**The textureregion layers for this object.*/
	public final TextureRegion[] regions;
	/**The position and rotation of the object.*/
	public float x, y, z, rotation;
	
	/**Creatures a layered object from all the regions.*/
	public LayeredObject(TextureRegion...regions){
		this.regions = regions;
	}
	
	/**Creatures a layered object from all the textures*/
	public LayeredObject(Texture... textures){
		regions = new TextureRegion[textures.length];
		for(int i = 0; i < textures.length; i ++) regions[i] = new TextureRegion(textures[i]);
	}
	
	/**Sets the position for the object. 
	 * @return the object, for chaining.*/
	public LayeredObject setPosition(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**Sets the position for the object. 
	 * @return the object, for chaining.*/
	public LayeredObject setPosition(float x, float y){
		return setPosition(x,y,z);
	}
}
