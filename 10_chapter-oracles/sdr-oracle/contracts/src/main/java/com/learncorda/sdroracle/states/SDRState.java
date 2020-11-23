package com.learncorda.sdroracle.states;

import com.learncorda.sdroracle.contracts.TemplateContract;
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
public class SDRState implements ContractState {
    private String date;
    private Float USDSDR;
    private Party requestor;

    public SDRState(String date, Float USDSDR, Party requestor) {
        this.date = date;
        this.USDSDR = USDSDR;
        this.requestor = requestor;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(requestor);
    }
}