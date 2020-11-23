package com.learncorda.tododist.states;

import com.google.common.collect.ImmutableList;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

public class ToDoSchemaV2 extends MappedSchema {
    public ToDoSchemaV2() {
        super(ToDoSchema.class, 2, ImmutableList.of(ToDoModel.class));
    }

    @Entity
    @Table(name="todo_model2")
    public static class ToDoModel extends PersistentState {
        @Column(name = "task")
        private final String task;
        @Column(name = "id")
        private final UUID linearId;
        @Column(name ="assignedTo")
        private final String assignedTo;

        ToDoModel(String task, UUID linearId, Party assignedTo) {
            this.task = task;
            this.linearId = linearId;
            this.assignedTo = assignedTo.getName().getOrganisation();
        }

        public String getTask() {
            return task;
        }

        public String getAssignedTo() {
            return assignedTo;
        }

        public UUID getLinearId() {
            return linearId;
        }
    }

}
