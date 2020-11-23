package com.learncorda.sdroracle.client;

import co.paralleluniverse.fibers.Suspendable;
import com.learncorda.sdroracle.contracts.GetSDRCommand;
import com.learncorda.sdroracle.states.SDRState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.FilteredTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class Initiator extends FlowLogic<Void> {
    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }
    private String date;
    private Float rate;
    public Initiator (String date, String rate) {
        this.date = date;
        this.rate = Float.parseFloat(rate);
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {

        ServiceHub sb = getServiceHub();
        Set<Party> parties = sb.getIdentityService().partiesFromName("Notary", true);
        Party notary = parties.iterator().next();

        parties = sb.getIdentityService().partiesFromName("SDROracle", true);
        Party oracleSDR = parties.iterator().next();
        SDRState sdr = new SDRState(date,rate,getOurIdentity());
        GetSDRCommand cmd = new GetSDRCommand(date,rate);

        List<PublicKey> signers = ImmutableList.of(oracleSDR.getOwningKey(),getOurIdentity().getOwningKey());

        TransactionBuilder tb = new TransactionBuilder(notary)
                .addOutputState(sdr)
                .addCommand(cmd,signers);
        tb.verify(sb);

        SignedTransaction ptx = sb.signInitialTransaction(tb);
        Predicate<Object> predicate = new Predicate<Object>() {
            @Override
            public boolean test(Object o) {
                if (o instanceof Command
                        && ((Command) o).getValue() instanceof GetSDRCommand
                        && ((Command) o).getSigners().contains(oracleSDR.getOwningKey()))
                    return true;
                else
                    return false;
            }
        };
        final FilteredTransaction filteredTransaction = ptx.buildFilteredTransaction(predicate);
        // Connect to oracle
        FlowSession session = initiateFlow(oracleSDR);

        //
        final TransactionSignature signature = session.sendAndReceive(TransactionSignature.class, filteredTransaction).unwrap(s -> s);
        final SignedTransaction signedTransaction = ptx.withAdditionalSignature(signature);

        subFlow(new FinalityFlow(signedTransaction,ImmutableList.of()));

        return null;
    }
}
