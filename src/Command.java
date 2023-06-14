import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Command Pattern

//Command Interface
interface ClassroomCommand {
    void execute(ResourceAllocationDepartment resourceAllocationDepartment, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime, int desiredCapacity, ClassroomIterator iterator);

    void unExecuteAvailable(ResourceAllocationDepartment resourceAllocationDepartment, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime);

    void executeAvailable(ResourceAllocationDepartment resourceAllocationDepartment, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime);

}
// Concrete Command
class QueryAvailableRoomsClassroomCommand implements ClassroomCommand {
    private final CampusComponent rootComponent;

    public QueryAvailableRoomsClassroomCommand(CampusComponent rootComponent) {
        this.rootComponent = rootComponent;
    }

    // query with days, capacity, and hours
    @Override
    public void execute(ResourceAllocationDepartment resourceAllocationDepartment, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime, int desiredCapacity, ClassroomIterator iterator) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" HH:mm");
        int count = 0;
        System.out.println("Available classrooms on " + testDays + startTime.format(formatter) + "-" + endTime.format(formatter) + ":");
        int index = 1;
        for (iterator.first(); !iterator.isDone(); iterator.next()) {
            Classroom classroom = iterator.currentItem();
            if (classroom.isAvailable(testDays, startTime, endTime) && classroom.getCapacity() >= desiredCapacity) {
                System.out.println(index + ". Classroom: " + classroom.getName() + " Capacity: " + classroom.getCapacity());
                count++;
            }
            index++;
        }
        System.out.println("Total number of rooms available for a test: " + count);
    }


    @Override
    public void unExecuteAvailable(ResourceAllocationDepartment resourceAllocationDepartment, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime) {
        // No need to implement unExecute for this command
    }

    @Override
    public void executeAvailable(ResourceAllocationDepartment resourceAllocationDepartment, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime) {
        // No need to implement unExecute for this command
    }
}
// Concrete Command
class MarkClassroomAvailabilityClassroomCommand implements ClassroomCommand {
    private final Classroom classroom;
    private boolean available;
    private final DayOfWeek testDay;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public MarkClassroomAvailabilityClassroomCommand(Classroom classroom, DayOfWeek testDay, LocalDateTime startTime, LocalDateTime endTime) {
        this.classroom = classroom;
        this.testDay = testDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public void execute(ResourceAllocationDepartment resourceAllocationDepartment, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime, int desiredCapacity, ClassroomIterator iterator) {
    }

    @Override
    public void unExecuteAvailable(ResourceAllocationDepartment resourceAllocationDepartment, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime) {
        classroom.setAvailable(testDays, startTime, endTime, available);
    }

    @Override
    public void executeAvailable(ResourceAllocationDepartment resourceAllocationDepartment, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime) {
        classroom.setAvailable(testDays, startTime, endTime, !available);
    }
}

// Invoker
class ClassroomAdmin {
    public void executeCommand(ClassroomCommand classroomCommand, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime, int desiredCapacity, ClassroomIterator iterator) {
        classroomCommand.execute(ResourceAllocationDepartment.getInstance(), testDays, startTime, endTime, desiredCapacity,iterator);
    }

    public void executeAvailabilityCommand(ClassroomCommand classroomCommand, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime) {
        classroomCommand.executeAvailable(ResourceAllocationDepartment.getInstance(), testDays, startTime, endTime);
    }

    public void unExecuteAvailabilityCommand(ClassroomCommand classroomCommand, DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime) {
        classroomCommand.unExecuteAvailable(ResourceAllocationDepartment.getInstance(), testDays, startTime, endTime);
    }

}
//Receiver - Classroom