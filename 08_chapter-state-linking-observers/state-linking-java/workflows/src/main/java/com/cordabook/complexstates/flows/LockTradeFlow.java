package com.cordabook.complexstates.flows;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import com.cordabook.complexstates.contracts.Commands;
import com.cordabook.complexstates.states.*;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.*;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.VaultService;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.NonEmptySet;

import java.security.PublicKey;
import java.util.List;
import java.util.UUID;


// flow start CreateToDoFlow task: "Get some cheese"
// flow start AssignToDoInitiator linearId: 3d3d3a7b-35bf-4b1d-83d7-9f10a9c98657 , assignedTo: PartyA

// ******************
// * TradeInitiator flow *
// ******************
@StartableByRPC
public class LockTradeFlow extends FlowLogic<Void> {

    private String linearId;

    public LockTradeFlow(String linearId) {
        this.linearId = linearId;
    }


    @Suspendable
    @Override
    public Void call() throws FlowException {
        ServiceHub serviceHub = getServiceHub();
        Party me = getOurIdentity();
        PublicKey myKey = me.getOwningKey();
        Party notary = serviceHub.getNetworkMapCache().getNotaryIdentities().get(0);

        QueryCriteria q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(UUID.fromString(linearId)));
        final List<StateAndRef<TradeState>> tradeStates = getServiceHub()
                .getVaultService()
                .queryBy(TradeState.class, q)
                .getStates();
        final StateAndRef<TradeState> tradeStateStateAndRef = tradeStates.get(0);
        final StateAndRef<TradeState> contractStateStateAndRef = getServiceHub().toStateAndRef(tradeStateStateAndRef.getRef());

        StateRef sr1 = contractStateStateAndRef.getRef();

        VaultService vs = getServiceHub().getVaultService();
        final NonEmptySet<StateRef> stateRefs = NonEmptySet.of(sr1);
        UUID lockID = UUID.randomUUID();
        System.out.println("Lock ID generated: " + lockID.toString());
        vs.softLockReserve(lockID,stateRefs);
        TransactionBuilder tb = new TransactionBuilder(notary)
                .addInputState(tradeStateStateAndRef)
                .addCommand(new Commands.Trade(),myKey);
        tb.setLockId(lockID);
        try {
            Strand.sleep(10000);
        } catch (SuspendExecution suspendExecution) {
            suspendExecution.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Awake... and sleeping");
        serviceHub.signInitialTransaction(tb);

        try {
            Strand.sleep(10000);
        } catch (SuspendExecution suspendExecution) {
            suspendExecution.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final NonEmptySet<StateRef> partialToRelease = NonEmptySet.of(sr1);
        vs.softLockRelease(lockID,stateRefs);


        return null;
    }
}

