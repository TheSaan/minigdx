package com.github.dwursteisen.minigdx.graph

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.dwursteisen.minigdx.scene.api.sprite.Sprite
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.file.Asset
import com.github.dwursteisen.minigdx.file.AssetsManager
import com.github.dwursteisen.minigdx.file.Texture

class GraphScene(
    val scene: Scene,
    internal val assetsManager: AssetsManager
) : Asset {

    internal val textureCache = mutableMapOf<Id, Texture>()

    // Temporary as it to match the previous API only.
    val sprites: Map<Id, Sprite>
        get() = scene.sprites

    val nodes: List<GraphNode> = scene.children.map {
        GraphNode(this, scene, it)
    }

    override fun load(gameContext: GameContext) = Unit

    /**
     * Get children of [type] from the whole graph scene.
     */
    fun getAll(type: ObjectType): List<GraphNode> = nodes.flatMap { it.getAll(type) }
}
