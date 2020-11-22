package com.learncorda.tododist.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.learncorda.tododist.contracts.DummyToDoCommand;
import com.learncorda.tododist.states.ToDoState;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;

import java.util.Collections;


// flow start CreateToDoFlow task: "Get some cheese"
// flow start AssignToDoInitiator linearId: 3d3d3a7b-35bf-4b1d-83d7-9f10a9c98657 , assignedTo: PartyA

// ******************
// * Initiator flow *
// ******************
@StartableByRPC
public class CreateToDoFlow extends FlowLogic<Void> {
    private final ProgressTracker progressTracker = new ProgressTracker();
    private final String taskDescription;

    public CreateToDoFlow(String task) {
        this.taskDescription = task;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        ServiceHub serviceHub = getServiceHub();
        Party me = getOurIdentity();
        ToDoState ts = new ToDoState(me,me,taskDescription);
        System.out.println("Linear ID of state is " + ts.getLinearId());
        Party notary = serviceHub.getNetworkMapCache().getNotaryIdentities().get(0);
        TransactionBuilder tb = new TransactionBuilder(notary);
        tb = tb.addCommand(new DummyToDoCommand(), me.getOwningKey());  // at least one is required
        tb = tb.addOutputState(ts);
        SignedTransaction stx = getServiceHub().signInitialTransaction(tb);
        subFlow(new FinalityFlow(stx, Collections.<FlowSession>emptySet()));
        System.out.println("1");



        return null;
    }
}

