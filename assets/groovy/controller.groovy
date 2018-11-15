import com.hector.engine.graphics.Animation
import com.hector.engine.graphics.components.AnimationComponent
import com.hector.engine.input.InputSystem
import com.hector.engine.maths.Vector2f
import com.hector.engine.physics.components.RigidBodyComponent
import com.hector.engine.scripting.components.GroovyScript
import org.lwjgl.glfw.GLFW

class Controller extends GroovyScript {

    private static final float speed = 1

    private AnimationComponent animation
    public RigidBodyComponent rb

    private boolean grounded = false

    private Animation runAnimation
    private Animation idleAnimation
    private Animation jumpAnimation

    @Override
    void init() {
        animation = parent.getComponent(AnimationComponent.class)
        rb = parent.getComponent(RigidBodyComponent.class)
        rb.acceleration = new Vector2f(0, -2f)

    }

    private float prevX = 0

    @Override
    void update(float delta) {
        if (parent.getPosition().y <= -0.6f) {
            rb.velocity.y = 0
            parent.getPosition().y = -0.6f
        }

        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_W) && grounded)
            rb.velocity.y = 2

        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_D))
            parent.getPosition().x += speed * delta

        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_A))
            parent.getPosition().x -= speed * delta

        animation.setFlipped(prevX - parent.position.x > 0)

        animation.play(false)

        boolean inAir = Math.abs(rb.velocity.y) > 0.01f

        if (inAir) {
            boolean up = rb.velocity.y > 0.01f
            animation.setAnimation(jumpAnimation)
            animation.stop()
            animation.setFrame(up ? 0 : 1)
        } else {
            if (Math.abs(prevX - parent.position.x) >= 0.001f && grounded) {
                animation.setAnimation(runAnimation)
            } else {
                animation.setAnimation(idleAnimation)
            }
        }

        grounded = parent.getPosition().y <= -0.6f

        prevX = parent.position.x
    }

}