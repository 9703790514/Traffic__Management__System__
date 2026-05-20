# Contributing to Traffic Management System

First off, thank you for considering contributing to the Traffic Management System! It's people like you that make this project better for everyone.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Process](#development-process)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Testing Guidelines](#testing-guidelines)

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for all. Please be respectful and constructive in all interactions.

### Our Standards

**Positive behavior includes:**
- Using welcoming and inclusive language
- Being respectful of differing viewpoints
- Gracefully accepting constructive criticism
- Focusing on what is best for the community

**Unacceptable behavior includes:**
- Harassment, trolling, or discriminatory comments
- Publishing others' private information
- Other conduct which could reasonably be considered inappropriate

## Getting Started

### Prerequisites

Before you begin, ensure you have:
- Java Development Kit (JDK) 1.6 or higher
- Oracle JDeveloper 11g or 12c
- Oracle WebLogic Server 10.3+ or 12c
- Oracle Database 11g or higher
- Git version control

### Setting Up Development Environment

1. **Fork the repository**
   ```bash
   # Click the 'Fork' button on GitHub
   ```

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/Traffic__Management__System__.git
   cd Traffic__Management__System__
   ```

3. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/9703790514/Traffic__Management__System__.git
   ```

4. **Configure database**
   - Follow instructions in [README.md](README.md#installation)
   - Set up local Oracle database
   - Configure WebLogic data source

5. **Open in JDeveloper**
   - Open TmsApp.jws workspace file
   - Configure application server connection
   - Build and deploy

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates.

**When submitting a bug report, include:**
- **Clear title** - Descriptive summary of the issue
- **Steps to reproduce** - Detailed steps to recreate the problem
- **Expected behavior** - What you expected to happen
- **Actual behavior** - What actually happened
- **Screenshots** - If applicable
- **Environment details:**
  - OS version
  - Java version
  - WebLogic version
  - Browser (if UI-related)

**Example:**
```markdown
**Title:** Vehicle Registration Fails When Owner Has Special Characters in Name

**Steps to Reproduce:**
1. Navigate to Clerk Dashboard
2. Click "Register New Vehicle"
3. Enter owner name with special characters (e.g., "O'Brien")
4. Submit form

**Expected:** Vehicle should be registered successfully
**Actual:** Error message displayed: "Invalid input"

**Environment:**
- OS: Windows 10
- Java: 1.8.0_281
- WebLogic: 12.2.1.4
- Browser: Chrome 95.0.4638.69
```

### Suggesting Enhancements

Enhancement suggestions are welcome! Please include:
- **Use case** - Why is this enhancement needed?
- **Proposed solution** - How should it work?
- **Alternatives considered** - Other approaches you've thought about
- **Impact** - Who benefits from this change?

### Code Contributions

We accept contributions in the following areas:

#### High Priority
- **Security improvements** (password hashing, CSRF protection, etc.)
- **Unit and integration tests**
- **Logging framework implementation**
- **Input validation and sanitization**
- **Exception handling improvements**

#### Medium Priority
- **Performance optimizations**
- **UI/UX enhancements**
- **Documentation improvements**
- **Code refactoring**
- **New features**

#### Low Priority
- **Code formatting**
- **Minor bug fixes**
- **Typo corrections**

## Development Process

### Workflow

1. **Create a branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b bugfix/issue-number-description
   ```

2. **Make your changes**
   - Write clean, well-documented code
   - Follow coding standards (see below)
   - Add/update tests
   - Update documentation

3. **Test thoroughly**
   - Test locally on WebLogic Server
   - Verify all existing functionality still works
   - Test edge cases

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "Add: Brief description of changes"
   ```

5. **Keep your branch updated**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create Pull Request**
   - Go to GitHub and create a PR
   - Fill out the PR template
   - Link related issues

## Coding Standards

### Java Code Style

#### General Rules
- **Indentation:** 4 spaces (no tabs)
- **Line length:** Maximum 120 characters
- **Braces:** Opening brace on same line (K&R style)
- **Naming conventions:**
  - Classes: PascalCase (e.g., `VehicleBean`)
  - Methods: camelCase (e.g., `getVehicleDetails()`)
  - Variables: camelCase (e.g., `vehicleId`)
  - Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRIES`)
  - Packages: lowercase (e.g., `training.iqgateway.entities`)

#### Example

```java
/**
 * Service bean for vehicle management operations.
 * Handles CRUD operations for vehicles and related entities.
 * 
 * @author Your Name
 * @version 1.0
 */
@Stateless
public class VehicleServiceBean implements VehicleService {
    
    private static final Logger logger = LoggerFactory.getLogger(VehicleServiceBean.class);
    private static final int MAX_RESULTS = 100;
    
    @PersistenceContext(unitName = "Model")
    private EntityManager em;
    
    /**
     * Retrieves all vehicles from the database.
     * 
     * @return List of all vehicles
     * @throws ServiceException if database operation fails
     */
    @Override
    public List<Vehicle> getAllVehicles() throws ServiceException {
        try {
            return em.createNamedQuery("Vehicle.findAll", Vehicle.class)
                     .setMaxResults(MAX_RESULTS)
                     .getResultList();
        } catch (PersistenceException e) {
            logger.error("Failed to retrieve vehicles", e);
            throw new ServiceException("Unable to retrieve vehicles", e);
        }
    }
}
```

### JavaDoc Requirements

**All public classes and methods must have JavaDoc comments including:**
- Class/method description
- `@param` for each parameter
- `@return` for return values
- `@throws` for exceptions
- `@author` for classes
- `@version` for classes

### Entity Classes

```java
@Entity
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    // Always include equals(), hashCode(), and toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

### Logging

**Use SLF4J with proper log levels:**

```java
logger.trace("Entering method with params: {}", params);  // Very detailed
logger.debug("Processing vehicle: {}", vehicleId);        // Debug info
logger.info("User logged in: {}", username);              // Important events
logger.warn("Invalid configuration detected: {}", config); // Warnings
logger.error("Database connection failed", exception);     // Errors
```

**Never log sensitive information:**
```java
// DON'T do this:
logger.info("User credentials: {} / {}", username, password);

// DO this instead:
logger.info("Login attempt for user: {}", username);
```

### Exception Handling

```java
// Good exception handling
public Vehicle createVehicle(Vehicle vehicle) throws ServiceException {
    try {
        validateVehicle(vehicle);
        em.persist(vehicle);
        logger.info("Vehicle created successfully: {}", vehicle.getId());
        return vehicle;
    } catch (ValidationException e) {
        logger.warn("Vehicle validation failed: {}", e.getMessage());
        throw new ServiceException("Invalid vehicle data", e);
    } catch (PersistenceException e) {
        logger.error("Failed to persist vehicle", e);
        throw new ServiceException("Unable to create vehicle", e);
    }
}
```

### Resource Management

```java
// Use try-with-resources
try (Connection conn = dataSource.getConnection();
     PreparedStatement stmt = conn.prepareStatement(sql)) {
    // use connection
} catch (SQLException e) {
    logger.error("Database operation failed", e);
}
```

## Commit Guidelines

### Commit Message Format

```
<type>: <subject>

<body>

<footer>
```

### Types
- **Add:** New feature or functionality
- **Fix:** Bug fix
- **Update:** Update existing functionality
- **Refactor:** Code refactoring (no functional changes)
- **Docs:** Documentation changes
- **Test:** Adding or updating tests
- **Style:** Code formatting (no functional changes)
- **Perf:** Performance improvements
- **Chore:** Maintenance tasks

### Examples

```bash
# Good commits
git commit -m "Add: Password hashing with BCrypt implementation"
git commit -m "Fix: Vehicle registration form validation error"
git commit -m "Update: Improve error messages in LoginBean"
git commit -m "Docs: Add API documentation for RtoSessionEJB"
git commit -m "Test: Add unit tests for AdminSessionEJBBean"

# Bad commits (avoid these)
git commit -m "fixed stuff"
git commit -m "updates"
git commit -m "asdfasdf"
```

### Detailed Commit Example

```
Add: Password hashing with BCrypt implementation

- Implemented BCryptPasswordEncoder for secure password storage
- Updated Users entity to store hashed passwords
- Modified LoginBean to verify hashed passwords
- Added password strength validation
- Updated database migration scripts

Resolves: #42
Related: #38, #45
```

## Pull Request Process

### Before Submitting

- [ ] Code follows the project's coding standards
- [ ] All tests pass
- [ ] New tests added for new functionality
- [ ] Documentation updated
- [ ] No merge conflicts with main branch
- [ ] Commit messages follow guidelines
- [ ] Code has been self-reviewed

### PR Title Format

```
[Type] Brief description

Examples:
[Feature] Add password hashing functionality
[Bugfix] Fix vehicle registration validation error
[Docs] Update installation instructions
[Refactor] Improve exception handling in service layer
```

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues
Fixes #(issue number)

## Changes Made
- Detailed list of changes
- Another change
- And another

## Testing
Describe how you tested your changes

## Screenshots (if applicable)
Add screenshots

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] All tests pass
```

### Review Process

1. **Automated checks** - Must pass before review
2. **Code review** - At least one approval required
3. **Testing** - Reviewer may test functionality
4. **Feedback** - Address review comments
5. **Approval** - Merge when approved

## Testing Guidelines

### Unit Tests

```java
@Test
public void testCreateVehicle_ValidData_Success() {
    // Arrange
    Vehicle vehicle = new Vehicle();
    vehicle.setVehName("Toyota Camry");
    vehicle.setManufacturerName("Toyota");
    
    // Act
    Vehicle result = vehicleService.createVehicle(vehicle);
    
    // Assert
    assertNotNull(result.getVehId());
    assertEquals("Toyota Camry", result.getVehName());
}

@Test(expected = ValidationException.class)
public void testCreateVehicle_InvalidData_ThrowsException() {
    Vehicle vehicle = new Vehicle();
    // Missing required fields
    vehicleService.createVehicle(vehicle);
}
```

### Integration Tests

- Test database operations
- Test EJB interactions
- Test complete user workflows

### Test Coverage

- Aim for **80%+ code coverage**
- Focus on critical business logic
- Test edge cases and error conditions

## Questions?

If you have questions about contributing:
- Check existing documentation
- Search closed issues
- Create a new issue with the "question" label
- Contact the maintainers

## Recognition

Contributors will be recognized in:
- README.md Contributors section
- Release notes
- Project documentation

Thank you for contributing to make the Traffic Management System better! 🚗🚦

---

**Last Updated:** May 21, 2026  
**Version:** 1.0
