package com.redisscrumboard.mapper;

import com.redisscrumboard.model.Task;
import com.redisscrumboard.model.TaskDetails;
import com.redisscrumboard.model.TaskType;
import com.redisscrumboard.model.response.RestTaskDetailsResponse;
import com.redisscrumboard.model.response.RestTaskResponse;
import com.redisscrumboard.model.spec.TaskCreateSpec;
import com.redisscrumboard.model.spec.TaskDetailsSpec;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "jsr330", imports = {UUID.class, Instant.class})
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "taskDetails", expression= "java(mapTaskType(spec.taskType(), spec.taskDetails()))")
    Task toEntity(TaskCreateSpec spec);

    default TaskDetails mapTaskType(TaskType taskType, TaskDetailsSpec taskDetails) {
            return switch (taskType) {
                case BUG -> toBugEntity((TaskDetailsSpec.BugDetailsSpec) taskDetails);
                case FEATURE -> toFeatureEntity((TaskDetailsSpec.FeatureDetailsSpec) taskDetails);
            };

//        if (TaskType.BUG.equals(type)) {
//            return toBugEntity((TaskDetailsSpec.BugDetailsSpec) taskDetails);
//        } else if (TaskType.FEATURE.equals(type)) {
//            return toFeatureEntity((TaskDetailsSpec.FeatureDetailsSpec) taskDetails);
//        }
//        return null;
    }

    TaskDetails.Bug toBugEntity(TaskDetailsSpec.BugDetailsSpec spec);

    TaskDetails.Feature toFeatureEntity(TaskDetailsSpec.FeatureDetailsSpec spec);

    @Mapping(target = "taskDetails", expression = "java(mapRestTaskType(task))")
    RestTaskResponse toRest(Task task);

    default RestTaskDetailsResponse mapRestTaskType(Task task) {
        if (TaskType.BUG.equals(task.getTaskType())) {
            return toRestBugResponse((TaskDetails.Bug) task.getTaskDetails());
        } else if (TaskType.FEATURE.equals(task.getTaskType())) {
            return toRestFeatureResponse((TaskDetails.Feature) task.getTaskDetails());
        }
        return null;
    }

    RestTaskDetailsResponse.RestBugResponse toRestBugResponse(TaskDetails.Bug taskDetails);

    RestTaskDetailsResponse.RestFeatureResponse toRestFeatureResponse(TaskDetails.Feature taskDetails);
}
