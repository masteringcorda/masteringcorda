package com.cordabook.complexstates.states;

import com.cordabook.complexstates.contracts.TemplateContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StaticPointer;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(TemplateContract.class)
public class ChildViaStaticPointerState implements ContractState {
    private Party party;
    private StaticPointer parent;
    public ChildViaStaticPointerState(StaticPointer parent, Party party) {
        this.parent = parent;
        this.party = party;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(party);
    }

    public Party getParty() {
        return party;
    }

    public StaticPointer getParent() {
        return parent;
    }
}