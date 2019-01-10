import com.hector.engine.entity.Entity
import com.hector.engine.entity.events.AddEntityEvent
import com.hector.engine.event.EventSystem
import com.hector.engine.graphics.components.TextureComponent
import com.hector.engine.maths.Vector2f
import com.hector.engine.scripting.components.GroovyScript

class World extends GroovyScript {

    private TileMap tilemap

    @Override
    void init() {
        int width = 100
        int height = width
        int[] tileData = new int[width * height]

        int amount = 8

        String[] textures = new String[amount]

        for (int i = 0; i < amount; i++) {
            textures[i] = "textures/tiles/tile_" + i.toString().padLeft(4, '0') + ".png"
        }

        for (int i = 0; i < width * height; i++) {
            tileData[i] = new Random().nextInt(amount)
        }

        tilemap = new TileMap(textures, tileData, width, height)
        tilemap.computeEntities()
    }

}

class TileMap {

    private static final float TILE_SIZE = 0.3f

    private Entity[] entities
    private String[] textures
    private int[] tileData
    private int width
    private int height

    public TileMap(String[] textures, int[] tileData, int width, int height) {
        this.textures = textures
        this.tileData = tileData
        this.width = width
        this.height = height

        this.entities = new Entity[width * height]
    }

    public void computeEntities() {
        List<Entity> entityList = new ArrayList<>()

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vector2f position = new Vector2f((float) (x * TILE_SIZE), (float) (y * TILE_SIZE))
                Entity e = new Entity(position, new Vector2f(TILE_SIZE, TILE_SIZE), 0f)
                e.addComponent(new TextureComponent(textures[tileData[x + y * width]]))
                entityList.add(e)
            }
        }

        EventSystem.publish(new AddEntityEvent(entityList))
    }
}
