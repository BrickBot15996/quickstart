package brickbot.quickstart.updatable

object UpdatableManager {
    private val updatableList = mutableListOf<Updatable>()

    fun addUpdatable(updatable: Updatable) {
        updatableList.add(updatable)
    }

    fun run() {
        updatableList.forEach { it.update() }
    }

    fun clear() {
        updatableList.clear()
    }
}