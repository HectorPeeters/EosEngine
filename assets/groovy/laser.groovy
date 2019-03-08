import com.hector.engine.graphics.Animation
import com.hector.engine.graphics.components.AnimationComponent
import com.hector.engine.resource.ResourceManager
import com.hector.engine.resource.resources.AnimationResource
import com.hector.engine.scripting.components.GroovyScript

class Laser extends GroovyScript {

    private Animation offAnimation
    private Animation onAnimation

    private AnimationComponent animation

    @Override
    void init() {
        animation = parent.getComponent(AnimationComponent.class)
        animation.setPlayOnce(true)

        onAnimation = ResourceManager.<AnimationResource>getResource("textures/laser/laser-turn-on.png.anim").getResource()
        offAnimation = ResourceManager.<AnimationResource>getResource("textures/laser/laser-turn-off.png.anim").getResource()
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