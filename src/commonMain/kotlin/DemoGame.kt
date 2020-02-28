import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.transpose
import threed.GL
import threed.Game
import threed.Seconds
import threed.entity.Bone
import threed.entity.BoneMesh
import threed.entity.Camera
import threed.entity.CanDraw
import threed.entity.Landmark
import threed.file.MeshReader
import threed.fileHandler
import threed.gl
import threed.graphics.Render
import threed.input.Key
import threed.inputs
import threed.shaders.DefaultShaders
import threed.shaders.ShaderUtils

class DemoGame : Game {

    private val camera = Camera.create(45, gl.canvas.width / gl.canvas.height, 0.1, 100)
    private val program = ShaderUtils.createShaderProgram(DefaultShaders.vertexShader, DefaultShaders.fragmentShader)

    lateinit var monkey: Render
    lateinit var cube: Render
    val armatures: MutableList<CanDraw> = mutableListOf()
    val landmark = Landmark.of()

    @ExperimentalStdlibApi
    override fun create() {
        program.createAttrib("aVertexPosition")
        program.createAttrib("aVertexColor")
        program.createAttrib("aNormal")

        // Model View Project Matrix
        program.createUniform("uModelMatrix")
        program.createUniform("uViewMatrix")
        program.createUniform("uProjectionMatrix")

        program.createUniform("uNormalMatrix")

        camera.translate(0, 0, -6)
        // camera.rotate(-90, 0, 0)

        fileHandler.readData("monkey_color2.protobuf").onLoaded {
            monkey = Render(MeshReader.fromProtobuf(it).first)
        }
/*
        fileHandler.readData("armature.protobuf").onLoaded {
            val fromByteArray = MeshReader.fromByteArray(it)
            cube = Render(fromByteArray.first)
            val rootBone = fromByteArray.second?.rootBone

            fun Bone.traverse(action: (Bone) -> Unit) {
                action(this)
                this.childs.forEach {
                    it.traverse(action)
                }
            }

            rootBone?.traverse {
                val boneMesh = BoneMesh.of(it.globalTransaction)
                armatures.add(boneMesh)
            }
        }
*/
        // TODO: add a convenient method to load a model
        fileHandler.readData("cube_armature_loop.protobuf").onLoaded {
            val fromByteArray = MeshReader.fromProtobuf(it)
            cube = Render(fromByteArray.first)
            val rootBone = fromByteArray.second?.rootBone

            fun Bone.traverse(action: (Bone, Bone) -> Unit) {
                this.childs.forEach {
                    action(this, it)
                    it.traverse(action)
                }
            }

            rootBone?.traverse { a, b ->
                val boneMesh = BoneMesh.of(a.globalTransaction, b.localTransformation)
                armatures.add(boneMesh)
            }
        }
    }

    private fun moveCamera(delta: Seconds) {
        camera.translate(0f, 2f * delta, 0f)
    }

    private fun moveCamera2(delta: Seconds) {
        camera.translate(0f, -2f * delta, 0f)
    }

    private fun rotateCamera(delta: Seconds) {
        camera.rotateX(25f * delta)
    }

    private fun moveCube(delta: Seconds) {
        cube.mesh.translate(0f, -2f * delta, 0f)
    }

    private fun moveCube2(delta: Seconds) {
        cube.mesh.translate(0f, 2f * delta, 0f)
    }

    private fun rotateCube(delta: Seconds) {
        cube.mesh.rotateX(25f * delta)
    }

    var index = 0

    var timer = 0f

    lateinit var action: (Seconds) -> Unit

    val actions = listOf(
        this::rotateCube,
        this::moveCamera,
        this::moveCube,
        this::moveCamera2,
        this::moveCube2
    )

    override fun render(delta: Seconds) {
        if (!fileHandler.isLoaded) {
            return
        }

        // --- act ---
        /*
        timer -= delta
        if (timer <= 0f) {
            timer = 4f
            action = actions[index]
            index = (index + 1) % actions.size
        }

        action.invoke(delta)
        */
        if (inputs.isKey(Key.U)) {
            camera.translate(0, delta, 0)
        } else if (inputs.isKey(Key.J)) {
            camera.translate(0, -delta, 0)
        }

        if (inputs.isKey(Key.H)) {
            camera.translate(delta, 0, 0)
        } else if (inputs.isKey(Key.K)) {
            camera.translate(-delta, 0, 0)
        }

        if (inputs.isKey(Key.Y)) {
            camera.translate(0, 0, delta)
        } else if (inputs.isKey(Key.I)) {
            camera.translate(0, 0, -delta)
        }
        val modelMatrix = camera.modelMatrix
        val normalMatrix = transpose(inverse(modelMatrix))
        // --- draw ---
        // clear
        gl.clearColor(0, 0, 0, 1)
        gl.clearDepth(1.0)
        gl.enable(GL.DEPTH_TEST)
        gl.depthFunc(GL.LEQUAL)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

        gl.useProgram(program)

        gl.uniformMatrix4fv(program.getUniform("uProjectionMatrix"), false, camera.projectionMatrix)
        gl.uniformMatrix4fv(program.getUniform("uViewMatrix"), false, camera.modelMatrix)
        gl.uniformMatrix4fv(program.getUniform("uNormalMatrix"), false, normalMatrix)

        // monkey.draw(program)
        //  cube.draw(program)
        armatures.forEach {
            it.draw(program)
        }

        //  landmark.draw(program)
    }
}
