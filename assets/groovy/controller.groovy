import com.hector.engine.input.InputSystem
import com.hector.engine.scripting.components.GroovyScript
import org.lwjgl.glfw.GLFW

class Controller extends GroovyScript {

    private static final float speed = 1

    @Override
    void init() {

    }

    @Override
    void update(float delta) {
        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_W))
            parent.getPosition().y += speed * delta

        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_S))
            parent.getPosition().y -= speed * delta

        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_D))
            parent.getPosition().x += speed * delta

        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_A))
            parent.getPosition().x -= speed * delta
    }

}