# ShoppingCart

![Build and Code Coverage](https://github.com/lehlogonolom84/ee-assessment/actions/workflows/build-and-coverage.yml/badge.svg)

## Solution Insight

This project follows industry best practices, including SOLID principles, Clean Code, Test-Driven Development (TDD), and Continuous Integration (CI).
The build and code coverage badges above reflect the CI pipeline, which automatically executes tests and measures code coverage on every commit to ensure quality and reliability.

More complex architectural patterns, such as Clean Architecture or Domain-Driven Design (DDD), were deliberately avoided in favor of the KISS (Keep It Simple, Stupid) principle. 
Given the project’s size and purpose, the folder structure is intentionally kept simple to ensure clarity, maintainability, and ease of navigation without introducing unnecessary architectural complexity.

The project comprises the following modules in the main folder:

```
src/main/java/com/shoppingcart/
├── constant/
├── implementations/
├── interfaces/
└── models/
```
### Folder Descriptions

- **constant/** - Contains constant values and enumerations used throughout the application, such as cache key prefixes, decimal place configurations, and product name definitions.

- **implementations/** - Contains the concrete implementations of the interfaces. This includes the shopping cart service logic, product catalog, in-memory caching, configuration provider, and validation logic.

- **interfaces/** - Defines the contracts (interfaces) that the implementations must adhere to. This promotes loose coupling and makes the codebase easier to test and maintain.

- **models/** - Contains the domain models and data transfer objects (DTOs) representing core entities like Cart, CartItem, ProductInfo, and result objects.

## Design Decisions

### InCodeConfigProviderImpl
Configuration values such as cache duration, product API base URL, and tax percentage are hardcoded in this implementation. This approach was chosen for simplicity, avoiding the overhead of external configuration files or environment variables for this project's scope. In a production environment, this could be replaced with implementations that read from properties files, environment variables, or a configuration service.

### InMemoryCacheImpl
A lightweight, thread-safe in-memory cache implementation using a HashMap with expiration support. This avoids the complexity of integrating external caching solutions like Redis or Memcached. The cache automatically removes expired entries on retrieval, keeping the implementation simple while meeting the project's caching requirements.

### ShoppingCartValidatorImpl
A central place for validating data before any cart operation can be effected. This ensures all validation logic is consolidated in one location, promoting consistency and maintainability. By centralizing validation, the shopping cart service can focus on business logic while delegating input validation to this dedicated component.

The project comprises the following modules in the test folder:

```
src/test/java/com/shoppingcart/
├── integration/
├── testdata/
└── unit/
```

### Folder Descriptions

- **integration/** - Contains integration tests that verify the interaction between project and the product rest api.

- **testdata/** - Contains test data classes and fixtures used across different test cases to ensure consistent and reusable test scenarios.

- **unit/** - Contains unit tests that verify individual components in isolation, typically using mocks for dependencies.

## Design Decisions

### TestData
Test data is centralized in the testdata folder to avoid duplication and ensure consistency across test cases. This makes it easier to maintain and update test fixtures when the domain models change.

