package com.github.dwursteisen.minigdx.ecs

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.ortho
import com.curiouscreature.kotlin.math.perspective
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.Camera
import com.dwursteisen.minigdx.scene.api.camera.OrthographicCamera
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.dwursteisen.minigdx.scene.api.model.Model
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.components.AnimatedModel
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.Text
import com.github.dwursteisen.minigdx.ecs.components.UICamera
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.entity.text.Font
import com.github.dwursteisen.minigdx.render.sprites.TextRenderStrategy

@ExperimentalStdlibApi
fun Engine.createFrom(
    model: Model,
    scene: Scene,
    context: GameContext,
    animationName: String? = null
): Entity {
    return this.create {
        val boxes = model.boxes.map { BoundingBox.from(it) }
        val transformation = Mat4.fromColumnMajor(*model.transformation.matrix)
        add(boxes)
        add(Position(transformation))

        if (model.armatureId < 0) {
            val primitives = model.mesh.primitives.map { primitive ->
                MeshPrimitive(
                    primitive = primitive,
                    material = scene.materials.values.first { it.id == primitive.materialId }
                )
            }
            add(primitives)
            context.glResourceClient.compile(model.name, primitives + boxes)
        } else {
            val allAnimations = scene.animations.getValue(model.armatureId)
            val animation =
                allAnimations.firstOrNull { animation -> animation.name == animationName } ?: allAnimations.last()
            val animatedModel = AnimatedModel(
                animation = animation.frames,
                referencePose = scene.armatures.getValue(model.armatureId),
                time = 0f,
                duration = animation.frames.maxBy { it.time }?.time ?: 0f
            )
            val animatedMeshPrimitive = model.mesh.primitives.map { primitive ->
                AnimatedMeshPrimitive(
                    primitive = primitive,
                    material = scene.materials.values.first { it.id == primitive.materialId }
                )
            }
            add(animatedModel)
            add(animatedMeshPrimitive)
            context.glResourceClient.compile(model.name, animatedMeshPrimitive + boxes)
        }
    }
}

fun Engine.createFrom(camera: Camera, context: GameContext): Entity {
    val cameraComponent = when (camera) {
        is PerspectiveCamera -> com.github.dwursteisen.minigdx.ecs.components.Camera(
            projection = perspective(
                fov = camera.fov,
                aspect = context.ratio,
                near = camera.near,
                far = camera.far
            )
        )
        is OrthographicCamera -> {
            val (w, h) = if (context.gl.screen.width >= context.gl.screen.height) {
                1f to (context.gl.screen.height / context.gl.screen.width.toFloat())
            } else {
                context.gl.screen.width / context.gl.screen.height.toFloat() to 1f
            }
            com.github.dwursteisen.minigdx.ecs.components.Camera(
                projection = ortho(
                    l = -camera.scale * 0.5f * w,
                    r = camera.scale * 0.5f * w,
                    b = -camera.scale * 0.5f * h,
                    t = camera.scale * 0.5f * h,
                    n = camera.near,
                    f = camera.far
                )
            )
        }
        else -> throw IllegalArgumentException("${camera::class} is not supported")
    }

    return this.create {
        add(cameraComponent)
        add(Position(Mat4.fromColumnMajor(*camera.transformation.matrix), way = -1f))
    }
}

fun Engine.createUICamera(gameContext: GameContext): Entity {
    return this.create {
        val width = gameContext.gl.screen.width
        val height = gameContext.gl.screen.height
        add(
            UICamera(
                projection = ortho(
                    l = width * -0.5f,
                    r = width * 0.5f,
                    b = height * -0.5f,
                    t = height * 0.5f,
                    n = 0.01f,
                    f = 1f
                )
            )
        )
        // put the camera in the center of the screen
        add(Position(way = -1f).translate(x = -width * 0.5f, y = -height * 0.5f))
    }
}

fun Engine.createFrom(font: Font, text: String, x: Float, y: Float, gameContext: GameContext): Entity {
    return this.create {
        add(Position().translate(x = x, y = y, z = 0f))
        val spritePrimitive = SpritePrimitive(
            texture = font.fontSprite,
            renderStrategy = TextRenderStrategy
        )
        add(spritePrimitive)
        add(Text(
                text = text,
                font = font
            )
        )
        gameContext.glResourceClient.compile(font.angelCode.info.fontFile, spritePrimitive)
    }
}
