package com.learncorda.tododist.contracts;

import net.corda.core.contracts.CommandData;

public interface Command extends CommandData {
    class CreateToDoCommand implements CommandData {}
    class AssignToDoCommand implements CommandData {}
    class AttachDocToDoCommand implements CommandData {}
    class GenToDoRefStatesCommand implements CommandData {}
    class AddSubTaskCommand implements CommandData {}
    class ModifyTaskCommand implements CommandData {}
    class CreateParentStateCommand implements CommandData{}
    class CreateAutoIncrementStateCommand implements CommandData{}
    class AddChildState implements CommandData {}

}
