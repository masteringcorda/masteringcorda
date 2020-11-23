package com.learncorda.tododist.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.learncorda.tododist.contracts.Command;
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
    private final String taskDescription;

    public CreateToDoFlow(String task) {
        this.taskDescription = task;
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
        tb = tb.addCommand(new Command.CreateToDoCommand(), me.getOwningKey());
        tb = tb.addOutputState(ts);
        SignedTransaction stx = serviceHub.signInitialTransaction(tb);
        subFlow(new FinalityFlow(stx, Collections.<FlowSession>emptySet()));

//        SignedTransaction stx = getServiceHub()
//                .signInitialTransaction(
//                        new TransactionBuilder().withItems(
//                                notary,
//                                new ToDoState(me,me,taskDescription),
//                                TimeWindow.between(Instant.now(),Instant.now().plusSeconds(300)),
//                                new CommandWithParties<>(ImmutableList.of(me.getOwningKey()),ImmutableList.of(me),new Command.CreateToDoCommand())));
//        subFlow(new FinalityFlow(
//                getServiceHub()
//                        .signInitialTransaction(
//                                new TransactionBuilder().withItems(
//                                        notary,
//                                        new ToDoState(me,me,taskDescription),
//                                        TimeWindow.between(Instant.now(),Instant.now().plusSeconds(300)),
//                                        new CommandWithParties<>(ImmutableList.of(
//                                                getOurIdentity().getOwningKey()),
//                                                ImmutableList.of(getOurIdentity()),
//                                                new Command.CreateToDoCommand()))),
//                Collections.<FlowSession>emptySet())
//        );



//        try {
//            ResultSet rs = serviceHub.jdbcSession().prepareStatement("SELECT v.transaction_id, v.output_index FROM vault_states v WHERE v.state_status = 0").executeQuery();
//            while(rs.next()) {
//                System.out.println(rs.getString(1)); // cant be 0
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return null;
    }
}

