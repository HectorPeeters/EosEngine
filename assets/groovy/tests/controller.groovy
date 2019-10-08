import com.hector.engine.audio.AudioBuffer
import com.hector.engine.audio.components.AudioSourceComponent
import com.hector.engine.graphics.Animation
import com.hector.engine.graphics.Camera
import com.hector.engine.graphics.components.AnimationComponent
import com.hector.engine.input.InputSystem
import com.hector.engine.logging.Logger
import com.hector.engine.physics.components.RigidbodyComponent
import com.hector.engine.resource.ResourceManager
import com.hector.engine.resource.resources.AnimationResource

import com.hector.engine.resource.resources.AudioResource
import com.hector.engine.scripting.components.GroovyScript
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class Controller extends GroovyScript {

    private static final float speed = 0.4f

    private AnimationComponent animation
    public RigidbodyComponent rb
    private AudioSourceComponent audioSource

    private boolean grounded = false
    private boolean prevInAir = false
    private boolean inAir = false
    private boolean running = false
//    private boolean landing = false

    private float fallMultiplier = 2.2f
    private float lowJumpMultiplier = 2f

    private Animation runAnimation
    private Animation idleAnimation
    private Animation jumpAnimation

    private AudioBuffer runSound
    private AudioBuffer landSound

    @Override
    void init() {
        animation = parent.getComponent(AnimationComponent.class)

        rb = parent.getComponent(RigidbodyComponent.class)
        rb.acceleration = new Vector3f(0, -1f, 0)

        audioSource = parent.getComponent(AudioSourceComponent.class)

        runAnimation = ResourceManager.<AnimationResource> getResource("textures/engineer/engineer-run.png.anim").getResource()
        idleAnimation = ResourceManager.<AnimationResource> getResource("textures/engineer/engineer-idle.png.anim").getResource()
        jumpAnimation = ResourceManager.<AnimationResource> getResource("textures/engineer/engineer-jump.png.anim").getResource()

        runSound = ResourceManager.<AudioResource> getResource("audio/footstep_1.wav").getResource()
        landSound = ResourceManager.<AudioResource> getResource("audio/jumpland.wav").getResource()
    }

    private float prevX = 0

    @Override
    void update(float delta) {
        updateMovement(delta)

        updateAnimations()

//        if (prevInAir && !inAir) {
//            landing = true
//            audioSource.setTrack(landSound)
//            audioSource.play()
//        }
//
//        if (landing) {
//            if (!audioSource.isPlaying()) {
//                landing = false
//                audioSource.setTrack(runSound)
//            }
//        }

        if (running && !inAir) {
            audioSource.play()
        } else {
            audioSource.stop()
        }

        Camera.main.getPosition().x = parent.position.x

        prevInAir = inAir
        prevInAir = inAir

        prevX = parent.position.x
    }

    private void updateMovement(float delta) {
        if (parent.getPosition().y <= -0.5f) {
            rb.velocity.y = 0
            parent.getPosition().y = -0.5f
        }

        if (rb.velocity.y < 0) {
            rb.velocity.y -= (fallMultiplier - 1) * delta
        } else if (rb.velocity.y > 0 && !InputSystem.isKeyDown(GLFW.GLFW_KEY_W)) {
            rb.velocity.y -= (lowJumpMultiplier - 1) * delta
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