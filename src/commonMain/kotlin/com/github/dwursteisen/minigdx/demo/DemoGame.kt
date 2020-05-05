package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldSize
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.behavior.JumpBehavior
import com.github.dwursteisen.minigdx.entity.delegate.Model
import com.github.dwursteisen.minigdx.entity.models.Camera2D
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.entity.models.Cube
import com.github.dwursteisen.minigdx.entity.text.Text
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

class Player(private val model: Cube = Cube("player")) : CanMove by model, CanDraw by model {

    private val jumpingCharge = JumpBehavior(
        charge = 0.8f,
        gravity = -2f,
        currentPosition = { position.y },
        groundPosition = { -10f },
        isJumping = { inputs.isKeyPressed(Key.SPACE) || inputs.isTouched(TouchSignal.TOUCH1) != null }
    )

    fun update(delta: Seconds) {
        jumpingCharge.update(delta)
        if (jumpingCharge.grounded) {
            setTranslate(y = -10f)
        } else {
            translate(y = jumpingCharge.dy)
        }
    }
}

class Obstacle(private val model: Model) : CanDraw by model, CanMove by model {

    fun update(delta: Seconds) {
        translate(-speed * delta)

        // off the screen
        if (position.x <= -20f) {
            setTranslate(x = 20f, y = -10f, z = 0f)
        }
    }

    companion object {

        private var speed = 12f
    }
}

class Score(private val text: Text) : CanDraw by text, CanMove by text {

    private var time: Int = 0

    fun update(delta: Seconds) {
        time += (delta * 100).toInt()

        val seconds = (time / 100f).toInt()
        val millis = time - (seconds * 100)
        val m = if (millis < 10) {
            "0$millis"
        } else {
            "$millis"
        }
        text.text = "$seconds:$m"
    }
}

@ExperimentalStdlibApi
class DemoGame : Game {

    override val worldSize: WorldSize = WorldSize(200, 200)

    private val camera = Camera3D.perspective(
        45,
        worldSize.ratio,
        near = 1f,
        far = 200f
    )

    private val cameraGUI = Camera2D.orthographic(
        width = 5f, height = 5f
    )

    private val shader = DefaultShaders.create3d()

    private val gui = DefaultShaders.create2d()

    private val model by fileHandler.get<Model>("monkey.protobuf")

    private val background by fileHandler.get<Model>("montains.protobuf")

    private val text by fileHandler.get<Text>("font")

    private val player = Player()

    private var obstacles = emptyList<Obstacle>()

    lateinit var score: Score

    override fun create() {
        camera.translate(0f, 0f, -50f)
        player.setTranslate(-10f, -10f, 0f)
        background
            .translate(y = -20f, z = 10f)
            .rotateZ(90f)
            .scale(Vector3(10f, 10f, 10f))

        cameraGUI.translate(x = 2.5f, y = 2.5f, z = 0f)

        score = Score(text).apply {
            text.text = "toto"
            text.setTranslate(-400, -400, 0)
        }

        (0 until 10).forEach {
            val obstacle = Obstacle(model.copy()).apply {
                setTranslate(
                    // put it offscreen
                    x = 20f + it * 20f,
                    y = -10f,
                    z = 0f
                )
            }

            obstacles = obstacles + obstacle
        }
    }

    override fun render(delta: Seconds) {
        // -- act --
        player.update(delta)
        obstacles.forEach { it.update(delta) }
        score.update(delta)

        background.rotateX(5f * delta)

        // -- draw --
        shader.render { shader ->
            clear(178 / 255f, 235 / 255f, 242 / 255f)
            camera.draw(shader)
            background.draw(shader)
            player.draw(shader)

            obstacles.forEach { it.draw(shader) }
        }

        gui.render { shader ->
            cameraGUI.draw(shader)
            score.draw(shader)
        }
    }
}
