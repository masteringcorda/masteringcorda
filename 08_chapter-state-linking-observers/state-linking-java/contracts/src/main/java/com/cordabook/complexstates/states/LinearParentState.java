package com.cordabook.complexstates.states;

import com.cordabook.complexstates.contracts.TemplateContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(TemplateContract.class)
public class LinearParentState implements LinearState {
    private Party party;
    private UniqueIdentifier linearId;
    public LinearParentState(Party party) {
        this.linearId = new UniqueIdentifier();
        this.party = party;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(party);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }
}