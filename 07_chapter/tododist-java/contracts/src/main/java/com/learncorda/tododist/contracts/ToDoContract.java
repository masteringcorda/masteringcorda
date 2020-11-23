package com.learncorda.tododist.contracts;

import com.learncorda.tododist.states.ToDoState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import static net.corda.core.contracts.ContractsDSL.requireThat;

import java.util.List;

public class ToDoContract implements Contract {
    @Override
    public void verify(LedgerTransaction tx) {
        System.out.println("ToDoContract's verify() method has been called");
        ToDoState toDoOutput = (ToDoState) tx.getOutputStates().get(0);
        final List<CommandWithParties<CommandData>> commands = tx.getCommands();
        final CommandData command = commands.get(0).getValue();
        if (command instanceof Command.CreateToDoCommand) {
            requireThat(r->{
                r.using("Task is blank", !toDoOutput.getTaskDescription().trim().equals(""));
                r.using("Task length is too large", toDoOutput.getTaskDescription().length()<25);
                return null;
            });
        } else if (command instanceof Command.AssignToDoCommand) {
            ToDoState toDoInput = (ToDoState) tx.getInputStates().get(0);
            requireThat(r-> {
                r.using("Already assigned to party",!toDoInput.getAssignedTo().equals(toDoOutput.getAssignedTo()));
                return null;
            });
        } else {
            System.out.println("No command found in contract");
        }
    }



}