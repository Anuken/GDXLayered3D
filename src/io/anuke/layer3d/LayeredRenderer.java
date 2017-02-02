package io.anuke.layer3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.SnapshotArray;

public class LayeredRenderer{
	/** Expansion coefficient.*/
	public static final float e = 0.001f;
	/**Global renderer instance. Use is optional.*/
	private static LayeredRenderer instance;
	/** Vertical spacing between layers. */
	public float spacing = 1f;
	/**Steps per layer. Increase this to reduce rough edges.*/
	public int steps = 1;
	/**The rotation of all the objects in the world. Basically camera rotation. */
	public float baserotation = 0f;
	/** The camera to use for rendering.*/
	public OrthographicCamera camera;
	/** Whether or not to draw shadows. Makes the model look more solid, but will affect performance. */
	public boolean drawShadows = false;
	private SnapshotArray<TextureLayer> layers = new SnapshotArray<TextureLayer>();
	private boolean needsSort;
	
	/** Returns the instance of the LayeredRenderer*/
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
		if(needsSort) sort();

		for(TextureLayer layer : layers){
			float x = 0, y = layer.getZ();
			float rotation = layer.object.rotation + baserotation;
			TextureRegion region = layer.object.regions[layer.index];

			float oy = layer.object.y;
			float ox = layer.object.x;
			ox -= camera.position.x;
			oy -= camera.position.y;

			float cos = (float) Math.cos(baserotation * MathUtils.degRad);
			float sin = (float) Math.sin(baserotation * MathUtils.degRad);

			float newX = ox * cos - oy * sin;
			float newY = ox * sin + oy * cos;

			ox = newX;
			oy = newY;

			x += ox + camera.position.x;
			y += oy + camera.position.y;

			if(drawShadows){
				batch.setColor(new Color(0, 0, 0, 0.1f));
				batch.draw(region, x - region.getRegionWidth() / 2, y - region.getRegionHeight() / 2 - spacing,
						region.getRegionWidth() / 2, region.getRegionHeight() / 2, region.getRegionWidth(),
						region.getRegionHeight(), 1, 1, rotation);

				batch.setColor(Color.WHITE);
			}

			for(int i = 0; i <= steps; i++){
				batch.draw(region, x - region.getRegionWidth() / 2 - e, y - region.getRegionHeight() / 2  - e,
						region.getRegionWidth() / 2, region.getRegionHeight() / 2, region.getRegionWidth() + e*2,
						region.getRegionHeight() + e*2, 1, 1, rotation);
				y += spacing / steps;
			}
		}
	}

	/** Adds an object to the renderer. */
	public void addObject(LayeredObject object){
		for(int i = 0; i < object.regions.length; i++)
			layers.add(new TextureLayer(object, i));
		needsSort = true;
	}

	/** Removes an object from the renderer. */
	public void removeObject(LayeredObject object){
		// Removes layers associated with this object.
		Object[] layers = this.layers.begin();
		for(Object layer : layers){
			if(layer == null) continue;
			if(((TextureLayer)layer).object == object){
				this.layers.removeValue((TextureLayer)layer, true);
			}
		}
		this.layers.end();
	}
	
	private class TextureLayer implements Comparable<TextureLayer>{
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
