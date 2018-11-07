import com.hector.engine.entity.Entity
import com.hector.engine.graphics.components.SpriteComponent
import com.hector.engine.maths.Vector2f
import com.hector.engine.scripting.components.GroovyScript

class Test extends GroovyScript {

    Vector2f targetPos

    @Override
    void init() {
        targetPos = new Vector2f((float) (Math.random() * 5f - 2.5f), (float) (Math.random() * 4f - 2f))
    }

    float timer

    @Override
    void update(float delta) {
        timer += delta

        if (timer > 50) {
            instantiate(new Entity(parent.getPosition(), new Vector2f(0.01f, 0.01f)).addComponent(new SpriteComponent(1)))
                //.addComponent(new GroovyScriptComponent("groovy/test.groovy")))
            timer = 0
        }

        Vector2f diff = targetPos.sub(parent.getPosition())

        if (diff.lengthSquared() < 1) {
            targetPos = new Vector2f((float) (Math.random() * 5f - 2.5f), (float) (Math.random() * 4f - 2f))
            return
        }

        parent.setPosition(parent.getPosition().add(diff.mul(0.005f)))
    }
}