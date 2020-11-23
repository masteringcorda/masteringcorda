package com.cordabook.complexstates.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.cordabook.complexstates.contracts.Commands;
import com.cordabook.complexstates.states.EquityCUSIPReferenceState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.StateRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.Collections;
import java.util.List;

// ******************
// * TradeInitiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class GenerateCUSIPReferenceStatesFlow extends FlowLogic<Void> {
    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        ServiceHub serviceHub = getServiceHub();
        Party me = getOurIdentity();
        PublicKey myKey = me.getOwningKey();
        Party notary = serviceHub.getNetworkMapCache().getNotaryIdentities().get(0);

        EquityCUSIPReferenceState ssIBM = new EquityCUSIPReferenceState("IBM", "459200101", me);
        EquityCUSIPReferenceState ssMSFT = new EquityCUSIPReferenceState("MSFT", "594918104", me);
        EquityCUSIPReferenceState ssGOOG = new EquityCUSIPReferenceState("GOOG", "02079K107", me);

        TransactionBuilder tb = new TransactionBuilder(notary)
                .addOutputState(ssIBM)
                .addOutputState(ssMSFT)
                .addOutputState(ssGOOG)
                .addCommand(new Commands.AddStockSymbol(),myKey);

        final SignedTransaction signedTransaction = signAndFinalize(tb);

        final List<StateRef> references = signedTransaction.getCoreTransaction().getReferences();

        for (StateRef sr : references) {
            StateAndRef<EquityCUSIPReferenceState> sar = serviceHub.toStateAndRef(sr);
            System.out.println("Symbols stored: " + sar.getState().getData().getSymbol());
        }

        return null;
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
}
