package com.learncorda.tododist.states;

import com.learncorda.tododist.contracts.TemplateContract;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@BelongsToContract(TemplateContract.class)
public class ToDoStatusRefState implements ContractState {

    final private Party creator;
    final private String status;

    public String getStatus() {
        return status;
    }

    public ToDoStatusRefState(Party creator, String status) {
        this.creator = creator;
        this.status = status;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(creator);
    }
}
