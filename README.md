# CreatorFlow – Creator Collaboration & Video Management Platform

**CreatorFlow** is a full-stack web application designed to streamline collaboration between content creators, editors, and administrators. It provides a centralized platform for managing creator projects, assigning editing tasks, submitting video drafts, reviewing submissions, providing feedback, and managing the video publishing workflow.

The platform simplifies the traditional content production process by bringing project management, video review, role-based access control, and publishing-related operations into one unified system.

---

## Table of Contents

- [Overview](#overview)
- [Problem Statement](#problem-statement)
- [Objectives](#objectives)
- [Key Features](#key-features)
- [User Roles](#user-roles)
- [Project Workflow](#project-workflow)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Core Modules](#core-modules)
- [Video Submission and Storage](#video-submission-and-storage)
- [Authentication and Security](#authentication-and-security)
- [Frontend Structure](#frontend-structure)
- [Backend Architecture](#backend-architecture)
- [Getting Started](#getting-started)
- [Future Enhancements](#future-enhancements)

---

## 1. Overview

CreatorFlow is developed to make content production and collaboration more organized, transparent, and efficient.

In a traditional creator-editor workflow, project assignments, video files, feedback, revisions, and publishing activities are often managed through different communication channels and storage platforms. This can result in miscommunication, delayed approvals, difficulty tracking project progress, and poor visibility into responsibilities.

CreatorFlow addresses these challenges through a centralized application where:

- Administrators can manage creators, editors, and projects.
- Editors can access assigned projects and work on video editing tasks.
- Creators can track their projects and review submitted videos.
- Editors can upload video submissions for review.
- Creators can preview, download, approve, or reject submitted videos.
- Project activities and feedback can be tracked throughout the workflow.
- YouTube-related connection and publishing functionality can be integrated into the publishing workflow.

The application follows a frontend-backend architecture, with React handling the user interface and Spring Boot providing REST APIs, business logic, authentication, and data management.

## 2. Problem Statement

Content creators frequently collaborate with editors and other team members to produce and publish videos. However, managing this process manually creates several problems:

- **Unorganized project management:** Difficulty assigning projects and tracking their progress.
- **Communication gaps:** Feedback and revision requirements can become scattered across multiple platforms.
- **Difficult video review:** Creators need a convenient way to access and evaluate submitted video drafts.
- **Version management challenges:** Tracking different versions of submitted videos can become confusing.
- **Limited access control:** Different users require different permissions depending on their responsibilities.
- **Publishing complexity:** Managing the transition from an approved video to the publishing stage requires additional coordination.

CreatorFlow aims to solve these problems by providing a structured, role-based collaboration and video management environment.

## 3. Objectives

- Develop a centralized creator-editor collaboration platform.
- Implement role-based authentication and authorization.
- Simplify project creation, assignment, and tracking.
- Enable editors to upload video submissions.
- Allow creators to preview and download submitted videos.
- Support video approval and rejection workflows.
- Maintain project activities and review feedback.
- Provide a structured publishing workflow with YouTube integration.
- Build a responsive and user-friendly dashboard experience.

## 4. Key Features

### Authentication and Authorization
- Secure user authentication using JWT.
- Access-token and refresh-token-based authentication.
- Role-based route protection.
- Separate experiences for different user roles.
- Protected backend endpoints.

### Project Management
- Create and manage creator projects.
- Assign projects to editors.
- Track project status and progress.
- Display project information through organized dashboard cards.
- Manage project-related activities.

### Video Submission and Review
- Allow editors to upload video drafts.
- Associate submissions with their respective projects.
- Maintain submission versions.
- Store uploaded video metadata.
- Preview submitted videos directly in the application.
- Download submitted videos.
- Display submission information, including uploader and upload timestamp.
- Support the review and approval/rejection workflow.

### Feedback and Revision Management
- Maintain project-related review feedback.
- Track activities associated with project progress.
- Support communication of revision requirements.
- Provide better visibility into the review process.

### Dashboard Experience
- Separate creator and editor dashboards.
- Organized project listings.
- Improved project status badges.
- Clear visual hierarchy for project information.
- Responsive layouts and reusable UI components.
- A warmer, premium dashboard and card design.

### YouTube Integration
- YouTube connection functionality within the publishing workflow.
- Publishing-related project operations.
- A structured transition from project approval toward video publishing.

*The exact OAuth scopes and publishing configuration depend on the backend integration and deployment setup.*

---

## 5. User Roles

CreatorFlow follows a role-based architecture.

| Role | Responsibilities |
|---|---|
| Admin | Manages users, creators, editors, and overall project operations. |
| Creator | Manages projects, tracks progress, reviews submissions, and handles approval decisions. |
| Editor | Accesses assigned projects, edits videos, uploads submissions, and responds to feedback. |

Each role has access to the functionality required for its responsibilities.

## 6. Project Workflow

The application follows a structured content production workflow.

1. **Project Creation:** A creator or authorized user creates a project with the required details.
2. **Project Assignment:** An editor is assigned to the project.
3. **Project Execution:** The editor works on the assigned video editing task.
4. **Video Submission:** The editor uploads the completed draft through the submission interface.
5. **Video Review:** The creator opens the project and previews the submitted video.
6. **Feedback and Decision:** The creator reviews the submission and can approve it or reject it with revision requirements.
7. **Revision:** If changes are requested, the editor can work on another version and submit it again.
8. **Publishing:** Once approved, the project can proceed to the publishing workflow.

This workflow creates a clear separation between project execution, review, and publishing.

---

## 7. Technology Stack

| Category | Technologies |
|---|---|
| Frontend | React.js, JavaScript |
| Frontend Build Tool | Vite |
| Styling | Tailwind CSS |
| UI Components | Reusable React components and dashboard components |
| Routing | React Router |
| HTTP Communication | Axios |
| Backend | Java, Spring Boot |
| Backend Architecture | RESTful APIs |
| Authentication | Spring Security, JWT |
| Database | Configured backend database |
| File Handling | Spring Boot MultipartFile |
| Video Storage | Local filesystem in the current implementation |
| Version Control | Git, GitHub |
| External Integration | YouTube publishing integration |

---

## 8. System Architecture

CreatorFlow follows a client-server architecture.

```text
                  CREATORFLOW
                       |
          +------------+------------+
          |                         |
     FRONTEND                  BACKEND
     React.js                 Spring Boot
          |                         |
     React Router              REST APIs
          |                         |
     Axios HTTP  <---------->  Controllers
                                    |
                               Services
                                    |
                               Repositories
                                    |
                                Database
                                    |
                            File Management
                                    |
                             Video Storage
```

### Frontend Responsibilities
- Render dashboards and application pages.
- Handle user interactions.
- Manage application state.
- Communicate with backend APIs using Axios.
- Handle authentication-related UI logic.
- Display projects, submissions, and feedback.

### Backend Responsibilities
- Handle HTTP requests and responses.
- Apply business rules and validations.
- Authenticate and authorize users.
- Manage projects and submissions.
- Handle multipart video uploads.
- Store and retrieve project information.
- Manage publishing-related operations.

---

## 9. Core Modules

### A. Admin Module
Provides administrative functionality for managing the platform.

Responsibilities include:
- Managing creator and editor accounts.
- Managing project-related operations.
- Monitoring platform activities.
- Maintaining centralized control over user and project management.

### B. Creator Module
Provides creators with a workspace to manage their content production activities.

Responsibilities include:
- Viewing creator projects.
- Tracking assigned editors and project progress.
- Viewing submitted video drafts.
- Previewing and downloading submissions.
- Reviewing video versions.
- Providing feedback.
- Approving or rejecting submissions.
- Accessing publishing-related functionality.

### C. Editor Module
Provides editors with a workspace to manage assigned editing tasks.

Responsibilities include:
- Viewing assigned projects.
- Accessing project information.
- Working on video editing tasks.
- Uploading video submissions.
- Maintaining submission versions.
- Tracking review feedback.
- Working on requested revisions.

### D. Project Management Module
Handles project-related business logic.

Main responsibilities:
- Project creation and retrieval.
- Project assignment.
- Project updates.
- Project status management.
- Project activity tracking.
- Project-specific review information.

### E. Video Submission Module
Manages the lifecycle of video drafts.

Responsibilities:
- Accepting multipart video uploads.
- Associating uploaded files with projects.
- Tracking submission versions.
- Recording uploader information and timestamps.
- Retrieving the latest submission.
- Providing video preview and download access.

### F. Publishing Module
Handles the publishing stage of the content workflow.

Responsibilities:
- Managing publishing-related project operations.
- Supporting YouTube connection functionality.
- Coordinating approved content with the publishing process.

---

## 10. Video Submission and Storage

The current video submission implementation uses Spring Boot's multipart file handling.

### Upload Process

1. The editor selects a video file.
2. The frontend creates a `FormData` object.
3. Axios sends the file to the backend using a multipart HTTP request.
4. Spring Boot receives the file using `MultipartFile`.
5. The backend stores the file in the configured upload directory.
6. The database stores the submission metadata and video filename.
7. The frontend retrieves submission information through the submission API.

### Backend Implementation

The upload endpoint follows this structure:

```java
@PostMapping("/{projectId}/submissions")
public SubmissionResponse uploadSubmission(
        @PathVariable Long projectId,
        @RequestParam("video") MultipartFile video,
        Authentication authentication
)
```

### Submission Entity

The `ProjectSubmission` entity maintains information such as:

- Submission ID
- Associated project
- Submission version
- Video filename/path
- Uploaded by
- Upload timestamp

### Latest Submission

The application provides an endpoint to retrieve the latest submission for a project.

The frontend uses this information to display:

- Video preview.
- Download functionality.
- Submission version.
- Uploader information.
- Upload date and time.

### Current Storage Configuration

The current implementation uses local filesystem storage.

Example configuration:

```properties
file.upload-dir=uploads/videos
```

The database stores the filename rather than duplicating the complete filesystem path.

A Spring MVC resource handler exposes the uploaded videos through the configured `/uploads/**` resource mapping.

**Planned improvement:** Move video storage to Amazon S3 to support scalable cloud-based storage and reduce dependence on local application storage.

---

## 11. Authentication and Security

Security is an important part of CreatorFlow because different users must access different project operations.

The application uses JWT-based authentication with Spring Security.

Key security mechanisms include:

- JWT-based authentication.
- Access and refresh token handling.
- Protected frontend routes.
- Backend authorization.
- Role-based access control.
- Authentication-aware API requests.
- Controlled access to project-related operations.

The backend remains responsible for enforcing permissions rather than relying only on frontend route restrictions.

---

## 12. Frontend Structure

The frontend is maintained in a separate repository:

`CreatorFlow-Frontend`

A high-level structure of the React application is:

```text
CreatorFlow-Frontend/
│
├── public/
│
├── src/
│   │
│   ├── assets/
│   │
│   ├── components/
│   │   ├── common/
│   │   ├── dashboard/
│   │   ├── projects/
│   │   └── ...
│   │
│   ├── context/
│   │   └── AuthContext
│   │
│   ├── hooks/
│   │
│   ├── layouts/
│   │
│   ├── pages/
│   │   ├── auth/
│   │   ├── admin/
│   │   ├── creator/
│   │   ├── editor/
│   │   └── projects/
│   │
│   ├── routes/
│   │
│   ├── services/
│   │   └── projectService.js
│   │
│   ├── utils/
│   │
│   ├── App.jsx
│   ├── main.jsx
│   └── ...
│
├── .env.example
├── package.json
├── vite.config.js
└── README.md
```

*This is a high-level organizational overview; the exact directory and filename structure should be synchronized with the actual repository.*

### Frontend Responsibilities by Directory

- `components/`: Reusable interface components.
- `context/`: Shared application state, including authentication.
- `pages/`: Main application pages for different roles.
- `routes/`: Application navigation and route protection.
- `services/`: API communication and backend service functions.
- `layouts/`: Shared layouts for dashboards and application sections.
- `utils/`: Common helper functions.

---

## 13. Backend Architecture

The backend follows a layered Spring Boot architecture.

```text
CreatorFlow-Backend/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── ...
│       │       ├── config/
│       │       ├── controller/
│       │       ├── service/
│       │       ├── repository/
│       │       ├── entity/
│       │       ├── dto/
│       │       ├── security/
│       │       └── ...
│       │
│       └── resources/
│           └── application.properties
│
├── uploads/
│   └── videos/
│
└── pom.xml
```

*The package names above illustrate the architectural layers and should be replaced with the exact package names from the backend repository.*

### Architectural Responsibilities

| Layer | Responsibility |
|---|---|
| Controller | Handles HTTP requests and API responses. |
| Service | Implements business logic and application workflows. |
| Repository | Handles database access. |
| Entity | Represents persistent database objects. |
| DTO | Transfers structured data between application layers and API clients. |
| Security | Handles authentication and authorization. |
| Config | Contains application and infrastructure configuration. |

---

## 14. Getting Started

### Prerequisites

Install the following:

- Node.js and npm.
- Java Development Kit (JDK) compatible with the backend project.
- Maven, if not using the Maven wrapper.
- A configured database.
- Git.

### Frontend Setup

Clone the frontend repository:

```bash
git clone https://github.com/GaneshMirgane40/CreatorFlow-Frontend.git
```

Navigate into the project:

```bash
cd CreatorFlow-Frontend
```

Install dependencies:

```bash
npm install
```

Configure the frontend environment variables according to the project's environment configuration.

Start the development server:

```bash
npm run dev
```

Vite will display the local development URL in the terminal.

### Backend Setup

Clone or navigate to the backend repository.

```bash
cd CreatorFlow-Backend
```

Configure the database connection and required application properties in `application.properties`.

Run the application using Maven:

```bash
./mvnw spring-boot:run
```

Alternatively:

```bash
mvn spring-boot:run
```

The backend will start using the configured server port.

**Note:** Database credentials, JWT secrets, and external integration credentials should be supplied through environment variables or secure configuration rather than committed to GitHub.

---

## 15. Future Enhancements

The following improvements are planned for making CreatorFlow more scalable and production-ready.

### Amazon S3 Integration
- Replace local video storage with Amazon S3.
- Store video files in cloud object storage.
- Maintain file metadata in the application database.
- Use secure access mechanisms such as pre-signed URLs where appropriate.
- Improve scalability for large video files.

### Docker Integration
- Containerize the React frontend and Spring Boot backend.
- Create consistent development and deployment environments.
- Simplify application setup.
- Use Docker Compose for local multi-service development.

### Cloud Deployment
- Deploy the backend and frontend to suitable cloud infrastructure.
- Configure production environment variables.
- Integrate managed database services.
- Configure scalable file storage.

### Additional Improvements
- Advanced video version comparison.
- Improved review notifications.
- Enhanced activity tracking.
- More detailed analytics dashboards.
- Improved publishing automation.
- Production-level logging and monitoring.

---

## 16. Project Status

CreatorFlow has an implemented foundation for its core creator-editor collaboration workflow.

| Component | Status |
|---|---|
| Authentication and role-based routing | Implemented |
| Creator and editor dashboards | Implemented |
| Project assignment workflow | Implemented |
| Project management operations | Implemented |
| Video multipart upload | Implemented |
| Submission version tracking | Implemented |
| Latest submission retrieval | Implemented |
| Video preview and download UI | Implemented |
| Review and feedback workflow | Developed |
| YouTube connection and publishing workflow | Integration developed; deployment configuration may be required |
| AWS S3 video storage | Planned |
| Docker containerization | Planned |
| Production cloud deployment | Planned |

*The status table reflects the development work discussed so far. Verify each feature against the latest backend and frontend code before treating it as a release checklist.*

---

## 17. Conclusion

CreatorFlow aims to provide a unified and structured environment for managing the complete content production collaboration process.

By combining project management, role-based access control, video submission, review workflows, feedback management, and publishing-related functionality, the platform reduces manual coordination and improves transparency between creators and editors.

The project also provides a foundation for future improvements involving cloud storage, containerization, and scalable deployment.

**CreatorFlow – Simplifying Creator Collaboration, From Editing to Publishing.**

---

## Author

Developed as a full-stack web application project.
CreatorFlow Frontend - https://github.com/GaneshMirgane40/CreatorFlow-Frontend

**Frontend Repository:** [CreatorFlow-Frontend](https://github.com/GaneshMirgane40/CreatorFlow-Frontend)

**Backend Repository:** Add the backend repository link here.
