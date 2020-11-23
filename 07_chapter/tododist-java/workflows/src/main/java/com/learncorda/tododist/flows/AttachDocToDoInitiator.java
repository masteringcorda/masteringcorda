package com.learncorda.tododist.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.learncorda.tododist.contracts.Command;
import com.learncorda.tododist.states.ToDoState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.AttachmentStorage;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.PublicKey;
import java.util.*;


// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class AttachDocToDoInitiator extends FlowLogic<Void> {
    private String linearId;
    private String fileURL;
    private String fileName;

    public AttachDocToDoInitiator(String linearId, String fileURL, String fileName) {
        this.linearId = linearId;
        this.fileURL = fileURL;
        this.fileName = fileName;
    }


    @Suspendable
    @Override
    public Void call() throws FlowException {

        String url= "https://github.com/masteringcorda/masteringcorda/07_chapter/raw/master/sample.jar.zip";

        ServiceHub serviceHub = getServiceHub();
        AttachmentStorage attachmentService = getServiceHub().getAttachments();


        BufferedInputStream in = null;
        SecureHash secureHash = null;
        try {
            in = new BufferedInputStream(new URL(fileURL).openStream());
            InputStream inputStream = new URL(fileURL).openStream();
            secureHash = attachmentService.importAttachment(inputStream, "", fileName);

            System.out.println(secureHash.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        final QueryCriteria q = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(UUID.fromString(linearId)));
        final Vault.Page<ToDoState> taskStatePage = serviceHub.getVaultService().queryBy(ToDoState.class, q);
        final List<StateAndRef<ToDoState>> states = taskStatePage.getStates();
        final StateAndRef<ToDoState> sar = states.get(0);
        final ToDoState toDoState = sar.getState().getData();

        Party notary = serviceHub.getNetworkMapCache().getNotaryIdentities().get(0);

        System.out.println("Building tb");
        PublicKey myKey = getOurIdentity().getOwningKey();

        List<PublicKey> signers = ImmutableSet.of(
                toDoState.getAssignedBy().getOwningKey(),
                toDoState.getAssignedTo().getOwningKey())
                .asList();

        TransactionBuilder tb = new TransactionBuilder(notary)
                .addInputState(sar)
                .addOutputState(toDoState)
                .addCommand(new Command.AttachDocToDoCommand(),signers)
                .addAttachment(secureHash);

        SignedTransaction ptx = getServiceHub().signInitialTransaction(tb);
        if(toDoState.getAssignedBy().equals(toDoState.getAssignedTo())) {
            subFlow(new FinalityFlow(ptx, Collections.<FlowSession>emptySet()));
        } else {
            List<AbstractParty> parties = toDoState.getParticipants();
            parties.remove(getOurIdentity());
            FlowSession assignedToSession = initiateFlow((Party)parties.get(0));
            SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, ImmutableSet.of(assignedToSession)));
            subFlow(new FinalityFlow(stx, Arrays.asList(assignedToSession)));
        }

        return null;
    }
}
