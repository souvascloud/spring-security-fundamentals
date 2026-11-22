# Spring Security Fundamentals

> Article 1 companion project for *Spring Security in Depth: From
> Fundamentals to Enterprise Best Practices*.

A focused learning project explaining how HTTP requests move through the
Servlet container, Spring Security's filter infrastructure, and Spring
MVC.

## Purpose and scope

This project demonstrates Spring Security architecture and request
lifecycle:

-   Spring Boot application bootstrap
-   `SecurityFilterChain` configuration
-   HTTP Basic authentication with an in-memory user store
-   BCrypt password encoding
-   Request authorization
-   Custom request logging filter
-   Authentication and access-denied handlers
-   MVC controller/service separation
-   Consistent success and error response structures
-   Centralized MVC exception handling

**Learning-project disclaimer:** In-memory users and sample credentials
are for local demonstration only. This is not a complete production
identity platform.

## Architecture

### End-to-end request lifecycle

``` mermaid
flowchart TD
    A["HTTP Client<br/>Browser / curl / API client"] --> B["Servlet Container<br/>Embedded Tomcat"]
    B --> C["DelegatingFilterProxy<br/>Servlet-to-Spring bridge"]
    C --> D["FilterChainProxy<br/>Security filter orchestrator"]
    D --> E{"Select matching<br/>SecurityFilterChain"}
    E --> F["Configured security filters<br/>including custom filter"]
    F --> G{"Authentication<br/>and authorization"}
    G -- "Unauthenticated / denied" --> H["SecurityExceptionHandler<br/>401 / 403"]
    G -- "Allowed" --> I["DispatcherServlet<br/>Spring MVC"]
    I --> J["EmployeeController"]
    J --> K["EmployeeService"]
    K --> L["In-memory employee data"]
    L --> K
    K --> J
    J --> M["ApiResponse / EmployeeResponse"]
    M --> N["HTTP Client"]
    H --> N
```

### Filter-chain view

``` mermaid
flowchart LR
    A["Servlet request"] --> B["DelegatingFilterProxy"]
    B --> C["FilterChainProxy"]
    C --> D["SecurityFilterChain matcher"]
    D --> E["RequestLoggingFilter<br/>custom filter"]
    E --> F["Spring Security filters"]
    F --> G["HTTP Basic authentication"]
    G --> H["Authorization rules"]
    H --> I["Continue to MVC<br/>or reject request"]
```

Spring Boot and Spring Security configure the framework infrastructure.
The application does **not** manually instantiate
`DelegatingFilterProxy` or `FilterChainProxy`.

Filter ordering is configuration-sensitive. These diagrams describe the
intended conceptual flow; inspect the actual configured chain and
startup logs for exact runtime ordering.

## Project structure

``` text
src/main/java/com/souvaniksaha/spring_security_fundamentals/
├── SpringSecurityApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   └── EmployeeController.java
├── dto/response/
│   ├── ApiResponse.java
│   └── EmployeeResponse.java
├── exception/
│   ├── EmployeeNotFoundException.java
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
├── filter/
│   └── RequestLoggingFilter.java
├── model/
│   └── Employee.java
├── security/
│   └── SecurityExceptionHandler.java
└── service/
    └── EmployeeService.java
```

The package namespace shown here is
`com.souvaniksaha.spring_security_fundamentals`.

## API examples

### Public health endpoint

If Actuator is included and health is enabled:

``` http
GET /actuator/health
```

This endpoint is configured as public.

### Authenticated employee endpoints

``` http
GET /api/v1/employees
GET /api/v1/employees/101
```

The current rules require authentication for employee endpoints and for
other unmatched endpoints.

``` bash
curl -u employee:change-me http://localhost:8080/api/v1/employees
```

Illustrative success response:

``` json
{
  "success": true,
  "message": "Employees fetched successfully",
  "data": [
    {
      "id": 101,
      "name": "Rahul",
      "department": "Engineering"
    }
  ],
  "timestamp": "2026-11-22T15:30:00Z"
}
```

The timestamp is generated at runtime; this value is an example.

### Error response

A missing employee is mapped to `404 Not Found` by the custom exception
and MVC exception handler.

``` json
{
  "success": false,
  "message": "Employee not found with ID: 999",
  "path": "/api/v1/employees/999",
  "timestamp": "2026-11-22T15:30:00Z"
}
```

Authentication and access failures occur before the controller. The
configured `AuthenticationEntryPoint` and `AccessDeniedHandler` return
structured `401` and `403` responses.

## Concepts demonstrated

  -------------------------------------------------------------------------------
  Concept                 Project implementation       Purpose
  ----------------------- ---------------------------- --------------------------
  Servlet integration     Framework-managed            Bridges Servlet filtering
                          `DelegatingFilterProxy`      to Spring Security

  Filter orchestration    `FilterChainProxy`           Delegates to the matching
                                                       security chain

  Chain configuration     `SecurityConfig`             Defines request rules and
                                                       authentication

  Authentication          HTTP Basic +                 Establishes caller
                          `UserDetailsService`         identity

  Password encoding       `BCryptPasswordEncoder`      Encodes demo passwords

  Authorization           `authorizeHttpRequests`      Decides whether requests
                                                       are permitted

  Custom filter           `RequestLoggingFilter`       Demonstrates filter
                                                       execution and
                                                       `filterChain.doFilter()`

  Security error handling `SecurityExceptionHandler`   Handles failures before
                                                       MVC

  MVC error handling      `GlobalExceptionHandler`     Maps application
                                                       exceptions to HTTP
                                                       responses

  API contract            `ApiResponse`,               Consistent response
                          `ErrorResponse`              structure
  -------------------------------------------------------------------------------

## Run locally

### Prerequisites

-   JDK 17
-   Maven or Maven Wrapper
-   IDE such as IntelliJ IDEA (optional)

``` bash
java -version
```

From the repository root:

``` bash
./mvnw spring-boot:run
```

Windows PowerShell:

``` powershell
.\mvnw.cmd spring-boot:run
```

If the Maven Wrapper is not included:

``` bash
mvn spring-boot:run
```

The application uses port `8080` unless changed in `application.yml`.

## Configuration and security notes

-   Spring Security logging is set to `DEBUG` for local learning. Reduce
    verbosity in production and avoid exposing sensitive security
    details.
-   Ensure the application logging package is
    `com.souvaniksaha.spring_security_fundamentals`.
-   `csrf.disable()` is a deliberate simplification for this API
    demonstration, **not a universal production recommendation**.
    Browser applications using cookie-based authentication need an
    appropriate CSRF strategy.
-   HTTP Basic is chosen for easy inspection of the authentication flow.
    Production authentication should be selected for the application's
    clients and threat model, commonly OAuth 2.0 / OpenID Connect or a
    properly configured resource server.
-   Demo credentials (`employee/change-me`, `admin/change-me-too`) must
    never be reused outside local development.
-   Never log passwords, Authorization headers, tokens, session
    identifiers, or sensitive personal data. Log only safe request
    metadata.

## Observability: logs, metrics, and traces

-   **Logs** describe events and diagnostic context.
-   **Metrics** measure request rate, error rate, and latency.
-   **Distributed traces** show the path and timing of operations across
    services using trace and span identifiers.

This project currently demonstrates request logging. Distributed tracing
is explained conceptually, not configured with a tracing backend. A
later extension could add Micrometer Tracing with OpenTelemetry and
export traces to a compatible collector/backend.

## Article 1 boundaries

Article 1 focuses on architecture and request lifecycle. These topics
are intentionally deferred to later articles:

-   Database-backed users
-   JWT and OAuth 2.0 / OpenID Connect
-   Method-level authorization
-   Fine-grained permissions
-   Production secrets management
-   Full distributed tracing deployment
-   Advanced browser session and CSRF patterns

------------------------------------------------------------------------

**Key takeaway:** Spring Security integrates with the Servlet filter
chain. It authenticates and authorizes requests before they reach Spring
MVC. Understanding this boundary is the foundation for customizing
filters, handling security failures, and building secure APIs.
