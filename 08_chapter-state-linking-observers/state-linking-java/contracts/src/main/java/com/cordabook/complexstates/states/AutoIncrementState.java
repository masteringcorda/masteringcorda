package com.cordabook.complexstates.states;

import com.cordabook.complexstates.contracts.TemplateContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(TemplateContract.class)
public class AutoIncrementState implements ContractState {
    private Party party;
    private Integer state;
    private static Integer counter = 0;
    public AutoIncrementState(Party party) {
        this.party = party;
        this.state = counter++;
    }

    public Integer get() {
        return state;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(party);
    }
}