package com.learncorda.tododist.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.learncorda.tododist.states.ToDoState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.StateRef;
import net.corda.core.flows.*;
import net.corda.core.node.ServiceHub;

import java.time.Instant;

@InitiatingFlow
@SchedulableFlow
public class AlarmFlow extends FlowLogic<Void> {

    private StateRef stateRef;
    static {
        System.out.println("AlarmFlow loaded");
    }

    public AlarmFlow(StateRef stateRef) {
        this.stateRef = stateRef;
        System.out.println("Constructor fired");
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
        System.out.println("Alarm fired");
        ServiceHub sb = getServiceHub();
        StateAndRef<ToDoState> todoStateAndRef = sb.toStateAndRef(stateRef);
        ToDoState todo = todoStateAndRef
                .getState()
                .getData();
        sb.getVaultService().addNoteToTransaction(stateRef.getTxhash(),"Reminder made: " + Instant.now());

        System.out.println("Deadline is coming up for task: " + todo.getTaskDescription());
        return null;
    }
}

