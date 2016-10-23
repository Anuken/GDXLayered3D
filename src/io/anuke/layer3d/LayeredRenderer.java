package io.anuke.layer3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

public class LayeredRenderer{
	/** Vertical spacing between layers */
	public static final float spacing = 0.8f;
	public static final int steps =2;
	/**The rotation of all the objects in the world. Basically camera rotation.*/
	public float baserotation = 0f;
	/**The world scale. Higher value to zoom in.*/
	public float worldScale = 1f;
	private static LayeredRenderer instance;
	private Array<LayeredObject> objects = new Array<LayeredObject>();
	private SnapshotArray<TextureLayer> layers = new SnapshotArray<TextureLayer>();

	public static LayeredRenderer instance(){
		if(instance == null)
			instance = new LayeredRenderer();
		return instance;
	}

	/** Sorts all the texture layers. */
	public void sort(){
		layers.sort();
	}

	/** Renders all the texture layers to the batch. */
	public void render(Batch batch){
		
		for(TextureLayer layer : layers){
			float x = 0, y = layer.getZ();
			float rotation = layer.object.rotation + baserotation;
			TextureRegion region = layer.object.regions[layer.index];
			
			float oy = layer.object.y;
			float ox = layer.object.x;
			ox -= Gdx.graphics.getWidth()/2 * worldScale;
			oy -= Gdx.graphics.getHeight()/2  * worldScale;
			
			float cos = (float)Math.cos(baserotation * MathUtils.degRad);
			float sin = (float)Math.sin(baserotation * MathUtils.degRad);

			float newX = ox * cos - oy * sin;
			float newY = ox * sin + oy * cos;

			ox = newX;
			oy = newY;
			
			x += ox + Gdx.graphics.getWidth()/2 * worldScale;
			y += oy + Gdx.graphics.getHeight()/2 * worldScale;
			
			for(int i = 0; i < steps; i++){
				batch.draw(region, x - region.getRegionWidth() / 2, y - region.getRegionHeight() / 2,
						region.getRegionWidth() / 2, region.getRegionHeight() / 2, region.getRegionWidth(),
						region.getRegionHeight(), 1, 1, rotation);
				y += 1f/steps;
			}
		}
	}

	/** Adds an object to the renderer. */
	public void addObject(LayeredObject object){
		objects.add(object);
		for(int i = 0; i < object.regions.length; i++)
			layers.add(new TextureLayer(object, i));
		sort();
	}

	/** Removes an object from the renderer. */
	public void removeObject(LayeredObject object){
		objects.removeValue(object, true);

		// Removes layers associated with this object.
		TextureLayer[] layers = this.layers.begin();
		for(TextureLayer layer : layers){
			if(layer.object == object){
				this.layers.removeValue(layer, true);
			}
		}
		this.layers.end();
	}

	/*
	 * private void resetLayers(){ layers.clear(); for(LayeredObject object :
	 * objects) for(int i = 0; i < object.regions.length; i++) layers.add(new
	 * TextureLayer(object, i)); sort(); }
	 */

	static private class TextureLayer implements Comparable<TextureLayer>{
		final LayeredObject object;
		final int index;

		public TextureLayer(LayeredObject object, int index) {
			this.index = index;
			this.object = object;
		}

		public float getZ(){
			return object.z + index * spacing;
		}

		@Override
		public int compareTo(TextureLayer tex){
			if(getZ() < tex.getZ())
				return -1;
			if(getZ() > tex.getZ())
				return 1;
			return 0;
		}
	}
}
