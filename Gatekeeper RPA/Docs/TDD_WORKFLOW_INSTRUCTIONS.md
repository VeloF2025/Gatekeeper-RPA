# TDD Workflow Instructions - Gatekeeper RPA Project

## Documentation-Driven Test Development (DDTD)

### 🎯 Principle
Tests MUST be created from PRD/PRP/ADR documentation BEFORE any implementation. This ensures:
- Requirements-driven development
- Comprehensive test coverage
- Adherence to planned scope
- Prevention of scope creep

### 📋 Workflow Process

#### 1. Requirements Analysis
- Parse PRD to extract functional requirements
- Identify non-functional requirements
- Map workflow steps to testable units
- Extract database schema requirements
- Define API endpoint specifications

#### 2. Test Specification Generation
- Convert each requirement into testable acceptance criteria
- Define test steps for each requirement
- Specify expected outcomes
- Categorize tests by type (unit, integration, e2e, performance)

#### 3. Test File Creation
- Generate test files with proper structure
- Include test documentation from requirements
- Create placeholder tests that clearly state requirements
- Organize tests by type and functionality

#### 4. Implementation Cycle
- Implement feature ONLY after tests exist
- Write minimal code to pass tests
- Refactor while maintaining test coverage
- Validate all tests pass

### 🚀 Running the TDD Workflow

#### Execute Complete Workflow
```bash
python tests/tdd_workflow.py
```

#### Manual Test Creation (Before Implementation)
```python
# Always refer to PRD before writing tests
# Each test must map to a specific requirement
# Include requirement reference in test documentation
```

### 📊 Test Types and Coverage Requirements

#### Unit Tests (30%)
- Individual component functionality
- Business logic validation
- Utility functions
- Data transformation

#### Integration Tests (40%)
- WhatsApp API integration
- Database operations
- RPA workflow coordination
- Third-party service interactions

#### E2E Tests (20%)
- Complete audit workflow
- WhatsApp → RPA → Database → Response
- Real-world scenario validation

#### Performance Tests (10%)
- End-to-end timing (<30s)
- WhatsApp response time (<1s)
- Database query performance
- RPA automation reliability

### 🎯 Quality Gates

#### Mandatory Requirements
- >95% test coverage
- All tests must pass before implementation
- No implementation without corresponding tests
- Tests must validate PRD requirements exactly

#### Test Documentation Standards
- Each test must reference PRD requirement
- Clear test steps and expected outcomes
- Proper test organization by type
- Comprehensive test coverage of all requirements

### 🔄 Development Workflow

#### Before Implementation
1. ✅ Parse PRD for requirements
2. ✅ Generate test specifications
3. ✅ Create test files
4. ✅ Validate test coverage

#### During Implementation
1. ✅ Run tests to verify they fail initially
2. ✅ Implement minimal code to pass tests
3. ✅ Refactor while maintaining test coverage
4. ✅ Validate all tests pass

#### After Implementation
1. ✅ Run complete test suite
2. ✅ Validate performance requirements
3. ✅ Verify all requirements are met
4. ✅ Update documentation if needed

### 📝 Test File Structure

```
tests/
├── unit/                 # Unit tests for individual components
│   ├── test_unit_workflow.py
│   └── ...
├── integration/          # Integration tests for service interactions
│   ├── test_integration_workflow.py
│   └── ...
├── e2e/                  # End-to-end tests for complete workflows
│   ├── test_e2e_workflow.py
│   └── ...
├── performance/          # Performance and load testing
│   ├── test_performance_workflow.py
│   └── ...
├── tdd_workflow.py       # TDD workflow executor
└── tdd_workflow_report.json # TDD execution report
```

### 🎯 Success Criteria

#### TDD Workflow Success
- ✅ All PRD requirements have corresponding tests
- ✅ Test coverage >95%
- ✅ Tests validate actual requirements
- ✅ Implementation follows test-driven approach

#### Implementation Success
- ✅ All tests pass
- ✅ Performance requirements met
- ✅ Code follows project standards
- ✅ Requirements fully implemented

### 🔧 Troubleshooting

#### Common Issues
- **Missing Requirements**: PRD doesn't contain enough detail
  - Solution: Clarify requirements before proceeding
- **Unclear Test Criteria**: Requirements not testable
  - Solution: Break down into smaller, testable units
- **Test Failures**: Tests don't validate correctly
  - Solution: Review test logic against requirements

#### Validation Commands
```bash
# Check test coverage
python -m pytest --cov=.

# Run specific test types
python -m pytest tests/unit/
python -m pytest tests/integration/
python -m pytest tests/e2e/
python -m pytest tests/performance/

# Generate coverage report
python -m pytest --cov=. --cov-report=html
```

### 📋 Checklist

#### Before Starting Development
- [ ] PRD thoroughly analyzed
- [ ] Test specifications generated
- [ ] Test files created from specifications
- [ ] Test coverage requirements validated
- [ ] All requirements have corresponding tests

#### During Development
- [ ] Tests run and fail initially
- [ ] Minimal code implemented to pass tests
- [ ] All tests pass after implementation
- [ ] Code refactored while maintaining tests
- [ ] Performance requirements validated

#### After Development
- [ ] Complete test suite passes
- [ ] Code coverage >95%
- [ ] Performance benchmarks met
- [ ] Documentation updated
- [ ] Requirements validation complete