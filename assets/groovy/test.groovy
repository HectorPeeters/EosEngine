import com.hector.engine.maths.Vector2f
import com.hector.engine.scripting.components.GroovyScript

class Test extends GroovyScript {

    private Vector2f targetPos

    @Override
    void init() {
        parent.name = "Test Entity"
        parent.rotation = (float)(Math.random() * 360)
    }

    float timer

    @Override
    void update(float delta) {
        timer += delta

        if (targetPos == null)
            targetPos = new Vector2f((float) (Math.random() * 5f - 2.5f), (float) (Math.random() * 4f - 2f))

        float scale = (float)(Math.sin(timer / 500f) / 3f)
//        parent.scale = new Vector2f(scale, scale)

        Vector2f diff = targetPos.sub(parent.getPosition())

        if (diff.lengthSquared() < 1) {
            targetPos = null
            return
        }

        parent.position = parent.position.add(diff.mul(0.005f))

        parent.rotation += delta * 0.01f
    }

    @Override
    void drawDebug() {

    }
}