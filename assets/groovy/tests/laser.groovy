import com.hector.engine.audio.components.AudioSourceComponent
import com.hector.engine.event.EventSystem
import com.hector.engine.event.Handler
import com.hector.engine.graphics.Animation
import com.hector.engine.graphics.components.AnimationComponent
import com.hector.engine.input.events.KeyEvent
import com.hector.engine.resource.ResourceManager
import com.hector.engine.resource.resources.AnimationResource
import com.hector.engine.scripting.components.GroovyScript
import org.lwjgl.glfw.GLFW

class Laser extends GroovyScript {

    private Animation offAnimation
    private Animation onAnimation

    private AnimationComponent animation

    @Override
    void init() {
        animation = parent.getComponent(AnimationComponent.class)
        animation.setPlayOnce(true)

        onAnimation = ResourceManager.<AnimationResource> getResource("textures/laser/laser-turn-on.png.anim").getResource()
        offAnimation = ResourceManager.<AnimationResource> getResource("textures/laser/laser-turn-off.png.anim").getResource()

        EventSystem.subscribe(this)
    }

    @Handler
    void keyPressed(KeyEvent event) {
        if (event.keycode == GLFW.GLFW_KEY_SPACE && event.pressed) {
            AudioSourceComponent audioSourceComponent = parent.getComponent(AudioSourceComponent.class)
            if (!audioSourceComponent.isPlaying())
                audioSourceComponent.play()
            else
                audioSourceComponent.pause()
        }
    }

    boolean turningOn = true

    @Override
    void update(float delta) {
        if (turningOn) {
            animation.setAnimation(onAnimation)
            animation.play(true)

            if (!animation.isPlaying()) {
//                animation.stop()
                turningOn = false
            }

        } else {
            println "TURNING OFF"
            animation.setAnimation(offAnimation)
            animation.play(true)

            if (!animation.isPlaying()) {
//                animation.stop()
                turningOn = true
            }
        }
    }

}