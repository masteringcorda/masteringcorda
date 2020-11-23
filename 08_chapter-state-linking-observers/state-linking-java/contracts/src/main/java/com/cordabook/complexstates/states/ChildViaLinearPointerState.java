package com.cordabook.complexstates.states;


import com.cordabook.complexstates.contracts.TemplateContract;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@BelongsToContract(TemplateContract.class)
public class ChildViaLinearPointerState implements LinearState {
    private final LinearPointer parent;
    private final Party party;
    private final UniqueIdentifier linearId;

    public ChildViaLinearPointerState(LinearPointer parent, Party party) {
        this.parent = parent;
        this.party = party;
        this.linearId = new UniqueIdentifier();
    }


    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(party);
    }

    public LinearPointer getParent() {
        return parent;
    }

    public Party getParty() {
        return party;
    }
}
