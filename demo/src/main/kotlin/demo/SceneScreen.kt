package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.createModel
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen

@ExperimentalStdlibApi
class SceneScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("proto/blend.protobuf")

    override fun createEntities(engine: Engine) {
        val models = scene.children.filter { it.type == ObjectType.MODEL }
        models.forEach { node ->
            engine.createModel(node, scene)
        }

        engine.createModel(scene.perspectiveCameras.values.first(), gameContext)
    }
}


@ExperimentalStdlibApi
class SceneGame(gameContext: GameContext) : GameSystem(gameContext, SceneScreen(gameContext))

