package com.cordabook.complexstates.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.cordabook.complexstates.contracts.Commands;
import com.cordabook.complexstates.states.EquityCUSIPReferenceState;
import com.cordabook.complexstates.states.TradeState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.core.contracts.FungibleState;
import net.corda.core.contracts.ReferencedStateAndRef;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.intellij.lang.annotations.Flow;

import javax.annotation.Signed;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// ******************
// * TradeInitiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class TradeInitiator extends FlowLogic<Void> {

    public TradeInitiator(String symbol, Integer quantity, String counterParty) {
        this.symbol = symbol;
        this.counterParty = counterParty;
        this.quantity = quantity;
    }

    private String symbol;
    private String counterParty;
    private Integer quantity;


    @Suspendable
    @Override
    public Void call() throws FlowException {
        ServiceHub serviceHub = getServiceHub();
        Party me = getOurIdentity();
        PublicKey myKey = me.getOwningKey();
        Party notary = serviceHub.getNetworkMapCache().getNotaryIdentities().get(0);

        Party tradingCounterParty = serviceHub.getIdentityService().partiesFromName(counterParty, true).iterator().next();
        final StateAndRef<EquityCUSIPReferenceState> cusipReferenceState = serviceHub.getVaultService().queryBy(EquityCUSIPReferenceState.class)
                .getStates()
                .stream()
                .filter(stockSymbolStateAndRef -> stockSymbolStateAndRef.getState().getData().getSymbol().equalsIgnoreCase(symbol))
                .collect(Collectors.toList())
                .get(0);

        TradeState trade = new TradeState(this.symbol,quantity,me,tradingCounterParty);
        System.out.println("LinearID: " + trade.getLinearId().getId().toString());
        TransactionBuilder tb = new TransactionBuilder(notary)
                .addReferenceState(new ReferencedStateAndRef<>(cusipReferenceState))
                .addCommand(new Commands.Trade(),myKey,tradingCounterParty.getOwningKey())
                .addOutputState(trade);

        FlowSession session = initiateFlow(tradingCounterParty);
        SignedTransaction stx = subFlow(new CollectSignaturesFlow(sign(tb), ImmutableList.of(session)));
        final SignedTransaction ftx = subFlow(new FinalityFlow(stx, ImmutableList.of(session)));
        subFlow(new ObserverInitiator(ftx));

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
