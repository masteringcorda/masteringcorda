package com.cordabook.complexstates.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.cordabook.complexstates.states.EquityCUSIPReferenceState;
import com.cordabook.complexstates.states.TradeState;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.stream.Collectors;

// ******************
// * TradeResponder flow *
// ******************
@InitiatedBy(TradeInitiator.class)
public class TradeResponder extends FlowLogic<Void> {
    private FlowSession counterpartySession;

    public TradeResponder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        ServiceHub serviceHub = getServiceHub();
        Party me = getOurIdentity();
        PublicKey myKey = me.getOwningKey();
        final SignTransactionFlow signTransactionFlow = new SignTransactionFlow(counterpartySession) {
            @Override
            protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException { // when is this called? doesnt seem to be called
                final StateAndRef<EquityCUSIPReferenceState> cusipRefState = serviceHub.toStateAndRef(stx.getCoreTransaction().getReferences().get(0));
                System.out.println("CUSIP received: " + cusipRefState.getState().getData().getCusip());

            }
        };
        SignedTransaction stx = subFlow(signTransactionFlow);
        subFlow(new ReceiveFinalityFlow(counterpartySession, stx.getId()));
        return null;
    }
}
