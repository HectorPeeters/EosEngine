import com.hector.engine.scripting.components.GroovyScript

class IntroController extends GroovyScript {

    @Override
    void init() {

    }

    @Override
    void update(float delta) {
        enterScene("test_scene.json")
    }

}