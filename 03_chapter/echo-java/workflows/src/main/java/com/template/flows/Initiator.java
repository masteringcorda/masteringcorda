package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.IdentityService;
import net.corda.core.node.services.NetworkMapCache;
import net.corda.core.utilities.ProgressTracker;

import java.util.List;
import java.util.Set;

// ******************
// * EchoInitiatorFlow flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class d Initiator extends FlowLogic<Void> {
    private final ProgressTracker progressTracker = new ProgressTracker();
    private String message;
    private String recipient;

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public Initiator(String message, String recipient) {
        this.message = message;
        this.recipient = recipient;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        ServiceHub serviceHub = getServiceHub();
        NodeInfo ni = serviceHub.getMyInfo();
        NetworkMapCache nm = serviceHub.getNetworkMapCache();


        UniqueIdentifier x = new UniqueIdentifier();

        IdentityService identityService = serviceHub.getIdentityService();
        Set<Party> partyRecipients = identityService.partiesFromName(this.recipient,true);
        Party partyRecipient = partyRecipients.iterator().next();
        FlowSession session = initiateFlow(partyRecipient);
        session.send(message);
        String echo = session.receive(String.class).unwrap(s -> s);
        System.out.println("Echo message: " + echo);
        return null;
    }

}
