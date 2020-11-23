package com.cordabook.complexstates.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.cordabook.complexstates.contracts.Commands;
import com.cordabook.complexstates.states.*;
import net.corda.core.contracts.*;
import net.corda.core.crypto.CryptoUtils;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.internal.StatePointerSearch;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.Vault;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import org.hibernate.Transaction;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


// flow start CreateToDoFlow task: "Get some cheese"
// flow start AssignToDoInitiator linearId: 3d3d3a7b-35bf-4b1d-83d7-9f10a9c98657 , assignedTo: PartyA

// ******************
// * TradeInitiator flow *
// ******************
@StartableByRPC
public class BuildComplexStatesFlow extends FlowLogic<Void> {



    private void describe(TransactionBuilder tb) {
        h1("TransactionBuilder details");

        h2("Input states:");
        final List<StateRef> inputStates = tb.inputStates();
        for(int i=0;i<inputStates.size();i++)
            System.out.println("Tx id: " + inputStates.get(i).getTxhash() + " Index: " + inputStates.get(i).getIndex());

        h2("Output states:");
        List<TransactionState<?>> outputStates = tb.outputStates();
        for (int i=0;i<outputStates.size();i++)
            System.out.println("Output index: " + i + " state:" + outputStates.get(i));

        h2("Reference states:");
        List<StateRef> refStates = tb.referenceStates();
        for (int i=0;i< refStates.size();i++)
            System.out.println("Output index: " + i + "state: " + refStates.get(i));

     }
    private void describe(SignedTransaction tx) {
        h1("SignedTransaction details");
        h2("Merkle root hash: " + tx.getId());
        h2("StateRefs:");

        System.out.println("Input states:");
        List<StateRef> inputStates = tx.getInputs();
        for(int i=0;i<inputStates.size();i++)
            System.out.println("Tx id: " + inputStates.get(i).getTxhash() + " Index: " + inputStates.get(i).getIndex());


        h2("References states:");
        List<StateRef> refStates = tx.getReferences();
        for (int i=0;i<refStates.size();i++)
            System.out.println("Output index: " + i + " state:" + refStates.get(i));

        h2("Signatures:");
        List<TransactionSignature> sigs = tx.getSigs();
        for (TransactionSignature s : sigs) {
            System.out.println(getServiceHub().getNetworkMapCache().getNodesByLegalIdentityKey(s.getBy()).get(0).getLegalIdentities().get(0).getName().getOrganisation());
        }
    }

    private void h1(String heading) {
        System.out.println();
        System.out.println();
        System.out.println("============ " + heading);
    }

    private void h2(String heading) {
        System.out.println("---- " + heading);
    }

    private PrivacySalt salt() {
        byte[] salt = null;
        try {
            salt = CryptoUtils.secureRandomBytes(32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new PrivacySalt(salt);
    }

    @Suspendable
    private SignedTransaction signAndFinalize(TransactionBuilder tb, boolean describe) throws FlowException {
        if (describe) describe(tb);
        SignedTransaction ptx = sign(tb);
        if (describe) describe(ptx);
        SignedTransaction ftx = finalize(ptx);
        if (describe) describe(ptx);
        return ftx;
    }

    private void describe(String o,StateAndRef sar) {
        System.out.println(o + "'s tx id and index: " + sar.getRef().getTxhash() + " " + sar.getRef().getIndex());
    }

    private void describe(String o, StateRef sr) {
        System.out.println(o + " points to " + sr.getTxhash() + " " + sr.getIndex());
    }

    private void describe (String m) {
        System.out.println(m);
    }

    @Suspendable
    private SignedTransaction finalize(SignedTransaction ptx) throws FlowException {
        return subFlow(new FinalityFlow(ptx, Collections.<FlowSession>emptySet()));
    }

    @Suspendable
    private SignedTransaction sign(TransactionBuilder tb) {
        return getServiceHub().signInitialTransaction(tb);
    }

    @Suspendable
    private SignedTransaction signAndFinalize(TransactionBuilder tb) throws FlowException {
        SignedTransaction ptx = sign(tb);
        SignedTransaction ftx = finalize(ptx);
        return ftx;
    }



    @Suspendable
    @Override
    public Void call() throws FlowException {

        // Setup
        ServiceHub serviceHub = getServiceHub();
        Party me = getOurIdentity();
        PublicKey myKey = me.getOwningKey();
        Party notary = serviceHub.getNetworkMapCache().getNotaryIdentities().get(0);
        SignedTransaction ftx = null;
        PrivacySalt salt = salt();

        h1("Example 1: Two parent states");
        // Two new parent states
        ParentState parent1 = new ParentState(me);
        ParentState parent2 = new ParentState(me);

        TransactionBuilder tb = new TransactionBuilder(notary)
                .addCommand(new Commands.CreateParentStateCommand(), myKey)
                .addOutputState(parent1)
                .addOutputState(parent2);
        ftx = signAndFinalize(tb);

        h1("Done");


        h1("Example 2: Identical parent states inserted again");
        signAndFinalize(new TransactionBuilder(notary)
                .addCommand(new Commands.CreateParentStateCommand(),myKey)
                .addOutputState(parent1)
                .addOutputState(parent2));
        h1("Done");



        h1("Example 3: Identical parent states inserted twice with same salt constant");
        signAndFinalize(new TransactionBuilder(notary)
                .addCommand(new Commands.CreateParentStateCommand(),myKey)
                .addOutputState(parent1)
                .setPrivacySalt(salt));

        signAndFinalize(new TransactionBuilder(notary)
                .addCommand(new Commands.CreateParentStateCommand(),myKey)
                .addOutputState(parent1)
                .setPrivacySalt(salt));

        h1("Done");


        h1("Example 4: Consume parent2 and see if a new merkle is created");
        // Add child state to first parent
        StateRef parent2StateRef = new StateRef(ftx.getId(),1);
        StateAndRef parentStateAndRef = serviceHub.toStateAndRef(parent2StateRef);
        ParentState parent2New = new ParentState(me);
        signAndFinalize(new TransactionBuilder(notary)
                .addCommand(new Commands.CreateParentStateCommand(),myKey)
                .addInputState(parentStateAndRef)
                .addOutputState(parent2New));
        h1("Done");


        h1("Example 5: Attach child to parent1 via StateRef");
        // Add child state to first parent
        StateRef parent1StateRef = new StateRef(ftx.getId(),0);
        ChildViaStateRefState childViaStateRefState = new ChildViaStateRefState(parent1StateRef,me);
        signAndFinalize( new TransactionBuilder(notary)
                .addCommand(new Commands.AddChildState(),myKey)
                .addOutputState(childViaStateRefState));

        StateRef parent = childViaStateRefState.getParent();
        ParentState ps = (ParentState) serviceHub
                .toStateAndRef(parent)
                .getState()
                .getData();
        h1("Done");


        h1("Example 6: Attach child to parent1 via StaticPointer");
        StaticPointer staticPointer = new StaticPointer<>(parent1StateRef,ParentState.class);
        StateAndRef resolvedStaticPointer = staticPointer.resolve(serviceHub);


        ChildViaStaticPointerState childViaStaticPointerState = new ChildViaStaticPointerState(staticPointer,me);
        signAndFinalize( new TransactionBuilder(notary)
                .addCommand(new Commands.AddChildState(),myKey)
                .addOutputState(childViaStaticPointerState));
        h1("Done");


        h1("Example 7: Add linear parent, attach child to linear parent via Linear ID");
        LinearParentState linearParent =  new LinearParentState(me);
        UniqueIdentifier parentLinearID = linearParent.getLinearId();
        ChildViaLinearIDState childViaLinearIDState = new ChildViaLinearIDState(parentLinearID,me);

        signAndFinalize( new TransactionBuilder(notary)
                .addCommand(new Commands.AddChildState(),myKey)
                .addOutputState(linearParent)
                .addOutputState(childViaLinearIDState));
        h1("Done");

        h1("Example 8: Attach child to linear parent1 via LinearPointer");
        LinearPointer linearParentStatePointer = new LinearPointer<>(parentLinearID,LinearParentState.class);
        if(linearParentStatePointer.isResolved()) {
            System.out.println("Resolved");
        } else {
            System.out.println("Not resolved");
        }
        StateAndRef parentLinearStateAndRef  = linearParentStatePointer.resolve(serviceHub);
        if(!linearParentStatePointer.isResolved())
            parentLinearStateAndRef  = linearParentStatePointer.resolve(serviceHub);

        ChildViaLinearPointerState childViaLinearPointerState = new ChildViaLinearPointerState(linearParentStatePointer,me);
        signAndFinalize( new TransactionBuilder(notary)
                .addCommand(new Commands.AddChildState(),myKey)
                .addOutputState(linearParent)
                .addOutputState(childViaLinearPointerState));
        h1("Done");


        h2("Example 9: Transition ParentState and LinearParentState and see what happens to child states");

        StateAndRef parent1StateAndRef = serviceHub.toStateAndRef(parent1StateRef);
        parentLinearStateAndRef = linearParentStatePointer.resolve(serviceHub);

        describe("Before Transition: ParentState",parent1StateAndRef);
        describe("Before Transition: ChildViaStateRef's parent field",childViaStateRefState.getParent());
        describe("Before Transition: ChildViaStaticPointer's parent field",childViaStaticPointerState.getParent().getPointer());

        tb = new TransactionBuilder(notary)
                .addInputState(parent1StateAndRef)
                .addOutputState(new ParentState(me));
        ftx = signAndFinalize(tb);

        describe("After Transition: ParentState" + ftx.getTx().getId() + " 0");
        childViaStateRefState = serviceHub.getVaultService().queryBy(ChildViaStateRefState.class).getStates().get(0).getState().getData();
        describe("After Transition: ChildViaStateRef's parent field",childViaStateRefState.getParent());

        childViaStaticPointerState = serviceHub.getVaultService().queryBy(ChildViaStaticPointerState.class).getStates().get(0).getState().getData();
        describe("After Transition: ChildViaStaticPointer's parent field",childViaStaticPointerState.getParent().getPointer());


            Vault.Page<ParentState> parentStatePage = serviceHub.getVaultService().queryBy(ParentState.class);
            List<StateAndRef<ParentState>> parentStates = parentStatePage.getStates();
            parentStates.forEach((stateAndRef->{
                System.out.println(
                        "Ref TX hash:"
                        + stateAndRef.getRef().getTxhash()
                        + "i: "
                        + stateAndRef.getRef().getIndex()
                );
            }));

            Party observer = serviceHub
                    .getIdentityService()
                    .partiesFromName("Observer", true)
                    .iterator()
                    .next();








        StateRef parentStateRef = new StateRef(ftx.getId(),0);
        StateAndRef<ParentState> parentState = serviceHub.toStateAndRef(parentStateRef);
        parentState.getState();

//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println("*** Identical second transaction - what happens to root hash?");
//        tb = new TransactionBuilder(notary)
//                .addCommand(new Command.CreateParentStateCommand(), myKey)
//                .addOutputState(parent1)
//                .addOutputState(parent2);
//        /*
//                .setPrivacySalt(new PrivacySalt(salt));
//        */
//        transactionStates = tb.outputStates();
//        for (int i=0;i<transactionStates.size();i++) {
//            System.out.println("Before signed -Output index: " + i + " state:" + transactionStates.get(i));
//        }
//
//        SignedTransaction stx2 = serviceHub.signInitialTransaction(tb);
//        System.out.println("ParentState Merkle root / tx hash: " + stx2.getId());
//        subFlow(new FinalityFlow(stx2, Collections.<FlowSession>emptySet()));
//
//        for (int i=0;i<transactionStates.size();i++) {
//            System.out.println("After signed - Output index: " + i + "state: " + transactionStates.get(i));
//        }
//        System.out.println("ParentState Query the vault");
//        //final QueryCriteria.VaultQueryCriteria vq = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
//        Vault.Page<ParentState> parentStatePage = serviceHub.getVaultService().queryBy(ParentState.class);
//        List<StateAndRef<ParentState>> parentStates = parentStatePage.getStates();
//        System.out.println("Total states available: " + parentStatePage.getTotalStatesAvailable());
//        for (StateAndRef<ParentState> stateAndRef : parentStates) {
//            System.out.println("Ref tx hash: " + stateAndRef.getRef().getTxhash() + " i:" + stateAndRef.getRef().getIndex() + " (no state)");
//        }
//
//        StateAndRef parent1StateAndRef = parentStates.get(0);
//        TransactionState<ParentState> parent1FromLedger = parent1StateAndRef.getState();
//        StateRef parent1StateRef = parent1StateAndRef.getRef();
//
//
//
//
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println("*** Building auto tx");
//
//        SignedTransaction fullySigned = subFlow(new FinalityFlow(
//            serviceHub.signInitialTransaction(
//                    new TransactionBuilder(notary)
//                    .addCommand(new Command.CreateAutoIncrementStateCommand(), myKey)
//                    .addOutputState(new AutoIncrementState(me))
//                    .addOutputState(new AutoIncrementState(me))
//                    .addOutputState(new AutoIncrementState(me))
//                    .addOutputState(new AutoIncrementState(me))),Collections.<FlowSession>emptySet()));
//
//        SignedTransaction stx3 = serviceHub.signInitialTransaction(tb);
//        subFlow(new FinalityFlow(stx3, ));
//        System.out.println("AutoIncrementState Merkle root / tx hash: " + stx3.getId());
//        for (int i=0;i<transactionStates.size();i++) {
//            System.out.println("After signed - Output index: " + i + "state: " + transactionStates.get(i));
//        }
//        System.out.println("AutoIncrementState - Query the vault");
//        //final QueryCriteria.VaultQueryCriteria vq = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
//        final Vault.Page<AutoIncrementState> autoIncrementStatePage = serviceHub.getVaultService().queryBy(AutoIncrementState.class);
//        final List<StateAndRef<AutoIncrementState>> autoIncrementStatePageStates = autoIncrementStatePage.getStates();
//        System.out.println("Total states available: " + autoIncrementStatePage.getTotalStatesAvailable());
//        for (StateAndRef<AutoIncrementState> sar : autoIncrementStatePageStates) {
//            System.out.println("Ref tx hash: " + sar.getRef().getTxhash() + " i:" + sar.getRef().getIndex() + " data:" + sar.getState().getData().get());
//        }
//
//
//        System.out.println();
//        System.out.println();
//        System.out.println("*** Building linear state tx");
//        ToDoState todos1 = new ToDoState(me,me,task1);
//        ToDoState todos2 = new ToDoState(me,me,task2);
//
//        System.out.println("Linear ID of state is " + todos1.getLinearId());
//        System.out.println("Linear ID of state is " + todos2.getLinearId());
//
//        tb = new TransactionBuilder(notary)
//                .addOutputState(todos1)
//                .addOutputState(todos2)
//                .addCommand(new Command.CreateToDoCommand(), myKey);
//
//        SignedTransaction stx4 = serviceHub.signInitialTransaction(tb);
//        subFlow(new FinalityFlow(stx4, Collections.<FlowSession>emptySet()));
//        System.out.println("ToDoState Merkle root / tx hash: " + stx4.getId());
//        for (int i=0;i<transactionStates.size();i++) {
//            System.out.println("After signed - Output index: " + i + " state:" + transactionStates.get(i));
//        }
//        System.out.println("ToDoState - Query the vault");
//        QueryCriteria q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(todos1.getLinearId().getId()));
//        Vault.Page<ToDoState> taskStatePage = serviceHub.getVaultService().queryBy(ToDoState.class, q);
//        List<StateAndRef<ToDoState>> states = taskStatePage.getStates();
//        StateAndRef<ToDoState> sar = states.get(0); // state and ref is the tx id, output id and transaction value
//        ToDoState toDoState = sar.getState().getData();
//
//        System.out.println(toDoState.getTaskDescription());
//
//        for (StateAndRef<ToDoState> s : states) {
//            System.out.println(s.getRef());
//            ToDoState ts = s.getState().getData();
//            System.out.println(ts.getTaskDescription());
//            System.out.println("Assigned to: " + ts.getAssignedTo().getName().getOrganisation());
//        }
//
//
//        // Cycle through multiple reference types
//        StateRef sr = sar.getRef();
//
//        // State ref to parent
//        System.out.println("State ref parent");
//        SubTaskStateRefParentState subTaskStateRefParentState = new SubTaskStateRefParentState(sr,"Check work",me);
//
//        tb = new TransactionBuilder(notary)
//                .addOutputState(subTaskStateRefParentState)
//                .addCommand(new Command.AddSubTaskCommand(),myKey);
//
//        SignedTransaction subTaskTx1 = serviceHub.signInitialTransaction(tb);
//        subFlow(new FinalityFlow(subTaskTx1, Collections.<FlowSession>emptySet()));
//
//        // Linear id to parent
//        System.out.println("Linear ID parent");
//        ChildViaLinearIDState subTaskLinearIDParentState = new ChildViaLinearIDState(toDoState.getLinearId(),"Check work2",me);
//        UniqueIdentifier subTaskLineardID = subTaskLinearIDParentState.getLinearId();
//        System.out.println("Linear ID for subtask is " + subTaskLineardID.getId().toString());
//        tb = new TransactionBuilder(notary)
//                .addOutputState(subTaskLinearIDParentState)
//                .addCommand(new Command.AddSubTaskCommand(),myKey);
//
//        SignedTransaction subTaskTx2 = serviceHub.signInitialTransaction(tb);
//        final SignedTransaction signedTransaction = subFlow(new FinalityFlow(subTaskTx2, Collections.<FlowSession>emptySet()));
//
//        // LinearPointer to parent
//        LinearPointer<ToDoState> lp = new LinearPointer<ToDoState>(toDoState.getLinearId(),ToDoState.class);
//        ChildViaLinearPointerState subTaskLinearPointerParentState = new ChildViaLinearPointerState(lp,"Check work 3",me);
//        UniqueIdentifier subTaskLinearPointerID = subTaskLinearPointerParentState.getLinearId();
//
//        System.out.println("Resolved?: " + lp.isResolved());
//        tb = new TransactionBuilder(notary)
//                .addOutputState(subTaskLinearPointerParentState)
//                .addCommand(new Command.AddSubTaskCommand(),myKey);
//
//        SignedTransaction subTaskTx3 = serviceHub.signInitialTransaction(tb);
//        subFlow(new FinalityFlow(subTaskTx3, Collections.<FlowSession>emptySet()));
//        System.out.println("Resolved?: " + lp.isResolved());
//
//
//        // StaticPointer to parent
//        StaticPointer<ToDoState> staticPointer = new StaticPointer<ToDoState>(sr,ToDoState.class);
//
//
//        // ---------------------------------------
//
//        // Transition tofo flo
//        System.out.println("Transitioning Todo");
//
//        ToDoState toDoState2 = new ToDoState(me,me, toDoState.getTaskDescription()+"Updated", toDoState.getLinearId());
//        tb = new TransactionBuilder(notary)
//                .addInputState(sar)
//                .addOutputState(toDoState2)
//                .addCommand(new Command.ModifyTaskCommand(),myKey);
//
//        SignedTransaction toDoState2Tx1 = serviceHub.signInitialTransaction(tb);
//        subFlow(new FinalityFlow(toDoState2Tx1, Collections.<FlowSession>emptySet()));
//        System.out.println("Transitioning Todo Complete");
//        // -------------------------------------------
//
//        // State ref results
//        System.out.println("Find subtask, find its parent, via state ref");
//        q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(subTaskStateRefParentState.getLinearId().getId()));
//        final Vault.Page<SubTaskStateRefParentState> subTaskStateRefParentStatePage = serviceHub.getVaultService().queryBy(SubTaskStateRefParentState.class, q);
//        final StateRef parent = subTaskStateRefParentStatePage.getStates().get(0).getState().getData().getParent();
//
//        QueryCriteria.VaultQueryCriteria vaultQueryCriteria =
//                new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED, ImmutableSet.of(ToDoState.class), ImmutableList.of(parent));
//        Vault.Page<ToDoState> toDoStatePage = serviceHub.getVaultService().queryBy(ToDoState.class, vaultQueryCriteria);
//        System.out.println("Unconsumed Todo count: " + toDoStatePage.getStates().size()); // 0
//
//        vaultQueryCriteria =
//                new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.CONSUMED, ImmutableSet.of(ToDoState.class), ImmutableList.of(parent));
//        toDoStatePage = serviceHub.getVaultService().queryBy(ToDoState.class, vaultQueryCriteria);
//        System.out.println("Consumed Todo count: " + toDoStatePage.getStates().size()); // 1 - StateRef is stale
//
//
//        // Linear ID parent results
//        System.out.println("Find subtask, find its parent, via linear id");
//        q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(subTaskLineardID.getId()));
//        final Vault.Page<ChildViaLinearIDState> subTaskLinearIDParentStatePage = serviceHub.getVaultService().queryBy(ChildViaLinearIDState.class, q);
//        final UUID id = subTaskLinearIDParentStatePage.getStates().get(0).getState().getData().getParent().getId();
//
//        q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(id));
//        toDoStatePage = serviceHub.getVaultService().queryBy(ToDoState.class, q);
//
//        System.out.println("Linear ID parent size: " + toDoStatePage.getStates().size());
//        if(toDoStatePage.getStates().size()>0) {
//            System.out.println("Description: " + toDoStatePage.getStates().get(0).getState().getData().getTaskDescription());
//        }
//
//
//        // Linear Point resolution
//        System.out.println("Find subtask, find its parent, via linear pointer");
//        System.out.println("Will search for: " + subTaskLinearPointerID.getId());
//        q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(subTaskLinearPointerID.getId()));
//        final Vault.Page<ChildViaLinearPointerState> subTaskLinearPointerParentStatePage = serviceHub.getVaultService().queryBy(ChildViaLinearPointerState.class, q);
//        System.out.println("lp size: " + subTaskLinearPointerParentStatePage.getStates().size());
//        final LinearPointer lparent1 = subTaskLinearPointerParentStatePage.getStates().get(0).getState().getData().getParent();
//        System.out.println("Is resolved? " + lparent1.isResolved());
//
//        q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(lparent1.getPointer().getId()));
//        toDoStatePage = serviceHub.getVaultService().queryBy(ToDoState.class, q);
//
//        System.out.println("Linear ID parent: " + toDoStatePage.getStates().size());
//        if(toDoStatePage.getStates().size()>0) {
//            System.out.println("Description: " + toDoStatePage.getStates().get(0).getState().getData().getTaskDescription());
//        }

        // Static point resolution

        //StaticPointer<ToDoState> staticPointer = new StaticPointer<ToDoState>(sr,ToDoState.class);






// Lookup transactionstates


//        System.out.println();
//        System.out.println();
//        System.out.println("*** Duplicate linear state tx");
//        tb = new TransactionBuilder(notary)
//                .addOutputState(todos1)
//                .addOutputState(todos2)
//                .addCommand(new Command.CreateToDoCommand(), myKey);
//
//        SignedTransaction stx6 = serviceHub.signInitialTransaction(tb);
//        System.out.println("ToDoState Merkle root / tx hash: " + stx6.getId());
//        outputs = stx1.getCoreTransaction().getOutputs();
//        for (int i=0;i<transactionStates.size();i++) {
//            System.out.println("After signed -Output index: " + i + " state:" + transactionStates.get(i));
//        }
//        System.out.println("ToDoState 2 - Query the vault");
//        taskStatePage = serviceHub.getVaultService().queryBy(ToDoState.class, q);
//        states = taskStatePage.getStates();
//        sar = states.get(0);
//        toDoState = sar.getState().getData();
//
//        System.out.println(toDoState.getTaskDescription());
//
//        for (StateAndRef<ToDoState> s : states) {
//            System.out.println(s.getRef());
//            ToDoState ts = s.getState().getData();
//            System.out.println(ts.getTaskDescription());
//            System.out.println("Assigned to: " + ts.getAssignedTo().getName().getOrganisation());
//        }
//
//        subFlow(new FinalityFlow(stx6, Collections.<FlowSession>emptySet()));


//
//
//
//        System.out.println();
//        System.out.println();
//        System.out.println("*** Building ownable state tx");
//
//        tb = new TransactionBuilder(notary)
//                .addCommand(new Command.CreateToDoCommand(), myKey)
//                .addOutputState(new OwnableTemplateState(me))
//                .addOutputState(new OwnableTemplateState(me))
//                .addOutputState(new OwnableTemplateState(me))
//                .addOutputState(new OwnableTemplateState(me));
//
//        SignedTransaction stx4 = serviceHub.signInitialTransaction(tb);
//        System.out.println("OwnableTemplateState Merkle root / tx hash: " + stx4.getId());
//        outputs = stx1.getCoreTransaction().getOutputs();
//        for (int i=0;i<transactionStates.size();i++) {
//            System.out.println("After signed -Output index: " + i + " state:" + transactionStates.get(i));
//        }
//        System.out.println("AutoIncrementState - Query the vault");
//        //final QueryCriteria.VaultQueryCriteria vq = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
//        final Vault.Page<OwnableTemplateState> ownableTemplateStatePage = serviceHub.getVaultService().queryBy(OwnableTemplateState.class);
//        final List<StateAndRef<OwnableTemplateState>> ownableTemplateStatePageStates = ownableTemplateStatePage.getStates();
//        System.out.println("Total states available: " + ownableTemplateStatePage.getTotalStatesAvailable());
//        for (StateAndRef<OwnableTemplateState> sar : ownableTemplateStatePageStates) {
//            System.out.println("Ref tx hash: " + sar.getRef().getTxhash() + " i:" + sar.getRef().getIndex() + "owner: " + sar.getState().getData().getOwner().nameOrNull());
//        }
//        subFlow(new FinalityFlow(stx4, Collections.<FlowSession>emptySet()));
//
//
//
//


//        // Coin selection
//
//        final Vault.Page<ParentState> templateStatePage = serviceHub.getVaultService().queryBy(ParentState.class, templateStates);
//        templateStatePage.getStates()
//        final List<StateRef> stateRefs = templateStates.getStateRefs();
//        for (StateRef sr : stateRefs) {
//            System.out.println(sr.getTxhash() + " Index: " + sr.getIndex());
//        }


//        final QueryCriteria q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(UUID.fromString(linearId)));
//        final Vault.Page<ToDoState> taskStatePage = serviceHub.getVaultService().queryBy(ToDoState.class, q);
//        final List<StateAndRef<ToDoState>> states = taskStatePage.getStates();
//        final StateAndRef<ToDoState> sar = states.get(0);
//        System.out.println("Ref of parent todo:" + sar.getRef());
//        StateRef ref = sar.getRef();
//        System.out.println(ref.);
//
//        // Transaction builder is not immutable
//        TransactionBuilder tb = new TransactionBuilder(notary);
//
//        // Command has two parameters - but this compiled when add command had only one param!
//        tb = tb. //, me.getOwningKey()); // at least one is required
//        System.out.println("1");
//        tb = tb.addOutputState(ts);
//        List<StateAndRef<ToDoStatusRefState>> refStates = serviceHub.getVaultService().queryBy(ToDoStatusRefState.class).getStates();
//        StateAndRef refz = null;
//        for (StateAndRef<ToDoStatusRefState> ref : refStates) {
//            if(ref.getState().getData().getStatus().equalsIgnoreCase("Open")) {
//                refz = ref;
//                System.out.println("found ");
//            }
//        }
//
//        ReferencedStateAndRef stateRef = refz.referenced();
//        tb = tb.addReferenceState(stateRef);
//        //CommandWithParties<Command.CreateToDoCommand> cmd =
//        //TimeWindow tw = TimeWindow.between(Instant.now(),Instant.now().plusSeconds(300));
//        //tb = tb.setTimeWindow(tw);
//        //tb = tb.withItems(notary,ts,tw);
//        System.out.println("1");
//        SignedTransaction stx = serviceHub.signInitialTransaction(tb);
////        SignedTransaction stx = getServiceHub()
////                .signInitialTransaction(
////                        new TransactionBuilder().withItems(
////                                notary,
////                                new ToDoState(me,me,taskDescription),
////                                TimeWindow.between(Instant.now(),Instant.now().plusSeconds(300)),
////                                new CommandWithParties<>(ImmutableList.of(me.getOwningKey()),ImmutableList.of(me),new Command.CreateToDoCommand())));
////        subFlow(new FinalityFlow(
////                getServiceHub()
////                        .signInitialTransaction(
////                                new TransactionBuilder().withItems(
////                                        notary,
////                                        new ToDoState(me,me,taskDescription),
////                                        TimeWindow.between(Instant.now(),Instant.now().plusSeconds(300)),
////                                        new CommandWithParties<>(ImmutableList.of(
////                                                getOurIdentity().getOwningKey()),
////                                                ImmutableList.of(getOurIdentity()),
////                                                new Command.CreateToDoCommand()))),
////                Collections.<FlowSession>emptySet())
////        );
////        System.out.println("1");
//        //subFlow(new FinalityFlow(stx));
//        subFlow(new FinalityFlow(stx, Collections.<FlowSession>emptySet()));
//        System.out.println("1");

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

