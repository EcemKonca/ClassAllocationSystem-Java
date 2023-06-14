// Iterator Pattern

// Iterator Interface
interface ClassroomIterator {

    void first();

    void next();

    Boolean isDone();

    Classroom currentItem();
}
// Aggregate Interface
interface Aggregate {
    ClassroomIterator createIterator();

    void add(Classroom it);  // Not needed for iteration.

    int getCount(); // Needed for iteration.

    Classroom get(int idx);
}

// Concrete Iterator
class ConcreteClassroomIterator implements ClassroomIterator {
    private final Aggregate classrooms;
    private int position;

    public ConcreteClassroomIterator(Aggregate classrooms) {
        this.classrooms = classrooms;
        this.position = 0;
    }

    public void first() {
        position = 0;
    }

    public void next() {
        position++;
    }

    public Boolean isDone() {
        return position >= classrooms.getCount();
    }

    public Classroom currentItem() {
        if (isDone()) {
            return null;
        }
        return classrooms.get(position);
    }
}
// Concrete Aggregate -- Classroom