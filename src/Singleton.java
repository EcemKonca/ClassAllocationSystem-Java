//ECEM KONCA
//PELİN TUNÇ
//MELİS BARAN
//AHMET ARSLAN
// Class Allocation System

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Singleton Pattern
class ResourceAllocationDepartment extends Department {
    private static ResourceAllocationDepartment instance = null;

    private ResourceAllocationDepartment(String name) {
        super(name);
    }

    public static synchronized ResourceAllocationDepartment getInstance() {
        if (instance == null) {
            instance = new ResourceAllocationDepartment("Resource Allocation Department");
        }
        return instance;
    }

    // Method to reserve a classroom
    public void reserve(Classroom classroom, DayOfWeek testDay, LocalDateTime startTime, LocalDateTime endTime) {
        System.out.println("You are directed to Resource Allocation Department");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (classroom.isAvailable(testDay, startTime, endTime)) {
            System.out.println("Classroom " + classroom.getName() + " with the capacity " + classroom.getCapacity() + " reserved for " + testDay
                    + " at " + startTime.format(formatter) + "-" + endTime.format(formatter) + ".");
            classroom.setAvailable(testDay, startTime, endTime, false);
        } else {
            System.out.println("Classroom " + classroom.getName() + " already reserved for " + testDay + " for that time.");
        }
    }

    //Method to cancel reservation
    public void cancelReservation(Classroom classroom, DayOfWeek testDay, LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (!classroom.isAvailable(testDay, startTime, endTime)) {
            System.out.println("On " + testDay.toString().toLowerCase() + " at " + startTime.format(formatter) + "-" +
                    endTime.format(formatter) + " Classroom " + classroom.getName() + " is not reserved anymore.");
            classroom.setAvailable(testDay, startTime, endTime, true);
        } else {
            System.out.println("Classroom " + classroom.getName() + " is already available on " + testDay.toString().toLowerCase() + " .");
        }
    }
}

class Main {

    public static void main(String[] args) {
        Building building1 = new Building("Main Building");
        Department department1 = new Department("Computer Science");
        Department department2 = new Department("Industrial Engineering");
        Floor floor1 = new Floor(1);
        Floor floor2 = new Floor(2);
        Floor floor3 = new Floor(3);
        Classroom classroom1 = new Classroom("M101", 105, department1.getName());
        Classroom classroom2 = new Classroom("M102", 45, department1.getName());
        Classroom classroom3 = new Classroom("M103", 60, department1.getName());
        Classroom classroom4 = new Classroom("M201", 80, department2.getName());
        Classroom classroom5 = new Classroom("M301", 30, department2.getName());

        Aggregate aggregate = new Classroom();
        aggregate.add(classroom1);
        aggregate.add(classroom2);
        aggregate.add(classroom3);
        aggregate.add(classroom4);
        aggregate.add(classroom5);
        ClassroomIterator iterator = aggregate.createIterator();

        // Add classrooms to floor
        floor1.add(classroom1);
        floor1.add(classroom2);
        floor1.add(classroom3);
        floor2.add(classroom4);
        floor3.add(classroom5);

        // Add floor to department
        department1.add(floor1);
        department2.add(floor2);
        department2.add(floor3);

        // Add department to building
        building1.add(department1);
        building1.add(department2);

        QueryAvailableRoomsClassroomCommand queryCommand = new QueryAvailableRoomsClassroomCommand(building1);

        // Execute the command
        ClassroomAdmin classroomAdmin = new ClassroomAdmin();
        ClassroomScheduler scheduler = new ClassroomScheduler(classroomAdmin);
        ResourceAllocationDepartment rsh = ResourceAllocationDepartment.getInstance();

        UnlockVisitor unlockVisitor1 = new UnlockVisitor(department1.getName(), "Ecem Konca");

        UnlockVisitor unlockVisitor2 = new UnlockVisitor(department2.getName(), "Melis Baran");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        classroom1.attach(scheduler);

        Scanner scanner = new Scanner(System.in);
        String testDay;
        DayOfWeek lessonDay;
        int startHour;
        int startMinute;

        System.out.println("Welcome to the Classroom Allocation System!");

        boolean flag = false;
        while (!flag) {
            System.out.println("1. Reserve a classroom");
            System.out.println("2. Cancel a classroom");
            System.out.println("3. Mark all classrooms unavailable");
            System.out.println("4. Mark all classrooms available");
            System.out.println("5. Display all classrooms");
            System.out.println("6. Exit");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Enter the day of the week for the exam:");
                    testDay = scanner.next();
                    try {
                        lessonDay = DayOfWeek.valueOf(testDay.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid day of the week. Classroom scheduling failed.");
                        continue;
                    }

                    System.out.println("Enter the start hour for the exam (24-hour format):");
                    startHour = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Enter the start minute for the exam:");
                    startMinute = scanner.nextInt();
                    scanner.nextLine();

                    LocalDateTime lessonStartDateTime = LocalDateTime.now()
                            .with(TemporalAdjusters.nextOrSame(lessonDay))
                            .withHour(startHour)
                            .withMinute(startMinute)
                            .withSecond(0)
                            .withNano(0);

                    LocalDateTime lessonEndDateTime = lessonStartDateTime.plusHours(1); // Assuming 1-hour duration for the exam

                    System.out.println("Enter the student capacity");
                    int capacity = scanner.nextInt();

                    classroomAdmin.executeCommand(queryCommand, lessonDay, lessonStartDateTime, lessonEndDateTime, capacity, iterator);
                    // Check if the selected classrooms are available for the lesson days
                    System.out.println("Enter the number of classrooms: ");
                    int numberOfClassroomsToReserve = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    List<Classroom> selectedClassrooms = new ArrayList<>();
                    for (int i = 0; i < numberOfClassroomsToReserve; i++) {
                        System.out.print("Enter the classroom number to reserve: ");
                        int classroomNumber = scanner.nextInt();
                        scanner.nextLine(); // Consume the newline character

                        Classroom selectedClassroom;
                        switch (classroomNumber) {
                            case 1:
                                selectedClassroom = classroom1;
                                break;
                            case 2:
                                selectedClassroom = classroom2;
                                break;
                            case 3:
                                selectedClassroom = classroom3;
                                break;
                            case 4:
                                selectedClassroom = classroom4;
                                break;
                            case 5:
                                selectedClassroom = classroom5;
                                break;
                            default:
                                System.out.println("Invalid classroom number. Reservation failed.");
                                return;
                        }
                        selectedClassrooms.add(selectedClassroom);
                    }
                    // Perform classroom reservations
                    for (Classroom classroom : selectedClassrooms) {
                        if (classroom.isAvailable(lessonDay, lessonStartDateTime, lessonEndDateTime)) {
                            // Check if the department has an assigned security personnel
                            String departmentName = classroom.getDepartment();
                            UnlockVisitor unlockVisitor;
                            if (departmentName.equals(department1.getName())) {
                                unlockVisitor = unlockVisitor1;
                            } else if (departmentName.equals(department2.getName())) {
                                unlockVisitor = unlockVisitor2;
                            } else {
                                System.out.println("Department not found. Unable to unlock classrooms.");
                                return;
                            }
                            classroom.accept(unlockVisitor);
                            rsh.reserve(classroom, lessonDay, lessonStartDateTime, lessonEndDateTime);
                        }
                    }
                    break;
                case 2:
                    System.out.println("Enter the day of the week for the exam:");
                    testDay = scanner.next();

                    try {
                        lessonDay = DayOfWeek.valueOf(testDay.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid day of the week. Exam scheduling failed.");
                        return;
                    }
                    System.out.println("Enter the start hour for the exam (24-hour format):");
                    startHour = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Enter the start minute for the exam:");
                    startMinute = scanner.nextInt();
                    scanner.nextLine();

                    LocalDateTime lessonStartDateTime2 = LocalDateTime.now()
                            .with(TemporalAdjusters.nextOrSame(lessonDay))
                            .withHour(startHour)
                            .withMinute(startMinute)
                            .withSecond(0)
                            .withNano(0);

                    LocalDateTime lessonEndDateTime2 = lessonStartDateTime2.plusHours(1); // Assuming 1-hour duration for the exam

                    System.out.println("Reserved classrooms for: " + lessonDay + " " + lessonStartDateTime2.format(formatter) + "-" + lessonEndDateTime2.format(formatter));
                    boolean hasReservedClassrooms = false;
                    for (iterator.first(); !iterator.isDone(); iterator.next()) {
                        if (!iterator.currentItem().isAvailable(lessonDay, lessonStartDateTime2, lessonEndDateTime2)) {
                            System.out.println("Classroom: " + iterator.currentItem().getName());
                            hasReservedClassrooms = true;
                        }
                    }
                    System.out.println();
                    if (!hasReservedClassrooms) {
                        System.out.println("No classrooms are reserved for " + lessonDay + " at " + lessonStartDateTime2.format(formatter) +
                                "-" + lessonEndDateTime2.format(formatter));
                    } else {
                        System.out.print("Enter the classroom name to cancel the reservation: ");
                        String classroomName = scanner.next();
                        // Find the selected classroom and cancel the reservation
                        Classroom selectedClassroom = null;
                        for (iterator.first(); !iterator.isDone(); iterator.next()) {
                            if (iterator.currentItem().getName().equals(classroomName) && !iterator.currentItem().isAvailable(lessonDay, lessonStartDateTime2, lessonEndDateTime2)) {
                                selectedClassroom = iterator.currentItem();
                                break;
                            }
                        }
                        System.out.println();

                        if (selectedClassroom != null) {
                            rsh.cancelReservation(selectedClassroom, lessonDay, lessonStartDateTime2, lessonEndDateTime2);
                            System.out.println("Reservation for Classroom " + selectedClassroom.getName() + " has been canceled.");
                        } else {
                            System.out.println("Invalid classroom name or the classroom is not reserved.");
                        }
                    }
                    break;
                case 3:
                    System.out.println("Enter the day of the week for the exam:");
                    testDay = scanner.next();

                    try {
                        lessonDay = DayOfWeek.valueOf(testDay.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid day of the week. Exam scheduling failed.");
                        continue;
                    }
                    System.out.println("Enter the start hour for the exam (24-hour format):");
                    startHour = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Enter the start minute for the exam:");
                    startMinute = scanner.nextInt();
                    scanner.nextLine();

                    LocalDateTime lessonStartDateTime3 = LocalDateTime.now()
                            .with(TemporalAdjusters.nextOrSame(lessonDay))
                            .withHour(startHour)
                            .withMinute(startMinute)
                            .withSecond(0)
                            .withNano(0);

                    LocalDateTime lessonEndDateTime3 = lessonStartDateTime3.plusHours(1); // Assuming 1-hour duration for the exam

                    scheduler.markClassroomsAsUnavailable(lessonDay, lessonStartDateTime3, lessonEndDateTime3, iterator);
                    System.out.println("On " + lessonDay + " at " + lessonStartDateTime3.format(formatter) + "-" + lessonEndDateTime3.format(formatter));

                    for (iterator.first(); !iterator.isDone(); iterator.next()) {
                        System.out.println("Classroom: " + iterator.currentItem().getName() + " - Capacity: " + iterator.currentItem().getCapacity() + " - Availability: " + (iterator.currentItem().isAvailable(lessonDay, lessonStartDateTime3, lessonEndDateTime3) ? "Available" : "Reserved"));
                    }
                    System.out.println();
                    break;
                case 4:
                    System.out.println("Enter the day of the week for the exam:");
                    testDay = scanner.next();
                    try {
                        lessonDay = DayOfWeek.valueOf(testDay.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid day of the week. Exam scheduling failed.");
                        continue;
                    }
                    System.out.println("Enter the start hour for the exam (24-hour format):");
                    startHour = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Enter the start minute for the exam:");
                    startMinute = scanner.nextInt();
                    scanner.nextLine();

                    LocalDateTime lessonStartDateTime4 = LocalDateTime.now()
                            .with(TemporalAdjusters.nextOrSame(lessonDay))
                            .withHour(startHour)
                            .withMinute(startMinute)
                            .withSecond(0)
                            .withNano(0);

                    LocalDateTime lessonEndDateTime4 = lessonStartDateTime4.plusHours(1); // Assuming 1-hour duration for the exam
                    scheduler.markClassroomsAsAvailable(lessonDay, lessonStartDateTime4, lessonEndDateTime4, iterator);
                    System.out.println("On " + lessonDay + " at " + lessonStartDateTime4.format(formatter) + "-" + lessonEndDateTime4.format(formatter));
                    for (iterator.first(); !iterator.isDone(); iterator.next()) {
                        System.out.println("Classroom: " + iterator.currentItem().getName() + " - Capacity: " + iterator.currentItem().getCapacity() + " - Availability: " + (iterator.currentItem().isAvailable(lessonDay, lessonStartDateTime4, lessonEndDateTime4) ? "Available" : "Reserved"));
                    }
                    System.out.println();
                    break;
                case 5:
                    System.out.println("Enter the day of the week for the exam:");
                    testDay = scanner.next();

                    try {
                        lessonDay = DayOfWeek.valueOf(testDay.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid day of the week. Exam scheduling failed.");
                        return;
                    }
                    System.out.println("Enter the start hour for the exam (24-hour format):");
                    startHour = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Enter the start minute for the exam:");
                    startMinute = scanner.nextInt();
                    scanner.nextLine();

                    LocalDateTime lessonStartDateTime5 = LocalDateTime.now()
                            .with(TemporalAdjusters.nextOrSame(lessonDay))
                            .withHour(startHour)
                            .withMinute(startMinute)
                            .withSecond(0)
                            .withNano(0);

                    LocalDateTime lessonEndDateTime5 = lessonStartDateTime5.plusHours(1); // Assuming 1-hour duration for the exam

                    System.out.println("Classroom availability for: " + lessonDay + " " + lessonStartDateTime5.format(formatter) + "-" + lessonEndDateTime5.format(formatter));
                    for (iterator.first(); !iterator.isDone(); iterator.next()) {
                        System.out.println("Classroom: " + iterator.currentItem().getName() + " - Capacity: " + iterator.currentItem().getCapacity() + " - Availability: " + (iterator.currentItem().isAvailable(lessonDay, lessonStartDateTime5, lessonEndDateTime5) ? "Available" : "Reserved"));
                    }
                    System.out.println();
                    break;
                case 6:
                    System.out.println("Thank you for using Class Allocation System...");
                    flag = true;
                    break;
                default:
                    break;
            }
        }
    }
}