import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//Composite pattern-Safe Implementation
// Component interface
// Visitor-- Element
interface CampusComponent {
    String getName();

    void accept(CampusVisitor visitor);

}

// Leaf class
// Concrete Element -- Visitor
class Classroom implements CampusComponent, ClassroomSubject, Aggregate {
    private final String name;
    private boolean available;
    private final List<ClassroomObserver> observers;

    private final int capacity;

    private final String department;

    private Map<DayOfWeek, Map<LocalDateTime, LocalDateTime>> availability = new HashMap<>();

    public Classroom() {
        this.name = "";
        this.available = true;
        this.observers = new ArrayList<>();
        this.capacity = 0;
        this.department = "";
    }


    public Classroom(String name, int capacity, String departmentName) {
        this.name = name;
        this.available = true;
        this.observers = new ArrayList<>();
        this.capacity = capacity;
        this.department = departmentName;
    }

    private ArrayList<Classroom> _items = new ArrayList<Classroom>();

    public int getCount() {
        return _items.size();
    }

    public void add(Classroom item) {
        _items.add(item);
    }

    public Classroom get(int index) {
        return _items.get(index);
    }

    public ConcreteClassroomIterator createIterator() {
        return new ConcreteClassroomIterator(this);
    }

    public String getDepartment() {
        return department;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public void accept(CampusVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void attach(ClassroomObserver observer) {
        observers.add(observer);
    }

    @Override
    public void deAttach(ClassroomObserver observer) {
        observers.remove(observer);
    }

    public String getName() {
        return name;
    }

    @Override
    public void notifyObservers(DayOfWeek testDay, LocalDateTime startTime, LocalDateTime endTime) {
        for (ClassroomObserver observer : observers) {
            observer.update(this, testDay, startTime, endTime);
        }
    }

    public boolean isAvailable(DayOfWeek dayOfWeek, LocalDateTime startTime, LocalDateTime endTime) {
        Map<LocalDateTime, LocalDateTime> dayAvailability = availability.get(dayOfWeek);
        if (dayAvailability == null) {
            return true;
        }
        for (Map.Entry<LocalDateTime, LocalDateTime> entry : dayAvailability.entrySet()) {
            LocalDateTime entryStartTime = entry.getKey();
            LocalDateTime entryEndTime = entry.getValue();

            if (startTime.isBefore(entryEndTime) && endTime.isAfter(entryStartTime)) {
                return false;
            }
        }
        return true;
    }

    public void setAvailable(DayOfWeek dayOfWeek, LocalDateTime startTime, LocalDateTime endTime, boolean available) {
        Map<LocalDateTime, LocalDateTime> dayAvailability = availability.computeIfAbsent(dayOfWeek, k -> new HashMap<>());
        if (dayAvailability.containsKey(startTime) && dayAvailability.get(startTime).equals(endTime)) {
            // If the entry exists, toggle its availability
            dayAvailability.remove(startTime);
        } else {
            // Set the availability to the specified value
            dayAvailability.put(startTime, endTime);
        }
        notifyObservers(dayOfWeek, startTime, endTime);
    }
}


// Composite class
class Building implements CampusComponent {
    private final String name;
    private final List<CampusComponent> departments = new ArrayList<>();

    public Building(String name) {
        this.name = name;
    }

    public void add(CampusComponent department) {
        departments.add(department);
    }

    @Override
    public void accept(CampusVisitor visitor) {
        visitor.visit(this);
        for (CampusComponent department : departments) {
            department.accept(visitor);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}

// Composite class
class Department implements CampusComponent {
    @Override
    public String getName() {
        return name;
    }

    private final String name;
    private final List<CampusComponent> floors = new ArrayList<>();


    public Department(String name) {
        this.name = name;

    }

    public void add(CampusComponent floor) {
        floors.add(floor);
    }

    @Override
    public void accept(CampusVisitor visitor) {
        visitor.visit(this);
        for (CampusComponent floor : floors) {
            floor.accept(visitor);
        }
    }
}

// Composite class
class Floor implements CampusComponent {

    private final int number;
    private String name;
    private final List<CampusComponent> classrooms = new ArrayList<>();

    public Floor(int number) {
        this.number = number;
    }

    @Override
    public String getName() {
        return name;
    }

    public void add(CampusComponent classroom) {
        classrooms.add(classroom);
    }

    @Override
    public void accept(CampusVisitor visitor) {
        visitor.visit(this);
        for (CampusComponent classroom : classrooms) {
            classroom.accept(visitor);
        }
    }
}
