// Visitor Pattern
// Visitor
interface CampusVisitor {
    void visit(Building building);

    void visit(Department department);

    void visit(Floor floor);

    void visit(Classroom classroom);
}

// Concrete Visitor
class UnlockVisitor implements CampusVisitor {
    private final String department;
    private final String securityPersonnel;

    public UnlockVisitor(String department, String securityPersonnel) {
        this.department = department;
        this.securityPersonnel = securityPersonnel;
    }

    @Override
    public void visit(Building building) {
        // Do nothing when visiting a building
    }

    // Check if the visitor's department matches the current department being visited
    @Override
    public void visit(Department department) {
        if (department.getName().equals(this.department)) {
            System.out.println("Unlocking classrooms in department " + department.getName() + " with the help of " + securityPersonnel);
        } else {
            System.out.println("Security personnel mismatch. Unable to unlock classrooms.");
        }
    }

    // Do nothing when visiting a floor
    @Override
    public void visit(Floor floor) {
    }

    @Override
    public void visit(Classroom classroom) {
        System.out.println("Unlocking classroom " + classroom.getName() + " with the help of " + securityPersonnel);
    }
}
