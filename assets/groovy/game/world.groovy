import com.hector.engine.entity.Entity
import com.hector.engine.entity.events.AddEntityEvent
import com.hector.engine.event.EventSystem
import com.hector.engine.graphics.components.TextureComponent
import com.hector.engine.graphics.Texture
import com.hector.engine.maths.Vector2f
import com.hector.engine.resource.ResourceManager
import com.hector.engine.resource.resources.TextureResource
import com.hector.engine.scripting.components.GroovyScript

class World extends GroovyScript {

    private int worldSize = 10

    private static Texture[] textures

    private Tile[] tiles

    void init() {
        textures = new Texture[2]
        textures[0] = ResourceManager.<TextureResource>getResource("textures/brick.png").getResource()
        textures[1] = ResourceManager.<TextureResource>getResource("textures/test.png").getResource()

        tiles = new Tile[worldSize * worldSize]

        List<Entity> entities = new ArrayList<>()

        for (int y = 0; y < worldSize; y++) {
            for (int x = 0; x < worldSize; x++) {
                Tile t = new Tile()

                tiles[x + y * worldSize] = t

                entities.add(createTileEntity(x, y, t))

                if (Math.random() > 0.5)
                    t.setType(Tile.TileType.STONE)
                else
                    t.setType(Tile.TileType.GRASS)
            }
        }

        EventSystem.publish(new AddEntityEvent(entities))
    }

    static Entity createTileEntity(int x, int y, Tile tile) {
        TextureComponent textureComponent = new TextureComponent("textures/brick.png")

        tile.action = new TileUpdateAction() {
            @Override
            void onTileUpdate(Tile.TileType type) {
                switch (type) {
                    case Tile.TileType.STONE:
                        textureComponent.texture = textures[0]
                        break
                    case Tile.TileType.GRASS:
                        textureComponent.texture = textures[1]
                        break
                }
                //textureComponent.textureId =
            }
        }

        Entity entity = new Entity(new Vector2f((float) (x), (float) (y)))
        entity.addComponent(textureComponent)
        return entity
    }

}

class Tile {

    private TileType type
    public TileUpdateAction action

    enum TileType {
        GRASS,
        STONE,
        WATER
    }

    public TileType getType() {
        return type
    }

    public void setType(TileType type) {
        if (action != null)
            action.onTileUpdate(type)
        this.type = type
    }

}

interface TileUpdateAction {
    void onTileUpdate(Tile.TileType type);
}
