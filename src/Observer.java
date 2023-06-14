import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Subject
interface ClassroomSubject {
    void attach(ClassroomObserver observer);

    void deAttach(ClassroomObserver observer);

    void notifyObservers(DayOfWeek testDay, LocalDateTime startTime, LocalDateTime endTime);
}

// Observer
interface ClassroomObserver {
    void update(Classroom classroom, DayOfWeek testDay, LocalDateTime startTime, LocalDateTime endTime);
}

// Concrete observer
class ClassroomScheduler implements ClassroomObserver {

    private final ClassroomAdmin classroomAdmin;

    public ClassroomScheduler(ClassroomAdmin classroomAdmin) {
        this.classroomAdmin = classroomAdmin;
    }

    //Method to make classrooms available
    public void markClassroomsAsAvailable(DayOfWeek testDays, LocalDateTime startTime, LocalDateTime endTime, ClassroomIterator iterator) {
        for (iterator.first(); !iterator.isDone(); iterator.next()) {
            Classroom classroom = iterator.currentItem();
            if (!classroom.isAvailable(testDays, startTime, endTime)) {
                MarkClassroomAvailabilityClassroomCommand command = new MarkClassroomAvailabilityClassroomCommand(classroom, testDays, startTime, endTime);
                classroomAdmin.unExecuteAvailabilityCommand(command, testDays, startTime, endTime);
            }
        }
    }

    // Method to make classrooms unavailable
    public void markClassroomsAsUnavailable(DayOfWeek testDay, LocalDateTime startTime, LocalDateTime endTime, ClassroomIterator iterator) {
        for (iterator.first(); !iterator.isDone(); iterator.next()) {
            Classroom classroom = iterator.currentItem();
            if (classroom.isAvailable(testDay, startTime, endTime)) {
                MarkClassroomAvailabilityClassroomCommand command = new MarkClassroomAvailabilityClassroomCommand(classroom, testDay, startTime, endTime);
                classroomAdmin.executeAvailabilityCommand(command, testDay, startTime, endTime);
            }
        }
    }

    //Method to notify about classroom status
    @Override
    public void update(Classroom classroom, DayOfWeek testDay, LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        System.out.println("Classroom " + classroom.getName() + " has been updated. Availability: " + (classroom.isAvailable(testDay, startTime, endTime) ? "Available" : "Reserved") + " on " + testDay.toString().toLowerCase() + " at " + startTime.format(formatter) + "-" + endTime.format(formatter));
    }
}
// Concrete Subject -- Classroom