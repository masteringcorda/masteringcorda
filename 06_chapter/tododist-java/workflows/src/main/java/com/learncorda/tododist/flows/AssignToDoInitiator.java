package com.learncorda.tododist.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.learncorda.tododist.contracts.DummyToDoCommand;
import com.learncorda.tododist.states.ToDoState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;


// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class AssignToDoInitiator extends FlowLogic<Void> {
    private String linearId;
    private String assignedTo;

    public AssignToDoInitiator(String linearId, String assignedTo) {
        this.linearId = linearId;
        this.assignedTo = assignedTo;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        final ServiceHub sb = getServiceHub();
        final QueryCriteria q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(UUID.fromString(linearId)));
        final Vault.Page<ToDoState> taskStatePage = sb.getVaultService().queryBy(ToDoState.class, q);
        final List<StateAndRef<ToDoState>> states = taskStatePage.getStates();
        final StateAndRef<ToDoState> sar = states.get(0);
        final ToDoState toDoState = sar.getState().getData();

        System.out.println(toDoState.getTaskDescription());

//        for (StateAndRef<ToDoState> s : states) {
//            System.out.println(s.getRef());
//            ToDoState ts = s.getState().getData();
//            System.out.println(ts.getTaskDescription());
//            System.out.println("Currently assigned to: " + ts.getAssignedTo().getName().getOrganisation());
//        }

        System.out.println("Looking up party " + assignedTo);
        final Set<Party> parties = sb.getIdentityService().partiesFromName(assignedTo, true);
        Party assignedToParty = parties.iterator().next();
        System.out.println("Found party");

        // getCommonName returned null.. why?
        System.out.println(assignedToParty.getName().getOrganisation());
        System.out.println("Assigning task");
        ToDoState newToDoState = toDoState.assign(assignedToParty);

        //ToDoState withNewAssignedTo = new ToDoState(toDoState.getAssignedBy(),assignedToParty, toDoState.getTaskDescription());

        // need receivers agreement to the task
        System.out.println("Looking up notary");
        Party notary = sb.getNetworkMapCache().getNotaryIdentities().get(0);

        System.out.println("Building tb");
        PublicKey myKey = getOurIdentity().getOwningKey();
        PublicKey counterPartyKey = assignedToParty.getOwningKey();
        TransactionBuilder tb = new TransactionBuilder(notary)
                .addInputState(sar)
                .addOutputState(newToDoState)
                .addCommand(new DummyToDoCommand(),myKey,counterPartyKey);
        SignedTransaction ptx = getServiceHub().signInitialTransaction(tb);

        System.out.println("1");
        FlowSession assignedToSession = initiateFlow(assignedToParty);
        SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, ImmutableSet.of(assignedToSession)));
        subFlow(new FinalityFlow(stx, Arrays.asList(assignedToSession)));

        return null;
    }
}
