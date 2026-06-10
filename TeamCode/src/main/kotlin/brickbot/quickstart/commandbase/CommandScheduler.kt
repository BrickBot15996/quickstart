package brickbot.quickstart.commandbase

object CommandScheduler {
    private var commands = mutableListOf<Command>()
    private var conflictMap = mutableMapOf<String, List<String>>()

    /**
     * This method clears both the commands and the declared conflicts.
     */
    fun reset() {
        clearCommands()
        clearConflicts()
    }

    /**
     * This method clears the list of active commands.
     */
    fun clearCommands() {
        commands.clear()
    }

    /**
     * This method schedules an enumeration of commands and removes any declared conflicts from the scheduler.
     * @param commands The enumeration of commands desired to be scheduled.
     */
    fun schedule(vararg commands: Command) {
        schedule(commands.toList())
    }

    /**
     * This method schedules a list of commands and removes any declared conflicts from the scheduler.
     * @param commands The list of commands desired to be scheduled.
     */
    fun schedule(commands: List<Command>) {
        for (command in commands) {
            schedule(command)
        }
    }

    /**
     * This method schedules a command and removes any declared conflicts from the scheduler.
     * @param command The desired command to be scheduled.
     */
    fun schedule(command: Command) {
        val conflicts = conflictMap.getOrDefault(command.commandName, mutableListOf())

        if (!conflicts.isEmpty()) {
            remove(conflicts)
        }

        commands.add(command.clone())
    }

    /**
     * This method removes an enumeration of commands from the list of scheduled commands.
     * @param commandNames The enumeration of command names to be removed.
     */
    fun remove(vararg commandNames: String) {
        remove(commandNames.toList())
    }

    /**
     * This method removes a list of commands from the list of scheduled commands.
     * @param commandNames The list of command names to be removed.
     */
    fun remove(commandNames: List<String>) {
        for (commandName in commandNames) {
            remove(commandName)
        }
    }

    /**
     * This method removes a command from the list of scheduled commands.
     * @param commandName The name of the command to be removed.
     */
    fun remove(commandName: String) {
        commands = commands.filter { it.commandName != commandName }.toMutableList()
    }

    /**
     * This method runs the list of scheduled commands.
     */
    fun run() {
        if (commands.isEmpty()) return

        commands.removeIf { it.run() }
    }

    /**
     * This method establishes a group of commands that cannot run simultaneously. When one
     * of these commands is added, the scheduler tries to remove all other ones from the list
     * of active commands.
     * @param commandNames Enumeration of command names that should not run concurrently.
     */
    fun addConflictGroup(vararg commandNames: String) {
        addConflictGroup(commandNames.toList())
    }

    /**
     * This method establishes a group of commands that cannot run simultaneously. When one
     * of these commands is added, the scheduler tries to remove all other ones from the list
     * of active commands.
     * @param commandNames List of command names that should not run concurrently.
     */
    fun addConflictGroup(commandNames: List<String>) {
        for (commandName in commandNames) {
            conflictMap[commandName] = commandNames
        }
    }

    fun clearConflicts() {
        conflictMap = mutableMapOf()
    }
}