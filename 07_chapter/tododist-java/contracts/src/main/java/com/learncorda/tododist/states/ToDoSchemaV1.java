package com.learncorda.tododist.states;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

public class ToDoSchemaV1 extends MappedSchema {
    public ToDoSchemaV1() {
        super(ToDoSchema.class, 1, ImmutableList.of(ToDoModel.class));
    }

    @Entity
    @Table(name="todo_model")
    public static class ToDoModel extends PersistentState {
        @Column(name = "task")
        private final String task;
        @Column(name = "id")
        private final UUID linearId;

        ToDoModel(String task, UUID linearId) {
            this.task = task;
            this.linearId = linearId;
        }

        public String getTask() {
            return task;
        }

        public UUID getLinearId() {
            return linearId;
        }
    }

}
