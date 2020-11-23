package com.learncorda.tododist.states;

import com.google.common.collect.ImmutableList;
import com.learncorda.tododist.contracts.ToDoContract;
import net.corda.core.contracts.*;
import net.corda.core.flows.FlowLogicRef;
import net.corda.core.flows.FlowLogicRefFactory;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(ToDoContract.class)
public class ToDoState implements ContractState, LinearState, QueryableState, SchedulableState {

    private final Party assignedBy;
    private final Party assignedTo;
    private final String taskDescription;
    private final UniqueIdentifier linearId;
    private final Instant deadlineReminder;

    public Party getAssignedBy() {
        return assignedBy;
    }

    public Party getAssignedTo() {
        return assignedTo;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public ToDoState(Party assignedBy, Party assignedTo, String taskDescription) {
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.taskDescription = taskDescription;
        this.linearId = new UniqueIdentifier();
        this.deadlineReminder = Instant.now().plusSeconds(30);
    }

    @ConstructorForDeserialization
    public ToDoState(Party assignedBy, Party assignedTo, String taskDescription, UniqueIdentifier linearId, Instant deadlineReminder) {
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.taskDescription = taskDescription;
        this.linearId = linearId;
        this.deadlineReminder = deadlineReminder;
    }
    // Be sure to update this method and add deadlineReminder
    public ToDoState assign(Party assignedTo) {
        return new ToDoState(assignedBy,assignedTo,taskDescription,linearId,deadlineReminder);
    }


    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(assignedBy, assignedTo);
    }

    @Override
    public UniqueIdentifier getLinearId() {
        System.out.println(linearId);
        return linearId;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof ToDoSchemaV1) {
            return new ToDoSchemaV1.ToDoModel(taskDescription, linearId.getId());
        } else if (schema instanceof ToDoSchemaV2) {
            return new ToDoSchemaV2.ToDoModel(taskDescription, linearId.getId(),assignedTo);
        } else
            throw new IllegalArgumentException("No supported schema found");

    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        System.out.println("supported schema called");
        return ImmutableList.of(new ToDoSchemaV1(), new ToDoSchemaV2());
    }

    // This will work

    @Nullable
    @Override
    public ScheduledActivity nextScheduledActivity(@NotNull StateRef thisStateRef, @NotNull FlowLogicRefFactory flowLogicRefFactory) {
        System.out.println("nextScheduledActivity() invoked");
        System.out.println("StateRef TX ID is " + thisStateRef.getTxhash());
        FlowLogicRef flowLogicRef = flowLogicRefFactory.create("com.learncorda.tododist.flows.AlarmFlow", thisStateRef);
        return new ScheduledActivity(flowLogicRef, deadlineReminder);
    }


//    // This will not work
//
//    @Nullable
//    @Override
//    public ScheduledActivity nextScheduledActivity(@NotNull StateRef thisStateRef, @NotNull FlowLogicRefFactory flowLogicRefFactory) {
//        System.out.println("nextScheduledActivity() invoked");
//        System.out.println("StateRef TX ID is " + thisStateRef.getTxhash());
//        FlowLogicRef flowLogicRef = flowLogicRefFactory.create("com.learncorda.tododist.flows.AlarmFlow", thisStateRef);
//        return new ScheduledActivity(flowLogicRef, Instant.now().plusSeconds(20));
//    }


//    // This will not work
//
//    @Nullable
//    @Override
//    public ScheduledActivity nextScheduledActivity(@NotNull StateRef thisStateRef, @NotNull FlowLogicRefFactory flowLogicRefFactory) {
//        System.out.println("next act called");
//        Instant requestTime = Instant.now().plusSeconds(10);
//        System.out.println("Tx hash: " + thisStateRef.getTxhash().toString());
//        // what if alarmflow is moved back to workflows package?s
//        //FlowLogicRef flowLogicRef = flowLogicRefFactory.create(AlarmFlow.class);
//        FlowLogicRef flowLogicRef = flowLogicRefFactory.create("com.cordabook.tododist.flows.AlarmFlow",thisStateRef);
//        ScheduledActivity scheduledActivity = new ScheduledActivity(flowLogicRef,requestTime);
//        return scheduledActivity;
//    }


}


