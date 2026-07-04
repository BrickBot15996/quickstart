package brickbot.quickstart.subsystems

abstract class Robot {
    abstract fun init()
    abstract fun update()

    open fun autonomousInit() { }
    open fun teleOpInit() { }
}