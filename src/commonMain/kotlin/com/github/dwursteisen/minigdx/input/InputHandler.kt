package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.math.Vector2

enum class TouchSignal {
    TOUCH1, TOUCH2, TOUCH3
}

enum class Key {
    ANY_KEY,
    BACKSPACE,
    TAB,
    ENTER,
    SHIFT,
    CTRL,
    ALT,
    PAUSE_BREAK,
    CAPS_LOCK,
    ESCAPE,
    PAGE_UP,
    SPACE,
    PAGE_DOWN,
    END,
    HOME,
    ARROW_LEFT,
    ARROW_UP,
    ARROW_RIGHT,
    ARROW_DOWN,
    PRINT_SCREEN,
    INSERT,
    DELETE,
    NUM0,
    NUM1,
    NUM2,
    NUM3,
    NUM4,
    NUM5,
    NUM6,
    NUM7,
    NUM8,
    NUM9,
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J,
    K,
    L,
    M,
    N,
    O,
    P,
    Q,
    R,
    S,
    T,
    U,
    V,
    W,
    X,
    Y,
    Z,
    LEFT_WINDOW_KEY,
    RIGHT_WINDOW_KEY,
    SELECT_KEY,
    NUMPAD0,
    NUMPAD1,
    NUMPAD2,
    NUMPAD3,
    NUMPAD4,
    NUMPAD5,
    NUMPAD6,
    NUMPAD7,
    NUMPAD8,
    NUMPAD9,
    MULTIPLY,
    ADD,
    SUBTRACT,
    DECIMAL_POINT,
    DIVIDE,
    F1,
    F2,
    F3,
    F4,
    F5,
    F6,
    F7,
    F8,
    F9,
    F10,
    F11,
    F12,
    NUM_LOCK,
    SCROLL_LOCK,
    MY_COMPUTER,
    MY_CALCULATOR,
    SEMI_COLON,
    EQUAL_SIGN,
    COMMA,
    DASH,
    PERIOD,
    FORWARD_SLASH,
    OPEN_BRACKET,
    BACK_SLASH,
    CLOSE_BRAKET,
    SINGLE_QUOTE
}

interface InputManager {

    fun record()
    fun reset()
}

interface InputHandler {

    /**
     * Is the [key] was pressed?
     *
     * It returns true once and will return true only if
     * the key is released then pressed again.
     *
     * This method should be used to count action trigger only
     * once (ie: starting an action like opening a door)
     */
    fun isKeyJustPressed(key: Key): Boolean

    /**
     * Is the [key] is actually pressed?
     *
     * This method should be used to know when the key is pressed
     * and running an action until the key is not released.
     * (ie: running while the key is pressed, stop when it's not)
     */
    fun isKeyPressed(key: Key): Boolean

    /**
     * Is any of [keys] passed in parameter are actually pressed?
     */
    fun isAnyKeysPressed(vararg keys: Key): Boolean = keys.any { isKeyPressed(it) }

    /**
     * Is all of [keys] passed in parameter are been just pressed?
     */
    fun isAllKeysPressed(vararg keys: Key): Boolean = keys.all { isKeyPressed(it) }

    fun isNoneKeysPressed(vararg keys: Key): Boolean = keys.none { isKeyPressed(it) }

    fun isTouched(signal: TouchSignal): Vector2?

    fun isJustTouched(signal: TouchSignal): Vector2?
}
