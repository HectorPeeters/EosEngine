import com.hector.engine.graphics.Animation
import com.hector.engine.graphics.Camera
import com.hector.engine.graphics.components.AnimationComponent
import com.hector.engine.input.InputSystem
import com.hector.engine.maths.Vector2f
import com.hector.engine.physics.components.RigidbodyComponent
import com.hector.engine.resource.ResourceManager
import com.hector.engine.resource.resources.AnimationResource
import com.hector.engine.scripting.components.GroovyScript
import org.lwjgl.glfw.GLFW

class Controller extends GroovyScript {

    private static final float speed = 0.4f

    private AnimationComponent animation
    public RigidbodyComponent rb

    private boolean grounded = false
    private boolean inAir = false
    private boolean running = false

    private Animation runAnimation
    private Animation idleAnimation
    private Animation jumpAnimation

    @Override
    void init() {
        animation = parent.getComponent(AnimationComponent.class)
        rb = parent.getComponent(RigidbodyComponent.class)
        rb.acceleration = new Vector2f(0, -1f)

        runAnimation = ResourceManager.<AnimationResource>getResource("textures/engineer/engineer-run.png.anim").getResource()
        idleAnimation = ResourceManager.<AnimationResource>getResource("textures/engineer/engineer-idle.png.anim").getResource()
        jumpAnimation = ResourceManager.<AnimationResource>getResource("textures/engineer/engineer-jump.png.anim").getResource()
    }

    private float prevX = 0

    @Override
    void update(float delta) {
        updateMovement(delta)

        updateAnimations()

        Camera.main.getPosition().x = parent.position.x

        prevX = parent.position.x
    }

    private void updateMovement(float delta) {
        if (parent.getPosition().y <= -0.5f) {
            rb.velocity.y = 0
            parent.getPosition().y = -0.5f
        }

        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_W) && grounded)
            rb.velocity.y = 1.5f

        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_D))
            parent.getPosition().x += speed * delta

        if (InputSystem.isKeyDown(GLFW.GLFW_KEY_A))
            parent.getPosition().x -= speed * delta

        running = Math.abs(prevX - parent.position.x) >= 0.001f
        inAir = Math.abs(rb.velocity.y) > 0.01f
        grounded = parent.getPosition().y <= -0.5f
    }

    private void updateAnimations() {
        if (running)
            animation.setFlipped(prevX - parent.position.x > 0)

        if (inAir) {
            boolean up = rb.velocity.y > 0.01f
            animation.setAnimation(jumpAnimation)
            animation.stop()
            animation.setFrame(up ? 0 : 1)

        } else {
            if (running && grounded) {
                animation.setAnimation(runAnimation)
            } else {
                animation.setAnimation(idleAnimation)
            }
            animation.play(false)
        }

    }

}