package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactoryDelegate
import com.github.dwursteisen.minigdx.file.get

class GameWrapper(val gameContext: GameContext, var game: Game) {

    private val engine = Engine(gameContext)

    fun create() {
        game.createDefaultSystems(engine).forEach { engine.addSystem(it) }
        game.createSystems(engine).forEach { engine.addSystem(it) }
        val renderStage = game.createRenderStage()
        renderStage.forEach { engine.addSystem(it) }

        val entityFactoryDelegate = EntityFactoryDelegate()
        entityFactoryDelegate.engine = engine
        entityFactoryDelegate.gameContext = gameContext

        game.createEntities(entityFactoryDelegate)
        // Load assets that can be loaded
        gameContext.assetsManager.update()
        engine.onGameStart()
        renderStage.forEach { it.compileShaders() }
    }

    fun resume() = Unit

    fun render(delta: Seconds) {
        game.render(engine, delta)
    }

    fun pause() = Unit

    fun destroy() {
        game.destroy(engine)
    }
}
